package org.amv.highmobility.cryptotool.command;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.amv.highmobility.cryptotool.BinaryExecutor;
import org.amv.highmobility.cryptotool.Cryptotool;
import org.amv.highmobility.cryptotool.CryptotoolImpl;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.requireNonNull;
import static org.amv.highmobility.cryptotool.command.CommandHelper.parseValueWithPrefix;

@Builder(builderClassName = "Builder")
public class AccessCommand implements Command<Cryptotool.AccessCertificate> {
    private final String gainingSerial;
    private final String publicKey;
    private final String providingSerial;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final String permissions;

    public AccessCommand(String gainingSerial, String publicKey, String providingSerial, LocalDateTime startDate, LocalDateTime endDate, String permissions) {
        checkArgument(!isNullOrEmpty(gainingSerial), "`gainingSerial` must not be empty");
        checkArgument(!isNullOrEmpty(publicKey), "`publicKey` must not be empty");
        checkArgument(!isNullOrEmpty(providingSerial), "`providingSerial` must not be empty");
        requireNonNull(startDate, "`startDate` must not be null");
        requireNonNull(endDate, "`endDate` must not be null");
        checkArgument(startDate.isBefore(endDate), "`startDate` must not be after `endDate`");
        checkArgument(!isNullOrEmpty(permissions), "`permissions` must not be empty");

        this.gainingSerial = gainingSerial;
        this.publicKey = publicKey;
        this.providingSerial = providingSerial;
        this.startDate = startDate;
        this.endDate = endDate;
        this.permissions = permissions;
    }

    @Override
    public Flux<Cryptotool.AccessCertificate> execute(BinaryExecutor executor) {
        requireNonNull(executor);

        String startDateAsString = startDate.format(DateTimeFormatter.ofPattern("YYMMddHHmm"));
        String endDateAsString = endDate.format(DateTimeFormatter.ofPattern("YYMMddHHmm"));

        List<String> args = ImmutableList.<String>builder()
                .add("access")
                .add(gainingSerial)
                .add(publicKey)
                .add(providingSerial)
                .add(startDateAsString)
                .add(endDateAsString)
                .add(permissions)
                .build();

        String accessCertPrefix = "ACCESS CERT: ";
        return executor.execute(args)
                .map(processResult -> parseValueWithPrefix(accessCertPrefix, processResult.getCleanedOutput())
                        .orElseThrow(() -> new IllegalStateException("Cannot find access certificate on stdout",
                                processResult.getException().orElse(null))))
                .map(accessCertificate -> CryptotoolImpl.AccessCertificateImpl.builder()
                        .accessCertificate(accessCertificate)
                        .validityStartDate(startDate)
                        .validityEndDate(endDate)
                        .build());
    }
}
