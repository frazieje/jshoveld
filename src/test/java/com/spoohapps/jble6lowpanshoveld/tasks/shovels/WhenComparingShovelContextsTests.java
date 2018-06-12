package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConnectionSettings;
import com.spoohapps.jble6lowpanshoveld.testhelpers.FakeConnectionFactory;
import com.spoohapps.jble6lowpanshoveld.testhelpers.TestConnectionSettings;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class WhenComparingShovelContextsTests {

    ShovelContext otherCtx = new ShovelContext(
                new FakeConnectionFactory(),
                new TestConnectionSettings(),
                new FakeConnectionFactory(),
                new TestConnectionSettings());

    @Test
    public void shouldEqual() {
        ShovelContext ctx = new ShovelContext(
                new FakeConnectionFactory(),
                new TestConnectionSettings(),
                new FakeConnectionFactory(),
                new TestConnectionSettings());

        assertEquals(ctx, otherCtx);
    }

    @Test
    public void shouldNotEqualIfSourceFactoryNotEqual() {
        ShovelContext ctx = new ShovelContext(
                    new FakeAlwaysNotEqualConnectionFactory(),
                    new TestConnectionSettings(),
                    new FakeConnectionFactory(),
                    new TestConnectionSettings());

        assertNotEquals(ctx, otherCtx);
    }

    @Test
    public void shouldNotEqualIfSourceSettingsNotEqual() {
        ShovelContext ctx = new ShovelContext(
                    new FakeConnectionFactory(),
                    new FakeAlwaysNotEqualConnectionSettings(),
                    new FakeConnectionFactory(),
                    new TestConnectionSettings());

        assertNotEquals(ctx, otherCtx);
    }

    @Test
    public void shouldNotEqualIfDestinationFactoryNotEqual() {
        ShovelContext ctx = new ShovelContext(
                    new FakeConnectionFactory(),
                    new TestConnectionSettings(),
                    new FakeAlwaysNotEqualConnectionFactory(),
                    new TestConnectionSettings());

        assertNotEquals(ctx, otherCtx);
    }

    @Test
    public void shouldNotEqualIfDestinationSettingsNotEqual() {
        ShovelContext ctx = new ShovelContext(
                    new FakeConnectionFactory(),
                    new TestConnectionSettings(),
                    new FakeConnectionFactory(),
                    new FakeAlwaysNotEqualConnectionSettings());

        assertNotEquals(ctx, otherCtx);
    }

    private class FakeAlwaysNotEqualConnectionFactory extends FakeConnectionFactory {
        @Override
        public boolean equals(Object obj) {
            return false;
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
