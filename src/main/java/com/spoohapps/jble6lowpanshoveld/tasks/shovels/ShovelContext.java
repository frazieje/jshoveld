package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConnectionFactory;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConnectionSettings;

public interface ShovelContext {
    ConnectionFactory sourceFactory();
    ConnectionFactory destinationFactory();
    ConnectionSettings sourceSettings();
    ConnectionSettings destinationSettings();
    MessageShovel createShovel();
}
