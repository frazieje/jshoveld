package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConnectionFactory;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConnectionSettings;

public class BasicShovelContext implements ShovelContext {


    @Override
    public ConnectionFactory sourceFactory() {
        return null;
    }

    @Override
    public ConnectionFactory destinationFactory() {
        return null;
    }

    @Override
    public ConnectionSettings sourceSettings() {
        return null;
    }

    @Override
    public ConnectionSettings destinationSettings() {
        return null;
    }
}
