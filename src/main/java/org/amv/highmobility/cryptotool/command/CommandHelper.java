package org.amv.highmobility.cryptotool.command;

import java.util.List;
import java.util.Optional;

final class CommandHelper {

    private CommandHelper() {
        throw new UnsupportedOperationException();
    }

    public static Optional<String> parseValueWithPrefix(String prefix, List<String> output) {
        return output.stream()
                .filter(line -> line.startsWith(prefix))
                .map(line -> line.replace(prefix, ""))
                .map(String::trim)
                .findFirst();
    }
}
