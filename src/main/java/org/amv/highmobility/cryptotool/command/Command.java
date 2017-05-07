package org.amv.highmobility.cryptotool.command;

import reactor.core.publisher.Flux;

public interface Command<T> {
    Flux<T> execute();
}
