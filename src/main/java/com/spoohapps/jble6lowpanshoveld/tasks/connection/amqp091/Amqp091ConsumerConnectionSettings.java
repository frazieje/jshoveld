package com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091;

import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConnectionSettings;

import java.util.Objects;

public class Amqp091ConsumerConnectionSettings implements ConnectionSettings {

    private String exchange;

    private String queue;

    private String routingKey;

    public Amqp091ConsumerConnectionSettings(String exchange, String queue, String routingKey) {
        this.exchange = exchange;
        this.queue = queue;
        this.routingKey = routingKey;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    static class SettingsKeys {
        static final String EXCHANGE = "exchange";
        static final String QUEUE = "queue";
        static final String ROUTING_KEY = "routing_key";
    }

    @Override
    public String get(String key) {

        switch (key.toLowerCase()) {
            case SettingsKeys.EXCHANGE:
                return exchange;
            case SettingsKeys.QUEUE:
                return queue;
            case SettingsKeys.ROUTING_KEY:
                return routingKey;
            default:
                throw new IllegalArgumentException("Unknown settings key was supplied.");
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(exchange, queue, routingKey);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null)
            return false;

        if (!Amqp091ConsumerConnectionSettings.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final Amqp091ConsumerConnectionSettings other = (Amqp091ConsumerConnectionSettings) obj;

        if (exchange == null ? other.getExchange() != null : !exchange.equals(other.getExchange()))
            return false;

        if (queue == null ? other.getQueue() != null : !queue.equals(other.getQueue()))
            return false;

        if (routingKey == null ? other.getRoutingKey() != null : !routingKey.equals(other.getRoutingKey()))
            return false;

        return true;

    }
}
