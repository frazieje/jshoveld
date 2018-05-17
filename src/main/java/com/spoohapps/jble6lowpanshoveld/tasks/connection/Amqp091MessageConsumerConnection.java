package com.spoohapps.jble6lowpanshoveld.tasks.connection;

import com.rabbitmq.client.*;

import com.spoohapps.jble6lowpanshoveld.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Amqp091MessageConsumerConnection implements MessageConsumerConnection {

    private String exchange;
    private String queue;
    private String routingKey;
    private Supplier<Connection> connection;
    private Channel channel;

    private Consumer<Message> messageConsumer;

    private AtomicBoolean isOpen = new AtomicBoolean(false);

    private final Logger logger = LoggerFactory.getLogger(Amqp091MessageConsumerConnection.class);

    public Amqp091MessageConsumerConnection(Supplier<Connection> connection, String exchange, String queue, String routingKey) {
        this.exchange = exchange;
        this.queue = queue;
        this.routingKey = routingKey;
        this.connection = connection;
    }

    @Override
    public void consume(Consumer<Message> messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    @Override
    public boolean isOpen() {
        return isOpen.get();
    }

    @Override
    public void open() {
        try {
            logger.info("opening consumer connection...");
            channel = connection.get().createChannel();
            channel.exchangeDeclare(exchange, BuiltinExchangeType.TOPIC, true);
            channel.queueDeclare(queue, true, false, false, null);
            channel.queueBind(queue, exchange, routingKey);

            channel.basicConsume(queue, false, this::consumeInternal, this::handleShutdownSignal);

            isOpen.set(true);
            logger.info("consumer connection opened.");
        } catch (IOException e) {
            //log exception
            e.printStackTrace();
            isOpen.set(false);
        }
    }

    @Override
    public void close() {
        try {
            channel.close();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void consumeInternal(String consumerTag, Delivery message) {
        try {
            int hops = 0;
            Map<String, Object> headers = message.getProperties().getHeaders();
            if (headers.containsKey("x-hops")) {
                hops = Integer.parseInt((String) headers.get("x-hops"));
            }

            Message newMessage = new Message(message.getEnvelope().getRoutingKey(), hops, message.getBody());

            if (messageConsumer != null) {
                messageConsumer.accept(newMessage);
            }

            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
        logger.error("connection shutdown: " + sig.getMessage());
        isOpen.set(false);
    }
}
