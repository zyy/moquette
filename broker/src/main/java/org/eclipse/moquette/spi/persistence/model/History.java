package org.eclipse.moquette.spi.persistence.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.Date;
import java.util.Set;

/**
 * Created by yycoder on 2015/1/13.
 */
@Entity(value = "history", noClassnameStored = true)
public class History {
    @Id
    private long messageId;
    private String sendId;
    private String topic;
    private Date time;
    private String content;
    private boolean isRead;

    public History() {
    }

    public History(Long messageId, String sendId, String topic, String content) {
        this.messageId = messageId;
        this.sendId = sendId;
        this.topic = topic;
        this.time = new Date();
        this.content = content;
    }

    public History(long messageId, String sendId, String topic, String content, boolean isRead) {
        this.messageId = messageId;
        this.sendId = sendId;
        this.topic = topic;
        this.content = content;
        this.isRead = isRead;
        this.time = new Date();
    }

    public String getSendId() {
        return sendId;
    }

    public void setSendId(String sendId) {
        this.sendId = sendId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getMessageId() {
        return messageId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }
}
