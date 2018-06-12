package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.jble6lowpanshoveld.model.Message;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.PublisherConnection;

public class DeviceIncomingMessageShovel extends AbstractMessageShovel<DeviceIncomingMessageShovel> {

    private final String profileId;

    public DeviceIncomingMessageShovel(ShovelContext context, String profileId) {
        super(context);
        this.profileId = profileId;
    }

    @Override
    protected void handleMessage(Message message, PublisherConnection publisher) {
        Message newMessage = new Message(profileId + "." + message.getTopic(), true, message.getPayload());
        publisher.publish(newMessage);
    }

}
