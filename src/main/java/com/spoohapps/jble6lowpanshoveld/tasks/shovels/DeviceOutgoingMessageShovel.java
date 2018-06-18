package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.jble6lowpanshoveld.model.Message;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.PublisherConnection;

public class DeviceOutgoingMessageShovel extends AbstractMessageShovel<DeviceOutgoingMessageShovel> {

    private final String profileId;

    public DeviceOutgoingMessageShovel(ShovelContext context, String profileId) {
        super(context);
        this.profileId = profileId;
    }

    @Override
    protected void handleMessage(Message message, PublisherConnection publisher) {
        logger.info("consumed message with topic {}, hops {}, deviceFlag {}", message.getTopic(), message.getHops(), message.hasDeviceFlag());

        if (!message.hasDeviceFlag() && message.getTopic().toLowerCase().startsWith(profileId + ".")) {
            Message newMessage = new Message(
                    message.getTopic().substring(profileId.length()+1),
                    true,
                    message.getPayload());

            publisher.publish(newMessage);
        }
    }
}
