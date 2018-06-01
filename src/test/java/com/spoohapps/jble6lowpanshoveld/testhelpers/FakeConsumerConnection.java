package com.spoohapps.jble6lowpanshoveld.testhelpers;

import com.spoohapps.jble6lowpanshoveld.model.Message;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConsumerConnection;

import java.util.function.Consumer;

public class FakeConsumerConnection implements ConsumerConnection {
    @Override
    public void open() {

    }

    @Override
    public void close() {

    }

    @Override
    public void onClosed(Runnable closed) {

    }

    @Override
    public void onConsume(Consumer<Message> messageConsumer) {

    }

    @Override
    public String getDescription() {
        return null;
    }
}
