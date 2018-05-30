package com.spoohapps.jble6lowpanshoveld.tasks.profile;

import com.spoohapps.jble6lowpanshoveld.model.Profile;
import com.spoohapps.jble6lowpanshoveld.testhelpers.ProfileFileHelper;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenStartingProfileManagerTests {

    private ProfileManager profileManager;

    private Path filePath = Paths.get(System.getProperty("user.home"), "profile.conf");

    private Profile currentProfile;

    @BeforeAll
    public void context() {
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
            Files.delete(filePath);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @Test
    public void shouldStartWithNullProfile() {
        assertNull(profileManager.get());
    }

    @Test
    public void shouldWriteEmptyProfileToFile() {
        assertNull(ProfileFileHelper.getFileContents(filePath));
    }
}
