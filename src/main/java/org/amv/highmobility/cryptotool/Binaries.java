package org.amv.highmobility.cryptotool;


import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
public final class Binaries {
    private static final String PATH_TO_BINARY_IN_JAR_WINDOWS = "/cryptotool/bin/crypto-tool.exe";
    private static final String PATH_TO_BINARY_IN_JAR_UNIX = "/cryptotool/bin/crypto-tool";
    private static final String PATH_TO_BINARY_IN_JAR_UNIX_REDHAT = "/cryptotool/bin/crypto-tool-fedora";

    private static final String TARGET_BINARY_NAME = "crypto-tool";

    private Binaries() {
        throw new UnsupportedOperationException();
    }

    public static Binary defaultBinary() throws IOException {
        return BinaryImpl.builder()
                .file(copyBinaryFromJarToDisk(findPathToDefaultBinaryInJar()))
                .build();
    }

    public static Binary windowsBinary() throws IOException {
        return BinaryImpl.builder()
                .file(copyBinaryFromJarToDisk(PATH_TO_BINARY_IN_JAR_WINDOWS))
                .build();
    }

    public static Binary unixBinary() throws IOException {
        return BinaryImpl.builder()
                .file(copyBinaryFromJarToDisk(PATH_TO_BINARY_IN_JAR_UNIX))
                .build();
    }

    public static Binary redhatBinary() throws IOException {
        return BinaryImpl.builder()
                .file(copyBinaryFromJarToDisk(PATH_TO_BINARY_IN_JAR_UNIX_REDHAT))
                .build();
    }

    private static File copyBinaryFromJarToDisk(String pathToBinaryInJar) throws IOException {
        File tempDir = Files.createTempDir();
        tempDir.deleteOnExit();

        File file = JarUtil.extractFileFromJarToDisk(pathToBinaryInJar,
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

    private static String findPathToDefaultBinaryInJar() throws IOException {
        boolean isOsSupported = SystemUtils.IS_OS_WINDOWS || SystemUtils.IS_OS_UNIX;
        checkArgument(isOsSupported, "Unsupported operating system");

        if (SystemHelper.isWindows()) {
            return PATH_TO_BINARY_IN_JAR_WINDOWS;
        } else if (SystemHelper.isRedhat()) {
            return PATH_TO_BINARY_IN_JAR_UNIX_REDHAT;
        } else if (SystemHelper.isLinux()) {
            return PATH_TO_BINARY_IN_JAR_UNIX;
        }

        throw new IllegalStateException("Non compatible operating system for " + TARGET_BINARY_NAME);
    }


    private static final class SystemHelper {

        private static boolean isWindows() {
            return SystemUtils.IS_OS_WINDOWS;
        }

        private static boolean isLinux() {
            return SystemUtils.IS_OS_LINUX;
        }

        private static boolean isRedhat() {
            return isLinux() && new File("/etc/redhat-release").exists();
        }
    }
}
