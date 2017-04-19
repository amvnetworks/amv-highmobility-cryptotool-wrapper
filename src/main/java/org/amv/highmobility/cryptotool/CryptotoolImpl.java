package org.amv.highmobility.cryptotool;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.requireNonNull;

@Slf4j
public class CryptotoolImpl implements Cryptotool {

    private final CryptotoolOptions options;

    public CryptotoolImpl(CryptotoolOptions options) throws IllegalArgumentException {
        this.options = requireNonNull(options, "`options` must not be null")
                .validOrThrow();
    }

    @Override
    public Mono<Version> version() throws IOException {
        String versionPrefix = "Cryptotool version";
        return this.execute(Command.VERSION)
                .map(processResult -> parseValueWithPrefix(versionPrefix, processResult)
                        .orElseThrow(() -> new IllegalStateException("Cannot find version on stdout")))
                .map(version -> {
                    String[] majorMinorPatch = version.split("\\.");
                    return VersionImpl.builder()
                            .major(majorMinorPatch.length > 0 ? majorMinorPatch[0] : "0")
                            .minor(majorMinorPatch.length > 1 ? majorMinorPatch[1] : "0")
                            .patch(majorMinorPatch.length > 2 ? majorMinorPatch[2] : "0")
                            .build();
                });
    }

    public Mono<Keys> generateKeys() throws IOException {
        String privateKeyPrefix = "PRIVATE: ";
        String publicKeyPrefix = "PUBLIC: ";

        return this.execute(Command.KEYS)
                .map(process -> {
                    List<String> stdOutput = process.getCleanedOutput();

                    String privateKey = parseValueWithPrefix(privateKeyPrefix, stdOutput)
                            .orElseThrow(() -> new IllegalStateException("Cannot find generated private key on stdout"));

                    String publicKey = parseValueWithPrefix(publicKeyPrefix, stdOutput)
                            .orElseThrow(() -> new IllegalStateException("Cannot find generated public key on stdout"));

                    return KeysImpl.builder()
                            .privateKey(privateKey)
                            .publicKey(publicKey)
                            .build();
                });
    }

    @Override
    public Mono<Signature> generateSignature(String message, String privateKey) throws IOException {
        requireNonNull(message, "`message` must not be null");
        checkArgument(!isNullOrEmpty(privateKey), "`privateKey` must not be empty");

        List<String> args = ImmutableList.<String>builder()
                .add(message)
                .add(privateKey)
                .build();

        String signPrefix = "SIGNATURE: ";
        return this.execute(Command.CREATE_SIGNATURE, args)
                .map(processResult -> parseValueWithPrefix(signPrefix, processResult)
                        .orElseThrow(() -> new IllegalStateException("Cannot find signature on stdout")))
                .map(signature -> SignatureImpl.builder()
                        .signature(signature)
                        .build());
    }

    @Override
    public Mono<Validity> verifySignature(String message, String signature, String publicKey) throws IOException {
        requireNonNull(message, "`message` must not be null");
        checkArgument(!isNullOrEmpty(signature), "`signature` must not be empty");
        checkArgument(!isNullOrEmpty(publicKey), "`publicKey` must not be empty");

        List<String> args = ImmutableList.<String>builder()
                .add(message)
                .add(signature)
                .add(publicKey)
                .build();

        String signPrefix = "VERIFY: ";
        return this.execute(Command.VERIFY_SIGNATURE, args)
                .map(processResult -> processResult.getCleanedOutput().stream()
                        .filter(line -> line.startsWith(signPrefix))
                        .map(line -> line.replace(signPrefix, ""))
                        .map(String::trim)
                        .filter(val -> "FALSE".equals(val) || "CORRECT".equals(val))
                        .findFirst()
                        .map(val -> "CORRECT".equals(val) ? Validity.VALID : Validity.INVALID)
                        .orElseThrow(() -> new IllegalStateException("Cannot find signature validity on stdout")));
    }

    @Override
    public Mono<Hmac> generateHmac(String message, String key) throws IOException {
        requireNonNull(message, "`message` must not be null");
        checkArgument(!isNullOrEmpty(key), "`key` must not be empty");

        List<String> args = ImmutableList.<String>builder()
                .add(message)
                .add(key)
                .build();

        String signPrefix = "HMAC: ";
        return this.execute(Command.CREATE_HMAC, args)
                .map(result -> parseValueWithPrefix(signPrefix, result)
                        .orElseThrow(() -> new IllegalStateException("Cannot find hmac on stdout")))
                .map(hmac -> HmacImpl.builder()
                        .hmac(hmac)
                        .build());
    }

