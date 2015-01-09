package org.eclipse.moquette.spi.persistence.model;

import org.eclipse.moquette.spi.impl.storage.StoredPublishEvent;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Created by yycoder on 2015/1/9.
 */
@Entity(value = "infight", noClassnameStored = true)
public class Infight {
    @Id
    private String publishKey;
    private StoredPublishEvent event;

    public String getPublishKey() {
        return publishKey;
    }

    public void setPublishKey(String publishKey) {
        this.publishKey = publishKey;
    }

    public StoredPublishEvent getEvent() {
        return event;
    }

    public void setEvent(StoredPublishEvent event) {
        this.event = event;
    }

    public Infight() {
    }

    public Infight(String publishKey, StoredPublishEvent event) {
        this.publishKey = publishKey;
        this.event = event;
    }
}
