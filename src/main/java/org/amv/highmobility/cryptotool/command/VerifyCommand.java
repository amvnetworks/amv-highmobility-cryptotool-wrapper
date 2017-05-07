package org.amv.highmobility.cryptotool.command;


import com.google.common.collect.ImmutableList;
import org.amv.highmobility.cryptotool.Cryptotool;
import reactor.core.publisher.Flux;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.requireNonNull;

public class VerifyCommand implements Command<Cryptotool.Validity> {

    private final CommandExecutor executor;
    private final String message;
    private final String signature;
    private final String publicKey;

    public VerifyCommand(CommandExecutor executor, String message, String signature, String publicKey) {
        checkArgument(!isNullOrEmpty(signature), "`signature` must not be empty");
        checkArgument(!isNullOrEmpty(publicKey), "`publicKey` must not be empty");

        this.executor = requireNonNull(executor);
        this.message = requireNonNull(message, "`message` must not be null");
        this.signature = requireNonNull(signature, "`signature` must not be null");
        this.publicKey = requireNonNull(publicKey, "`publicKey` must not be null");
    }

    @Override
    public Flux<Cryptotool.Validity> execute() {

        List<String> args = ImmutableList.<String>builder()
                .add("verify")
                .add(message)
                .add(signature)
                .add(publicKey)
                .build();

        String signPrefix = "VERIFY: ";
        return executor.execute(args)
                .map(processResult -> processResult.getCleanedOutput().stream()
                        .filter(line -> line.startsWith(signPrefix))
                        .map(line -> line.replace(signPrefix, ""))
                        .map(String::trim)
                        .filter(val -> "FALSE".equals(val) || "CORRECT".equals(val))
                        .findFirst()
                        .map(val -> "CORRECT".equals(val) ? Cryptotool.Validity.VALID : Cryptotool.Validity.INVALID)
                        .orElseThrow(() -> new IllegalStateException("Cannot find signature validity on stdout")));
    }
}
