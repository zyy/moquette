package org.eclipse.moquette.util;

import org.eclipse.moquette.server.ConfigurationParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.ParseException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by larry on 14-5-7.
 * time:41,instanceId:12,sequence:11
 * 21年,4096实例,2048/ms并发
 */
public class IDGenerator {

    protected static final Logger LOG = LoggerFactory.getLogger(IDGenerator.class);
    private static long epoch = 1321929000000L;
    private static long instanceIdBits = 12L;
    private static long sequenceBits = 11L;
    private static long maxInstanceId = -1L ^ (-1L << instanceIdBits);
    private static long instanceIdShift = sequenceBits;
    private static long timestampLeftShift = sequenceBits + instanceIdBits;
    private static long instanceId;
    private static long instanceIdValue;

    private static AtomicInteger sequence = new AtomicInteger(0);
    private static long lastId = -1L;

    static {
        String configPath = System.getProperty("moquette.path", null);
        ConfigurationParser confParser = new ConfigurationParser();
        try {
            confParser.parse(new File(configPath, "config/moquette.conf"));
        } catch (ParseException pex) {
            LOG.warn("An error occurred in parsing configuration, fallback on default configuration", pex);
        }
        Properties configProps = confParser.getProperties();
        instanceId = Integer.parseInt(configProps.getProperty("instance_id"));
        if (instanceId > maxInstanceId || instanceId < 0) {
            throw new IllegalArgumentException(String.format("instance Id can't be greater than %d or less than 0", maxInstanceId));
        }
        instanceIdValue = (instanceId << instanceIdShift);
    }

    public static long parseTimeMillisFromId(long id) {
        return (id >>> timestampLeftShift) + epoch;
    }

    public static long nextId() {
        long id;
        if (sequence.compareAndSet(1999, 0)) {
            id = ((System.currentTimeMillis() - epoch) << timestampLeftShift) | instanceIdValue | 0;
        } else {
            id = ((System.currentTimeMillis() - epoch) << timestampLeftShift) | instanceIdValue | sequence.getAndIncrement();
        }
        while (id <= lastId) {
            if (System.currentTimeMillis() < parseTimeMillisFromId(id)) {
                LOG.error(String.format("clock is moving backwards.  Rejecting requests until %d.", parseTimeMillisFromId(id)));
                throw new RuntimeException(String.format("clock is moving backwards.  Rejecting requests until %d.", parseTimeMillisFromId(id)));
            }
            id = ((System.currentTimeMillis() - epoch) << timestampLeftShift) | instanceIdValue | sequence.getAndIncrement();
        }
        return lastId = id;
    }


    public static void main(String[] args) throws InterruptedException {
        System.out.println(Long.MAX_VALUE);
        System.out.println((System.currentTimeMillis() - 1321929000000l) << 23);
        System.out.println(nextId());
    }

}
