package com.spoohapps.jble6lowpanshoveld.model;

import sun.security.provider.X509Factory;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Profile {

    private static final int PROFILE_ID_LENGTH = 8;

    private String id;

    private TLSContext nodeContext = new TLSContext();

    private TLSContext apiContext = new TLSContext();

    private static final String lineSeparator = System.getProperty("line.separator");

    private Profile() {

    }

    public static Profile from(String id, TLSContext nodeContext, TLSContext apiContext) {
        Profile p = from(id);

        if (nodeContext != null) {
            if (!nodeContext.hasValue())
                throw new IllegalArgumentException("Could not create profile. Incomplete TLS credentials for the node client.");
            p.nodeContext = nodeContext;
        }

        if (apiContext != null) {
            if (!apiContext.hasValue())
                throw new IllegalArgumentException("Could not create profile. Incomplete TLS credentials for the api client.");
            p.apiContext = apiContext;
        }
        return p;
    }

    public static Profile from(String id) {
        if (id == null)
            throw new IllegalArgumentException("Profile id must not be null");

        Profile p = new Profile();

        String lower = id.toLowerCase();

        p.verifyProfileId(lower);

        p.id = id;

        return p;
    }

    public static Profile from(InputStream profileStream) {

        if (profileStream == null)
            throw new IllegalArgumentException("Error reading profile from stream: null stream");

        Profile p = new Profile();
        try {
            readSection(new InputStreamReader(profileStream), p.sectionDelimiters);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Error reading profile: " + e.getMessage());
        } finally {
            try { profileStream.close(); } catch (Throwable ignore) {}
        }

        if (p.id != null)
            return p;

        return null;
    }

    private static void readSection(Reader readerImpl, Map<String, Map.Entry<String, Consumer<String>>> delimiters) throws IOException {
        String str;
        String start = null;
        String end = null;
        StringBuilder buf = new StringBuilder();
        BufferedReader reader = new BufferedReader(readerImpl);
        while ((str = reader.readLine()) != null) {
            if (delimiters.containsKey(str)) {
                start = str;
                end = delimiters.get(str).getKey();
            }
            buf.append(str).append(lineSeparator);
            if (str.equals(end)) {
                delimiters.get(start).getValue().accept(buf.toString());
                buf.delete(0, buf.length());
            }
        }
    }

    private void readNode(String section) {
        String node = removeFirstAndLastLines(section);
        try {
            readSection(new StringReader(node), getSectionDetailDelimiters(this::readNodePrivateKey, this::readNodeCertificate, this::readNodeCaCertificate));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Improper profile format: could not read node section");
        }
    }

    private void readApi(String section) {
        String api = removeFirstAndLastLines(section);
        try {
            readSection(new StringReader(api), getSectionDetailDelimiters(this::readApiPrivateKey, this::readApiCertificate, this::readApiCaCertificate));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Improper profile format: could not read api section");
        }
    }

    private void readClientCertAndKey(String section, Consumer<String> readKey, Consumer<String> readCert) {
        String data = removeFirstAndLastLines(section);
        try {
            readSection(new StringReader(data), getKeyAndCertDelimiters(readKey, readCert));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Improper profile format: could not read client section");
        }
    }

    private void readCaCert(String section, Consumer<String> readCert) {
        String data = removeFirstAndLastLines(section);
        try {
            readSection(new StringReader(data), getKeyAndCertDelimiters(null, readCert));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Improper profile format: could not read ca certificate");
        }
    }


    private void readProfileId(String section) {
        if (section == null || section.length() == 0)
            throw new IllegalArgumentException("Improper profile format: no profile id found");

        String data = removeFirstAndLastLines(section);

        String lower = data.toLowerCase().trim();

        verifyProfileId(lower);

        id = lower;
    }

    private void verifyProfileId(String id) {
        if (id.length() != PROFILE_ID_LENGTH)
            throw new IllegalArgumentException("Profile id should be " + PROFILE_ID_LENGTH + " characters long.");
        for (int i = 0; i < id.length(); i++) {
            if (!hexString.contains(id.substring(i, i+1))) {
                throw new IllegalArgumentException("Improper profile format: profile must only contain characters 0-9 and a-f");
            }
        }
    }

    private void readApiPrivateKey(String privateKey) {
        try {
            apiContext.setPrivateKey(generatePrivateKeyFromDER(parseDERFromPEM(privateKey)));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Problem reading api client key: " + e.getMessage());
        }
    }

    private void readApiCertificate(String certificate) {
        try {
            apiContext.setCertificate(generateCertificateFromDER(parseDERFromPEM(certificate)));
        } catch (CertificateException e) {
            throw new IllegalArgumentException("Problem reading api certificate: " + e.getMessage());
        }
    }

    private void readApiCaCertificate(String certificate) {
        try {
            apiContext.setCaCertificate(generateCertificateFromDER(parseDERFromPEM(certificate)));
        } catch (CertificateException e) {
            throw new IllegalArgumentException("Problem reading api ca certificate: " + e.getMessage());
        }
    }

    private void readNodePrivateKey(String privateKey) {
        try {
            nodeContext.setPrivateKey(generatePrivateKeyFromDER(parseDERFromPEM(privateKey)));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Problem reading node private key: " + e.getMessage());
        }
    }

    private void readNodeCertificate(String certificate) {
        try {
            nodeContext.setCertificate(generateCertificateFromDER(parseDERFromPEM(certificate)));
        } catch (CertificateException e) {
            throw new IllegalArgumentException("Problem reading node certificate: " + e.getMessage());
        }
    }

    private void readNodeCaCertificate(String certificate) {
        try {
            nodeContext.setCaCertificate(generateCertificateFromDER(parseDERFromPEM(certificate)));
        } catch (CertificateException e) {
            throw new IllegalArgumentException("Problem reading node ca certificate: " + e.getMessage());
        }
    }

    public byte[] toByteArray() {
        StringBuilder buf = new StringBuilder();
        Base64.Encoder encoder = Base64.getMimeEncoder(64, lineSeparator.getBytes(StandardCharsets.UTF_8));

        if (id != null && id.length() > 0) {
            buf.append(profileIdStartDelimiter).append(lineSeparator);
            buf.append(id).append(lineSeparator);
            buf.append(profileIdEndDelimiter).append(lineSeparator);

            writeSection(encoder, buf, nodeContext, nodeStartDelimiter, nodeEndDelimiter);

            writeSection(encoder, buf, apiContext, apiStartDelimiter, apiEndDelimiter);
        }

        return buf.toString().getBytes(StandardCharsets.UTF_8);
    }

    private void writeSection(Base64.Encoder encoder, StringBuilder buf, TLSContext context, String startDelimiter, String endDelimiter) {
        if (context.hasValue()) {
            try {
                String cert = encoder.encodeToString(context.getCertificate().getEncoded());
                String privateKey = encoder.encodeToString(context.getPrivateKey().getEncoded());
                String cacert = encoder.encodeToString(context.getCaCertificate().getEncoded());

                buf.append(startDelimiter).append(lineSeparator);

                buf.append(clientCertAndKeyStartDelimiter).append(lineSeparator);

                buf.append(privateKeyStartDelimiter).append(lineSeparator);
                buf.append(privateKey).append(lineSeparator);
                buf.append(privateKeyEndDelimiter).append(lineSeparator);

                buf.append(certificateStartDelimiter).append(lineSeparator);
                buf.append(cert).append(lineSeparator);
                buf.append(certificateEndDelimiter).append(lineSeparator);

                buf.append(clientCertAndKeyEndDelimiter).append(lineSeparator);

                buf.append(caCertStartDelimiter).append(lineSeparator);

                buf.append(certificateStartDelimiter).append(lineSeparator);
                buf.append(cacert).append(lineSeparator);
                buf.append(certificateEndDelimiter).append(lineSeparator);

                buf.append(caCertEndDelimiter).append(lineSeparator);

                buf.append(endDelimiter).append(lineSeparator);

            } catch (CertificateEncodingException ignored) {}
        }
    }

    private static String removeFirstAndLastLines(String target) {
        String data = target.substring(target.indexOf(lineSeparator)+1, target.lastIndexOf(lineSeparator));
        return data.substring(0, data.lastIndexOf(lineSeparator)+1);
    }

    private static byte[] parseDERFromPEM(String section) {
        String data = removeFirstAndLastLines(section);
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

    private static String getCommonNameFromCertificate(X509Certificate certificate) {
        return Stream.of(certificate)
                .map(cert -> cert.getSubjectX500Principal().getName())
                .flatMap(name -> {
                    try {
                        return new LdapName(name).getRdns().stream()
                                .filter(rdn -> rdn.getType().equalsIgnoreCase("cn"))
                                .map(rdn -> rdn.getValue().toString());
                    } catch (InvalidNameException e) {
                        return Stream.empty();
                    }
                }).collect(Collectors.joining());
    }

    @Override
    public String toString() {
        return new String(toByteArray(), StandardCharsets.UTF_8);
    }

    @Override
    public boolean equals(Object other) {
        Profile otherProfile = (Profile)other;

        if (other == null)
            return false;

        return Arrays.equals(toByteArray(), otherProfile.toByteArray());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

    public String getId() {
        return id;
    }

    public TLSContext getNodeContext() {
        return nodeContext;
    }

    public TLSContext getApiContext() {
        return apiContext;
    }

    private final static String hexString = "0123456789abcdef";

    private static final String profileIdStartDelimiter = "-----BEGIN PROFILE IDENTIFIER-----";
    private static final String profileIdEndDelimiter = "-----END PROFILE IDENTIFIER-----";

    private static final String nodeStartDelimiter = "-----BEGIN NODE-----";
    private static final String nodeEndDelimiter = "-----END NODE-----";

    private static final String apiStartDelimiter = "-----BEGIN API-----";
    private static final String apiEndDelimiter = "-----END API-----";

    private static final String clientCertAndKeyStartDelimiter = "-----BEGIN CLIENT CERT AND KEY-----";
    private static final String clientCertAndKeyEndDelimiter = "-----END CLIENT CERT AND KEY-----";

    private static final String caCertStartDelimiter = "-----BEGIN CA CERT-----";
    private static final String caCertEndDelimiter = "-----END CA CERT-----";

    private static final String privateKeyStartDelimiter = "-----BEGIN PRIVATE KEY-----";
    private static final String privateKeyEndDelimiter = "-----END PRIVATE KEY-----";
    private static final String certificateStartDelimiter = X509Factory.BEGIN_CERT;
    private static final String certificateEndDelimiter = X509Factory.END_CERT;

    private Map<String, Map.Entry<String, Consumer<String>>> sectionDelimiters =
            Collections.unmodifiableMap(Stream.of(
                    getEntry(profileIdStartDelimiter,
                            profileIdEndDelimiter,
                            this::readProfileId),
                    getEntry(nodeStartDelimiter,
                            nodeEndDelimiter,
                            this::readNode),
                    getEntry(apiStartDelimiter,
                            apiEndDelimiter,
                            this::readApi)
            ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

    private Map<String, Map.Entry<String, Consumer<String>>> getSectionDetailDelimiters(Consumer<String> readKey, Consumer<String> readCert, Consumer<String> readCaCert) {
        return Collections.unmodifiableMap(Stream.of(
                getEntry(clientCertAndKeyStartDelimiter,
                        clientCertAndKeyEndDelimiter,
                        str -> readClientCertAndKey(str, readKey, readCert)),
                getEntry(caCertStartDelimiter,
                        caCertEndDelimiter,
                        str -> readCaCert(str, readCaCert))
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    private Map<String, Map.Entry<String, Consumer<String>>> getKeyAndCertDelimiters(Consumer<String> readKey, Consumer<String> readCert) {
        return Collections.unmodifiableMap(Stream.of(
                getEntry(privateKeyStartDelimiter,
                        privateKeyEndDelimiter,
                        readKey),
                getEntry(certificateStartDelimiter,
                        certificateEndDelimiter,
                        readCert)
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    private static Map.Entry<String, Map.Entry<String, Consumer<String>>> getEntry(String startDelimiter, String endDelimiter, Consumer<String> parser) {
        return new SimpleEntry<>(startDelimiter, new SimpleEntry<>(endDelimiter, parser));
    }
}
