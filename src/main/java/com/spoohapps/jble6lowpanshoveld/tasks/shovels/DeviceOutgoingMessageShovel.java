package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.farcommon.connection.PublisherConnection;
import com.spoohapps.jble6lowpanshoveld.model.ShovelMessage;

public class DeviceOutgoingMessageShovel extends AbstractMessageShovel<DeviceOutgoingMessageShovel> {

    private final String profileId;

    public DeviceOutgoingMessageShovel(ShovelContext context, String name, String profileId) {
        super(context, name);
        this.profileId = profileId;
    }

    @Override
    public MessageShovel clone() {
        return new DeviceOutgoingMessageShovel(context, name, profileId);
    }

    @Override
    protected void handleMessage(ShovelMessage message, PublisherConnection publisher) {
        logger.info("consumed message with topic {}, hops {}, deviceFlag {}", message.getTopic(), message.getHops(), message.hasDeviceFlag());

        if (!message.hasDeviceFlag() && message.getTopic().toLowerCase().startsWith(profileId + ".")) {
            ShovelMessage newMessage = new ShovelMessage(
                    message.getTopic().substring(profileId.length()+1),
                    message.getPayload(),
                    message.getHeaders());
            newMessage.setDeviceFlag(true);
            publisher.publish(newMessage);
        }
    }
}
