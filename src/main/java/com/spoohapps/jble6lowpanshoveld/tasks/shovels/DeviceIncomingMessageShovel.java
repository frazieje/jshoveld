package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.farcommon.connection.PublisherConnection;
import com.spoohapps.jble6lowpanshoveld.model.ShovelMessage;

public class DeviceIncomingMessageShovel extends AbstractMessageShovel<DeviceIncomingMessageShovel> {

    private final String profileId;

    public DeviceIncomingMessageShovel(ShovelContext context, String name, String profileId) {
        super(context, name);
        this.profileId = profileId;
    }

    @Override
    public MessageShovel clone() {
        return new DeviceIncomingMessageShovel(context, name, profileId);
    }

    @Override
    protected void handleMessage(ShovelMessage message, PublisherConnection publisher) {
        logger.info("consumed message with topic {}, hops {}, deviceFlag {}", message.getTopic(), message.getHops(), message.hasDeviceFlag());
        if (!message.hasDeviceFlag() && !message.getTopic().toLowerCase().startsWith(profileId + ".")) {
            ShovelMessage newMessage = new ShovelMessage(
                    profileId + "." + message.getTopic(),
                    message.getPayload(),
                    message.getHeaders());
            newMessage.setDeviceFlag(true);
            publisher.publish(newMessage);
        }
    }

}
