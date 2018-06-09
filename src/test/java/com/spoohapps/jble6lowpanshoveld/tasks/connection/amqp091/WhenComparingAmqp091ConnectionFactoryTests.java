package com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenComparingAmqp091ConnectionFactoryTests {

    Amqp091ConnectionSupplier fakeAlwaysEqualSupplier = new FakeAlwaysEqualAmqp091ConnectionSupplier();
    Amqp091ConnectionSupplier fakeAlwyasNotEqualSupplier = new FakeAlwaysNotEqualAmqp091ConnectionSupplier();

    @Test
    public void shouldEqualIfConnectionSuppliersEqual() {

        Amqp091ConnectionFactory factory = new Amqp091ConnectionFactory(fakeAlwaysEqualSupplier);

        Amqp091ConnectionFactory other = new Amqp091ConnectionFactory(fakeAlwaysEqualSupplier);

        assertEquals(factory, other);
    }

    @Test
    public void shouldEqualIfBothConnectionSuppliersNull() {

        Amqp091ConnectionFactory factory = new Amqp091ConnectionFactory(null);

        Amqp091ConnectionFactory other = new Amqp091ConnectionFactory(null);

        assertEquals(factory, other);
    }

    @Test void shouldNotEqualIfConnectionSuppliersNotEqual() {
        Amqp091ConnectionFactory factory = new Amqp091ConnectionFactory(fakeAlwyasNotEqualSupplier);

        Amqp091ConnectionFactory other = new Amqp091ConnectionFactory(fakeAlwyasNotEqualSupplier);

        assertNotEquals(factory, other);
    }

    @Test void shouldNotEqualIfOneConnectionSupplierNull() {
        Amqp091ConnectionFactory factory = new Amqp091ConnectionFactory(null);

        Amqp091ConnectionFactory other = new Amqp091ConnectionFactory(fakeAlwaysEqualSupplier);

        assertNotEquals(factory, other);
    }

    private class FakeAlwaysEqualAmqp091ConnectionSupplier implements Amqp091ConnectionSupplier {
        @Override
        public Amqp091Connection newConnection() throws IOException, TimeoutException {
            return null;
        }

        @Override
        public boolean equals(Object obj) {
            return true;
        }
    }

    private class FakeAlwaysNotEqualAmqp091ConnectionSupplier implements Amqp091ConnectionSupplier {
        @Override
        public Amqp091Connection newConnection() throws IOException, TimeoutException {
            return null;
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    }

}
