package org.amv.highmobility.cryptotool.command;


import com.google.common.collect.ImmutableList;
import org.amv.highmobility.cryptotool.Cryptotool;
import reactor.core.publisher.Flux;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.requireNonNull;

public class HmacVerifyCommand implements Command<Cryptotool.Validity> {

    private final CommandExecutor executor;
    private final String message;
    private final String key;
    private final String hmac;

    public HmacVerifyCommand(CommandExecutor executor, String message, String key, String hmac) {
        checkArgument(!isNullOrEmpty(key), "`signature` must not be empty");
        checkArgument(!isNullOrEmpty(hmac), "`publicKey` must not be empty");

        this.executor = requireNonNull(executor);
        this.message = requireNonNull(message, "`message` must not be null");
        this.key = requireNonNull(key, "`key` must not be null");
        this.hmac = requireNonNull(hmac, "`hmac` must not be null");
    }

    @Override
    public Flux<Cryptotool.Validity> execute() {
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
