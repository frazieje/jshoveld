package com.spoohapps.jble6lowpanshoveld.testhelpers;

import com.spoohapps.jble6lowpanshoveld.model.Profile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProfileFileHelper {

    public static void writeFileContents(Path filePath, Profile profile) {
        try {
            byte[] bytes = profile.toString().getBytes();
            Files.write(filePath, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Profile getFileContents(Path filePath) {
        try {
            return Profile.from(new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    public static void deleteFile(Path filePath) {
        try {
            Files.delete(filePath);
        } catch (IOException ioe) {
        }
    }
}
