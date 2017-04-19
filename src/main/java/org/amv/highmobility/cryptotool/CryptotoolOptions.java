package org.amv.highmobility.cryptotool;

import java.io.File;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

public interface CryptotoolOptions {
    File getPathToExecutable();

    File getWorkingDirectory();

    default CryptotoolOptions validOrThrow() throws IllegalArgumentException {
        requireNonNull(this.getPathToExecutable());
        requireNonNull(this.getWorkingDirectory());
        checkArgument(this.getPathToExecutable().exists(), "`pathToExecutable` does not exist");
        checkArgument(this.getWorkingDirectory().exists(), "`workingDirectory` does not exist");

        return this;
    }
}