    @Override
    public Mono<Validity> verifyHmac(String message, String key, String hmac) throws IOException {
        requireNonNull(message, "`message` must not be null");
        checkArgument(!isNullOrEmpty(key), "`key` must not be empty");
        checkArgument(!isNullOrEmpty(hmac), "`hmac` must not be empty");

        List<String> args = ImmutableList.<String>builder()
                .add(message)
                .add(key)
                .add(hmac)
                .build();

        String signPrefix = "HMAC VERIFY: ";
        return this.execute(Command.VERIFY_HMAC, args)
                .map(result -> result.getCleanedOutput().stream()
                        .filter(line -> line.startsWith(signPrefix))
                        .map(line -> line.replace(signPrefix, ""))
                        .map(String::trim)
                        .filter(val -> "FALSE".equals(val) || "CORRECT".equals(val))
                        .findFirst()
                        .map(val -> "CORRECT".equals(val) ? Validity.VALID : Validity.INVALID)
                        .orElseThrow(() -> new IllegalStateException("Cannot find hmac validity on stdout")));
    }

    @Override
    public Mono<AccessCertificate> createAccessCertificate(String gainingSerial, String publicKey, String providingSerial, LocalDateTime startDate, LocalDateTime endDate, Collection<String> permissions) throws IOException {
        checkArgument(!isNullOrEmpty(gainingSerial), "`gainingSerial` must not be empty");
        checkArgument(!isNullOrEmpty(publicKey), "`publicKey` must not be empty");
        checkArgument(!isNullOrEmpty(providingSerial), "`providingSerial` must not be empty");
        requireNonNull(startDate, "`startDate` must not be null");
        requireNonNull(endDate, "`endDate` must not be null");
        checkArgument(startDate.isBefore(endDate), "`startDate` must not be after endDate");

        String startDateAsString = startDate.format(DateTimeFormatter.ofPattern("YYMMddHHmm"));
        String endDateAsString = endDate.format(DateTimeFormatter.ofPattern("YYMMddHHmm"));

        List<String> args = ImmutableList.<String>builder()
                .add(gainingSerial)
                .add(publicKey)
                .add(providingSerial)
                .add(startDateAsString)
                .add(endDateAsString)
                .build();

        String accessCertPrefix = "ACCESS CERT: ";
        return this.execute(Command.CREATE_ACCESS_CERTIFICATE, args)
                .map(result -> parseValueWithPrefix(accessCertPrefix, result)
                        .orElseThrow(() -> new IllegalStateException("Cannot find access certificate on stdout")))
                .map(accessCertificate -> AccessCertificateImpl.builder()
                        .accessCertificate(accessCertificate)
                        .validityStartDate(startDate)
                        .validityEndDate(endDate)
                        .build());
    }

    @Override
    public Mono<DeviceCertificate> createDeviceCertificate(String issuer, String appId, String serial, String publicKey) throws IOException {
        checkArgument(!isNullOrEmpty(issuer), "`issuer` must not be empty");
        checkArgument(!isNullOrEmpty(appId), "`appId` must not be empty");
        checkArgument(!isNullOrEmpty(serial), "`serial` must not be empty");
        checkArgument(!isNullOrEmpty(publicKey), "`publicKey` must not be empty");

        List<String> args = ImmutableList.<String>builder()
                .add(issuer)
                .add(appId)
                .add(serial)
                .add(publicKey)
                .build();

        String deviceCertPrefix = "DEVICE CERT: ";
        return this.execute(Command.CREATE_DEVICE_CERTIFICATE, args)
                .map(result -> parseValueWithPrefix(deviceCertPrefix, result)
                        .orElseThrow(() -> new IllegalStateException("Cannot find device certificate on stdout")))
                .map(deviceCertificate -> DeviceCertificateImpl.builder()
                        .deviceCertificate(deviceCertificate)
                        .build());
    }

    private Optional<String> parseValueWithPrefix(String prefix, ProcessWrapper.ProcessResult processResult) {
        return parseValueWithPrefix(prefix, processResult.getCleanedOutput());
    }

    private Optional<String> parseValueWithPrefix(String prefix, List<String> output) {
        return output.stream()
                .filter(line -> line.startsWith(prefix))
                .map(line -> line.replace(prefix, ""))
                .map(String::trim)
                .findFirst();
    }

    private Mono<ProcessWrapper.ProcessResult> execute(Command command) throws IOException {
        return execute(command, Collections.emptyList());
    }

    private Mono<ProcessWrapper.ProcessResult> execute(Command command, List<String> args) throws IOException {
        requireNonNull(command);
        requireNonNull(args);

        ImmutableList<String> commands = ImmutableList.<String>builder()
                .add(this.options.getPathToExecutable().getAbsolutePath())
                .add(command.getCommand())
                .addAll(args)
                .build();

        ProcessWrapper processWrapper = new ProcessWrapper(
                this.options.getWorkingDirectory(),
                commands,
                Collections.emptyMap());

        return processWrapper.execute()
                .doOnNext(processResult -> {
                    if (processResult.hasErrors() && log.isWarnEnabled()) {
                        log.warn("Found output on stderr: \n{}", processResult.getErrors());
                    }
                });
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