package org.amv.highmobility.cryptotool.command;

import org.amv.highmobility.cryptotool.BinaryExecutor;
import reactor.core.publisher.Flux;

public interface Command<T> {
    Flux<T> execute(BinaryExecutor binaryExecutor);
}
