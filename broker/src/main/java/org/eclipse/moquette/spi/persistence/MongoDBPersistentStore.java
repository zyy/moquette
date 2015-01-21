package org.eclipse.moquette.spi.persistence;

/**
 * Created by yycoder on 2015/1/12.
 */

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.eclipse.moquette.proto.messages.AbstractMessage;
import org.eclipse.moquette.spi.IMatchingCondition;
import org.eclipse.moquette.spi.IMessagesStore;
import org.eclipse.moquette.spi.ISessionsStore;
import org.eclipse.moquette.spi.impl.events.PublishEvent;
import org.eclipse.moquette.spi.impl.storage.StoredPublishEvent;
import org.eclipse.moquette.spi.impl.subscriptions.Subscription;
import org.eclipse.moquette.spi.persistence.dao.*;
import org.eclipse.moquette.spi.persistence.model.History;
import org.eclipse.moquette.spi.persistence.model.Retained;
import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by yycoder on 2015/1/9.
 */
public class MongoDBPersistentStore implements IMessagesStore, ISessionsStore {
    private static final Logger LOG = LoggerFactory.getLogger(MongoDBPersistentStore.class);
    private Properties props;
    private MongoClient mongoClient;
    private RetainedDao retainedDao;
    //maps clientID to the list of pending messages stored
    private PersistentDao persistentDao;
    //bind clientID+MsgID -> evt message published
    private InfightDao inflightDao;
    //bind clientID+MsgID -> evt message published
    private QoS2Dao qos2Dao;
    //persistent Map of clientID, set of Subscriptions
    private SubscriptionsDao subscriptionsDao;
    //persistent message history
    private HistoryDao historyDao;

    public MongoDBPersistentStore(Properties props) {
        this.props = props;
    }

    @Override
    public void initStore() {
        try {
            ServerAddress serverAddress = new ServerAddress(props.getProperty("mongo_ip"),
                    Integer.parseInt(props.getProperty("mongo_port")));
            List<MongoCredential> mongoCredentials = Arrays.asList(MongoCredential.createMongoCRCredential(props.getProperty("mongo_usr"),
                    props.getProperty("mongo_db"), props.getProperty("mongo_pwd").toCharArray()));
            MongoClientOptions opts = MongoClientOptions.builder().maxWaitTime(Integer.parseInt(props.getProperty("mongo_maxWaitTime"))).build();
            mongoClient = new MongoClient(serverAddress, mongoCredentials, opts);
            String mongo_db = props.getProperty("mongo_db");
            Morphia morphia = new Morphia();
            retainedDao = new RetainedDao(morphia.createDatastore(mongoClient, mongo_db));
            persistentDao = new PersistentDao(morphia.createDatastore(mongoClient, mongo_db));
            inflightDao = new InfightDao(morphia.createDatastore(mongoClient, mongo_db));
            subscriptionsDao = new SubscriptionsDao(morphia.createDatastore(mongoClient, mongo_db));
            qos2Dao = new QoS2Dao(morphia.createDatastore(mongoClient, mongo_db));
            historyDao = new HistoryDao(morphia.createDatastore(mongoClient, mongo_db));
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("mongo db init error", e);
        }
    }

    @Override
    public void cleanRetained(String topic) {
        retainedDao.deleteByTopic(topic);
    }

    @Override
    public void storeRetained(String topic, ByteBuffer message, AbstractMessage.QOSType qos) {
        if (!message.hasRemaining()) {
            //clean the message from topic
            retainedDao.deleteByTopic(topic);
        } else {
            //store the message to the topic
            byte[] raw = new byte[message.remaining()];
            message.get(raw);
            retainedDao.save(new Retained(topic, new StoredMessage(raw, qos, topic)));
        }
    }

