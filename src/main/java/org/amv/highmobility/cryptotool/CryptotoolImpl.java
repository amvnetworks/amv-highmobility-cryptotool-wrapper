package org.amv.highmobility.cryptotool;

import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.amv.highmobility.cryptotool.command.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.requireNonNull;

@Slf4j
public class CryptotoolImpl implements Cryptotool {

    private final CryptotoolOptions options;
    private final CommandExecutor commandExecutor;

    public CryptotoolImpl(CryptotoolOptions options) throws IllegalArgumentException {
        this.options = requireNonNull(options, "`options` must not be null")
                .validOrThrow();
        this.commandExecutor = new CommandExecutor(options);
    }

    @Override
    public Mono<Version> version() {
        return new VersionCommand(commandExecutor).execute()
                .single();
    }

    public Mono<Keys> generateKeys() {
        return new KeysCommand(commandExecutor).execute()
                .single();
    }

    @Override
    public Mono<Signature> generateSignature(String message, String privateKey) {
        requireNonNull(message, "`message` must not be null");
        checkArgument(!isNullOrEmpty(privateKey), "`privateKey` must not be empty");

        return new SignCommand(commandExecutor, message, privateKey).execute()
                .single();
    }

    @Override
    public Mono<Validity> verifySignature(String message, String signature, String publicKey) {
        requireNonNull(message, "`message` must not be null");
        checkArgument(!isNullOrEmpty(signature), "`signature` must not be empty");
        checkArgument(!isNullOrEmpty(publicKey), "`publicKey` must not be empty");

        return new VerifyCommand(commandExecutor, message, signature, publicKey).execute()
                .single();
    }

    @Override
    public Mono<Hmac> generateHmac(String message, String key) {
        requireNonNull(message, "`message` must not be null");
        checkArgument(!isNullOrEmpty(key), "`key` must not be empty");

        return new HmacCommand(commandExecutor, message, key).execute()
                .single();
    }

    @Override
    public Mono<Validity> verifyHmac(String message, String key, String hmac) {
        requireNonNull(message, "`message` must not be null");
        checkArgument(!isNullOrEmpty(key), "`key` must not be empty");
        checkArgument(!isNullOrEmpty(hmac), "`hmac` must not be empty");

        return new HmacVerifyCommand(commandExecutor, message, key, hmac).execute()
                .single();
    }

    @Override
    public Mono<AccessCertificate> createAccessCertificate(String gainingSerial,
                                                           String publicKey,
                                                           String providingSerial,
                                                           LocalDateTime startDate,
                                                           LocalDateTime endDate,
                                                           Collection<String> permissions) {
        checkArgument(!isNullOrEmpty(gainingSerial), "`gainingSerial` must not be empty");
        checkArgument(!isNullOrEmpty(publicKey), "`publicKey` must not be empty");
        checkArgument(!isNullOrEmpty(providingSerial), "`providingSerial` must not be empty");
        requireNonNull(startDate, "`startDate` must not be null");
        requireNonNull(endDate, "`endDate` must not be null");
        checkArgument(startDate.isBefore(endDate), "`startDate` must not be after `endDate`");

        return new AccessCommand(commandExecutor, gainingSerial, publicKey, providingSerial, startDate, endDate)
                .execute()
                .single();
    }

    @Override
    public Mono<DeviceCertificate> createDeviceCertificate(String issuer,
                                                           String appId,
                                                           String serial,
                                                           String publicKey) {
        checkArgument(!isNullOrEmpty(issuer), "`issuer` must not be empty");
        checkArgument(!isNullOrEmpty(appId), "`appId` must not be empty");
        checkArgument(!isNullOrEmpty(serial), "`serial` must not be empty");
        checkArgument(!isNullOrEmpty(publicKey), "`publicKey` must not be empty");

        return new DeviceCommand(commandExecutor, issuer, appId, serial, publicKey).execute()
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