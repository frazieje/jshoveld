package com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.rabbitmq;

import com.rabbitmq.client.Connection;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.Amqp091Channel;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.Amqp091Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RabbitMqAmqp091Connection implements Amqp091Connection {

    private final Connection sourceConnection;

    private final List<Consumer<String>> shutdownListeners;

    private final Logger logger = LoggerFactory.getLogger(RabbitMqAmqp091Connection.class);

    public RabbitMqAmqp091Connection(Connection rabbitConnection) {
        this.sourceConnection = rabbitConnection;
        this.shutdownListeners = new ArrayList<>();
    }

    @Override
    public Amqp091Channel createChannel() throws IOException {
        return new RabbitMqAmqp091Channel(sourceConnection.createChannel());
    }

    @Override
    public String getName() {
        return sourceConnection.getClientProvidedName();
    }

    @Override
    public void addShutdownListener(Consumer<String> message) {
        sourceConnection.addShutdownListener(cause -> message.accept(cause.getMessage()));
    }

    @Override
    public void close() throws IOException {
        sourceConnection.close();
    }
}
