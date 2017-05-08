package org.amv.highmobility.cryptotool;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import static java.util.Objects.requireNonNull;

@Getter
@Builder(builderClassName = "Builder")
public class CryptotoolOptionsImpl implements CryptotoolOptions {

    public static CryptotoolOptionsImpl createDefault() {
        return CryptotoolOptionsImpl.builder()
                .binaryExecutor(BinaryExecutorImpl.createDefault())
                .build();
    }

    private final BinaryExecutor binaryExecutor;

    public CryptotoolOptionsImpl(BinaryExecutor binaryExecutor) {
        requireNonNull(binaryExecutor, "`binaryExecutor` must not be null");

        this.binaryExecutor = binaryExecutor;
    }
}
