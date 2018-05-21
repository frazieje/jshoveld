package com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.Amqp091Connection;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.Amqp091ConnectionSupplier;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

public class RabbitMqAmqp091ConnectionSupplier implements Amqp091ConnectionSupplier {

    private final ConnectionFactory connectionFactory;

    private final ExecutorService executorService;

    public RabbitMqAmqp091ConnectionSupplier(ExecutorService executorService, String host, int port, String username, String password) {
        this.executorService = executorService;
        connectionFactory = new ConnectionFactory();
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
    }

    @Override
    public Amqp091Connection newConnection() throws IOException, TimeoutException {
        String name = "[" + connectionFactory.getHost() + ":" + connectionFactory.getPort() + "]";
        return new RabbitMqAmqp091Connection(connectionFactory.newConnection(executorService, name));
    }
}
