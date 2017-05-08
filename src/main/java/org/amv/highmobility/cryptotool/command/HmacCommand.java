package org.amv.highmobility.cryptotool.command;


import com.google.common.collect.ImmutableList;
import org.amv.highmobility.cryptotool.BinaryExecutor;
import org.amv.highmobility.cryptotool.Cryptotool;
import org.amv.highmobility.cryptotool.CryptotoolImpl;
import reactor.core.publisher.Flux;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.requireNonNull;

public class HmacCommand implements Command<Cryptotool.Hmac> {

    private final BinaryExecutor executor;
    private final String message;
    private final String key;

    public HmacCommand(BinaryExecutor executor, String message, String key) {
        checkArgument(!isNullOrEmpty(key), "`key` must not be empty");

        this.executor = requireNonNull(executor);
        this.message = requireNonNull(message, "`message` must not be null");
        this.key = requireNonNull(key, "`key` must not be null");
    }

    @Override
    public Flux<Cryptotool.Hmac> execute() {

        List<String> args = ImmutableList.<String>builder()
                .add("hmac")
                .add(message)
                .add(key)
                .build();

        String signPrefix = "HMAC: ";
        return executor.execute(args)
                .map(result -> CommandHelper.parseValueWithPrefix(signPrefix, result.getCleanedOutput())
                        .orElseThrow(() -> new IllegalStateException("Cannot find hmac on stdout")))
                .map(hmac -> CryptotoolImpl.HmacImpl.builder()
                        .hmac(hmac)
                        .build());
    }
}
