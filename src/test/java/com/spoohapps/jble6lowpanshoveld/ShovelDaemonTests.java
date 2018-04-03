package com.spoohapps.jble6lowpanshoveld;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ShovelDaemonTests {

    private ShovelDaemon daemon;

    @BeforeAll
    public void context() {
        daemon = new ShovelDaemon();
    }

    @AfterAll
    public void teardown() {
        try {
            daemon.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldStart() {
        boolean start;
        try {
            daemon.start();
            start = true;
        } catch (Exception e) {
            start = false;
        }
        assertTrue(start);
    }
}
