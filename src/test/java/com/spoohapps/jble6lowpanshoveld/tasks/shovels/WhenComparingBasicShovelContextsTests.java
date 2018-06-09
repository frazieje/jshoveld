package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.jble6lowpanshoveld.model.Message;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConnectionSettings;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConsumerConnection;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.PublisherConnection;
import com.spoohapps.jble6lowpanshoveld.testhelpers.FakeConnectionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenComparingBasicShovelContextsTests {

    ShovelContext otherCtx = BasicShovelContext.from(
            new FakeAlwaysEqualConnectionFactory(),
            new FakeAlwaysEqualConnectionSettings(),
            new FakeAlwaysEqualConnectionFactory(),
            new FakeAlwaysEqualConnectionSettings(),
            RemoteMessageRetrievalShovel.class);

    @Test
    public void shouldEqual() {
        ShovelContext ctx = BasicShovelContext.from(
                new FakeAlwaysEqualConnectionFactory(),
                new FakeAlwaysEqualConnectionSettings(),
                new FakeAlwaysEqualConnectionFactory(),
                new FakeAlwaysEqualConnectionSettings(),
                RemoteMessageRetrievalShovel.class);

        assertEquals(ctx, otherCtx);
    }

    @Test
    public void shouldNotEqualIfSourceFactoryNotEqual() {
        ShovelContext ctx = BasicShovelContext.from(
                new FakeAlwaysNotEqualConnectionFactory(),
                new FakeAlwaysEqualConnectionSettings(),
                new FakeAlwaysEqualConnectionFactory(),
                new FakeAlwaysEqualConnectionSettings(),
                RemoteMessageRetrievalShovel.class);

        assertNotEquals(ctx, otherCtx);
    }

    @Test
    public void shouldNotEqualIfSourceSettingsNotEqual() {
        ShovelContext ctx = BasicShovelContext.from(
                new FakeAlwaysEqualConnectionFactory(),
                new FakeAlwaysNotEqualConnectionSettings(),
                new FakeAlwaysEqualConnectionFactory(),
                new FakeAlwaysEqualConnectionSettings(),
                RemoteMessageRetrievalShovel.class);

        assertNotEquals(ctx, otherCtx);
    }

    @Test
    public void shouldNotEqualIfDestinationFactoryNotEqual() {
        ShovelContext ctx = BasicShovelContext.from(
                new FakeAlwaysEqualConnectionFactory(),
                new FakeAlwaysEqualConnectionSettings(),
                new FakeAlwaysNotEqualConnectionFactory(),
                new FakeAlwaysEqualConnectionSettings(),
                RemoteMessageRetrievalShovel.class);

        assertNotEquals(ctx, otherCtx);
    }

    @Test
    public void shouldNotEqualIfDestinationSettingsNotEqual() {
        ShovelContext ctx = BasicShovelContext.from(
                new FakeAlwaysEqualConnectionFactory(),
                new FakeAlwaysEqualConnectionSettings(),
                new FakeAlwaysEqualConnectionFactory(),
                new FakeAlwaysNotEqualConnectionSettings(),
                RemoteMessageRetrievalShovel.class);

        assertNotEquals(ctx, otherCtx);
    }

    @Test
    public void shouldNotEqualIfShovelImplIsNotEqual() {

    }

    private class FakeShovel extends AbstractMessageShovel<FakeShovel> {

        public FakeShovel(ConsumerConnection consumer, PublisherConnection publisher) {
            super(consumer, publisher);
        }

        @Override
        protected void handleMessage(Message message, PublisherConnection publisher) {

        }
    }

    private class FakeAlwaysEqualConnectionFactory extends FakeConnectionFactory {
        @Override
        public boolean equals(Object obj) {
            return true;
        }
    }

    private class FakeAlwaysNotEqualConnectionFactory extends FakeConnectionFactory {
        @Override
        public boolean equals(Object obj) {
            return false;
        }
    }

    private class FakeAlwaysEqualConnectionSettings implements ConnectionSettings {
        @Override
        public boolean equals(Object obj) {
            return true;
        }

        @Override
        public String get(String key) {
            return null;
        }
    }

    private class FakeAlwaysNotEqualConnectionSettings implements ConnectionSettings {
        @Override
        public boolean equals(Object obj) {
            return false;
        }

        @Override
        public String get(String key) {
            return null;
        }
    }
}
