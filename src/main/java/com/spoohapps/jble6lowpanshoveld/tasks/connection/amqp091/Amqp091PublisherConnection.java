package com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091;

import com.spoohapps.jble6lowpanshoveld.model.Message;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.PublisherConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Amqp091PublisherConnection implements PublisherConnection {

    private String exchange;
    private Amqp091ChannelSupplier channelSupplier;
    private Amqp091Channel channel;

    private Runnable onClosed;

    private final Logger logger = LoggerFactory.getLogger(Amqp091PublisherConnection.class);

    public Amqp091PublisherConnection(Amqp091ChannelSupplier channelSupplier, String exchange) {
        this.exchange = exchange;
        this.channelSupplier = channelSupplier;
    }

    @Override
    public void publish(Message message) {
        logger.info("Publishing message to {} exchange, routed for '{}', payload size {} bytes", exchange, message.getTopic(), message.getPayload().length);

        Map<String, Object> headers = new HashMap<>();

        headers.put("x-hops", message.getHops());

        if (message.isFromDevice()) {
            headers.put("x-device", 1);
        }

        try {
            channel.publish(exchange,
                    message.getTopic(),
                    headers,
                    message.getPayload());
        } catch (IOException e) {
            logger.error("Error publishing message: {}", e.getMessage());
            e.printStackTrace();
            closeInternal();
        }
    }

    @Override
    public String getDescription() {
        try {
            return "Publishing to " + channel.getConnectionName();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void open() {
        logger.info("opening publisher connection...");
        try {
            channel = channelSupplier.getChannel();
            channel.addShutdownListener(this::handleShutdown);
            channel.exchangeDeclare(exchange);
            logger.info("publisher connection opened");
        } catch (IOException | TimeoutException e) {
            logger.error("Error opening publisher channel: {}", e.getMessage());
            e.printStackTrace();
            if (channel != null) {
                closeInternal();
            } else {
                notifyShutdown();
            }
        }
    }

    @Override
    public void onClosed(Runnable closed) {
        this.onClosed = closed;
    }

    private void handleShutdown(String message) {
        logger.info("publisher connection shutdown: " + message);
        notifyShutdown();
    }

    @Override
    public void close() {
        logger.info("explicitly closing publisher connection");
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
