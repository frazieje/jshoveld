package com.spoohapps.jble6lowpanshoveld.model;

public class Message {

    private final String topic;

    private final int hops;

    private final byte[] payload;

    public String getTopic() {
        return topic;
    }

    public int getHops() {
        return hops;
    }

    public byte[] getPayload() {
        return payload;
    }

    public Message(String topic, int hops, byte[] payload) {
        this.topic = topic;
        this.hops = hops;
        this.payload = payload;
    }
}
