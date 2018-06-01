package com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.rabbitmq;

import com.rabbitmq.client.*;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.Amqp091Channel;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.Amqp091Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class RabbitMqAmqp091Channel implements Amqp091Channel {

    private final Channel sourceChannel;

    private final List<Consumer<String>> shutdownListeners;

    private final Logger logger = LoggerFactory.getLogger(RabbitMqAmqp091Channel.class);

    public RabbitMqAmqp091Channel(Channel rabbitChannel) {
        this.sourceChannel = rabbitChannel;
        this.shutdownListeners = new ArrayList<>();
    }

    @Override
    public void publish(String exchange, String routingKey, Map<String, Object> headers, byte[] payload) throws IOException {
        sourceChannel.basicPublish(
                exchange,
                routingKey,
                new AMQP.BasicProperties.Builder()
                        .headers(headers)
                        .build(),
                payload);
    }

    @Override
    public void consume(String queue, Consumer<Amqp091Message> messageConsumer, Consumer<String> cancelled) throws IOException {
        sourceChannel.basicConsume(queue, (consumerTag, message) -> {
            try {
                if (messageConsumer != null) {
                    messageConsumer.accept(new RabbitMqAmqp091Message(message));
                }
            } finally {
                sourceChannel.basicAck(message.getEnvelope().getDeliveryTag(), false);
            }
        }, (consumerTag, sig) -> {
            cancelled.accept(sig.getMessage());
        });
    }

    @Override
    public void exchangeDeclare(String exchange) throws IOException {
        sourceChannel.exchangeDeclare(exchange, BuiltinExchangeType.TOPIC, true);
    }

    @Override
    public void queueDeclare(String queue) throws IOException {
        sourceChannel.queueDeclare(queue, true, false, false, null);
    }

    @Override
    public void queueBind(String queue, String exchange, String routingKey) throws IOException {
        sourceChannel.queueBind(queue, exchange, routingKey);
    }

    @Override
    public void addShutdownListener(Consumer<String> message) {
        sourceChannel.addShutdownListener(cause -> message.accept(cause.getMessage()));
    }

    @Override
    public void close() throws IOException, TimeoutException {
        sourceChannel.close();
    }

    @Override
    public String getConnectionName() {
        return sourceChannel.getConnection().getClientProvidedName();
    }
}
