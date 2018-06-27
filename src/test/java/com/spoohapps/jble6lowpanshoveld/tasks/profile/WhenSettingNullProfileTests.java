package com.spoohapps.jble6lowpanshoveld.tasks.profile;

import com.spoohapps.farcommon.model.Profile;
import com.spoohapps.farcommon.testhelpers.ProfileFileHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenSettingNullProfileTests {

    private ProfileManager profileManager;

    private Path filePath = Paths.get(System.getProperty("user.home"), "profile.conf");

    private Profile currentProfile;

    @BeforeAll
    public void context() {
        currentProfile = Profile.from("2a341cb8");
        ProfileFileHelper.deleteFile(filePath);
        ProfileFileHelper.writeFileContents(filePath, currentProfile);
        profileManager = new FileBasedProfileManager(filePath);
        profileManager.onChanged(this::setProfile);
        try {
            profileManager.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sleep(1500);
        profileManager.set(null);
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
    public void shouldNotNotifyObservers() {
        assertNotNull(currentProfile);
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
