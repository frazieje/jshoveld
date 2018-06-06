package com.spoohapps.jble6lowpanshoveld.integrationtests.connection.amqp091;

import com.spoohapps.jble6lowpanshoveld.model.Message;
import com.spoohapps.jble6lowpanshoveld.model.Profile;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConnectionFactory;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConnectionSettings;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConsumerConnection;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.PublisherConnection;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.Amqp091ConnectionFactory;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.Amqp091ConsumerConnectionSettings;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.Amqp091PublisherConnectionSettings;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.rabbitmq.RabbitMqAmqp091ConnectionSupplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenConnectingToRealBrokerWithProfileTests {
    private ConnectionFactory factory;

    private PublisherConnection publisherConnection;

    private ConsumerConnection consumerConnection;

    private ExecutorService testExecutor;

    private final String testTopic = "test.topic";

    @BeforeAll
    public void setup() throws IOException {
        testExecutor = Executors.newFixedThreadPool(3);

        Path profilePath = Paths.get(System.getProperty("user.home"), "jble6lowpanshoveld", "client", "profile.conf");

        Profile profile = Profile.from(Files.newInputStream(profilePath));

        factory = new Amqp091ConnectionFactory(
                new RabbitMqAmqp091ConnectionSupplier(
                        testExecutor,
                        "www.spoohapps.com",
                        5671,
                        "jble6lowpanshoveld",
                        "jble6lowpanshoveld",
                        profile.getApiContext()
                ));

        ConnectionSettings publisherSettings = new Amqp091PublisherConnectionSettings("TestExchange");
        ConnectionSettings consumerSettings = new Amqp091ConsumerConnectionSettings("TestExchange", "TestQueue", testTopic);
        publisherConnection = factory.newPublisherConnection(publisherSettings);
        consumerConnection = factory.newConsumerConnection(consumerSettings);
        consumerConnection.onConsume(message -> receivedMessage = message);
        publisherConnection.onClosed(() -> publisherClosed = true);
        consumerConnection.onClosed(() -> consumerClosed = true);
        consumerConnection.open();
        publisherConnection.open();
        sleep(3000);
    }

    @AfterAll
    public void teardown() {
        publisherConnection.close();
        consumerConnection.close();
        testExecutor.shutdown();
    }

    private boolean publisherClosed = false;
    private boolean consumerClosed = false;
    private Message receivedMessage;

    @Test
    public void consumerShouldConnectToServer() {
        assertFalse(consumerClosed);
    }


    @Test
    public void publisherShouldConnectToServer() {
        assertFalse(publisherClosed);
    }

    @Test
    public void shouldConsumeMessage() {
        publisherConnection.publish(new Message(testTopic, new byte[] { 1, 4 }));
        sleep(1500);
        assertNotNull(receivedMessage);
    }

    @Test
    public void shouldConsumeMessageWithSameTopic() {
        publisherConnection.publish(new Message(testTopic, new byte[] { 1, 2 }));
        sleep(1500);
        assertEquals(testTopic, receivedMessage.getTopic());
    }

    @Test
    public void shouldConsumeMessageWithSamePayload() {
        byte[] expectedPayload = new byte[] { 1, 3 };
        publisherConnection.publish(new Message(testTopic, new byte[] { 1, 3 }));
        sleep(1500);
        assertArrayEquals(expectedPayload, receivedMessage.getPayload());
    }

    @Test
    public void shouldConsumeMessageWithSameHops() {
        int expectedHops = 1;
        publisherConnection.publish(new Message(testTopic, expectedHops, new byte[] { 1, 3 }));
        sleep(1500);
        assertEquals(expectedHops, receivedMessage.getHops());
    }

    private void sleep(int timeMs) {
        try {
            Thread.sleep(timeMs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
