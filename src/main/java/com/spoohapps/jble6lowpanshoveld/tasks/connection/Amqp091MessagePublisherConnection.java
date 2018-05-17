package com.spoohapps.jble6lowpanshoveld.tasks.connection;

import com.rabbitmq.client.*;
import com.spoohapps.jble6lowpanshoveld.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class Amqp091MessagePublisherConnection implements MessagePublisherConnection {

    private String exchange;
    private Supplier<Connection> connection;
    private Channel channel;

    private AtomicBoolean isOpen = new AtomicBoolean(false);

    private final Logger logger = LoggerFactory.getLogger(Amqp091MessagePublisherConnection.class);

    public Amqp091MessagePublisherConnection(Supplier<Connection> connection, String exchange) {
        this.exchange = exchange;
        this.connection = connection;
    }

    @Override
    public void publish(Message message) {

        Map<String, Object> headers = new HashMap<>();

        headers.put("x-hops", message.getHops());

        try {
            channel.basicPublish(exchange,
                    message.getTopic(),
                    new AMQP.BasicProperties.Builder()
                            .headers(headers)
                            .build(),
                    message.getPayload());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isOpen() {
        return isOpen.get();
    }

    @Override
    public void open() {
        try {
            logger.info("opening publisher connection...");
            channel = connection.get().createChannel();
            channel.exchangeDeclare(exchange, BuiltinExchangeType.TOPIC, true);
            channel.addShutdownListener(this::handleShutdown);
            isOpen.set(true);
            logger.info("publisher connection opened");
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            isOpen.set(false);
        }
    }

    private void handleShutdown(ShutdownSignalException cause) {
        isOpen.set(false);
    }

    @Override
    public void close() {
        try {
            channel.close();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
