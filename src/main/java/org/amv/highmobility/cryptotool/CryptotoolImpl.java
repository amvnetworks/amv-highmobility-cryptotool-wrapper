package org.amv.highmobility.cryptotool;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.amv.highmobility.cryptotool.command.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static com.google.common.base.Preconditions.checkArgument;
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

    @Override
    public Mono<AccessCertificate> createAccessCertificate(int version,
                                                           String issuer,
                                                           String providingSerial,
                                                           String gainingSerial,
                                                           String gainingPublicKey,
                                                           LocalDateTime startDate,
                                                           LocalDateTime endDate,
                                                           String permissions) {
        checkArgument(version == 0 || version == 1, "`version` has invalid value");

        Command<Cryptotool.AccessCertificate> command;
        if (version == 0) {
            command = createAccessCommandV0(providingSerial, gainingSerial, gainingPublicKey, startDate, endDate, permissions);
        } else {
            command = createAccessCommandV1(issuer, providingSerial, gainingSerial, gainingPublicKey, startDate, endDate, permissions);
        }

        return command
                .execute(binaryExecutor)
                .timeout(options.getCommandTimeout())
                .single();
    }


    private Command<Cryptotool.AccessCertificate> createAccessCommandV0(String providingSerial,
                                                                        String gainingSerial,
                                                                        String gainingPublicKey,
                                                                        LocalDateTime startDate,
                                                                        LocalDateTime endDate,
                                                                        String permissions) {
        return AccessCommandV0.builder()
                .providingSerial(providingSerial)
                .gainingSerial(gainingSerial)
                .gainingPublicKey(gainingPublicKey)
                .startDate(startDate)
                .endDate(endDate)
                .permissions(permissions)
                .build();
    }

    private Command<Cryptotool.AccessCertificate> createAccessCommandV1(String issuer,
                                                                        String providingSerial,
                                                                        String gainingSerial,
                                                                        String gainingPublicKey,
                                                                        LocalDateTime startDate,
                                                                        LocalDateTime endDate,
                                                                        String permissions) {
        return AccessCommandV1.builder()
                .issuer(issuer)
                .providingSerial(providingSerial)
                .gainingSerial(gainingSerial)
                .gainingPublicKey(gainingPublicKey)
                .startDate(startDate)
                .endDate(endDate)
                .permissions(permissions)
                .build();
    }

    @Getter
    @Builder(builderClassName = "Builder")
    public static class VersionImpl implements Version {
        private String major;
        private String minor;
        private String patch;
    }

    @Getter
    @Builder(builderClassName = "Builder")
    public static class KeysImpl implements Keys {
        private String privateKey;
        private String publicKey;
    }

    @Getter
    @Builder(builderClassName = "Builder")
    public static class AccessCertificateImpl implements AccessCertificate {
        private String accessCertificate;
        private LocalDateTime validityStartDate;
        private LocalDateTime validityEndDate;
    }

    @Getter
    @Builder(builderClassName = "Builder")
    public static class DeviceCertificateImpl implements DeviceCertificate {
        private String deviceCertificate;
    }

    @Getter
    @Builder(builderClassName = "Builder")
    public static class SignatureImpl implements Signature {
        private String signature;
    }

    @Getter
    @Builder(builderClassName = "Builder")
    public static class HmacImpl implements Hmac {
        private String hmac;
    }

}