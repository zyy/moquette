package org.eclipse.moquette.spi.persistence.dao;

import com.mongodb.WriteResult;
import org.eclipse.moquette.spi.impl.storage.StoredPublishEvent;
import org.eclipse.moquette.spi.persistence.model.Infight;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;

import java.io.Serializable;

/**
 * Created by yycoder on 2015/1/9.
 */
public class InfightDao extends BasicDAO<Infight, Serializable> {
    public InfightDao(Datastore ds) {
        super(Infight.class, ds);
    }

    private Query getQuery() {
        return getDs().createQuery(getEntityClazz());
    }

    public WriteResult deleteByPublishKey(String publishKey) {
        Query query = getQuery();
        query.filter("publishKey", publishKey);
        return deleteByQuery(query);
    }

    public void saveMessage(String publishKey, StoredPublishEvent storedEvt) {
        save(new Infight(publishKey, storedEvt));
    }
}
