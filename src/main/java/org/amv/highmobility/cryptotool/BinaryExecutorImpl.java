package org.amv.highmobility.cryptotool;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@Slf4j
@Builder(builderClassName = "Builder")
public class BinaryExecutorImpl implements BinaryExecutor {

    public static BinaryExecutorImpl createDefault() {
        try {
            final Binary binary = Binaries.defaultBinary();
            final File workingDirectory = Files.createTempDir();
            return BinaryExecutorImpl.builder()
                    .binary(binary)
                    .workingDirectory(workingDirectory)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("Error creating default binary executor", e);
        }
    }

    private final Binary binary;
    private final File workingDirectory;

    public BinaryExecutorImpl(Binary binary, File workingDirectory) throws IllegalArgumentException {
        requireNonNull(binary, "`binary` must not be null");
        requireNonNull(workingDirectory, "`workingDirectory` must not be null");
        checkArgument(workingDirectory.exists(), "`workingDirectory` does not exist");

        this.binary = binary;
        this.workingDirectory = workingDirectory;
    }

    @Override
    public Flux<ProcessResult> execute(List<String> args) {
        requireNonNull(args);

        ImmutableList<String> commands = ImmutableList.<String>builder()
                .add(this.binary.getFile().getAbsolutePath())
                .addAll(args)
                .build();

        ProcessWrapper processWrapper = new ProcessWrapper(
                this.workingDirectory,
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
    private static class ProcessWrapper {
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

    }
}
