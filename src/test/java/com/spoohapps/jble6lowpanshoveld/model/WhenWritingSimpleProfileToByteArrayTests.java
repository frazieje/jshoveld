package com.spoohapps.jble6lowpanshoveld.model;

import com.spoohapps.jble6lowpanshoveld.testhelpers.ProfileFileHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenWritingSimpleProfileToByteArrayTests {

    Profile profile;

    private static final String expectedProfileId = "a2a48c93";

    @BeforeAll
    public void setup() {
        profile = Profile.from(expectedProfileId);
    }

    @Test
    public void shouldWriteProfileId() {
        assertArrayEquals(ProfileFileHelper.bytesWithProfileId(expectedProfileId), profile.toByteArray());
    }

}
