package com.spoohapps.jble6lowpanshoveld.tasks.connection;

public class Amqp091MessagePublisherConnectionSettings implements MessageConnectionSettings {

    private String exchange;

    public Amqp091MessagePublisherConnectionSettings(String exchange) {
        this.exchange = exchange;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    @Override
    public String get(String key) {
        switch (key.toLowerCase()) {
            case "exchange":
                return exchange;
            default:
                throw new IllegalArgumentException("Unknown settings key was supplied.");
        }
    }
}
