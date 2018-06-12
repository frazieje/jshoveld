package com.spoohapps.jble6lowpanshoveld.testhelpers;

import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConnectionSettings;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TestConnectionSettings implements ConnectionSettings {

    private final Map<String, String> values;

    public TestConnectionSettings() {
        values = new HashMap<>();
    }

    public TestConnectionSettings(Map<String, String> values) {
        this.values = values;
    }

    public void add(String key, String value) {
        values.put(key, value);
    }

    public void remove(String key) {
        values.remove(key);
    }

    @Override
    public String get(String key) {
        return values.get(key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!TestConnectionSettings.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final TestConnectionSettings other = (TestConnectionSettings) obj;

        if (values == null ? other.values != null : !values.equals(other.values))
            return false;

        return true;
    }

}
