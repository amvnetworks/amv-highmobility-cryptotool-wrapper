package org.amv.highmobility.cryptotool.command;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
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

@Value
@Builder(builderClassName = "Builder")
public class AccessCommand implements Command<Cryptotool.AccessCertificate> {
    private final String gainingSerial;
    private final String publicKey;
    private final String providingSerial;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public AccessCommand(String gainingSerial, String publicKey, String providingSerial, LocalDateTime startDate, LocalDateTime endDate) {
        checkArgument(!isNullOrEmpty(gainingSerial), "`gainingSerial` must not be empty");
        checkArgument(!isNullOrEmpty(publicKey), "`publicKey` must not be empty");
        checkArgument(!isNullOrEmpty(providingSerial), "`providingSerial` must not be empty");
        requireNonNull(startDate, "`startDate` must not be null");
        requireNonNull(endDate, "`endDate` must not be null");
        checkArgument(startDate.isBefore(endDate), "`startDate` must not be after `endDate`");

        this.gainingSerial = gainingSerial;
        this.publicKey = publicKey;
        this.providingSerial = providingSerial;
        this.startDate = startDate;
        this.endDate = endDate;
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
                .build();

        String accessCertPrefix = "ACCESS CERT: ";
        return executor.execute(args)
                .map(result -> CommandHelper.parseValueWithPrefix(accessCertPrefix, result.getCleanedOutput())
                        .orElseThrow(() -> new IllegalStateException("Cannot find access certificate on stdout")))
                .map(accessCertificate -> CryptotoolImpl.AccessCertificateImpl.builder()
                        .accessCertificate(accessCertificate)
                        .validityStartDate(startDate)
                        .validityEndDate(endDate)
                        .build());
    }
}