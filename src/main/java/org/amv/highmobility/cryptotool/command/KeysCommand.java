package org.amv.highmobility.cryptotool.command;


import org.amv.highmobility.cryptotool.Cryptotool;
import org.amv.highmobility.cryptotool.CryptotoolImpl;
import reactor.core.publisher.Flux;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class KeysCommand implements Command<Cryptotool.Keys> {

    private final CommandExecutor executor;

    public KeysCommand(CommandExecutor executor) {
        this.executor = requireNonNull(executor);
    }

    @Override
    public Flux<Cryptotool.Keys> execute() {
        String privateKeyPrefix = "PRIVATE: ";
        String publicKeyPrefix = "PUBLIC: ";

        return executor.execute("keys")
                .map(process -> {
                    List<String> stdOutput = process.getCleanedOutput();

                    String privateKey = CommandHelper.parseValueWithPrefix(privateKeyPrefix, stdOutput)
                            .orElseThrow(() -> new IllegalStateException("Cannot find private key on stdout"));

                    String publicKey = CommandHelper.parseValueWithPrefix(publicKeyPrefix, stdOutput)
                            .orElseThrow(() -> new IllegalStateException("Cannot find public key on stdout"));

                    return CryptotoolImpl.KeysImpl.builder()
                            .privateKey(privateKey)
                            .publicKey(publicKey)
                            .build();
                });
    }
}
