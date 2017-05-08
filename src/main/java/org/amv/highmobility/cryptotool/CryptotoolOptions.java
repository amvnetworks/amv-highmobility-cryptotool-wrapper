package org.amv.highmobility.cryptotool;

import java.time.Duration;

public interface CryptotoolOptions {
    BinaryExecutor getBinaryExecutor();

    default Duration getCommandTimeout() {
        return Duration.ofSeconds(1L);
    }
}