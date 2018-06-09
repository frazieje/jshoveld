package com.spoohapps.jble6lowpanshoveld.model;

import com.spoohapps.jble6lowpanshoveld.testhelpers.ProfileFileHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenHashingTLSContextsTests {

    @Test
    public void shouldHaveSameHashWhenEqual() throws CertificateException, InvalidKeySpecException, NoSuchAlgorithmException {
        TLSContext context = new TLSContext(
                ProfileFileHelper.nodeCertificate(),
                ProfileFileHelper.nodePrivateKey(),
                ProfileFileHelper.nodeCaCertificate());

        TLSContext other = new TLSContext(
                ProfileFileHelper.nodeCertificate(),
                ProfileFileHelper.nodePrivateKey(),
                ProfileFileHelper.nodeCaCertificate());

        assertEquals(context.hashCode(), other.hashCode());
    }

    @Test
    public void shouldNotHaveSameHashWhenNotEqual() throws CertificateException, InvalidKeySpecException, NoSuchAlgorithmException {
        TLSContext context = new TLSContext(
                ProfileFileHelper.nodeCertificate(),
                ProfileFileHelper.nodePrivateKey(),
                ProfileFileHelper.nodeCaCertificate());

        TLSContext other = new TLSContext(
                ProfileFileHelper.nodeCertificate(), null, null);

        assertNotEquals(context.hashCode(), other.hashCode());
    }

}
