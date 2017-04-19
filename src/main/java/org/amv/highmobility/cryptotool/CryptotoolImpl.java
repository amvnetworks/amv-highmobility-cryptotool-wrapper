package org.amv.highmobility.cryptotool;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

@Slf4j
public class CryptotoolImpl implements Cryptotool {

    private final CryptotoolOptions options;

    public CryptotoolImpl(CryptotoolOptions options) throws IllegalArgumentException {
        this.options = requireNonNull(options, "`options` must not be null")
                .validOrThrow();
    }

    @Override
    public Mono<Version> version() throws IOException {
        return this.execute(Command.VERSION, stringStream -> {
            String version = stringStream
                    .filter(line -> line.startsWith("Cryptotool version"))
                    .map(line -> line.replace("Cryptotool version", ""))
                    .map(String::trim)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Cannot find version on stdout"));

            String[] majorMinorPatch = version.split("\\.");

            return VersionImpl.builder()
                    .major(majorMinorPatch.length > 0 ? majorMinorPatch[0] : "0")
                    .minor(majorMinorPatch.length > 1 ? majorMinorPatch[1] : "0")
                    .patch(majorMinorPatch.length > 2 ? majorMinorPatch[2] : "0")
                    .build();
        });
    }

    public Mono<Keys> generateKeys() throws IOException {
        return this.execute(Command.KEYS, stringStream -> {
            List<String> stdOutput = stringStream.collect(toList());

            String privateKeyPrefix = "PRIVATE: ";
            String privateKey = stdOutput.stream()
                    .filter(line -> line.startsWith(privateKeyPrefix))
                    .map(line -> line.replace(privateKeyPrefix, ""))
                    .map(String::trim)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Cannot find generated private key on stdout"));

            String publicKeyPrefix = "PUBLIC: ";
            String publicKey = stdOutput.stream()
                    .filter(line -> line.startsWith(publicKeyPrefix))
                    .map(line -> line.replace(publicKeyPrefix, ""))
                    .map(String::trim)
                    .findFirst()
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

        return this.execute(Command.CREATE_SIGNATURE, args, stringStream -> {
            String signPrefix = "SIGNATURE: ";
            String signature = stringStream
                    .filter(line -> line.startsWith(signPrefix))
                    .map(line -> line.replace(signPrefix, ""))
                    .map(String::trim)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Cannot find signature on stdout"));

            return SignatureImpl.builder()
                    .signature(signature)
                    .build();
        });
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

        return this.execute(Command.VERIFY_SIGNATURE, args, stringStream -> {
            String signPrefix = "VERIFY: ";
            Validity validity = stringStream
                    .filter(line -> line.startsWith(signPrefix))
                    .map(line -> line.replace(signPrefix, ""))
                    .map(String::trim)
                    .filter(val -> "FALSE".equals(val) || "CORRECT".equals(val))
                    .findFirst()
                    .map(val -> "CORRECT".equals(val) ? Validity.VALID : Validity.INVALID)
                    .orElseThrow(() -> new IllegalStateException("Cannot find signature validity on stdout"));

            return validity;
        });
    }

    @Override
    public Mono<Hmac> generateHmac(String message, String key) throws IOException {
        requireNonNull(message, "`message` must not be null");
        checkArgument(!isNullOrEmpty(key), "`key` must not be empty");

        List<String> args = ImmutableList.<String>builder()
                .add(message)
                .add(key)
                .build();

        return this.execute(Command.CREATE_HMAC, args, stringStream -> {
            String signPrefix = "HMAC: ";
            String hmac = stringStream
                    .filter(line -> line.startsWith(signPrefix))
                    .map(line -> line.replace(signPrefix, ""))
                    .map(String::trim)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Cannot find hmac on stdout"));

            return HmacImpl.builder()
                    .hmac(hmac)
                    .build();
        });
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

        return this.execute(Command.VERIFY_HMAC, args, stringStream -> {
            String signPrefix = "HMAC VERIFY: ";
            Validity validity = stringStream
                    .filter(line -> line.startsWith(signPrefix))
                    .map(line -> line.replace(signPrefix, ""))
                    .map(String::trim)
                    .filter(val -> "FALSE".equals(val) || "CORRECT".equals(val))
                    .findFirst()
                    .map(val -> "CORRECT".equals(val) ? Validity.VALID : Validity.INVALID)
                    .orElseThrow(() -> new IllegalStateException("Cannot find hmac validity on stdout"));

            return validity;
        });
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

        return this.execute(Command.CREATE_ACCESS_CERTIFICATE, args, stringStream -> {
            String accessCertPrefix = "ACCESS CERT: ";
            String accessCertificate = stringStream
                    .filter(line -> line.startsWith(accessCertPrefix))
                    .map(line -> line.replace(accessCertPrefix, ""))
                    .map(String::trim)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Cannot find access certificate on stdout"));

            return AccessCertificateImpl.builder()
                    .accessCertificate(accessCertificate)
                    .validityStartDate(startDate)
                    .validityEndDate(endDate)
                    .build();
        });
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

        return this.execute(Command.CREATE_DEVICE_CERTIFICATE, args, stringStream -> {
            String deviceCertPrefix = "DEVICE CERT: ";
            String deviceCertificate = stringStream
                    .filter(line -> line.startsWith(deviceCertPrefix))
                    .map(line -> line.replace(deviceCertPrefix, ""))
                    .map(String::trim)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Cannot find device certificate on stdout"));

            return DeviceCertificateImpl.builder()
                    .deviceCertificate(deviceCertificate)
                    .build();
        });
    }

    private <T> Mono<T> execute(Command command, Function<Stream<String>, T> transformer) throws IOException {
        return execute(command, Collections.emptyList(), transformer);
    }

    private <T> Mono<T> execute(Command command, List<String> args, Function<Stream<String>, T> transformer) throws IOException {
        requireNonNull(command);
        requireNonNull(args);
        requireNonNull(transformer);

        ImmutableList<String> commands = ImmutableList.<String>builder()
                .add(this.options.getPathToExecutable().getAbsolutePath())
                .add(command.getCommand())
                .addAll(args)
                .build();

        ProcessWrapper processWrapper = new ProcessWrapper(
                this.options.getWorkingDirectory(),
                commands,
                Collections.emptyMap());

        ProcessWrapper.ProcessResult processResult = processWrapper.execute();

        if (processResult.hasErrors()) {
            log.error("Found output on stderr: \n{}", processResult.getErrors());
        }

        Predicate<String> isNewLine = line -> "\n".equals(line) || System.lineSeparator().equals(line);
        Predicate<String> isEmptyLine = StringUtils::isBlank;

        Stream<String> stdInputStream = processResult.getInfos().stream()
                .filter(isEmptyLine.negate())
                .filter(isNewLine.negate());

        return Mono.fromCallable(() -> transformer.apply(stdInputStream));
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