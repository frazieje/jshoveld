package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConnectionFactory;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConnectionSettings;

public class ShovelContext {

    private final ConnectionFactory sourceFactory;
    private final ConnectionSettings sourceSettings;
    private final ConnectionFactory destinationFactory;
    private final ConnectionSettings destinationSettings;

    public ShovelContext(
            ConnectionFactory sourceFactory,
            ConnectionSettings sourceSettings,
            ConnectionFactory destinationFactory,
            ConnectionSettings destinationSettings) {
        this.sourceFactory = sourceFactory;
        this.sourceSettings = sourceSettings;
        this.destinationFactory = destinationFactory;
        this.destinationSettings = destinationSettings;
    }

    public ConnectionFactory getSourceFactory() {
        return sourceFactory;
    }

    public ConnectionSettings getSourceSettings() {
        return sourceSettings;
    }

    public ConnectionFactory getDestinationFactory() {
        return destinationFactory;
    }

    public ConnectionSettings getDestinationSettings() {
        return destinationSettings;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null)
            return false;

        if (!ShovelContext.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final ShovelContext other = (ShovelContext) obj;

        if (sourceFactory == null ? other.sourceFactory != null : !sourceFactory.equals(other.sourceFactory))
            return false;

        if (sourceSettings == null ? other.sourceSettings != null : !sourceSettings.equals(other.sourceSettings))
            return false;

        if (destinationFactory == null ? other.destinationFactory != null : !destinationFactory.equals(other.destinationFactory))
            return false;

        if (destinationSettings == null ? other.destinationSettings != null : !destinationSettings.equals(other.destinationSettings))
            return false;

        return true;
    }
}
