package com.spoohapps.jble6lowpanshoveld.model;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;

public class TLSCredentialContext {

    private X509Certificate certificate;
    private RSAPrivateKey privateKey;
    private X509Certificate caCertificate;

    public TLSCredentialContext() {

    }

    public TLSCredentialContext(X509Certificate certificate, RSAPrivateKey privateKey, X509Certificate caCertificate) {
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

    public SSLContext toSSLContext() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException, KeyManagementException {

        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(null, "changeit".toCharArray());
        keystore.setCertificateEntry("cert-alias", certificate);
        keystore.setKeyEntry("key-alias", privateKey, "changeit".toCharArray(), new Certificate[] { certificate });

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");

        kmf.init(keystore, "changeit".toCharArray());

        KeyStore trustStore = KeyStore.getInstance("JKS");
        keystore.load(null, "changeit".toCharArray());
        keystore.setCertificateEntry("cert-alias", caCertificate);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(trustStore);

        SSLContext c = SSLContext.getInstance("TLSv1.2");
        c.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return c;
    }
}
