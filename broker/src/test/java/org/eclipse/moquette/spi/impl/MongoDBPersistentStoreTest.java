package org.eclipse.moquette.spi.impl;

import org.eclipse.moquette.proto.messages.AbstractMessage;
import org.eclipse.moquette.server.ConfigurationParser;
import org.eclipse.moquette.spi.impl.subscriptions.Subscription;
import org.eclipse.moquette.spi.persistence.MongoDBPersistentStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by yycoder on 2015/1/12.
 */
public class MongoDBPersistentStoreTest {
    MongoDBPersistentStore m_storageService;

    @Before
    public void setUp() throws Exception {
        ConfigurationParser confParser = new ConfigurationParser();
        m_storageService = new MongoDBPersistentStore(confParser.getProperties());
        m_storageService.initStore();
    }

    @After
    public void tearDown() {
        if (m_storageService != null) {
            m_storageService.close();
        }
    }

    //@Test
    public void overridingSubscriptions() {
        Subscription oldSubscription = new Subscription("FAKE_CLI_ID_1", "/topic", AbstractMessage.QOSType.MOST_ONE, false);
        m_storageService.addNewSubscription(oldSubscription, oldSubscription.getClientId());
        Subscription overrindingSubscription = new Subscription("FAKE_CLI_ID_1", "/topic", AbstractMessage.QOSType.EXACTLY_ONCE, false);
        m_storageService.addNewSubscription(overrindingSubscription, overrindingSubscription.getClientId());

        //Verify
        List<Subscription> subscriptions = m_storageService.listAllSubscriptions();
        assertEquals(1, subscriptions.size());
        Subscription sub = subscriptions.get(0);
        assertEquals(overrindingSubscription.getRequestedQos(), sub.getRequestedQos());
    }
 }
