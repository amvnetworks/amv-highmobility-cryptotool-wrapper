package org.amv.highmobility.cryptotool;


import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
public final class BinaryHelper {

    private static final String PATH_TO_BINARY_IN_JAR_WINDOWS = "/cryptotool/bin/crypto-tool.exe";
    private static final String PATH_TO_BINARY_IN_JAR_UNIX = "/cryptotool/bin/crypto-tool";

    private static final String TARGET_BINARY_NAME = "crypto-tool";

    private BinaryHelper() {
        throw new UnsupportedOperationException();
    }

    public static File getCryptotoolBinary() throws URISyntaxException, IOException {
        File tempDir = Files.createTempDir();
        tempDir.deleteOnExit();

        File file = JarUtil.extractFileFromJarToDisk(getPathToBinaryInJar(),
                tempDir.getAbsolutePath(),
                TARGET_BINARY_NAME);
        File executableFile = makeFileExecutableOrThrow(file);

        if (log.isDebugEnabled()) {
            log.debug("Copied {} to {}", TARGET_BINARY_NAME, file.getAbsolutePath());
        }

        return executableFile;
    }

    private static File makeFileExecutableOrThrow(File file) {
        boolean makeExecutable = true;
        boolean ownerOnly = false;
        if (!file.setExecutable(makeExecutable, ownerOnly)) {
            throw new IllegalStateException("Could not set executable flag on file " + file.getName());
        }
        return file;
    }

    private static String getPathToBinaryInJar() throws URISyntaxException, IOException {
        boolean isOsSupported = SystemUtils.IS_OS_WINDOWS || SystemUtils.IS_OS_UNIX;
        checkArgument(isOsSupported, "Unsupported operating system");

        if (SystemUtils.IS_OS_WINDOWS) {
            return PATH_TO_BINARY_IN_JAR_WINDOWS;
        } else {
            return PATH_TO_BINARY_IN_JAR_UNIX;
        }
    }
}
