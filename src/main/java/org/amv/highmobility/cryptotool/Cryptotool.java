package org.amv.highmobility.cryptotool;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public interface Cryptotool {
    Mono<Version> version();

    Mono<Keys> generateKeys();

    Mono<Signature> generateSignature(String message, String privateKey);

    Mono<Validity> verifySignature(String message, String signature, String publicKey);

    Mono<Hmac> generateHmac(String message, String key);

    Mono<Validity> verifyHmac(String message, String key, String hmac);

    default Mono<AccessCertificate> createAccessCertificateV0(
            String providingSerial,
            String gainingSerial,
            String gainingPublicKey,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Permissions permissions) {
        requireNonNull(permissions);

        return createAccessCertificate(0,
                null, providingSerial, gainingSerial, gainingPublicKey,
                startDate, endDate, permissions.getPermissions());
    }
    default Mono<AccessCertificate> createAccessCertificateV1(
            String issuer,
            String providingSerial,
            String gainingSerial,
            String gainingPublicKey,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Permissions permissions) {
        requireNonNull(permissions);

        return createAccessCertificate(1,
                issuer, providingSerial, gainingSerial, gainingPublicKey,
                startDate, endDate, permissions.getPermissions());
    }

    Mono<AccessCertificate> createAccessCertificate(
            int version,
            String issuer,
            String providingSerial,
            String gainingSerial,
            String gainingPublicKey,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String permissions
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

    interface Permissions {
        String getPermissions();
    }

    enum Validity {
        VALID,
        INVALID
    }
}