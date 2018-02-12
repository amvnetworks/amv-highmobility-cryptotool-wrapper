package org.amv.highmobility.cryptotool.command;

import lombok.Builder;
import org.amv.highmobility.cryptotool.BinaryExecutor;
import org.amv.highmobility.cryptotool.Cryptotool;
import org.amv.highmobility.cryptotool.KeysImpl;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import reactor.core.publisher.Flux;

import java.util.List;

@Builder(builderClassName = "Builder")
public class KeysCommand implements Command<Cryptotool.Keys> {

    @Override
    public Flux<Cryptotool.Keys> execute(BinaryExecutor executor) {
        String privateKeyPrefix = "PRIVATE: ";
        String publicKeyPrefix = "PUBLIC: ";

        return executor.execute("keys")
                .map(processResult -> {
                    List<String> stdOutput = processResult.getStdoutLines();

                    String privateKey = CommandHelper.parseValueWithPrefix(privateKeyPrefix, stdOutput)
                            .orElseThrow(() -> new IllegalStateException("Cannot find private key on stdout",
                                    processResult.getException().orElse(null)));

                    String publicKey = CommandHelper.parseValueWithPrefix(publicKeyPrefix, stdOutput)
                            .orElseThrow(() -> new IllegalStateException("Cannot find public key on stdout",
                                    processResult.getException().orElse(null)));

                    try {
                        return KeysImpl.builder()
                                .privateKey(Hex.decodeHex(privateKey.toCharArray()))
                                .publicKey(Hex.decodeHex(publicKey.toCharArray()))
                                .build();
                    } catch (DecoderException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
