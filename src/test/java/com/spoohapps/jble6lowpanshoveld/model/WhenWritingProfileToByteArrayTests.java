package com.spoohapps.jble6lowpanshoveld.model;

import com.spoohapps.jble6lowpanshoveld.testhelpers.ProfileFileHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenWritingProfileToByteArrayTests {

    Profile profile;

    private static final String expectedProfileId = "a2a48c93";

    @BeforeAll
    public void setup() throws InvalidKeySpecException, NoSuchAlgorithmException, CertificateException {
        TLSCredentialContext nodeContext = new TLSCredentialContext();
        nodeContext.setPrivateKey(ProfileFileHelper.nodePrivateKey());
        nodeContext.setCertificate(ProfileFileHelper.nodeCertificate());
        nodeContext.setCaCertificate(ProfileFileHelper.nodeCaCertificate());
        profile = Profile.from(expectedProfileId, nodeContext, null);
    }

    @Test
    public void shouldWriteCorrectBytes() {
        assertArrayEquals(ProfileFileHelper.bytesWithProfileIdAndNode(expectedProfileId), profile.toByteArray());
    }

}
