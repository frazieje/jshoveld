package com.spoohapps.jble6lowpanshoveld.model;

public class Message {

    private final String topic;

    private final int hops;

    private final byte[] payload;

    private final boolean deviceFlag;

    public String getTopic() {
        return topic;
    }

    public int getHops() {
        return hops;
    }

    public byte[] getPayload() {
        return payload;
    }

    public boolean hasDeviceFlag() { return deviceFlag; }

    public Message(String topic, byte[] payload) {
        this.topic = topic;
        this.hops = 0;
        this.payload = payload;
        this.deviceFlag = false;
    }

    public Message(String topic, int hops, byte[] payload) {
        this.topic = topic;
        this.hops = hops;
        this.payload = payload;
        this.deviceFlag = false;
    }

    public Message(String topic, boolean deviceFlag, byte[] payload) {
        this.topic = topic;
        this.hops = 0;
        this.deviceFlag = deviceFlag;
        this.payload = payload;
    }

    public Message(String topic, int hops, boolean deviceFlag, byte[] payload) {
        this.topic = topic;
        this.hops = hops;
        this.deviceFlag = deviceFlag;
        this.payload = payload;
    }
}
