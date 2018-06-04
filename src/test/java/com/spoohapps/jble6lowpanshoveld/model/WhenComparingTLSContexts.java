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
public class WhenComparingTLSContexts {

    @Test
    public void shouldEqual() throws CertificateException, InvalidKeySpecException, NoSuchAlgorithmException {

        TLSContext context = new TLSContext(
                ProfileFileHelper.nodeCertificate(),
                ProfileFileHelper.nodePrivateKey(),
                ProfileFileHelper.nodeCaCertificate());

        TLSContext other = new TLSContext(
                ProfileFileHelper.nodeCertificate(),
                ProfileFileHelper.nodePrivateKey(),
                ProfileFileHelper.nodeCaCertificate());

        assertEquals(context, other);
    }

    @Test
    public void shouldNotEqualNullContext() throws CertificateException, InvalidKeySpecException, NoSuchAlgorithmException {

        TLSContext context = new TLSContext(
                ProfileFileHelper.nodeCertificate(),
                ProfileFileHelper.nodePrivateKey(),
                ProfileFileHelper.nodeCaCertificate());

        TLSContext other = null;

        assertNotEquals(context, other);
    }

    @Test
    public void shouldEqualCertificateOnly() throws CertificateException, InvalidKeySpecException, NoSuchAlgorithmException {

        TLSContext context = new TLSContext(
                ProfileFileHelper.nodeCertificate(),
                null,
                null);

        TLSContext other = new TLSContext(
                ProfileFileHelper.nodeCertificate(),
                null,
                null);

        assertEquals(context, other);
    }

    @Test
    public void shouldEqualNullCerts() throws CertificateException, InvalidKeySpecException, NoSuchAlgorithmException {

        TLSContext context = new TLSContext(
                null,
                null,
                null);

        TLSContext other = new TLSContext(
                null,
                null,
                null);

        assertEquals(context, other);
    }
}
