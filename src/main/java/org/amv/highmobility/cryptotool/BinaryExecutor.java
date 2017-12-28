package org.amv.highmobility.cryptotool;

import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;

public interface BinaryExecutor {

    default Flux<ProcessResult> execute(String arg) {
        return execute(Collections.singletonList(arg));
    }

    Flux<ProcessResult> execute(List<String> args);

}
