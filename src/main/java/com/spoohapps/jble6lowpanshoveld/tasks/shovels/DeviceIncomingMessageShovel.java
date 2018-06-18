package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.jble6lowpanshoveld.model.Message;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.PublisherConnection;

public class DeviceIncomingMessageShovel extends AbstractMessageShovel<DeviceIncomingMessageShovel> {

    private final String profileId;

    public DeviceIncomingMessageShovel(ShovelContext context, String name, String profileId) {
        super(context, name);
        this.profileId = profileId;
    }

    @Override
    protected void handleMessage(Message message, PublisherConnection publisher) {
        logger.info("consumed message with topic {}, hops {}, deviceFlag {}", message.getTopic(), message.getHops(), message.hasDeviceFlag());
        if (!message.hasDeviceFlag() && !message.getTopic().toLowerCase().startsWith(profileId + ".")) {
            Message newMessage = new Message(
                    profileId + "." + message.getTopic(),
                    true,
                    message.getPayload());
            publisher.publish(newMessage);
        }
    }

}
