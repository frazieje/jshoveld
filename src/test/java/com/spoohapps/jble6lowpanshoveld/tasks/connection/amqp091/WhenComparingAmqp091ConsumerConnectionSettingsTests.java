package com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class WhenComparingAmqp091ConsumerConnectionSettingsTests {

    private String expectedExchange = "exchange";
    private String expectedQueue = "queue";
    private String expectedRoutingKey = "routingKey";

    @Test
    public void shouldEqual() {
        Amqp091ConsumerConnectionSettings settings = new Amqp091ConsumerConnectionSettings(
                expectedExchange,
                expectedQueue,
                expectedRoutingKey);

        Amqp091ConsumerConnectionSettings otherSettings = new Amqp091ConsumerConnectionSettings(
                expectedExchange,
                expectedQueue,
                expectedRoutingKey);

        assertEquals(settings, otherSettings);
    }

    @Test
    public void shouldNotEqualIfDifferentExchange() {
        Amqp091ConsumerConnectionSettings settings = new Amqp091ConsumerConnectionSettings(
                expectedExchange,
                expectedQueue,
                expectedRoutingKey);

        Amqp091ConsumerConnectionSettings otherSettings = new Amqp091ConsumerConnectionSettings(
                "otherExchange",
                expectedQueue,
                expectedRoutingKey);

        assertNotEquals(settings, otherSettings);
    }

    @Test
    public void shouldNotEqualIfDifferentQueue() {
        Amqp091ConsumerConnectionSettings settings = new Amqp091ConsumerConnectionSettings(
                expectedExchange,
                expectedQueue,
                expectedRoutingKey);

        Amqp091ConsumerConnectionSettings otherSettings = new Amqp091ConsumerConnectionSettings(
                expectedExchange,
                "otherQueue",
                expectedRoutingKey);

        assertNotEquals(settings, otherSettings);
    }

    @Test
    public void shouldNotEqualIfDifferentRoutingKey() {
        Amqp091ConsumerConnectionSettings settings = new Amqp091ConsumerConnectionSettings(
                expectedExchange,
                expectedQueue,
                expectedRoutingKey);

        Amqp091ConsumerConnectionSettings otherSettings = new Amqp091ConsumerConnectionSettings(
                expectedExchange,
                expectedQueue,
                "otherKey");

        assertNotEquals(settings, otherSettings);
    }

    @Test
    public void shouldNotEqualNull() {
        Amqp091ConsumerConnectionSettings settings = new Amqp091ConsumerConnectionSettings(
                expectedExchange,
                expectedQueue,
                expectedRoutingKey);

        assertNotEquals(settings, null);
    }

    @Test
    public void shouldNotEqual() {
        Amqp091ConsumerConnectionSettings settings = new Amqp091ConsumerConnectionSettings(
                expectedExchange,
                expectedQueue,
                expectedRoutingKey);

        Amqp091ConsumerConnectionSettings otherSettings = new Amqp091ConsumerConnectionSettings(
                "otherExchange",
                "otherQueue",
                "otherKey");

        assertNotEquals(settings, otherSettings);
    }

}
