package com.spoohapps.jble6lowpanshoveld.tasks.connection;

import com.rabbitmq.client.*;
import com.spoohapps.jble6lowpanshoveld.model.Message;

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
            channel = connection.get().createChannel();
            channel.exchangeDeclare(exchange, BuiltinExchangeType.TOPIC, true);
            channel.addShutdownListener(this::handleShutdown);
            isOpen.set(true);
        } catch (IOException e) {
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
