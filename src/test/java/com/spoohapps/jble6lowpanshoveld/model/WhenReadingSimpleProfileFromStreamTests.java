package com.spoohapps.jble6lowpanshoveld.model;

import com.spoohapps.jble6lowpanshoveld.testhelpers.ProfileFileHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenReadingSimpleProfileFromStreamTests {

    Profile profile;

    private static final String expectedProfileId = "a2a48c93";

    @BeforeAll
    public void setup() {
        profile = Profile.from(ProfileFileHelper.streamWithProfileId(expectedProfileId));
    }

    @Test
    public void shouldHaveTheCorrectProfileId() {
        assertEquals(expectedProfileId, profile.getId());
    }

}
