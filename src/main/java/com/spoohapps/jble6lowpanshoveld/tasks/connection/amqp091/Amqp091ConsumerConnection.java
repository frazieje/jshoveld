package com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091;

import com.spoohapps.jble6lowpanshoveld.model.Message;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConsumerConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class Amqp091ConsumerConnection implements ConsumerConnection {

    private String exchange;
    private String queue;
    private String routingKey;
    private Runnable onClosed;

    private Amqp091ChannelSupplier channelSupplier;
    private Amqp091Channel channel;

    private Consumer<Message> messageConsumer;

    private final Logger logger = LoggerFactory.getLogger(Amqp091ConsumerConnection.class);

    public Amqp091ConsumerConnection(Amqp091ChannelSupplier channelSupplier, String exchange, String queue, String routingKey) {
        this.exchange = exchange;
        this.queue = queue;
        this.routingKey = routingKey;
        this.channelSupplier = channelSupplier;
    }

    @Override
    public void onConsume(Consumer<Message> messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    @Override
    public String getDescription() {
        try {
            return "Consuming from " + channel.getConnectionName();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void open() {
        logger.info("opening consumer connection...");
        try {
            channel = channelSupplier.getChannel();
            channel.addShutdownListener(this::handleShutdown);
            channel.exchangeDeclare(exchange);
            channel.queueDeclare(queue);
            channel.queueBind(queue, exchange, routingKey);
            channel.consume(queue, this::consumeInternal, this::handleShutdown);
            logger.info("consumer connection opened.");
        } catch (IOException | TimeoutException e) {
            logger.error("Error opening consumer connection: {}", e.getMessage());
            e.printStackTrace();
            if (channel != null)
                closeInternal();
            else
                notifyShutdown();
        }
    }

    private void consumeInternal(Amqp091Message message) {
        int hops = 0;
        Map<String, Object> headers = message.getHeaders();
        if (headers.containsKey("x-hops")) {
            try {
                hops = (Integer) headers.get("x-hops");
            } catch (Exception e) {
                try {
                    hops = Integer.parseInt((String)headers.get("x-hops"));
                } catch (Exception se) {}
            }
        }

        Message newMessage = new Message(message.getRoutingKey(), hops, message.getBody());

        if (messageConsumer != null)
            messageConsumer.accept(newMessage);
    }

    @Override
    public void onClosed(Runnable closed) {
        this.onClosed = closed;
    }

    private void handleShutdown(String message) {
        logger.info("consumer connection shutdown: " + message);
        notifyShutdown();
    }

    @Override
    public void close() {
        logger.info("explicitly closing consumer connection");
        closeInternal();
    }

    private void closeInternal() {
        try {
            if (channel != null)
                channel.close();
        } catch (IOException | TimeoutException e) {
            logger.error("Error closing connection: {}", e.getMessage());
            e.printStackTrace();
            notifyShutdown();
        }
    }

    private void notifyShutdown() {
        if (onClosed != null) {
            onClosed.run();
        }
    }
}
