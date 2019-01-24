package org.amv.highmobility.cryptotool.command;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import org.amv.highmobility.cryptotool.BinaryExecutor;
import org.amv.highmobility.cryptotool.Cryptotool;
import org.amv.highmobility.cryptotool.CryptotoolImpl;
import org.amv.highmobility.cryptotool.CryptotoolUtils;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.requireNonNull;
import static org.amv.highmobility.cryptotool.command.CommandHelper.parseValueWithPrefix;

@Builder(builderClassName = "Builder")
public class AccessCommand implements Command<Cryptotool.AccessCertificate> {
    private final String gainingSerial;
    private final String gainingPublicKey;
    private final String providingSerial;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final String permissions;

    public AccessCommand(String gainingSerial, String gainingPublicKey, String providingSerial, LocalDateTime startDate, LocalDateTime endDate, String permissions) {
        checkArgument(!isNullOrEmpty(gainingSerial), "`gainingSerial` must not be empty");
        checkArgument(!isNullOrEmpty(gainingPublicKey), "`gainingPublicKey` must not be empty");
        checkArgument(!isNullOrEmpty(providingSerial), "`providingSerial` must not be empty");
        requireNonNull(startDate, "`startDate` must not be null");
        requireNonNull(endDate, "`endDate` must not be null");
        checkArgument(startDate.isBefore(endDate), "`startDate` must not be after `endDate`");
        checkArgument(!isNullOrEmpty(permissions), "`permissions` must not be empty");

        this.gainingSerial = gainingSerial;
        this.gainingPublicKey = gainingPublicKey;
        this.providingSerial = providingSerial;
        this.startDate = startDate;
        this.endDate = endDate;
        this.permissions = permissions;
    }

    @Override
    public Flux<Cryptotool.AccessCertificate> execute(BinaryExecutor executor) {
        requireNonNull(executor);

        String startDateAsString = CryptotoolUtils.encodeAsHex(startDate);
        String endDateAsString = CryptotoolUtils.encodeAsHex(endDate);

        List<String> args = ImmutableList.<String>builder()
                .add("access")
                .add(providingSerial)
                .add(gainingSerial)
                .add(gainingPublicKey)
                .add(startDateAsString)
                .add(endDateAsString)
                .add(permissions)
                .add("-ac0")
                .build();

        String accessCertPrefix = "ACCESS CERT: ";
        return executor.execute(args)
                .map(processResult -> parseValueWithPrefix(accessCertPrefix, processResult.getStdoutLines())
                        .orElseThrow(() -> new IllegalStateException("Cannot find access certificate on stdout",
                                processResult.getException().orElse(null))))
                .map(accessCertificate -> CryptotoolImpl.AccessCertificateImpl.builder()
                        .accessCertificate(accessCertificate)
                        .validityStartDate(startDate)
                        .validityEndDate(endDate)
                        .build());
    }

}
