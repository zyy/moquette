package org.eclipse.moquette.spi.persistence.dao;

import org.eclipse.moquette.spi.persistence.model.SingleHistory;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yycoder on 2015/1/9.
 */
public class SingleHistoryDao extends BasicDAO<SingleHistory, Serializable> {
    public SingleHistoryDao(Datastore ds) {
        super(SingleHistory.class, ds);
    }

    private Query getQuery() {
        return getDs().createQuery(getEntityClazz());
    }

    public UpdateResults saveReadHistory(Long messageID) {
        Query query = getQuery().disableValidation();
        query.filter("_id", messageID);
        UpdateOperations opts = getDs().createUpdateOperations(getEntityClazz());
        opts.set("isRead", true);
        return updateFirst(query, opts);
    }

    public List<SingleHistory> findUnreadMessage(String clientID) {
        Query query = getQuery().disableValidation();
        query.filter("topic", clientID);
        query.filter("isRead", false);
        query.order("-time");
        return find(query).asList();
    }
}
