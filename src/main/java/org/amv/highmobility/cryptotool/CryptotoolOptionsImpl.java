package org.amv.highmobility.cryptotool;

import com.google.common.io.Files;
import lombok.Builder;
import lombok.Value;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

@Value
@Builder(builderClassName = "Builder")
public class CryptotoolOptionsImpl implements CryptotoolOptions {

    public static CryptotoolOptionsImpl createDefault() {
        try {
            return CryptotoolOptionsImpl.builder()
                    .pathToExecutable(BinaryHelper.getCryptotoolBinary())
                    .workingDirectory(Files.createTempDir())
                    .build();
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Error creating default cryptotool instance", e);
        }
    }

    private File pathToExecutable;
    private File workingDirectory;
}
