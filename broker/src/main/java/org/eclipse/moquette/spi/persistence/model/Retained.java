package org.eclipse.moquette.spi.persistence.model;

import org.eclipse.moquette.spi.IMessagesStore;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Created by yycoder on 2015/1/9.
 */
@Entity(value = "retained", noClassnameStored = true)
public class Retained {
    @Id
    private String topic;
    private IMessagesStore.StoredMessage message;

    public Retained() {
    }

    public Retained(String topic, IMessagesStore.StoredMessage message) {
        this.topic = topic;
        this.message = message;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public IMessagesStore.StoredMessage getMessage() {
        return message;
    }

    public void setMessage(IMessagesStore.StoredMessage message) {
        this.message = message;
    }
}
