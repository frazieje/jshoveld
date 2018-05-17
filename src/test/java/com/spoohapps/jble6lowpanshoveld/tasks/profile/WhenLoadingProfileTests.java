package com.spoohapps.jble6lowpanshoveld.tasks.profile;

import com.spoohapps.jble6lowpanshoveld.model.Profile;
import com.spoohapps.jble6lowpanshoveld.testhelpers.ProfileFileHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenLoadingProfileTests {
    private ProfileManager profileManager;

    private Path filePath = Paths.get(System.getProperty("user.home"), "profile.conf");

    private static final String existingProfileId = "2a341cb8";

    @BeforeAll
    public void context() {
        ProfileFileHelper.deleteFile(filePath);
        ProfileFileHelper.writeFileContents(filePath, Profile.from(existingProfileId));
        profileManager = new FileBasedProfileManager(filePath);
        try {
            profileManager.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public void teardown() {
        try {
            profileManager.stop();
            ProfileFileHelper.deleteFile(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldLoadAProfile() {
        assertNotNull(profileManager.get());
    }

    @Test
    public void shouldNotOverwriteWithGeneratedProfile() {
        assertEquals(Profile.from(existingProfileId), profileManager.get());
    }

    @Test
    public void shouldWriteProfileToFile() {
        assertEquals(Profile.from(existingProfileId), ProfileFileHelper.getFileContents(filePath));
    }

}
