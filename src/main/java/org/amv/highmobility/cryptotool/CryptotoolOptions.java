package org.amv.highmobility.cryptotool;

import java.time.Duration;

public interface CryptotoolOptions {
    BinaryExecutor getBinaryExecutor();

    Duration getCommandTimeout();
}