package org.amv.highmobility.cryptotool;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.*;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

@Slf4j
final class JarUtil {

    private JarUtil() {
        throw new UnsupportedOperationException();
    }

    static File extractFileFromJarToDisk(String pathToFileInsideJar, String pathToTargetDirectory, String targetFileName) throws IOException {
        requireNonNull(pathToFileInsideJar, "`pathToFileInsideJar` must not be null");
        checkArgument(!Strings.isNullOrEmpty(pathToFileInsideJar));

        requireNonNull(pathToTargetDirectory, "`pathToTargetDirectory` must not be null");
        File targetDirectory = new File(pathToTargetDirectory);
        checkArgument(targetDirectory.exists(), "`targetDirectory` must exist");
        checkArgument(targetDirectory.canWrite(), "`targetDirectory` must be writable");

        requireNonNull(targetFileName);
        checkArgument(!Strings.isNullOrEmpty(targetFileName));

        String pathToTargetFile = targetDirectory.getAbsolutePath() + File.separator + targetFileName;

        if (log.isDebugEnabled()) {
            log.debug("Extract file in jar from {} to {}", pathToFileInsideJar, pathToTargetFile);
        }

        try (InputStream is = JarUtil.class.getResource(pathToFileInsideJar).openStream();
             OutputStream os = new FileOutputStream(pathToTargetFile)) {
            IOUtils.copy(is, os);
        }

        File targetFile = new File(pathToTargetFile);
        checkArgument(targetFile.exists(), "Sanity check if file exists failed");

        return targetFile;
    }
}
