package com.spoohapps.jble6lowpanshoveld.model;

public class Message {

    private final String topic;

    private final int hops;

    private final byte[] payload;

    private final boolean fromDevice;

    public String getTopic() {
        return topic;
    }

    public int getHops() {
        return hops;
    }

    public byte[] getPayload() {
        return payload;
    }

    public boolean isFromDevice() { return fromDevice; }

    public Message(String topic, byte[] payload) {
        this.topic = topic;
        this.hops = 0;
        this.payload = payload;
        this.fromDevice = false;
    }

    public Message(String topic, int hops, byte[] payload) {
        this.topic = topic;
        this.hops = hops;
        this.payload = payload;
        this.fromDevice = false;
    }

    public Message(String topic, boolean fromDevice, byte[] payload) {
        this.topic = topic;
        this.hops = 0;
        this.fromDevice = fromDevice;
        this.payload = payload;
    }

    public Message(String topic, int hops, boolean fromDevice, byte[] payload) {
        this.topic = topic;
        this.hops = hops;
        this.fromDevice = fromDevice;
        this.payload = payload;
    }
}
