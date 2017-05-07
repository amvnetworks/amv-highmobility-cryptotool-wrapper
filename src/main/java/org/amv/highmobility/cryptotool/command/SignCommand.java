package org.amv.highmobility.cryptotool.command;


import com.google.common.collect.ImmutableList;
import org.amv.highmobility.cryptotool.Cryptotool;
import org.amv.highmobility.cryptotool.CryptotoolImpl;
import reactor.core.publisher.Flux;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.requireNonNull;

public class SignCommand implements Command<Cryptotool.Signature> {

    private final CommandExecutor executor;
    private final String message;
    private final String privateKey;

    public SignCommand(CommandExecutor executor, String message, String privateKey) {
        checkArgument(!isNullOrEmpty(privateKey), "`privateKey` must not be empty");

        this.executor = requireNonNull(executor);
        this.message = requireNonNull(message, "`message` must not be null");
        this.privateKey = requireNonNull(privateKey, "`privateKey` must not be null");
    }

    @Override
    public Flux<Cryptotool.Signature> execute() {
        List<String> args = ImmutableList.<String>builder()
                .add("sign")
                .add(message)
                .add(privateKey)
                .build();

        String signPrefix = "SIGNATURE: ";
        return executor.execute(args)
                .map(processResult -> CommandHelper.parseValueWithPrefix(signPrefix, processResult.getCleanedOutput())
                        .orElseThrow(() -> new IllegalStateException("Cannot find signature on stdout")))
                .map(signature -> CryptotoolImpl.SignatureImpl.builder()
                        .signature(signature)
                        .build());
    }
}
