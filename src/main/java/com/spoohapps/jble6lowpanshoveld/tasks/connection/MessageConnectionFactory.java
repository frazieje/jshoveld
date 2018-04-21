package com.spoohapps.jble6lowpanshoveld.tasks.connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface MessageConnectionFactory {
    MessagePublisherConnection newPublisherConnection(MessageConnectionSettings settings);
    MessageConsumerConnection newConsumerConnection(MessageConnectionSettings settings);
}
