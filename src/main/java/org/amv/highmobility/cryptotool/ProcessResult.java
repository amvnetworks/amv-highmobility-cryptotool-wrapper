package org.amv.highmobility.cryptotool;

import java.util.List;
import java.util.Optional;

public interface ProcessResult {
    boolean hasErrors();

    List<String> getStdoutLines();

    List<String> getStderrLines();

    Optional<Throwable> getException();
}
