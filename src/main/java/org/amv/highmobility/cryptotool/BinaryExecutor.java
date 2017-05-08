package org.amv.highmobility.cryptotool;

import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public interface BinaryExecutor {

    default Flux<ProcessResult> execute(String arg) {
        return execute(Collections.singletonList(arg));
    }

    Flux<ProcessResult> execute(List<String> args);

    @Value
    @Builder(builderClassName = "Builder")
    class ProcessResult {
        private List<String> errors;
        private List<String> output;
        private int status;

        public boolean hasErrors() {
            return !getErrors().isEmpty();
        }

        public List<String> getCleanedOutput() {
            Predicate<String> isNewLine = line -> "\n".equals(line) || System.lineSeparator().equals(line);
            Predicate<String> isEmptyLine = StringUtils::isBlank;

            return getOutput().stream()
                    .filter(isEmptyLine.negate())
                    .filter(isNewLine.negate())
                    .collect(toList());
        }

    }
}
