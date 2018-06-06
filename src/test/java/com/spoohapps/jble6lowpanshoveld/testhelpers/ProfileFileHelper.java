package com.spoohapps.jble6lowpanshoveld.testhelpers;

import com.spoohapps.jble6lowpanshoveld.model.Profile;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.function.Consumer;

public class ProfileFileHelper {

    public static void writeFileContents(Path filePath, Profile profile) {
        try {
            byte[] bytes = profile.toByteArray();
            Files.write(filePath, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Profile getFileContents(Path filePath) {
        try {
            return Profile.from(Files.newInputStream(filePath));
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

    public static X509Certificate certifcateFromPem(Path pemCertPath) throws IOException, CertificateException {
        String fileContents = new String(Files.readAllBytes(pemCertPath), StandardCharsets.UTF_8);
        byte[] certificateBytes = parseDERFromPEM(fileContents);
        return generateCertificateFromDER(certificateBytes);
    }

    public static RSAPrivateKey privateKeyFromPem(Path pemKeyPath) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        String fileContents = new String(Files.readAllBytes(pemKeyPath), StandardCharsets.UTF_8);
        byte[] keyBytes = parseDERFromPEM(fileContents);
        return generatePrivateKeyFromDER(keyBytes);
    }

    public static InputStream streamWithProfileId(String profileId) {
        return streamOf(getProfileIdContents(profileId));
    }

    public static InputStream streamWithProfileIdAndNode(String profileId) {
        return streamOf(getProfileIdContents(profileId) + getNodeContents());
    }

    public static InputStream streamWithProfileIdNodeAndApi(String profileId) {
        return streamOf(getProfileIdContents(profileId) + getNodeContents() + getApiContents());
    }

    public static byte[] bytesWithProfileId(String profileId) {
        return byteArrayOf(getProfileIdContents(profileId));
    }

    public static byte[] bytesWithProfileIdAndNode(String profileId) {
        return byteArrayOf(getProfileIdContents(profileId) + getNodeContents());
    }

    public static byte[] bytesWithProfileIdNodeAndApi(String profileId) {
        return byteArrayOf(getProfileIdContents(profileId) + getNodeContents() + getApiContents());
    }

    public static RSAPrivateKey nodePrivateKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] privateKeyBytes = parseDERFromPEM(getNodePrivateKeyContents());
        return generatePrivateKeyFromDER(privateKeyBytes);
    }

    public static X509Certificate nodeCertificate() throws CertificateException {
        byte[] certificateBytes = parseDERFromPEM(getNodeCertificateContents());
        return generateCertificateFromDER(certificateBytes);
    }

    public static X509Certificate nodeCaCertificate() throws CertificateException {
        byte[] caCertificateBytes = parseDERFromPEM(getNodeCaCertificateContents());
        return generateCertificateFromDER(caCertificateBytes);
    }

    public static RSAPrivateKey apiPrivateKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] privateKeyBytes = parseDERFromPEM(getNodePrivateKeyContents());
        return generatePrivateKeyFromDER(privateKeyBytes);
    }

    public static X509Certificate apiCertificate() throws CertificateException {
        byte[] certificateBytes = parseDERFromPEM(getNodeCertificateContents());
        return generateCertificateFromDER(certificateBytes);
    }

    public static X509Certificate apiCaCertificate() throws CertificateException {
        byte[] caCertificateBytes = parseDERFromPEM(getNodeCaCertificateContents());
        return generateCertificateFromDER(caCertificateBytes);
    }

    private static String getProfileIdContents(String profileId) {
        StringBuilder buf = new StringBuilder();
        Consumer<String> w = getWriter(buf);
        w.accept("-----BEGIN PROFILE IDENTIFIER-----");
        w.accept(profileId);
        w.accept("-----END PROFILE IDENTIFIER-----");
        return buf.toString();
    }

    private static String getNodeContents() {
        return getContents("-----BEGIN NODE-----", "-----END NODE-----");
    }

    private static String getApiContents() {
        return getContents("-----BEGIN API-----", "-----END API-----");
    }

    private static String getContents(String startDelimiter, String endDelimiter) {
        StringBuilder buf = new StringBuilder();
        Consumer<String> w = getWriter(buf);
        w.accept(startDelimiter);
        w.accept("-----BEGIN CLIENT CERT AND KEY-----");
        buf.append(getNodePrivateKeyContents());
        buf.append(getNodeCertificateContents());
        w.accept("-----END CLIENT CERT AND KEY-----");
        w.accept("-----BEGIN CA CERT-----");
        buf.append(getNodeCaCertificateContents());
        w.accept("-----END CA CERT-----");
        w.accept(endDelimiter);
        return buf.toString();
    }



