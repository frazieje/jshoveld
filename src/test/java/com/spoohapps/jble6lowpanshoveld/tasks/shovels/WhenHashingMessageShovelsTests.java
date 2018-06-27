package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.farcommon.testhelpers.FakeConnectionFactory;
import com.spoohapps.farcommon.testhelpers.TestConnectionSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenHashingMessageShovelsTests {

    MessageShovel otherShovel = new HopsIncrementingMessageShovel(
            new ShovelContext(
                    new FakeConnectionFactory(),
                    new TestConnectionSettings(),
                    new FakeConnectionFactory(),
                    new TestConnectionSettings()),
            HopsIncrementingMessageShovel.class.getSimpleName());

    @Test
    public void shouldHaveSameResult() {
        MessageShovel shovel = new HopsIncrementingMessageShovel(
                new ShovelContext(
                        new FakeConnectionFactory(),
                        new TestConnectionSettings(),
                        new FakeConnectionFactory(),
                        new TestConnectionSettings()),
                HopsIncrementingMessageShovel.class.getSimpleName());

        assertEquals(shovel.hashCode(), otherShovel.hashCode());
    }

    @Test
    public void shouldHaveDifferentResultForDifferentImpl() {
        MessageShovel shovel = new SimpleMessageShovel(
                new ShovelContext(
                        new FakeConnectionFactory(),
                        new TestConnectionSettings(),
                        new FakeConnectionFactory(),
                        new TestConnectionSettings()),
                SimpleMessageShovel.class.getSimpleName());

        assertNotEquals(shovel.hashCode(), otherShovel.hashCode());
    }

}
