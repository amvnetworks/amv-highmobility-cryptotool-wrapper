package org.amv.highmobility.cryptotool;

import com.google.common.base.Charsets;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.codec.binary.Hex;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

public interface CryptotoolWithIssuer extends Cryptotool {
    interface CertificateIssuer {
        String getName();

        Cryptotool.Keys getKeys();

        default String getPublicKeyBase64() {
            return Optional.ofNullable(getKeys())
                    .map(Keys::getPublicKey)
                    .map(CryptotoolUtils::encodeHexAsBase64)
                    .orElseThrow(IllegalStateException::new);
        }

        default String getNameInHex() {
            return Optional.ofNullable(getName())
                    .map(val -> val.getBytes(Charsets.UTF_8))
                    .map(Hex::encodeHexString)
                    .orElseThrow(IllegalStateException::new);
        }
    }

    @Getter
    @Builder
    class CertificateIssuerImpl implements CertificateIssuer {
        private static final int NAME_LENGTH = 4;

        private String name;
        private Cryptotool.Keys keys;

        CertificateIssuerImpl(String name, Cryptotool.Keys keys) {
            requireNonNull(name, "`name` must not be null");
            requireNonNull(keys, "`keys` must not be null");
            checkArgument(name.length() == NAME_LENGTH, String.format(
                    "`name` must be a string with length %d", NAME_LENGTH));

            this.name = name;
            this.keys = keys;
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
        return createDeviceCertificate(getCertificateIssuer().getNameInHex(), appId, serial,
                getCertificateIssuer().getKeys().getPublicKey());
    }
}