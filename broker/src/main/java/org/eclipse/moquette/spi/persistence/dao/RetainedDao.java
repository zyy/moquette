package org.eclipse.moquette.spi.persistence.dao;

import org.eclipse.moquette.spi.persistence.model.Retained;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;

import java.io.Serializable;

/**
 * Created by yycoder on 2015/1/9.
 */
public class RetainedDao extends BasicDAO<Retained, Serializable> {
    public RetainedDao(Datastore ds) {
        super(Retained.class, ds);
    }

    public void deleteByTopic(String topic) {
        Query query = getQuery();
        query.filter("topic", topic);
        deleteByQuery(query);
    }

    private Query getQuery() {
        return getDs().createQuery(getEntityClazz());
    }
}
