package com.spoohapps.jble6lowpanshoveld;

import com.spoohapps.jble6lowpanshoveld.config.ShovelDaemonConfig;
import com.spoohapps.jble6lowpanshoveld.testhelpers.FakeProfileManager;
import com.spoohapps.jble6lowpanshoveld.testhelpers.FakeShovelManager;
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
        daemon = new ShovelDaemon(new TestConfig(), new FakeProfileManager(), new FakeShovelManager());
        try {
            daemon.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }

    private class TestConfig implements ShovelDaemonConfig {

        @Override
        public String profileFilePath() {
            return "";
        }

        @Override
        public String apiHost() {
            return "";
        }

        @Override
        public String nodeHost() {
            return "";
        }

        @Override
        public int apiPort() {
            return 5672;
        }

        @Override
        public int nodePort() {
            return 5672;
        }

        @Override
        public ShovelDaemonConfig apply(ShovelDaemonConfig other) {
            return null;
        }
    }
}