    private static String getNodePrivateKeyContents() {
        StringBuilder buf = new StringBuilder();
        Consumer<String> w = getWriter(buf);
        w.accept("-----BEGIN PRIVATE KEY-----");
        w.accept("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDLe6TkLUs4Lgjr");
        w.accept("B2bNU205MvZUOLfoXxe6MHj1VUbxxsY1Wua94az4gSE8nIdWcwCJSQHIT6DUz5Dt");
        w.accept("TBONfbo9C4NN6mKAs0OJDmuGylvnOeFXhySj3uQR2zXl+Cr9Rdhum38Pv8M7Doi8");
        w.accept("Ra3xhz32RU3T8wj9eXyUDFyZCg+ujj8xVDKZEKMRsVxJnpsr5kaSmw0zFz//LCa4");
        w.accept("fZetzOTxX+Hoz1SDgFhh5Raw+swOlAF9qXnixjsbt7aCvUxRbWXCKpH21kdRW2Ic");
        w.accept("4lQmHoDwNJIEHQViPyXSehKQe5AOCdFa1Au2/5sKyhnIkoUD4HLvHOL6uZZlQSP9");
        w.accept("QBhQ34inAgMBAAECggEBAMdjPu3/nAdOxJpYxFlJ+GrmDw30DfF6zKs7OCteBoh/");
        w.accept("eFVr31IMwws2rTTRRKRnSA0+Jqr7q+McCS0dMMOigU2z7FP66c6m2fSA1shbnbZz");
        w.accept("tuWnnTWeAOmmXagchzNqr2uintz10P4bfczOkmVrWkHpIxwet5543qPLgSjM/RTS");
        w.accept("uVz0RjD1autQnaWHIJFonhS3ZI6SUHVxGbfTgMK89TzMSZG9k+fU7ZlwiauicvyN");
        w.accept("TULpX4ovGJmFckm+uu+x5fSVfoDR+XD5by4owB3Sj7m/9Fs3DFPoy16TufUhr13f");
        w.accept("ONdBJgt9oumqqnZecchgQwPlTApWWWLbVTu3KKD8c3ECgYEA9sgnP4GZ6cSfGCRh");
        w.accept("6HZZIy3h5W/0u7UnWtNE1/e9Fc8m2I+yEqg9zfZhHu2CO0TO5om2B64WFcXPTl6O");
        w.accept("6cPDWwWfVmHksySybb4LQCMMl28xk4BhrBBymU3TV2Rkv7QHqz+a8xViDhzSYA2g");
        w.accept("SnMzl1j9QTG/3l+ulNlDyeSTBMMCgYEA0xVyPKsI397plb0sJ2/7yZL1ulfVNT1H");
        w.accept("pvAFH1QZvk3R+FmSd6jaL7o5DPJb2EzyKyUZ24NMOiBUhPFFQZ+0FMW7FNcsExMM");
        w.accept("N0GaoIHT1l4t+7xA8ZTAH0LGehQZqonFdvcK1SWJDPvyHLN0wnFp3Ui0vYYnTPk8");
        w.accept("UxO0FkMC3k0CgYA54FMkF7cLFivhs5aquCbLk1UpRAp3g1LJgEbjB5z24nBP1dOD");
        w.accept("gKWOCjxYzob+c3K6qo1gW7mePZgS3yZROLI2RKlLzwWd5ftatXlZ/15SnadY2oEN");
        w.accept("o4Xc4l2wX0EpnIU36mDipZ8rhCLqmAeBrmbpFdu/UHWZJ4OAMTwuu0anlQKBgCSS");
        w.accept("k691Rt1bBwe9thfDLFH5l3/I1hUaX/7JmWmbLbauTxIDmwAGjn80ecwHdehdNJxL");
        w.accept("GlbRQfTUQzChiQlcvVvYApkSyv0nELfGMx9aPzTmLntuW6Y/yqXf8PmX3/aPVlpN");
        w.accept("ZWAW188bHBDi+vjxo5EGluI7izWn/U67nDk7NRUFAoGBAK2ijXHlcyHuiFz9kjBJ");
        w.accept("tbQDk+YZ9y70FrC8vuzogseoJ7E+ncgD1rO3x/TgXBwgIX2oYUzH8vtTrZz+3yl2");
        w.accept("DlZqqVvFlQjTQtxVxqgX0Zx6JjJKI7DeK/95l0mNkbYZXMnqp0YGSVsM8Irj17bz");
        w.accept("fInwv1uLrQE8FBj2SvxfD/cI");
        w.accept("-----END PRIVATE KEY-----");
        return buf.toString();
    }

