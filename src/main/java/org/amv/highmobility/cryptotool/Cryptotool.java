package org.amv.highmobility.cryptotool;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Cryptotool {

    Mono<Version> version();

    Mono<Keys> generateKeys();

    Mono<Signature> generateSignature(String message, String privateKey);

    Mono<Validity> verifySignature(String message, String signature, String publicKey);

    Mono<Hmac> generateHmac(String message, String key);

    Mono<Validity> verifyHmac(String message, String key, String hmac);

    default Mono<AccessCertificate> createAccessCertificate(
            String gainingSerial,
            String publicKey,
            String providingSerial,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        return createAccessCertificate(
                gainingSerial, publicKey, providingSerial,
                startDate, endDate,
                Collections.emptyList());
    }

    Mono<AccessCertificate> createAccessCertificate(
            String gainingSerial,
            String publicKey,
            String providingSerial,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Collection<String> permissions
    );

    Mono<DeviceCertificate> createDeviceCertificate(
            String issuer,
            String appId,
            String serial,
            String publicKey
    );

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