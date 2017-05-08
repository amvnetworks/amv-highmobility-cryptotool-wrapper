package org.amv.highmobility.cryptotool.command;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.amv.highmobility.cryptotool.CryptotoolOptions;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@Slf4j
public class CommandExecutor {

    private final CryptotoolOptions options;

    public CommandExecutor(CryptotoolOptions options) throws IllegalArgumentException {
        this.options = requireNonNull(options, "`options` must not be null")
                .validOrThrow();
    }

    public Flux<ProcessWrapper.ProcessResult> execute(String arg) {
        return execute(Collections.singletonList(arg));
    }

    public Flux<ProcessWrapper.ProcessResult> execute(List<String> args) {
        requireNonNull(args);

        ImmutableList<String> commands = ImmutableList.<String>builder()
                .add(this.options.getPathToExecutable().getAbsolutePath())
                .addAll(args)
                .build();

        ProcessWrapper processWrapper = new ProcessWrapper(
                this.options.getWorkingDirectory(),
                commands,
                Collections.emptyMap());

        return processWrapper.execute()
                .doOnNext(processResult -> {
                    if (processResult.hasErrors() && log.isWarnEnabled()) {
                        log.warn("Found output on stderr: \n{}", processResult.getErrors());
                    }
                });
    }

    /**
     * A wrapper for the {@link ProcessBuilder} that reads all relevant streams which can cause a 'hanging'
     * {@link Process}. The read data of the streams is provided as strings.
     */
    static class ProcessWrapper {
        private final File directory;
        private final List<String> commands;
        private final Map<String, String> environment;

        ProcessWrapper(File directory, List<String> commands, Map<String, String> environment) {
            this.directory = directory;
            this.commands = ImmutableList.copyOf(requireNonNull(commands));
            this.environment = ImmutableMap.copyOf(requireNonNull(environment));
        }

        public Flux<ProcessResult> execute() {
            ProcessBuilder pb = new ProcessBuilder(commands);

            if (directory != null) {
                pb.directory(directory);
            }
            if (environment != null && !environment.isEmpty()) {
                pb.environment().putAll(environment);
            }
            return Mono.fromCallable(() -> {
                if (log.isDebugEnabled()) {
                    log.debug("Executed: {}", commands.stream().collect(joining(" ")));
                }
                return pb.start();
            }).flatMap(this::readProcessOutput);
        }

        private Mono<ProcessResult> readProcessOutput(Process process) {
            return Mono.fromCallable(() -> {
                try {
                    ExecutorService executor = Executors.newFixedThreadPool(2);

                    int status = process.waitFor();
                    try (InputStream stdoutStream = process.getInputStream();
                         InputStream stderrStream = process.getErrorStream()) {
                        Future<List<String>> stdout = executor.submit(new StreamBoozer(stdoutStream));
                        Future<List<String>> stderr = executor.submit(new StreamBoozer(stderrStream));

                        ProcessResult processResult = ProcessResult.builder()
                                .status(status)
                                .output(stdout.get())
                                .errors(stderr.get()).build();

                        executor.shutdown();

                        if (log.isDebugEnabled()) {
                            log.debug("Command has terminated with status: " + processResult.getStatus());
                            log.debug("Output:\n" + processResult.getOutput());
                            log.debug("Error:\n" + processResult.getErrors());
                        }

                        return processResult;
                    }
                } catch (InterruptedException | ExecutionException | IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        private static class StreamBoozer implements Callable<List<String>> {
            private InputStream in;

            StreamBoozer(InputStream in) {
                this.in = requireNonNull(in);
            }

            @Override
            public List<String> call() {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.in, Charsets.UTF_8));
                return bufferedReader.lines()
                        .collect(toList());
            }
        }

        @Value
        @Builder(builderClassName = "Builder")
        static class ProcessResult {
            private List<String> errors;
            private List<String> output;
            private int status;

            boolean hasErrors() {
                return !getErrors().isEmpty();
            }

            List<String> getCleanedOutput() {
                Predicate<String> isNewLine = line -> "\n".equals(line) || System.lineSeparator().equals(line);
                Predicate<String> isEmptyLine = StringUtils::isBlank;

                return getOutput().stream()
                        .filter(isEmptyLine.negate())
                        .filter(isNewLine.negate())
                        .collect(toList());
            }

        }
    }
}
