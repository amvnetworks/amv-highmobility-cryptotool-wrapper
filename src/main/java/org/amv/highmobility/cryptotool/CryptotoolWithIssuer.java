package org.amv.highmobility.cryptotool;

import lombok.Builder;
import lombok.Value;

import java.io.IOException;

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

    default Signature generateSignature(String message) throws IOException {
        return generateSignature(message, getCertificateIssuer().getKeys().getPrivateKey());
    }

    default Validity verifySignature(String message, String signature) throws IOException {
        return verifySignature(message, signature, getCertificateIssuer().getKeys().getPublicKey());
    }

    default DeviceCertificate createDeviceCertificate(String appId, String serial) throws IOException {
        // TODO: verify that the issuers public key is the right one! Or do whe need the devicePublicKey? probably not.
        return createDeviceCertificate( getCertificateIssuer().getName(), appId, serial, getCertificateIssuer().getKeys().getPublicKey());
    }
}