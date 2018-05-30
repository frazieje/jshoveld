package com.spoohapps.jble6lowpanshoveld.model;

import com.spoohapps.jble6lowpanshoveld.testhelpers.ProfileFileHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenReadingApiProfileFromStreamTests {

    Profile profile;

    private static final String expectedProfileId = "a2a48c93";

    @BeforeAll
    public void setup() {
        profile = Profile.from(ProfileFileHelper.streamWithProfileIdNodeAndApi(expectedProfileId));
    }

    @Test
    public void shouldHaveTheCorrectProfileId() {
        assertEquals(expectedProfileId, profile.getId());
    }

    @Test
    public void shouldHaveTheCorrectNodeCertificate() throws CertificateException {
        assertArrayEquals(profile.getNodeContext().getCertificate().getEncoded(), ProfileFileHelper.nodeCertificate().getEncoded());
    }

    @Test
    public void shouldHaveTheCorrectNodePrivateKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        assertArrayEquals(profile.getNodeContext().getPrivateKey().getEncoded(), ProfileFileHelper.nodePrivateKey().getEncoded());
    }

    @Test
    public void shouldHaveTheCorrectNodeCaCertificate() throws CertificateException {
        assertArrayEquals(profile.getNodeContext().getCaCertificate().getEncoded(), ProfileFileHelper.nodeCaCertificate().getEncoded());
    }

    @Test
    public void shouldHaveTheCorrectApiCertificate() throws CertificateException {
        assertArrayEquals(profile.getApiContext().getCertificate().getEncoded(), ProfileFileHelper.apiCertificate().getEncoded());
    }

    @Test
    public void shouldHaveTheCorrectApiPrivateKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        assertArrayEquals(profile.getApiContext().getPrivateKey().getEncoded(), ProfileFileHelper.apiPrivateKey().getEncoded());
    }

    @Test
    public void shouldHaveTheCorrectApiCaCertificate() throws CertificateException {
        assertArrayEquals(profile.getApiContext().getCaCertificate().getEncoded(), ProfileFileHelper.apiCaCertificate().getEncoded());
    }

    @Test
    public void shouldHaveTheCorrectBytes() {
        assertArrayEquals(profile.toByteArray(), ProfileFileHelper.bytesWithProfileIdNodeAndApi(expectedProfileId));
    }

}
