package com.spoohapps.jble6lowpanshoveld.integrationtests.connection.amqp091;

import com.spoohapps.jble6lowpanshoveld.model.Message;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenConnectingToRealBrokerWithTLS {
    private ConnectionFactory factory;

    private PublisherConnection publisherConnection;

    private ConsumerConnection consumerConnection;

    private ExecutorService testExecutor;

    private final String testTopic = "test.topic";

    @BeforeAll
    public void setup() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        testExecutor = Executors.newFixedThreadPool(3);

        char[] keyPassphrase = "SomePassPhrase".toCharArray();
        KeyStore ks = KeyStore.getInstance("PKCS12");

        Path pkcs12Path = Paths.get(System.getProperty("user.home"), "client", "keycert.p12");
        Path trustStorePath = Paths.get(System.getProperty("user.home"), "client", "spoohappsca.jks");

        ks.load(Files.newInputStream(pkcs12Path), keyPassphrase);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, keyPassphrase);

        char[] trustPassphrase = "SomePassPhrase".toCharArray();
        KeyStore tks = KeyStore.getInstance("JKS");
        tks.load(Files.newInputStream(trustStorePath), trustPassphrase);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(tks);

        SSLContext c = SSLContext.getInstance("TLSv1.2");
        c.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        factory = new Amqp091ConnectionFactory(
                new RabbitMqAmqp091ConnectionSupplier(
                        testExecutor,
                        "www.spoohapps.com",
                        5671,
                        "somepassphrase",
                        "somepassphrase",
                        c
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
        publisherConnection.publish(new Message(testTopic, new byte[] { 1, 2 }));
        sleep(3000);
        assertNotNull(receivedMessage);
    }

    private void sleep(int timeMs) {
        try {
            Thread.sleep(timeMs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
