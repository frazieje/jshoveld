package com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultSaslConfig;
import com.spoohapps.jble6lowpanshoveld.model.TLSContext;
import com.spoohapps.jble6lowpanshoveld.model.TLSContextException;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.Amqp091Connection;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.Amqp091ConnectionSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

public class RabbitMqAmqp091ConnectionSupplier implements Amqp091ConnectionSupplier {

    private ConnectionFactory connectionFactory;

    private final ExecutorService executorService;

    private final String host;

    private final int port;

    private final String username;

    private final String password;

    private final TLSContext tlsContext;

    private final Logger logger = LoggerFactory.getLogger(RabbitMqAmqp091ConnectionSupplier.class);

    public RabbitMqAmqp091ConnectionSupplier(ExecutorService executorService, String host, int port, String username, String password, TLSContext tlsContext) {
        this.executorService = executorService;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.tlsContext = tlsContext;
    }

    public RabbitMqAmqp091ConnectionSupplier(ExecutorService executorService, String host, int port, TLSContext tlsContext) {
        this.executorService = executorService;
        this.host = host;
        this.port = port;
        this.username = null;
        this.password = null;
        this.tlsContext = tlsContext;
    }

    private ConnectionFactory getConnectionFactory() {

        if (connectionFactory == null) {

            connectionFactory = new ConnectionFactory();

            if (username != null)
                connectionFactory.setUsername(username);

            if (password != null)
                connectionFactory.setPassword(password);

            connectionFactory.setHost(host);
            connectionFactory.setPort(port);

            try {
                connectionFactory.useSslProtocol(tlsContext.toSSLContext());
            } catch (TLSContextException e) {
                logger.error("Error initializing TLS Context", e);
            }
        }

        return connectionFactory;
    }

    @Override
    public Amqp091Connection newConnection() throws IOException, TimeoutException {
        String name = "[" + host + ":" + port + "]";
        return new RabbitMqAmqp091Connection(getConnectionFactory().newConnection(executorService, name));
    }

    @Override
    public int hashCode() {
        return Objects.hash(executorService, host, port, username, password, tlsContext);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null)
            return false;

        if (!RabbitMqAmqp091ConnectionSupplier.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final RabbitMqAmqp091ConnectionSupplier other = (RabbitMqAmqp091ConnectionSupplier) obj;

        if (executorService != other.executorService)
            return false;

        if (host == null ? other.host != null : !host.equals(other.host))
            return false;

        if (port != other.port)
            return false;

        if (username == null ? other.username != null : !username.equals(other.username))
            return false;

        if (password == null ? other.password != null : !password.equals(other.password))
            return false;

        if (tlsContext == null ? other.tlsContext != null : !tlsContext.equals(other.tlsContext))
            return false;

        return true;
    }
}
