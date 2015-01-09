package org.eclipse.moquette.spi.persistence.dao;

import com.mongodb.WriteResult;
import org.eclipse.moquette.spi.impl.storage.StoredPublishEvent;
import org.eclipse.moquette.spi.persistence.model.QoS2;
import org.eclipse.moquette.spi.persistence.model.Retained;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;

import java.io.Serializable;

/**
 * Created by yycoder on 2015/1/9.
 */
public class QoS2Dao extends BasicDAO<QoS2, Serializable> {
    public QoS2Dao(Datastore ds) {
        super(QoS2.class, ds);
    }

    private Query getQuery() {
        return getDs().createQuery(getEntityClazz());
    }

    public void saveMessage(String publishKey, StoredPublishEvent storedPublishEvent) {
        save(new QoS2(publishKey, storedPublishEvent));
    }

    public WriteResult removeByPublishKey(String publishKey) {
        Query query = getQuery();
        query.filter("publishKey", publishKey);
        return deleteByQuery(query);
    }

    public StoredPublishEvent getMessageByPublishKey(String publishKey) {
        Query query = getQuery();
        query.filter("publishKey", publishKey);
        return findOne(query).getEvent();
    }
}
