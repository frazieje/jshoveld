package com.spoohapps.jble6lowpanshoveld;

import com.spoohapps.jble6lowpanshoveld.config.ShovelDaemonConfig;
import com.spoohapps.jble6lowpanshoveld.testhelpers.FakeMessageConnectionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ShovelDaemonTests {

    private ShovelDaemon daemon;

    @BeforeAll
    public void context() {
        daemon = new ShovelDaemon(new TestConfig(), new FakeMessageConnectionFactory(), new FakeMessageConnectionFactory());
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
            e.printStackTrace();
            start = false;
        }
        assertTrue(start);
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
        public String apiExchange() {
            return "far.app";
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
        public String incomingExchange() {
            return "far.incoming";
        }

        @Override
        public String deviceExchange() {
            return "far.device";
        }

        @Override
        public String appExchange() {
            return "far.app";
        }

        @Override
        public String outgoingExchange() {
            return "far.outgoing";
        }

        @Override
        public ShovelDaemonConfig apply(ShovelDaemonConfig other) {
            throw new NotImplementedException();
        }
    }
}
