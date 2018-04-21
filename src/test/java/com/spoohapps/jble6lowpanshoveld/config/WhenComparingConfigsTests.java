package com.spoohapps.jble6lowpanshoveld.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WhenComparingConfigsTests {

    @Test
    public void emptyConfigsShouldBeEqual() {
        assertEquals(Config.fromArgs(new String[] {}), Config.fromArgs(new String [] {}));
    }

    @Test
    public void defaultConfigsShouldBeEqual() {
        assertEquals(Config.fromDefaults(), Config.fromDefaults());
    }

    @Test
    public void identicalArgsShouldBeEqual() {
        String[] simpleArgs = new String[] {
                "-a", "",
                "-p", "" + 5672,
                "-l", "" + 5672,
                "-"
                "-f", "./profile.conf"
        };

        String[] verboseArgs = new String[] {
                "-apiHost", "",
                "-apiPort", "" + 5672,
                "-nodePort", "" + 5672,
                "-nodeHost", "",
                "-profileFilePath", "./profile.conf"
        };

        assertEquals(Config.fromArgs(simpleArgs), Config.fromArgs(verboseArgs));

    }
}
