package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.jble6lowpanshoveld.model.Message;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConnectionFactory;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConnectionSettings;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.PublisherConnection;

public class RemoteMessageRetrievalShovel extends AbstractMessageShovel<RemoteMessageRetrievalShovel> {

    public RemoteMessageRetrievalShovel(ConnectionFactory sourceFactory, ConnectionSettings sourceSettings, ConnectionFactory destinationFactory, ConnectionSettings destinationSettings) {
        super(sourceFactory, sourceSettings, destinationFactory, destinationSettings);
    }

    @Override
    protected void handleMessage(Message message, PublisherConnection publisher) {
        logger.info("consumed message with topic {}, hops {}", message.getTopic(), message.getHops());

        if (message.getHops() == 0) {
            Message newMessage = new Message(message.getTopic(), message.getHops()+1, message.getPayload());

            publisher.publish(newMessage);
        }
    }
}
