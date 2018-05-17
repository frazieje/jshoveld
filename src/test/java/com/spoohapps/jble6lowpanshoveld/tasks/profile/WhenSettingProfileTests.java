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
public class WhenSettingProfileTests {
    private ProfileManager profileManager;

    private Path filePath = Paths.get(System.getProperty("user.home"), "profile.conf");

    private static final String expectedProfileId = "8cf314d2";

    private Profile currentProfile;

    @BeforeAll
    public void context() {
        ProfileFileHelper.deleteFile(filePath);
        ProfileFileHelper.writeFileContents(filePath, Profile.from("2a341cb8"));
        profileManager = new FileBasedProfileManager(filePath);
        profileManager.onChanged(this::setProfile);
        try {
            profileManager.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sleep(1500);
        profileManager.set(Profile.from(expectedProfileId));
        sleep(10000);
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
    public void shouldHaveAProfile() {
        assertNotNull(profileManager.get());
    }

    @Test
    public void shouldOverwriteWithNewProfile() {
        assertEquals(Profile.from(expectedProfileId), profileManager.get());
    }

    @Test
    public void shouldWriteNewProfileToFile() {
        assertEquals(Profile.from(expectedProfileId), ProfileFileHelper.getFileContents(filePath));
    }

    @Test
    public void shouldNofifyListenersOnChange() {
        assertEquals(Profile.from(expectedProfileId), currentProfile);
    }

    private void setProfile(Profile profile) {
        currentProfile = profile;
    }

    private void sleep(int timeMs) {
        try {
            Thread.sleep(timeMs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
