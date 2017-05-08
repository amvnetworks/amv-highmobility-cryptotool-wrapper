package org.amv.highmobility.cryptotool;

import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.amv.highmobility.cryptotool.command.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collection;

import static java.util.Objects.requireNonNull;

@Slf4j
public class CryptotoolImpl implements Cryptotool {

    private final BinaryExecutor binaryExecutor;
    private final CryptotoolOptions options;

    public CryptotoolImpl(CryptotoolOptions options) throws IllegalArgumentException {
        this.options = requireNonNull(options, "`options` must not be null");
        this.binaryExecutor = requireNonNull(options.getBinaryExecutor());
    }

    @Override
    public Mono<Version> version() {
        return VersionCommand.builder()
                .build()
                .execute(binaryExecutor)
                .timeout(options.getCommandTimeout())
                .single();
    }

    public Mono<Keys> generateKeys() {
        return KeysCommand.builder()
                .build()
                .execute(binaryExecutor)
                .timeout(options.getCommandTimeout())
                .single();
    }

    @Override
    public Mono<Signature> generateSignature(String message, String privateKey) {
        return SignCommand.builder()
                .message(message)
                .privateKey(privateKey)
                .build()
                .execute(binaryExecutor)
                .timeout(options.getCommandTimeout())
                .single();
    }

    @Override
    public Mono<Validity> verifySignature(String message, String signature, String publicKey) {
        return VerifyCommand.builder()
                .message(message)
                .signature(signature)
                .publicKey(publicKey)
                .build()
                .execute(binaryExecutor)
                .timeout(options.getCommandTimeout())
                .single();
    }

    @Override
    public Mono<Hmac> generateHmac(String message, String key) {
        return HmacCommand.builder()
                .message(message)
                .key(key)
                .build()
                .execute(binaryExecutor)
                .timeout(options.getCommandTimeout())
                .single();
    }

    @Override
    public Mono<Validity> verifyHmac(String message, String key, String hmac) {
        return HmacVerifyCommand.builder()
                .message(message)
                .key(key)
                .hmac(hmac)
                .build()
                .execute(binaryExecutor)
                .timeout(options.getCommandTimeout())
                .single();
    }

    @Override
    public Mono<AccessCertificate> createAccessCertificate(String gainingSerial,
                                                           String publicKey,
                                                           String providingSerial,
                                                           LocalDateTime startDate,
                                                           LocalDateTime endDate,
                                                           Collection<String> permissions) {
        return AccessCommand.builder()
                .gainingSerial(gainingSerial)
                .publicKey(publicKey)
                .providingSerial(providingSerial)
                .startDate(startDate)
                .endDate(endDate)
                .build()
                .execute(binaryExecutor)
                .timeout(options.getCommandTimeout())
                .single();
    }

    @Override
    public Mono<DeviceCertificate> createDeviceCertificate(String issuer,
                                                           String appId,
                                                           String serial,
                                                           String publicKey) {
        return DeviceCommand.builder()
                .issuer(issuer)
                .appId(appId)
                .serial(serial)
                .publicKey(publicKey)
                .build()
                .execute(binaryExecutor)
                .timeout(options.getCommandTimeout())
                .single();
    }

    @Value
    @Builder(builderClassName = "Builder")
    public static class VersionImpl implements Version {
        private String major;
        private String minor;
        private String patch;
    }

    @Value
    @Builder(builderClassName = "Builder")
    public static class KeysImpl implements Keys {
        private String privateKey;
        private String publicKey;
    }

    @Value
    @Builder(builderClassName = "Builder")
    public static class AccessCertificateImpl implements AccessCertificate {
        private String accessCertificate;
        private LocalDateTime validityStartDate;
        private LocalDateTime validityEndDate;
    }

    @Value
    @Builder(builderClassName = "Builder")
    public static class DeviceCertificateImpl implements DeviceCertificate {
        private String deviceCertificate;
    }

    @Value
    @Builder(builderClassName = "Builder")
    public static class SignatureImpl implements Signature {
        private String signature;
    }

    @Value
    @Builder(builderClassName = "Builder")
    public static class HmacImpl implements Hmac {
        private String hmac;
    }
}