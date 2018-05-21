package com.spoohapps.jble6lowpanshoveld.testhelpers;

import com.spoohapps.jble6lowpanshoveld.model.Message;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.PublisherConnection;

public class FakePublisherConnection implements PublisherConnection {
    @Override
    public void publish(Message message) {

    }

    @Override
    public void open() {

    }

    @Override
    public void close() {

    }

    @Override
    public void onClosed(Runnable closed) {

    }
}
