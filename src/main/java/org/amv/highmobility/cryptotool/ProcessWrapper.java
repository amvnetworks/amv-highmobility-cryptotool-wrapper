package org.amv.highmobility.cryptotool;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * A wrapper for the {@link ProcessBuilder} that reads all relevant streams which can cause an 'hanging' {@link Process}. The
 * read data of the streams is provided as strings.
 */
@Slf4j
class ProcessWrapper {
    private final File directory;
    private final List<String> commands;
    private final Map<String, String> environment;

    public ProcessWrapper(File directory, List<String> commands) throws Exception {
        this(directory, commands, Collections.emptyMap());
    }

    public ProcessWrapper(File directory, List<String> commands, Map<String, String> environment) {
        this.directory = directory;
        this.commands = ImmutableList.copyOf(requireNonNull(commands));
        this.environment = ImmutableMap.copyOf(requireNonNull(environment));
    }

    public ProcessResult execute() {
        ProcessBuilder pb = new ProcessBuilder(commands);

        if (directory != null) {
            pb.directory(directory);
        }
        if (environment != null && !environment.isEmpty()) {
            pb.environment().putAll(environment);
        }

        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {

            if (log.isDebugEnabled()) {
                log.debug("Executing: {}", commands.stream().collect(joining(" ")));
            }

            Process process = pb.start();

            try (InputStream stdoutStream = process.getInputStream();
                 InputStream stderrStream = process.getErrorStream()) {
                Future<List<String>> stdout = executor.submit(new StreamBoozer(stdoutStream));
                Future<List<String>> stderr = executor.submit(new StreamBoozer(stderrStream));

                int status = process.waitFor();

                ProcessResult processResult = new ProcessResult(status, stdout.get(), stderr.get());

                executor.shutdown();

                if (log.isDebugEnabled()) {
                    log.debug("Command has terminated with status: " + processResult.getStatus());
                    log.debug("Output:\n" + processResult.getInfos());
                    log.debug("Error:\n" + processResult.getErrors());
                }

                return processResult;
            }
        } catch (InterruptedException | ExecutionException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static class StreamBoozer implements Callable<List<String>> {
        private InputStream in;

        StreamBoozer(InputStream in) {
            this.in = requireNonNull(in);
        }

        @Override
        public List<String> call() {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.in));
            return bufferedReader.lines().collect(toList());
        }
    }

    public static class ProcessResult {
        private List<String> stderrLines;
        private List<String> stdoutLines;
        private int status;

        public ProcessResult(int status, List<String> stdoutLines, List<String> stderrLines) {
            this.status = status;
            this.stderrLines = requireNonNull(stderrLines);
            this.stdoutLines = requireNonNull(stdoutLines);
        }

        public int getStatus() {
            return status;
        }

        public List<String> getErrors() {
            return ImmutableList.copyOf(stderrLines);
        }

        public boolean hasErrors() {
            return !getErrors().isEmpty();
        }

        public List<String> getInfos() {
            return ImmutableList.copyOf(stdoutLines);
        }

    }
}