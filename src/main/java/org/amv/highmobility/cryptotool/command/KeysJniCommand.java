package org.amv.highmobility.cryptotool.command;

import com.highmobility.crypto.Crypto;
import com.highmobility.crypto.HMKeyPair;
import lombok.Builder;
import lombok.Value;
import org.amv.highmobility.cryptotool.BinaryExecutor;
import org.amv.highmobility.cryptotool.Cryptotool;
import org.amv.highmobility.cryptotool.KeysImpl;
import reactor.core.publisher.Flux;

@Value
@Builder(builderClassName = "Builder")
public class KeysJniCommand implements Command<Cryptotool.Keys> {

    @Override
    public Flux<Cryptotool.Keys> execute(BinaryExecutor executor) {
        return Flux.just(1)
                .map(foo -> {
                    HMKeyPair keypair = Crypto.createKeypair();
                    return KeysImpl.builder()
                            .publicKey(keypair.getPublicKey())
                            .privateKey(keypair.getPrivateKey())
                            .build();
                });
    }
}
