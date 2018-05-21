package com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.rabbitmq;

import com.rabbitmq.client.Delivery;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.Amqp091Message;

import java.util.Map;

public class RabbitMqAmqp091Message implements Amqp091Message {

    private final Delivery rabbitmqDelivery;

    public RabbitMqAmqp091Message(Delivery rabbitmqDelivery) {
        this.rabbitmqDelivery = rabbitmqDelivery;
    }

    @Override
    public Map<String, Object> getHeaders() {
        return rabbitmqDelivery.getProperties().getHeaders();
    }

    @Override
    public String getRoutingKey() {
        return rabbitmqDelivery.getEnvelope().getRoutingKey();
    }

    @Override
    public byte[] getBody() {
        return rabbitmqDelivery.getBody();
    }
}
