package org.amv.highmobility.cryptotool;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Cryptotool {

    Version version() throws IOException;

    Keys generateKeys() throws IOException;

    Signature generateSignature(String message, String privateKey) throws IOException;

    Validity verifySignature(String message, String signature, String publicKey) throws IOException;

    Hmac generateHmac(String message, String key) throws IOException;

    Validity verifyHmac(String message, String key, String hmac) throws IOException;

    default AccessCertificate createAccessCertificate(
            String gainingSerial,
            String publicKey,
            String providingSerial,
            LocalDateTime startDate,
            LocalDateTime endDate) throws IOException {
        return createAccessCertificate(
                gainingSerial, publicKey, providingSerial,
                startDate, endDate,
                Collections.emptyList());
    }

    AccessCertificate createAccessCertificate(
            String gainingSerial,
            String publicKey,
            String providingSerial,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Collection<String> permissions
    ) throws IOException;

    DeviceCertificate createDeviceCertificate(
            String issuer,
            String appId,
            String serial,
            String publicKey
    ) throws IOException;

    interface Keys {
        String getPrivateKey();

        String getPublicKey();
    }

    interface Version {
        default String getFullVersionString() {
            return Stream.of(getMajor(), getMinor(), getPatch())
                    .collect(Collectors.joining("."));
        }

        String getMajor();

        String getMinor();

        String getPatch();
    }

    interface Signature {
        String getSignature();
    }

    interface Hmac {
        String getHmac();
    }

    interface AccessCertificate {
        String getAccessCertificate();
        LocalDateTime getValidityStartDate();
        LocalDateTime getValidityEndDate();
    }

    interface DeviceCertificate {
        String getDeviceCertificate();
    }

    enum Validity {
        VALID,
        INVALID
    }
}