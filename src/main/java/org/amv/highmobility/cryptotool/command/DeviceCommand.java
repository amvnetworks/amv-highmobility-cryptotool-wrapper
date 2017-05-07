package org.amv.highmobility.cryptotool.command;


import com.google.common.collect.ImmutableList;
import org.amv.highmobility.cryptotool.Cryptotool;
import org.amv.highmobility.cryptotool.CryptotoolImpl;
import reactor.core.publisher.Flux;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.requireNonNull;

public class DeviceCommand implements Command<Cryptotool.DeviceCertificate> {

    private final CommandExecutor executor;
    private final String issuer;
    private final String appId;
    private final String serial;
    private final String publicKey;

    public DeviceCommand(CommandExecutor executor, String issuer, String appId, String serial, String publicKey) {
        checkArgument(!isNullOrEmpty(issuer), "`issuer` must not be empty");
        checkArgument(!isNullOrEmpty(appId), "`appId` must not be empty");
        checkArgument(!isNullOrEmpty(serial), "`serial` must not be empty");
        checkArgument(!isNullOrEmpty(publicKey), "`publicKey` must not be empty");

        this.executor = requireNonNull(executor);
        this.issuer = issuer;
        this.appId = appId;
        this.serial = serial;
        this.publicKey = publicKey;
    }

    @Override
    public Flux<Cryptotool.DeviceCertificate> execute() {
        List<String> args = ImmutableList.<String>builder()
                .add("device")
                .add(issuer)
                .add(appId)
                .add(serial)
                .add(publicKey)
                .build();

        String deviceCertPrefix = "DEVICE CERT: ";
        return executor.execute(args)
                .map(result -> CommandHelper.parseValueWithPrefix(deviceCertPrefix, result.getCleanedOutput())
                        .orElseThrow(() -> new IllegalStateException("Cannot find device certificate on stdout")))
                .map(deviceCertificate -> CryptotoolImpl.DeviceCertificateImpl.builder()
                        .deviceCertificate(deviceCertificate)
                        .build());
    }
}
