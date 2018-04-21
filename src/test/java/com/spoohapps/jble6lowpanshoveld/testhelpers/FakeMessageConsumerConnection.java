package com.spoohapps.jble6lowpanshoveld.testhelpers;

import com.spoohapps.jble6lowpanshoveld.model.Message;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.MessageConsumerConnection;

import java.util.function.Consumer;

public class FakeMessageConsumerConnection implements MessageConsumerConnection {
    @Override
    public void consume(Consumer<Message> messageConsumer) {

    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public void open() {

    }

    @Override
    public void close() {

    }
}
