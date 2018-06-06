package com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091;

import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConnectionSettings;

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

    public enum SettingsKeys {EXCHANGE, QUEUE, ROUTING_KEY}

    @Override
    public String get(String key) {

        switch (SettingsKeys.valueOf(key.toUpperCase())) {
            case EXCHANGE:
                return exchange;
            case QUEUE:
                return queue;
            case ROUTING_KEY:
                return routingKey;
            default:
                throw new IllegalArgumentException("Unknown settings key was supplied.");
        }
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
