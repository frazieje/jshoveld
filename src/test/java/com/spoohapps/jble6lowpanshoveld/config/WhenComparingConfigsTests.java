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
                "-f", "./profile.conf",
                "-x", "amq.app",
                "-i", "amq.incoming",
                "-d", "amq.device",
                "-b", "amq.app",
                "-o", "amq.outgoing"
        };

        String[] verboseArgs = new String[] {
                "-apiUrl", "",
                "-apiPort", "" + 5672,
                "-localPort", "" + 5672,
                "-profileFilePath", "./profile.conf",
                "-apiExchange", "amq.app",
                "-incomingExchange", "amq.incoming",
                "-deviceExchange", "amq.device",
                "-appExchange", "amq.app",
                "-outgoingExchange", "amq.outgoing"
        };

        assertEquals(Config.fromArgs(simpleArgs), Config.fromArgs(verboseArgs));

    }
}
