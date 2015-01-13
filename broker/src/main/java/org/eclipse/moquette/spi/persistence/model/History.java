package org.eclipse.moquette.spi.persistence.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.Date;

/**
 * Created by yycoder on 2015/1/13.
 */
@Entity(value = "history", noClassnameStored = true)
public class History {
    @Id
    private ObjectId id;
    private String fromId;
    private String toId;
    private Date time;
    private String message;

    public History() {
    }

    public History(String fromId, String toId, String message) {
        this.id = new ObjectId();
        this.fromId = fromId;
        this.toId = toId;
        this.message = message;
        this.time = new Date();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
