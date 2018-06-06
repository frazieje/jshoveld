package com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class WhenComparingAmqp091PublisherConnectionSettingsTests {

    private String expectedExchange = "exchange";

    @Test
    public void shouldEqual() {
        Amqp091PublisherConnectionSettings settings = new Amqp091PublisherConnectionSettings(
                expectedExchange);

        Amqp091PublisherConnectionSettings otherSettings = new Amqp091PublisherConnectionSettings(
                expectedExchange);

        assertEquals(settings, otherSettings);
    }

    @Test
    public void shouldNotEqualIfDifferentExchange() {
        Amqp091PublisherConnectionSettings settings = new Amqp091PublisherConnectionSettings(
                expectedExchange);

        Amqp091PublisherConnectionSettings otherSettings = new Amqp091PublisherConnectionSettings(
                "otherExchange");

        assertNotEquals(settings, otherSettings);
    }

    @Test
    public void shouldNotEqualNull() {
        Amqp091PublisherConnectionSettings settings = new Amqp091PublisherConnectionSettings(
                expectedExchange);

        assertNotEquals(settings, null);
    }
}
