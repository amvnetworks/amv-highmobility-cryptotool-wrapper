package org.amv.highmobility.cryptotool;

import lombok.Builder;
import lombok.Value;
import reactor.core.publisher.Mono;

public interface CryptotoolWithIssuer extends Cryptotool {
    interface CertificateIssuer {
        String getName();

        Cryptotool.Keys getKeys();

        String getPublicKeyBase64();
    }

    @Value
    @Builder
    class CertificateIssuerImpl implements CertificateIssuer {
        private String name;
        private Cryptotool.Keys keys;

        @Override
        public String getPublicKeyBase64() {
            return CryptotoolUtils.encodeHexAsBase64(keys.getPublicKey());
        }
    }

    CertificateIssuer getCertificateIssuer();

    default Mono<Signature> generateSignature(String message) {
        return generateSignature(message, getCertificateIssuer().getKeys().getPrivateKey());
    }

    default Mono<Validity> verifySignature(String message, String signature) {
        return verifySignature(message, signature, getCertificateIssuer().getKeys().getPublicKey());
    }

    default Mono<DeviceCertificate> createDeviceCertificate(String appId, String serial) {
        return createDeviceCertificate(getCertificateIssuer().getName(), appId, serial,
                getCertificateIssuer().getKeys().getPublicKey());
    }
}