    private static String getNodeCertificateContents() {
        StringBuilder buf = new StringBuilder();
        Consumer<String> w = getWriter(buf);
        w.accept("-----BEGIN CERTIFICATE-----");
        w.accept("MIID6DCCAdCgAwIBAgIBAjANBgkqhkiG9w0BAQsFADAaMRgwFgYDVQQDDA9TcG9v");
        w.accept("aGFwcHNUZXN0Q0EwHhcNMTgwNTMwMDQzOTIzWhcNMjgwNTI3MDQzOTIzWjAkMREw");
        w.accept("DwYDVQQDDAh0ZXN0dXNlcjEPMA0GA1UECgwGY2xpZW50MIIBIjANBgkqhkiG9w0B");
        w.accept("AQEFAAOCAQ8AMIIBCgKCAQEAy3uk5C1LOC4I6wdmzVNtOTL2VDi36F8XujB49VVG");
        w.accept("8cbGNVrmveGs+IEhPJyHVnMAiUkByE+g1M+Q7UwTjX26PQuDTepigLNDiQ5rhspb");
        w.accept("5znhV4cko97kEds15fgq/UXYbpt/D7/DOw6IvEWt8Yc99kVN0/MI/Xl8lAxcmQoP");
        w.accept("ro4/MVQymRCjEbFcSZ6bK+ZGkpsNMxc//ywmuH2Xrczk8V/h6M9Ug4BYYeUWsPrM");
        w.accept("DpQBfal54sY7G7e2gr1MUW1lwiqR9tZHUVtiHOJUJh6A8DSSBB0FYj8l0noSkHuQ");
        w.accept("DgnRWtQLtv+bCsoZyJKFA+By7xzi+rmWZUEj/UAYUN+IpwIDAQABoy8wLTAJBgNV");
        w.accept("HRMEAjAAMAsGA1UdDwQEAwIFoDATBgNVHSUEDDAKBggrBgEFBQcDAjANBgkqhkiG");
        w.accept("9w0BAQsFAAOCAgEAvDniC7Vybkbz/6LDnU7+me/+hj/bx6W5lHAbAeWPmgbfTRKI");
        w.accept("8SfAHrdVT+Vc8WhhpnqYAbWs5w1OyQG58EFlHZ0s+Z99FEMggCYvLIQSqIsU56xt");
        w.accept("WttQfOgMzLovUn/VyK6cCIYd5dfyOtjL/9Zi3Sa/2vB3mZO8ugqcJxxPNFCBIJaO");
        w.accept("JsFroauD8TbyiRRLSxArMPEn2HBerpyY+DFpPBcgSfMrikjWLIwYQrzg43F//o7e");
        w.accept("/IYC5cwiCiDBiH+iPajo7GNZAk/T5VKSTXrYOA3V8nCKejs0rBRDJA0Kf8Og/cXM");
        w.accept("iqhLJFMNQscuLjbUtajLvh4fddgd98CUH2exUAVEMmKnNtliLSFZomnWpvQXf8om");
        w.accept("dt4t5+J6Ze3zshHSQZ+smRaBJb/LsmEGURmwWcqRlNWfCrTbs0YX8vrl/DQYrQw8");
        w.accept("cqUAXeAEqQNq8Zjpl1IX/xv3aUg8VBKK/yTcJ4NvCmRVWh2Nma3G+2xi3VnljVx+");
        w.accept("9kLTXbBG2nQl76WHyxNyZpkJnIUp66rGw5J1V8Mh3+JYaJ0CsBn8aG32N/gxssuZ");
        w.accept("clavGbrSDivCa1VaxHhAbFDqFlqOiO45JUHjNQpwBLj48rRn+GdX7bR3dBoAuGCX");
        w.accept("2I8D3/BDnhUp+rII88/AQzD5HJWXXKd6X5lTC7MToEgoCXdiII+S51Ksm6Y=");
        w.accept("-----END CERTIFICATE-----");
        return buf.toString();
    }

