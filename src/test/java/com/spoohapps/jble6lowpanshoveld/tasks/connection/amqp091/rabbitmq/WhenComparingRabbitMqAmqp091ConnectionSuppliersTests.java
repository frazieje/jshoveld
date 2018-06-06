package com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.rabbitmq;

import com.spoohapps.jble6lowpanshoveld.model.Profile;
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

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenComparingRabbitMqAmqp091ConnectionSuppliersTests {

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
    public void shouldEqual() {
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

        assertEquals(supplier, otherSupplier);
    }

    @Test
    public void shouldNotEqualIfDifferentExecutors() {
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

        assertNotEquals(supplier, otherSupplier);
    }

    @Test
    public void shouldNotEqualIfDifferentHosts() {
        Amqp091ConnectionSupplier supplier = new RabbitMqAmqp091ConnectionSupplier(
                executor,
                host,
                port,
                username,
                password,
                tlsContext);

        Amqp091ConnectionSupplier otherSupplier = new RabbitMqAmqp091ConnectionSupplier(
                executor,
                "otherhost.com",
                port,
                username,
                password,
                tlsContext);

        assertNotEquals(supplier, otherSupplier);
    }

    @Test
    public void shouldNotEqualIfDifferentPorts() {
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
                1234,
                username,
                password,
                tlsContext);

        assertNotEquals(supplier, otherSupplier);
    }

    @Test
    public void shouldNotEqualIfDifferentUsernames() {
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
                "otheruser",
                password,
                tlsContext);

        assertNotEquals(supplier, otherSupplier);
    }

    @Test
    public void shouldNotEqualIfDifferentPasswords() {
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
                "otherpassword",
                tlsContext);

        assertNotEquals(supplier, otherSupplier);
    }

    @Test
    public void shouldNotEqualIfDifferentTLSContexts() throws CertificateException, InvalidKeySpecException, NoSuchAlgorithmException {
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

        assertNotEquals(supplier, otherSupplier);
    }

    @Test
    public void shouldNotEqualNull() throws CertificateException, InvalidKeySpecException, NoSuchAlgorithmException {
        Amqp091ConnectionSupplier supplier = new RabbitMqAmqp091ConnectionSupplier(
                executor,
                host,
                port,
                username,
                password,
                tlsContext);

        assertNotEquals(supplier, null);
    }


    @Test
    public void shouldNotEqual() throws CertificateException, InvalidKeySpecException, NoSuchAlgorithmException {
        Amqp091ConnectionSupplier supplier = new RabbitMqAmqp091ConnectionSupplier(
                executor,
                host,
                port,
                username,
                password,
                tlsContext);

        Amqp091ConnectionSupplier otherSupplier = new RabbitMqAmqp091ConnectionSupplier(
                Executors.newSingleThreadExecutor(),
                "otherhost.com",
                1234,
                "otheruser",
                "otherpassword",
                new TLSContext(
                        ProfileFileHelper.nodeCertificate(),
                        ProfileFileHelper.nodePrivateKey(),
                        ProfileFileHelper.nodeCertificate()));

        assertNotEquals(supplier, otherSupplier);
    }

}
