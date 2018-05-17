package com.spoohapps.jble6lowpanshoveld.tasks.connection;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ShutdownSignalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Amqp091MessageConnectionFactory implements MessageConnectionFactory {

    ConnectionFactory connectionFactory;
    ExecutorService executorService;

    Connection activeConnection;
    AtomicBoolean isOpen = new AtomicBoolean(false);

    private final Logger logger = LoggerFactory.getLogger(Amqp091MessageConnectionFactory.class);

    public Amqp091MessageConnectionFactory(ExecutorService executorService, String host, int port, String username, String password) {

        connectionFactory = new ConnectionFactory();
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);

        this.executorService = executorService;
    }

    @Override
    public MessageConsumerConnection newConsumerConnection(MessageConnectionSettings settings) {
        return new Amqp091MessageConsumerConnection(this::getOrCreateConnection, settings.get("exchange"), settings.get("queue"), settings.get("routingKey"));
    }

    @Override
    public MessagePublisherConnection newPublisherConnection(MessageConnectionSettings settings) {
        return new Amqp091MessagePublisherConnection(this::getOrCreateConnection, settings.get("exchange"));
    }

    private Connection getOrCreateConnection() {
        logger.info("getting connection");
        if (activeConnection != null && isOpen.get()) {
            logger.info("returning active connection");
            return activeConnection;
        }
        try {
            logger.info("creating connection");
            activeConnection = connectionFactory.newConnection(executorService);
            isOpen.set(true);
            activeConnection.addShutdownListener(this::shutdownCompleted);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
            isOpen.set(false);
        }
        return null;
    }

    private void shutdownCompleted(ShutdownSignalException cause) {
        isOpen.set(false);
    }
}
