package com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.rabbitmq;

import com.spoohapps.jble6lowpanshoveld.model.TLSContext;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.Amqp091ConnectionSupplier;
import com.spoohapps.jble6lowpanshoveld.testhelpers.ProfileFileHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenHashingRabbitMqAmqp091ConnectionSuppliersTests {

    private ExecutorService executor;
    private TLSContext tlsContext;
    private String host;
    private int port;
    private String username;
    private String password;

    @BeforeAll
    public void setup() throws CertificateException, InvalidKeySpecException, NoSuchAlgorithmException {
        executor = Executors.newSingleThreadExecutor();
        host = "www.spoohapps.com";
        port = 5671;
        username = "some user";
        password = "some password";
        tlsContext = new TLSContext(
                ProfileFileHelper.nodeCertificate(),
                ProfileFileHelper.nodePrivateKey(),
                ProfileFileHelper.nodeCaCertificate());
    }


    @Test
    public void shouldHaveSameHashCodeWhenEqual() {
        Amqp091ConnectionSupplier supplier = new RabbitMqAmqp091ConnectionSupplier(
                executor,
                host,
                port,
                username,
                password,
                tlsContext);

        Amqp091ConnectionSupplier otherSupplier = new RabbitMqAmqp091ConnectionSupplier(
                executor,
                host,
                port,
                username,
                password,
                tlsContext);

        assertEquals(supplier.hashCode(), otherSupplier.hashCode());
    }

    @Test
    public void shouldNotHaveSameHashCodesIfDDifferentExecutors() {
        Amqp091ConnectionSupplier supplier = new RabbitMqAmqp091ConnectionSupplier(
                executor,
                host,
                port,
                username,
                password,
                tlsContext);

        Amqp091ConnectionSupplier otherSupplier = new RabbitMqAmqp091ConnectionSupplier(
                Executors.newSingleThreadExecutor(),
                host,
                port,
                username,
                password,
                tlsContext);

        assertNotEquals(supplier.hashCode(), otherSupplier.hashCode());
    }

    @Test
    public void shouldNotHaveSameHashCodeIfDifferentTLSContexts() throws CertificateException, InvalidKeySpecException, NoSuchAlgorithmException {
        Amqp091ConnectionSupplier supplier = new RabbitMqAmqp091ConnectionSupplier(
                executor,
                host,
                port,
                username,
                password,
                tlsContext);

        Amqp091ConnectionSupplier otherSupplier = new RabbitMqAmqp091ConnectionSupplier(
                executor,
                host,
                port,
                username,
                password,
                new TLSContext(
                        ProfileFileHelper.nodeCertificate(),
                        ProfileFileHelper.nodePrivateKey(),
                        ProfileFileHelper.nodeCertificate()));

        assertNotEquals(supplier.hashCode(), otherSupplier.hashCode());
    }

}
