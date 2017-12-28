package org.amv.highmobility.cryptotool;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.google.common.collect.ImmutableList.toImmutableList;

@Getter
@Builder(builderClassName = "Builder")
public class ProcessResultImpl implements ProcessResult {
    @NonNull
    private List<String> errors;
    @NonNull
    private List<String> output;
    private int status;

    @Override
    public boolean hasErrors() {
        return !getErrors().isEmpty();
    }

    @Override
    public List<String> getStdoutLines() {
        Predicate<String> isNewLine = line -> "\n".equals(line) || System.lineSeparator().equals(line);
        Predicate<String> isEmptyLine = StringUtils::isBlank;

        return getOutput().stream()
                .filter(isEmptyLine.negate())
                .filter(isNewLine.negate())
                .collect(toImmutableList());
    }

    @Override
    public List<String> getStderrLines() {
        return getErrors();
    }

    @Override
    public Optional<Throwable> getException() {
        return errors.stream()
                .filter(StringUtils::isNotBlank)
                .findFirst()
                .map(IllegalStateException::new);
    }

    private List<String> getErrors() {
        return errors.stream()
                .filter(StringUtils::isNotBlank)
                .filter(val -> !val.trim().toLowerCase().startsWith("debug"))
                .collect(toImmutableList());
    }
}
