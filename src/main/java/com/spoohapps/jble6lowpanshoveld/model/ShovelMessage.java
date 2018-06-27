package com.spoohapps.jble6lowpanshoveld.model;

import com.spoohapps.farcommon.model.Message;
import java.util.Map;

public class ShovelMessage extends Message {

    public static final String hopsHeaderKey = "x-hops";
    public static final String deviceHeaderKey = "x-device";

    public ShovelMessage(String topic, byte[] payload) {
        super(topic, payload);
    }

    public ShovelMessage(String topic, byte[] payload, Map<String, Object> headers) {
        super(topic, payload, headers);
    }

    public static ShovelMessage from(Message message) {
        return new ShovelMessage(message.getTopic(), message.getPayload(), message.getHeaders());
    }

    public void setHops(int hops) {
        getHeaders().put(hopsHeaderKey, hops);
    }

    public int getHops() {
        int hops = 0;
        if (getHeaders().containsKey(hopsHeaderKey)) {
            try {
                hops = (Integer) getHeaders().get(hopsHeaderKey);
            } catch (Exception e) {
                try {
                    hops = Integer.parseInt((String)getHeaders().get(hopsHeaderKey));
                } catch (Exception se) {}
            }
        }
        return hops;
    }

    public void setDeviceFlag(boolean deviceFlag) {
        if (deviceFlag)
            getHeaders().put(deviceHeaderKey, true);
        else
            getHeaders().remove(deviceHeaderKey);
    }

    public boolean hasDeviceFlag() { return getHeaders().containsKey(deviceHeaderKey); }

}
