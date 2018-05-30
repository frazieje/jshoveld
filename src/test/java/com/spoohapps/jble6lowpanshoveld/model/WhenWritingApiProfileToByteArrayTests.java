package com.spoohapps.jble6lowpanshoveld.model;

import com.spoohapps.jble6lowpanshoveld.testhelpers.ProfileFileHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenWritingApiProfileToByteArrayTests {

    Profile profile;

    private static final String expectedProfileId = "a2a48c93";

    @BeforeAll
    public void setup() throws InvalidKeySpecException, NoSuchAlgorithmException, CertificateException {

        TLSCredentialContext nodeContext = new TLSCredentialContext();
        nodeContext.setPrivateKey(ProfileFileHelper.nodePrivateKey());
        nodeContext.setCertificate(ProfileFileHelper.nodeCertificate());
        nodeContext.setCaCertificate(ProfileFileHelper.nodeCaCertificate());

        TLSCredentialContext apiContext = new TLSCredentialContext();
        apiContext.setPrivateKey(ProfileFileHelper.apiPrivateKey());
        apiContext.setCertificate(ProfileFileHelper.apiCertificate());
        apiContext.setCaCertificate(ProfileFileHelper.apiCaCertificate());

        profile = Profile.from(expectedProfileId, nodeContext, apiContext);
    }

    @Test
    public void shouldWriteCorrectBytes() {
        assertArrayEquals(ProfileFileHelper.bytesWithProfileIdNodeAndApi(expectedProfileId), profile.toByteArray());
    }

}
