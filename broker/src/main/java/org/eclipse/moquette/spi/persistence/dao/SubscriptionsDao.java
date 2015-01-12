package org.eclipse.moquette.spi.persistence.dao;

import com.mongodb.WriteResult;
import org.eclipse.moquette.spi.impl.subscriptions.Subscription;
import org.eclipse.moquette.spi.persistence.model.Subscriptions;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yycoder on 2015/1/9.
 */
public class SubscriptionsDao extends BasicDAO<Subscriptions, Serializable> {
    public SubscriptionsDao(Datastore ds) {
        super(Subscriptions.class, ds);
    }

    private Query getQuery() {
        return getDs().createQuery(getEntityClazz());
    }

    public void addNewSubscription(Subscription newSubscription, String clientID) {
        Query query = getQuery();
        query.filter("clientID", clientID);
        Subscriptions subscriptions = findOne(query);
        boolean save = false;
        if (null == subscriptions) {
            save = true;
            subscriptions = new Subscriptions(clientID, new HashSet<Subscription>());
        }

        Set<Subscription> subs = subscriptions.getSubscriptions();
        if (subs != null && !subs.contains(newSubscription)) {
            //TODO check the subs doesn't contain another subscription to the same topic with different
            Subscription existingSubscription = null;
            for (Subscription scanSub : subs) {
                if (newSubscription.getTopicFilter().equals(scanSub.getTopicFilter())) {
                    existingSubscription = scanSub;
                    break;
                }
            }
            if (existingSubscription != null) {
                subs.remove(existingSubscription);
            }
            subs.add(newSubscription);
            subscriptions.setSubscriptions(subs);
        }

        if (save) {
            save(subscriptions);
        } else {
            if (subs == null || subs.size() == 0) {
                deleteByQuery(query);
            } else {
                UpdateOperations opts = getDs().createUpdateOperations(getEntityClazz());
                opts.set("subscriptions", subs);
                update(query, opts);
            }
        }
    }

    public WriteResult wipeSubscriptions(String clientID) {
        Query query = getQuery();
        query.filter("clientID", clientID);
        return deleteByQuery(query);
    }

    public UpdateResults updateSubscriptions(String clientID, Set<Subscription> subscriptions) {
        Query query = getQuery();
        query.filter("clientID", clientID);
        UpdateOperations opts = getDs().createUpdateOperations(getEntityClazz());
        opts.set("subscriptions", subscriptions);
        return update(query, opts);
    }

    public List<Subscription> listAllSubscriptions() {
        List<Subscriptions> subscriptions = find().asList();
        List<Subscription> list = new ArrayList<Subscription>();
        for (Subscriptions sub : subscriptions) {
            if (sub.getSubscriptions() != null)
                list.addAll(sub.getSubscriptions());
        }
        return list;
    }

    public boolean contains(String clientID) {
        Query query = getQuery();
        query.filter("clientID", clientID);
        return findOne(query) != null;
    }
}
