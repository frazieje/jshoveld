package com.spoohapps.jble6lowpanshoveld.integrationtests.connection.amqp091;

import com.spoohapps.jble6lowpanshoveld.model.Message;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConnectionFactory;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConnectionSettings;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConsumerConnection;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.PublisherConnection;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.*;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.rabbitmq.RabbitMqAmqp091Connection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenConnectingToRealBrokerWIthKeystoresTests {
    private ConnectionFactory factory;

    private PublisherConnection publisherConnection;

    private ConsumerConnection consumerConnection;

    private ExecutorService testExecutor;

    private final String testTopic = "test.topic";

    @BeforeAll
    public void setup() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        testExecutor = Executors.newFixedThreadPool(3);

        char[] keyPassphrase = "YepP48fF".toCharArray();
        KeyStore ks = KeyStore.getInstance("PKCS12");

        Path pkcs12Path = Paths.get(System.getProperty("user.home"), "jble6lowpanshoveld", "client", "spoohappsmqclient.p12");
        Path trustStorePath = Paths.get(System.getProperty("user.home"), "jble6lowpanshoveld", "client", "spoohappsmqca.jks");

        ks.load(Files.newInputStream(pkcs12Path), keyPassphrase);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, keyPassphrase);

        char[] trustPassphrase = "YepP48fF".toCharArray();
        KeyStore tks = KeyStore.getInstance("JKS");
        tks.load(Files.newInputStream(trustStorePath), trustPassphrase);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(tks);

        SSLContext c = SSLContext.getInstance("TLSv1.2");
        c.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        factory = new Amqp091ConnectionFactory(
            () -> {
                com.rabbitmq.client.ConnectionFactory connectionFactory = new com.rabbitmq.client.ConnectionFactory();
                connectionFactory.setUsername("jble6lowpanshoveld");
                connectionFactory.setPassword("jble6lowpanshoveld");
                connectionFactory.setHost("www.spoohapps.com");
                connectionFactory.setPort(5671);
                connectionFactory.useSslProtocol(c);
                return new RabbitMqAmqp091Connection(connectionFactory.newConnection(testExecutor));
            }
        );

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