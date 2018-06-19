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
public class WhenLoadingProfileWIthNoId {
    private ProfileManager profileManager;

    private Path filePath = Paths.get(System.getProperty("user.home"), "profile.conf");

    private Profile expectedProfile = Profile.from(ProfileFileHelper.streamWithNode());

    @BeforeAll
    public void context() {
        ProfileFileHelper.deleteFile(filePath);
        Profile test = Profile.from(ProfileFileHelper.streamWithNode());
        ProfileFileHelper.writeFileContents(filePath, test);
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
        assertEquals(expectedProfile, profileManager.get());
    }

    @Test
    public void shouldWriteProfileToFile() {
        assertEquals(expectedProfile, ProfileFileHelper.getFileContents(filePath));
    }
}
