package org.amv.highmobility.cryptotool.command;

import lombok.Builder;
import lombok.Value;
import org.amv.highmobility.cryptotool.BinaryExecutor;
import org.amv.highmobility.cryptotool.Cryptotool;
import org.amv.highmobility.cryptotool.CryptotoolImpl;
import reactor.core.publisher.Flux;

import java.util.List;

import static java.util.Objects.requireNonNull;

@Builder(builderClassName = "Builder")
public class KeysCommand implements Command<Cryptotool.Keys> {

    @Override
    public Flux<Cryptotool.Keys> execute(BinaryExecutor executor) {
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
