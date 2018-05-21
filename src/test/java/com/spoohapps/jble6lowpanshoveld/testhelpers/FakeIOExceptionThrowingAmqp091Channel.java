package com.spoohapps.jble6lowpanshoveld.testhelpers;

import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.Amqp091Channel;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.Amqp091Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class FakeIOExceptionThrowingAmqp091Channel implements Amqp091Channel {

    private final List<Consumer<String>> listeners = new ArrayList<>();

    @Override
    public void publish(String exchange, String routingKey, Map<String, Object> headers, byte[] payload) throws IOException {
        throw new IOException();
    }

    @Override
    public void consume(String queue, Consumer<Amqp091Message> messageConsumer, Consumer<String> cancelled) throws IOException {
        throw new IOException();
    }

    @Override
    public void exchangeDeclare(String exchange) throws IOException {
        throw new IOException();
    }

    @Override
    public void queueDeclare(String queue) throws IOException {
        throw new IOException();
    }

    @Override
    public void queueBind(String queue, String exchange, String routingKey) throws IOException {
        throw new IOException();
    }

    @Override
    public void addShutdownListener(Consumer<String> message) {
        listeners.add(message);
    }

    @Override
    public void close() throws IOException, TimeoutException {
        listeners.forEach(l -> l.accept("test"));
    }
}