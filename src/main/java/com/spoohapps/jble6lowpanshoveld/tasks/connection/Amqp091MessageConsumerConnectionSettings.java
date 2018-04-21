package com.spoohapps.jble6lowpanshoveld.tasks.connection;

public class Amqp091MessageConsumerConnectionSettings implements MessageConnectionSettings {

    private String exchange;

    private String queue;

    private String routingKey;

    public Amqp091MessageConsumerConnectionSettings(String exchange, String queue, String routingKey) {
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

    @Override
    public String get(String key) {
        switch (key.toLowerCase()) {
            case "exchange":
                return exchange;
            case "queue":
                return queue;
            case "routingkey":
                return routingKey;
            default:
                throw new IllegalArgumentException("Unknown settings key was supplied.");
        }
    }
}
