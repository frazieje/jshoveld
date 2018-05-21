package com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091;

import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConnectionSettings;

public class Amqp091PublisherConnectionSettings implements ConnectionSettings {

    private String exchange;

    public Amqp091PublisherConnectionSettings(String exchange) {
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
