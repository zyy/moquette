package org.eclipse.moquette.spi.persistence.model;

import org.eclipse.moquette.spi.impl.subscriptions.Subscription;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.Set;

/**
 * Created by yycoder on 2015/1/9.
 */
@Entity(value = "subscriptions", noClassnameStored = true)
public class Subscriptions {
    @Id
    private String clientID;
    @Embedded
    private Set<Subscription> subscriptions;

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public Set<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public Subscriptions() {
    }

    public Subscriptions(String clientID, Set<Subscription> subscriptions) {
        this.clientID = clientID;
        this.subscriptions = subscriptions;
    }
}
