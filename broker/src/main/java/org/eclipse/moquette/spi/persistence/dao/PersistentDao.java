package org.eclipse.moquette.spi.persistence.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
import org.eclipse.moquette.spi.impl.events.PublishEvent;
import org.eclipse.moquette.spi.impl.storage.StoredPublishEvent;
import org.eclipse.moquette.spi.persistence.MongoDBPersistentStore;
import org.eclipse.moquette.spi.persistence.model.Persistent;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yycoder on 2015/1/9.
 */
public class PersistentDao extends BasicDAO<Persistent, Serializable> {
    public PersistentDao(Datastore ds) {
        super(Persistent.class, ds);
    }

    private Query getQuery() {
        return getDs().createQuery(getEntityClazz());
    }

    public void saveEvent(PublishEvent evt) {
        Query query = getQuery();
        query.filter("clientID", evt.getClientID());
        Persistent persistentEvt = findOne(query);
        if (null == persistentEvt) {
            List<StoredPublishEvent> events = new ArrayList<StoredPublishEvent>();
            events.add(MongoDBPersistentStore.convertToStored(evt));
            persistentEvt = new Persistent(evt.getClientID(), events);
            save(persistentEvt);
        } else {
            UpdateOperations opts = getDs().createUpdateOperations(getEntityClazz());
            opts.add("events", MongoDBPersistentStore.convertToStored(evt));
            update(query, opts);
        }
    }

    public List<StoredPublishEvent> getByClientID(String clientID) {
        Query query = getQuery();
        query.filter("clientID", clientID);
        Persistent persistent = findOne(query);
        return persistent != null ? persistent.getEvents() : null;
    }

    public UpdateResults cleanPublishMessage(String clientID, int messageID) {
        Query query = getQuery();
        query.filter("clientID", clientID);
        UpdateOperations opts = getDs().createUpdateOperations(getEntityClazz());
        opts.removeAll("events", new BasicDBObject("m_msgID", messageID));
        return update(query, opts);
    }

    public WriteResult cleanPublishes(String clientID) {
        Query query = getQuery();
        query.filter("clientID", clientID);
        return deleteByQuery(query);
    }
}