    @Override
    public Collection<StoredMessage> searchMatching(IMatchingCondition condition) {
        LOG.debug("searchMatching scanning all retained messages, presents are {}", retainedDao.count());

        List<StoredMessage> results = new ArrayList<StoredMessage>();

        List<Retained> retainedList = retainedDao.find().asList();
        for (Retained m : retainedList) {
            if (condition.match(m.getTopic())) {
                results.add(m.getMessage());
            }
        }

        return results;
    }

    @Override
    public void storePublishForFuture(PublishEvent evt) {
        persistentDao.saveEvent(evt);
        //NB rewind the evt message content
        LOG.debug("Stored published message for client <{}> on topic <{}>", evt.getClientID(), evt.getTopic());
    }

    @Override
    public List<PublishEvent> retrievePersistedPublishes(String clientID) {
        List<StoredPublishEvent> storedEvts = persistentDao.getByClientID(clientID);
        if (storedEvts == null) {
            return null;
        }
        List<PublishEvent> liveEvts = new ArrayList<PublishEvent>();
        for (StoredPublishEvent storedEvt : storedEvts) {
            liveEvts.add(convertFromStored(storedEvt));
        }
        return liveEvts;
    }

    @Override
    public void cleanPersistedPublishMessage(String clientID, int messageID) {
        persistentDao.cleanPublishMessage(clientID, messageID);
    }

    @Override
    public void cleanPersistedPublishes(String clientID) {
        persistentDao.cleanPublishes(clientID);
    }

    public void cleanInFlight(String msgID) {
        inflightDao.deleteByPublishKey(msgID);
    }

    public void addInFlight(PublishEvent evt, String publishKey) {
        StoredPublishEvent storedEvt = convertToStored(evt);
        inflightDao.saveMessage(publishKey, storedEvt);
    }

    public void addNewSubscription(Subscription newSubscription, String clientID) {
        LOG.debug("addNewSubscription invoked with subscription {} for client {}", newSubscription, clientID);
        subscriptionsDao.addNewSubscription(newSubscription, clientID);
    }

    public void wipeSubscriptions(String clientID) {
        subscriptionsDao.wipeSubscriptions(clientID);
    }

    @Override
    public void updateSubscriptions(String clientID, Set<Subscription> subscriptions) {
        subscriptionsDao.updateSubscriptions(clientID, subscriptions);
    }

    public List<Subscription> listAllSubscriptions() {
        return subscriptionsDao.listAllSubscriptions();
    }

    @Override
    public boolean contains(String clientID) {
        return subscriptionsDao.contains(clientID);
    }

    @Override
    public void close() {
        if (mongoClient != null)
            mongoClient.close();
        LOG.debug("closed mongo db storage");
    }

    /*-------- QoS 2  storage management --------------*/
    public void persistQoS2Message(String publishKey, PublishEvent evt) {
        LOG.debug("persistQoS2Message store pubKey: {}, evt: {}", publishKey, evt);
        qos2Dao.saveMessage(publishKey, convertToStored(evt));
    }

    public void removeQoS2Message(String publishKey) {
        qos2Dao.removeByPublishKey(publishKey);
    }

    public void saveHistoryMessage(String fromId, String toId, ByteBuffer message) {
        History msg = new History(fromId, toId, new String(message.array(), Charset.forName("UTF-8")));
        historyDao.save(msg);
    }

    public PublishEvent retrieveQoS2Message(String publishKey) {
        StoredPublishEvent storedEvt = qos2Dao.getMessageByPublishKey(publishKey);
        return convertFromStored(storedEvt);
    }

    public static StoredPublishEvent convertToStored(PublishEvent evt) {
        StoredPublishEvent storedEvt = new StoredPublishEvent(evt);
        return storedEvt;
    }

    public static PublishEvent convertFromStored(StoredPublishEvent evt) {
        byte[] message = evt.getMessage();
        ByteBuffer bbmessage = ByteBuffer.wrap(message);
        //bbmessage.flip();
        PublishEvent liveEvt = new PublishEvent(evt.getTopic(), evt.getQos(),
                bbmessage, evt.isRetain(), evt.getClientID(), evt.getMessageID());
        return liveEvt;
    }
}