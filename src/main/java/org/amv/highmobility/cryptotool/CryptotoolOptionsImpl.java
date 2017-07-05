package org.amv.highmobility.cryptotool;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

import java.time.Duration;

import static java.util.Objects.requireNonNull;

@Getter
@Builder(builderClassName = "Builder")
public class CryptotoolOptionsImpl implements CryptotoolOptions {

    public static CryptotoolOptionsImpl createDefault() {
        return CryptotoolOptionsImpl.builder()
                .binaryExecutor(BinaryExecutorImpl.createDefault())
                .build();
    }

    private BinaryExecutor binaryExecutor;

    /**
     * The current timeout for each command is set to 3 seconds.
     * This value has been chosen because it is the smallest integer where all
     * tests succeed on reasonable hardware. Value of 1 and 2 second was
     * dismissed as some commands produced timeouts.
     */
    @Default
    private Duration commandTimeout = Duration.ofSeconds(3L);

    CryptotoolOptionsImpl(BinaryExecutor binaryExecutor, Duration commandTimeout) {
        requireNonNull(binaryExecutor, "`binaryExecutor` must not be null");
        requireNonNull(commandTimeout, "`commandTimeout` must not be null");

        this.binaryExecutor = binaryExecutor;
        this.commandTimeout = commandTimeout;
    }
}
