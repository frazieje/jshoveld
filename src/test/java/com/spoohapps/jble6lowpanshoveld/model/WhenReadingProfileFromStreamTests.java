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
public class WhenReadingProfileFromStreamTests {

    Profile profile;

    private static final String expectedProfileId = "a2a48c93";

    @BeforeAll
    public void setup() {
        profile = Profile.from(ProfileFileHelper.streamWithProfileIdAndNode(expectedProfileId));
    }

    @Test
    public void shouldHaveTheCorrectProfileId() {
        assertEquals(expectedProfileId, profile.getId());
    }

    @Test
    public void shouldHaveTheCorrectCertificate() throws CertificateException {
        assertArrayEquals(profile.getNodeContext().getCertificate().getEncoded(), ProfileFileHelper.nodeCertificate().getEncoded());
    }

    @Test
    public void shouldHaveTheCorrectPrivateKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        assertArrayEquals(profile.getNodeContext().getPrivateKey().getEncoded(), ProfileFileHelper.nodePrivateKey().getEncoded());
    }

    @Test
    public void shouldHaveTheCorrectCaCertificate() throws CertificateException {
        assertArrayEquals(profile.getNodeContext().getCaCertificate().getEncoded(), ProfileFileHelper.nodeCaCertificate().getEncoded());
    }

    @Test
    public void shouldHaveTheCorrectBytes() {
        assertArrayEquals(profile.toByteArray(), ProfileFileHelper.bytesWithProfileIdAndNode(expectedProfileId));
    }

}
