package com.spoohapps.jble6lowpanshoveld.tasks.connection;

import com.spoohapps.jble6lowpanshoveld.model.Message;

public interface PublisherConnection {
    void open();
    void close();
    void onClosed(Runnable closed);
    void publish(Message message);
    String getDescription();
}
