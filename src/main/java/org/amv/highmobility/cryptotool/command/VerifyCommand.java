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

@Builder(builderClassName = "Builder")
public class VerifyCommand implements Command<Cryptotool.Validity> {

    private final String message;
    private final String signature;
    private final String publicKey;

    public VerifyCommand(String message, String signature, String publicKey) {
        requireNonNull(message, "`message` must not be null");
        checkArgument(!isNullOrEmpty(signature), "`signature` must not be empty");
        checkArgument(!isNullOrEmpty(publicKey), "`publicKey` must not be empty");

        this.message = message;
        this.signature = signature;
        this.publicKey = publicKey;
    }

    @Override
    public Flux<Cryptotool.Validity> execute(BinaryExecutor executor) {

        List<String> args = ImmutableList.<String>builder()
                .add("verify")
                .add(message)
                .add(signature)
                .add(publicKey)
                .build();

        String signPrefix = "VERIFY: ";
        return executor.execute(args)
                .map(process -> process.getCleanedOutput().stream()
                        .filter(line -> line.startsWith(signPrefix))
                        .map(line -> line.replace(signPrefix, ""))
                        .map(String::trim)
                        .filter(val -> "FALSE".equals(val) || "CORRECT".equals(val))
                        .findFirst()
                        .map(val -> "CORRECT".equals(val) ? Cryptotool.Validity.VALID : Cryptotool.Validity.INVALID)
                        .orElseThrow(() -> new IllegalStateException("Cannot find signature validity on stdout",
                                process.getException().orElse(null))));
    }
}
