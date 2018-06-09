package com.spoohapps.jble6lowpanshoveld.model;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.Arrays;
import java.util.Objects;

public class TLSContext {

    private X509Certificate certificate;
    private RSAPrivateKey privateKey;
    private X509Certificate caCertificate;

    public TLSContext() {

    }

    public TLSContext(X509Certificate certificate, RSAPrivateKey privateKey, X509Certificate caCertificate) {
        this.certificate = certificate;
        this.privateKey = privateKey;
        this.caCertificate = caCertificate;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(X509Certificate certificate) {
        this.certificate = certificate;
    }

    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(RSAPrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public X509Certificate getCaCertificate() {
        return caCertificate;
    }

    public void setCaCertificate(X509Certificate caCertificate) {
        this.caCertificate = caCertificate;
    }

    public boolean hasValue() {
        return certificate != null && privateKey != null && caCertificate != null;
    }

    @Override
    public boolean equals(Object other) {

        if (other == null) {
            return false;
        }

        TLSContext otherCtx = (TLSContext)other;

        if (certificate == null) {
            if (otherCtx.getCertificate() != null) {
                return false;
            }
        } else {
            try {
                if (otherCtx.getCertificate() == null) {
                    return false;
                }
                if (!Arrays.equals(certificate.getEncoded(), otherCtx.getCertificate().getEncoded())) {
                    return false;
                }
            } catch (CertificateEncodingException e) {
                return false;
            }
        }

        if (caCertificate == null) {
            if (otherCtx.getCaCertificate() != null) {
                return false;
            }
        } else {
            try {
                if (otherCtx.getCaCertificate() == null) {
                    return false;
                }
                if (!Arrays.equals(caCertificate.getEncoded(), otherCtx.getCaCertificate().getEncoded())) {
                    return false;
                }
            } catch (CertificateEncodingException e) {
                return false;
            }
        }

        if (privateKey == null) {
            if (otherCtx.getPrivateKey() != null) {
                return false;
            }
        } else {
            if (otherCtx.getPrivateKey() == null) {
                return false;
            }
            return Arrays.equals(privateKey.getEncoded(), otherCtx.getPrivateKey().getEncoded());
        }

        return true;
    }

    @Override
    public int hashCode() {
        int certHash = 7;
        if (certificate != null) {
            try {
                certHash = Arrays.hashCode(certificate.getEncoded());
            } catch (CertificateEncodingException ignored) {}
        }
        int privateKeyHash = 11;
        if (privateKey != null) {
            privateKeyHash = Arrays.hashCode(privateKey.getEncoded());
        }
        int caCertHash = 31;
        if (caCertificate != null) {
            try {
                caCertHash = Arrays.hashCode(caCertificate.getEncoded());
            } catch (CertificateEncodingException ignored) {}
        }
        return Objects.hash(certHash, privateKeyHash, caCertHash);
    }

    public SSLContext toSSLContext() throws TLSContextException {

        try {
            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(null, "changeit".toCharArray());
            keystore.setCertificateEntry("cert-alias", certificate);
            keystore.setKeyEntry("key-alias", privateKey, "changeit".toCharArray(), new Certificate[]{ certificate });

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");

            kmf.init(keystore, "changeit".toCharArray());

            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(null, "changeit".toCharArray());
            trustStore.setCertificateEntry("cert-alias", caCertificate);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(trustStore);

            SSLContext c = SSLContext.getInstance("TLSv1.2");

            TrustManager[] managers = tmf.getTrustManagers();

            c.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            return c;
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableKeyException | KeyManagementException e) {
            throw new TLSContextException(e);
        }
    }
}
