package org.amv.highmobility.cryptotool;

import lombok.Builder;
import lombok.Value;

import java.io.File;

@Value
@Builder(builderClassName = "Builder")
public class CryptotoolOptionsImpl implements CryptotoolOptions {
    private File pathToExecutable;
    private File workingDirectory;
}
