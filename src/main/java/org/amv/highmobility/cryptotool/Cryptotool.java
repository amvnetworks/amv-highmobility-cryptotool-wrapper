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

    default Mono<AccessCertificate> createAccessCertificate(
            String gainingSerial,
            String publicKey,
            String providingSerial,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Permissions permissions) {
        requireNonNull(permissions);

        return createAccessCertificate(
                gainingSerial, publicKey, providingSerial,
                startDate, endDate, permissions.getPermissions());
    }

    Mono<AccessCertificate> createAccessCertificate(
            String gainingSerial,
            String publicKey,
            String providingSerial,
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
        /**
         * @return private key in hex
         */
        String getPrivateKey();

        /**
         * @return public key in hex
         */
        String getPublicKey();

        /**
         * @return private key in base64
         */
        String getPrivateKeyBase64();

        /**
         * @return public key in base64
         */
        String getPublicKeyBase64();

        /**
         * @return raw private key
         */
        byte[] getPrivateKeyBytes();

        /**
         * @return raw public key
         */
        byte[] getPublicKeyBytes();
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
        String getAccessCertificateBase64();

        String getAccessCertificate();

        byte[] getAccessCertificateBytes();

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