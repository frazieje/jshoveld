package com.spoohapps.jble6lowpanshoveld.tasks.profile;

import com.spoohapps.farcommon.model.Profile;
import com.spoohapps.farcommon.testhelpers.ProfileFileHelper;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenStartingProfileManagerWithNoFileTests {

    private ProfileManager profileManager;

    private Path filePath = Paths.get(System.getProperty("user.home"), "profile.conf");

    private Profile currentProfile = Profile.from("adbc1234");

    @BeforeAll
    public void context() {
        ProfileFileHelper.deleteFile(filePath);
        profileManager = new FileBasedProfileManager(filePath);
        profileManager.onChanged(this::setProfile);
        profileManager.start();
    }

    @AfterAll
    public void teardown() {
        profileManager.stop();
        ProfileFileHelper.deleteFile(filePath);
    }

    private void setProfile(Profile profile) {
        currentProfile = profile;
    }

    @Test
    public void shouldStartWithNullProfile() {
        assertNull(profileManager.get());
    }

    @Test
    public void shouldWriteEmptyProfileToFile() {
        assertNull(ProfileFileHelper.getFileContents(filePath));
    }

    @Test
    public void shouldNotNotifyObserversOfNullProfile() {
        assertNotNull(currentProfile);
    }
}
