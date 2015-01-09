package org.eclipse.moquette.spi.persistence.model;

import org.eclipse.moquette.spi.impl.storage.StoredPublishEvent;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.List;

/**
 * Created by yycoder on 2015/1/9.
 */
@Entity(value = "persistent", noClassnameStored = true)
public class Persistent {
    @Id
    private String clientID;
    private List<StoredPublishEvent> events;

    public Persistent() {
    }

    public Persistent(String clientID, List<StoredPublishEvent> events) {
        this.clientID = clientID;
        this.events = events;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public List<StoredPublishEvent> getEvents() {
        return events;
    }

    public void setEvents(List<StoredPublishEvent> events) {
        this.events = events;
    }


}
