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

    static class SettingsKeys {
        static final String EXCHANGE = "exchange";
    }

    @Override
    public String get(String key) {
        switch (key.toLowerCase()) {
            case SettingsKeys.EXCHANGE:
                return exchange;
            default:
                throw new IllegalArgumentException("Unknown settings key was supplied.");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!Amqp091PublisherConnectionSettings.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final Amqp091PublisherConnectionSettings other = (Amqp091PublisherConnectionSettings) obj;

        if (exchange == null ? other.getExchange() != null : !exchange.equals(other.getExchange()))
            return false;

        return true;

    }
}
