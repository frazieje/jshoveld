package com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public interface Amqp091Channel {
    void publish(String exchange, String routingKey, Map<String, Object> headers, byte[] payload) throws IOException;
    void consume(String queue, Consumer<Amqp091Message> messageConsumer, Consumer<String> cancelled) throws IOException;
    void exchangeDeclare(String exchange) throws IOException;
    void queueDeclare(String queue) throws IOException;
    void queueBind(String queue, String exchange, String routingKey) throws IOException;
    void addShutdownListener(Consumer<String> message);
    void close() throws IOException, TimeoutException;
}
