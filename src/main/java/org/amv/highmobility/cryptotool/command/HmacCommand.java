package org.amv.highmobility.cryptotool.command;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.amv.highmobility.cryptotool.BinaryExecutor;
import org.amv.highmobility.cryptotool.Cryptotool;
import org.amv.highmobility.cryptotool.CryptotoolImpl;
import reactor.core.publisher.Flux;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.requireNonNull;
import static org.amv.highmobility.cryptotool.command.CommandHelper.*;

@Builder(builderClassName = "Builder")
public class HmacCommand implements Command<Cryptotool.Hmac> {

    private final String message;
    private final String key;

    public HmacCommand(String message, String key) {
        requireNonNull(message, "`message` must not be null");
        checkArgument(!isNullOrEmpty(key), "`key` must not be empty");

        this.message = message;
        this.key = key;
    }

    @Override
    public Flux<Cryptotool.Hmac> execute(BinaryExecutor executor) {
        requireNonNull(executor);

        List<String> args = ImmutableList.<String>builder()
                .add("hmac")
                .add(message)
                .add(key)
                .build();

        String signPrefix = "HMAC: ";
        return executor.execute(args)
                .map(processResult -> parseValueWithPrefix(signPrefix, processResult.getCleanedOutput())
                        .orElseThrow(() -> new IllegalStateException("Cannot find hmac on stdout",
                                processResult.getException().orElse(null))))
                .map(hmac -> CryptotoolImpl.HmacImpl.builder()
                        .hmac(hmac)
                        .build());
    }
}
