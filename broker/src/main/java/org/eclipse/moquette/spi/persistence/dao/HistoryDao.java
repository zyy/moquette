package org.eclipse.moquette.spi.persistence.dao;

import org.eclipse.moquette.spi.persistence.model.History;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;

import java.io.Serializable;

/**
 * Created by yycoder on 2015/1/9.
 */
public class HistoryDao extends BasicDAO<History, Serializable> {
    public HistoryDao(Datastore ds) {
        super(History.class, ds);
    }

    private Query getQuery() {
        return getDs().createQuery(getEntityClazz());
    }
}
