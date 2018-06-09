package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConnectionFactory;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConnectionSettings;

import java.util.Objects;

public class BasicShovelContext implements ShovelContext {

    private final ConnectionFactory sourceFactory;
    private final ConnectionSettings sourceSettings;
    private final ConnectionFactory destinationFactory;
    private final ConnectionSettings destinationSettings;

    private BasicShovelContext(
            ConnectionFactory sourceFactory,
            ConnectionSettings sourceSettings,
            ConnectionFactory destinationFactory,
            ConnectionSettings destinationSettings) {
        this.sourceFactory = sourceFactory;
        this.sourceSettings = sourceSettings;
        this.destinationFactory = destinationFactory;
        this.destinationSettings = destinationSettings;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceFactory, sourceSettings, destinationFactory, destinationSettings);
    }

    public static ShovelContext from(
            ConnectionFactory sourceFactory,
            ConnectionSettings sourceSettings,
            ConnectionFactory destinationFactory,
            ConnectionSettings destinationSettings) {
        return new BasicShovelContext(
                sourceFactory,
                sourceSettings,

                destinationFactory,
                destinationSettings);
    }

    @Override
    public ConnectionFactory sourceFactory() {
        return sourceFactory;
    }

    @Override
    public ConnectionFactory destinationFactory() {
        return destinationFactory;
    }

    @Override
    public ConnectionSettings sourceSettings() {
        return sourceSettings;
    }

    @Override
    public ConnectionSettings destinationSettings() {
        return destinationSettings;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!BasicShovelContext.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final BasicShovelContext other = (BasicShovelContext) obj;

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
