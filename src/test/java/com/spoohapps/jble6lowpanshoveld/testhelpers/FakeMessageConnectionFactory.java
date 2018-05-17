package com.spoohapps.jble6lowpanshoveld.testhelpers;

import com.spoohapps.jble6lowpanshoveld.tasks.connection.MessageConnectionFactory;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.MessageConnectionSettings;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.MessageConsumerConnection;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.MessagePublisherConnection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class FakeMessageConnectionFactory implements MessageConnectionFactory {
    @Override
    public MessageConsumerConnection newConsumerConnection(MessageConnectionSettings settings) {
        return new FakeMessageConsumerConnection();
    }

    @Override
    public MessagePublisherConnection newPublisherConnection(MessageConnectionSettings settings) {
        return new FakeMessagePublisherConnection();
    }
}
