package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.farcommon.testhelpers.FakeConnectionFactory;
import com.spoohapps.farcommon.testhelpers.TestConnectionSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenCloningMessageShovelTests {

    MessageShovel original = new HopsIncrementingMessageShovel(
            new ShovelContext(
                new FakeConnectionFactory(),
                new TestConnectionSettings(),
                new FakeConnectionFactory(),
                new TestConnectionSettings()),
            HopsIncrementingMessageShovel.class.getSimpleName());

    @Test
    public void shouldEqualClone() {
        assertEquals(original.clone(), original);
    }
}
