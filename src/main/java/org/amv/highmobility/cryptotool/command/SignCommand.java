package org.amv.highmobility.cryptotool.command;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Value;
import org.amv.highmobility.cryptotool.BinaryExecutor;
import org.amv.highmobility.cryptotool.Cryptotool;
import org.amv.highmobility.cryptotool.CryptotoolImpl;
import reactor.core.publisher.Flux;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.requireNonNull;
import static org.amv.highmobility.cryptotool.command.CommandHelper.parseValueWithPrefix;

@Builder(builderClassName = "Builder")
public class SignCommand implements Command<Cryptotool.Signature> {

    private final String message;
    private final String privateKey;

    public SignCommand(String message, String privateKey) {
        requireNonNull(message, "`message` must not be null");
        checkArgument(!isNullOrEmpty(privateKey), "`privateKey` must not be empty");

        this.message = message;
        this.privateKey = privateKey;
    }

    @Override
    public Flux<Cryptotool.Signature> execute(BinaryExecutor executor) {
        List<String> args = ImmutableList.<String>builder()
                .add("sign")
                .add(message)
                .add(privateKey)
                .build();

        String signPrefix = "SIGNATURE: ";
        return executor.execute(args)
                .map(processResult -> parseValueWithPrefix(signPrefix, processResult.getCleanedOutput())
                        .orElseThrow(() -> new IllegalStateException("Cannot find signature on stdout",
                                processResult.getException().orElse(null))))
                .map(signature -> CryptotoolImpl.SignatureImpl.builder()
                        .signature(signature)
                        .build());
    }
}