    private static String getNodeCaCertificateContents() {
        StringBuilder buf = new StringBuilder();
        Consumer<String> w = getWriter(buf);
        w.accept("-----BEGIN CERTIFICATE-----");
        w.accept("MIIE1DCCArygAwIBAgIJAImh2ojUC0hNMA0GCSqGSIb3DQEBCwUAMBoxGDAWBgNV");
        w.accept("BAMMD1Nwb29oYXBwc1Rlc3RDQTAeFw0xODA1MzAwNDMyMTJaFw0zODA1MjUwNDMy");
        w.accept("MTJaMBoxGDAWBgNVBAMMD1Nwb29oYXBwc1Rlc3RDQTCCAiIwDQYJKoZIhvcNAQEB");
        w.accept("BQADggIPADCCAgoCggIBAMyIP8toqBFamK76woF8golfVaPjq3qtrGAlbrHNsybP");
        w.accept("XVZJ3EQLTFZI9pM2/+stuaWlO5fg7TDqCqRwUIiV8do4env9eQRvZa0zDPrLkG7E");
        w.accept("y67KlFS7Mm+Vj5c2nuVgLlzxsELLhJfZlAdFlKguTjam6vpcVAIsWMfLnJVxFoVN");
        w.accept("7fNgQNqTn/9U6OkfWjFzy22u9kwaQtdDC/kC3/lp77nVbZCnFwCS+dEaSnnqBLuX");
        w.accept("DDcjCvnaZH/mMDQbMmslXqBcD0B4+9zqgbylijQPzFAxwmuSM78b7sA7ygDpX5X+");
        w.accept("3SRy/XImjnm9ulOpnIdd32fcEPJXZFTYmP1mgNDr5y2BmXHanxx5qVQhKIKPz4KY");
        w.accept("XnQ1vN+ENJiE4XvCjXl1XnwLHrEj9hap5cTsZR+9Y8gsM17QUFCFy5afD+XyNxRQ");
        w.accept("WlPUibVCCJ9PE48QLVr+R2XAs+6rjfLMXA1TlR1t1D24a55lXMVwKafKf/YIHjI1");
        w.accept("PD3mUGPppHATfsMX1mWcfWqCvN+mrlZ4+XCp52O9YT8RNC8WcVEd5xvIc7kZj58A");
        w.accept("WkPfM/ia33mahbKv789lTOjtYzhohyHM4shJ6VfoL8JmsEq3JWK7xBGZBHYkPaDI");
        w.accept("GR6L2UJlzBHj9SIqJOs3ONyVxSYSfN0HDC6OaSnmBXyq6hVo7HWjL+xXUGUrCBcZ");
        w.accept("AgMBAAGjHTAbMAwGA1UdEwQFMAMBAf8wCwYDVR0PBAQDAgEGMA0GCSqGSIb3DQEB");
        w.accept("CwUAA4ICAQBxrmK0XWaTeH0B5qsf5N8CVDy5+bebU5oiZBiOap7JtOWXlIGZhrxI");
        w.accept("7PROnkuzk4KG84ea5gV4U+8NXp1zMgRqi9NaFBGtWFLUJ3vyOk4IfSfPTKGQhhT1");
        w.accept("rFblQuqoBGU8dn6QgbMNlokmbq03SLC+4TsrbhMZBtZGMzoLRaO8nCTRgYdJK/Mt");
        w.accept("kUuAKGLlhdGSzmDiUwYQcLlLguK46GZ6mY3tBqfRbam9xIuXiXyfdhsesTLAmz99");
        w.accept("MKPJJiDuiYL4Y0KqdyRlKqpntEMVBIzoaQI61deLMBHu6LgTy+7UeiFY+UG/ehTB");
        w.accept("JQPZUEnVYJnO6JHmGk029HFg0tPowfv9tM7/I9gZKaIfC3bVweLnH5HjBw9M+CuI");
        w.accept("cuj1oX2MUTR3QCvZ6M7m4Ff945BdOEiA8n4ipRiz2JpjuMIGI+3JwbjqWT5ZAFoc");
        w.accept("3tvFD4yScbRUDx3S7gH11ThOLouI7Jgl28Qm0/a8E3UmkjnWqnb4Itv/IY9BE1GI");
        w.accept("d8+8R/mrlQ6qifUnn0WrSyh+Y1G7mOrNoZsvA0XR7twVNHr9hkfbgFJoD9xTsweK");
        w.accept("GxWaosg++cYKX22IQDO6o7X8WM0FVuH3bZla4CssyJ3HVZptI2wtWMHMbyvhJxkx");
        w.accept("Tjy2QViZVcY6O2frarkxPdSLhP2CrHn3QaVZnjvbhZ9+PXU6lUwyjQ==");
        w.accept("-----END CERTIFICATE-----");
        return buf.toString();
    }

    private static Consumer<String> getWriter(StringBuilder buf) {
        return l -> buf.append(l).append(lineSeparator);
    }

    private static InputStream streamOf(String contents) {
        return new BufferedInputStream(new ByteArrayInputStream(byteArrayOf(contents)));
    }

    private static byte[] byteArrayOf(String contents) {
        return contents.getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] parseDERFromPEM(String section) {
        String data = section.substring(section.indexOf(lineSeparator)+1, section.lastIndexOf(lineSeparator));
        data = data.substring(0, data.lastIndexOf(lineSeparator)+1);
        return DatatypeConverter.parseBase64Binary(data);
    }

    private static RSAPrivateKey generatePrivateKeyFromDER(byte[] keyBytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);

        KeyFactory factory = KeyFactory.getInstance("RSA");

        return (RSAPrivateKey)factory.generatePrivate(spec);
    }

    private static X509Certificate generateCertificateFromDER(byte[] certBytes) throws CertificateException {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");

        return (X509Certificate)factory.generateCertificate(new ByteArrayInputStream(certBytes));
    }

    private static final String lineSeparator = System.getProperty("line.separator");
}
