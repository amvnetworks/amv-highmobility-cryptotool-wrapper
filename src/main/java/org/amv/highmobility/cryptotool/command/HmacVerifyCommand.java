package org.amv.highmobility.cryptotool.command;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Value;
import org.amv.highmobility.cryptotool.BinaryExecutor;
import org.amv.highmobility.cryptotool.Cryptotool;
import reactor.core.publisher.Flux;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.requireNonNull;

@Value
@Builder(builderClassName = "Builder")
public class HmacVerifyCommand implements Command<Cryptotool.Validity> {

    private final String message;
    private final String key;
    private final String hmac;

    public HmacVerifyCommand(String message, String key, String hmac) {
        requireNonNull(message, "`message` must not be null");
        checkArgument(!isNullOrEmpty(key), "`key` must not be empty");
        checkArgument(!isNullOrEmpty(hmac), "`hmac` must not be empty");

        this.message = message;
        this.key = key;
        this.hmac = hmac;
    }

    @Override
    public Flux<Cryptotool.Validity> execute(BinaryExecutor executor) {
        List<String> args = ImmutableList.<String>builder()
                .add("hmacver")
                .add(message)
                .add(key)
                .add(hmac)
                .build();

        String signPrefix = "HMAC VERIFY: ";
        return executor.execute(args)
                .map(result -> result.getCleanedOutput().stream()
                        .filter(line -> line.startsWith(signPrefix))
                        .map(line -> line.replace(signPrefix, ""))
                        .map(String::trim)
                        .filter(val -> "FALSE".equals(val) || "CORRECT".equals(val))
                        .findFirst()
                        .map(val -> "CORRECT".equals(val) ? Cryptotool.Validity.VALID : Cryptotool.Validity.INVALID)
                        .orElseThrow(() -> new IllegalStateException("Cannot find hmac validity on stdout")));
    }
}
