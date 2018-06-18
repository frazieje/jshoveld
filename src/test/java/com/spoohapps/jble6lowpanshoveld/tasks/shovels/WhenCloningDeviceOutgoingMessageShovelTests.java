package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.jble6lowpanshoveld.testhelpers.FakeConnectionFactory;
import com.spoohapps.jble6lowpanshoveld.testhelpers.TestConnectionSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenCloningDeviceOutgoingMessageShovelTests {

    MessageShovel original = new DeviceOutgoingMessageShovel(
            new ShovelContext(
                    new FakeConnectionFactory(),
                    new TestConnectionSettings(),
                    new FakeConnectionFactory(),
                    new TestConnectionSettings()),
            DeviceOutgoingMessageShovel.class.getSimpleName(),
            "1234abcd");

    @Test
    public void shouldEqualClone() {
        assertEquals(original.clone(), original);
    }

}
