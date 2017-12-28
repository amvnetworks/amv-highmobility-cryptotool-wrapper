package org.amv.highmobility.cryptotool.command;

import lombok.Builder;
import org.amv.highmobility.cryptotool.BinaryExecutor;
import org.amv.highmobility.cryptotool.Cryptotool.Version;
import org.amv.highmobility.cryptotool.CryptotoolImpl;
import reactor.core.publisher.Flux;

import static org.amv.highmobility.cryptotool.command.CommandHelper.parseValueWithPrefix;

@Builder(builderClassName = "Builder")
public class VersionCommand implements Command<Version> {

    @Override
    public Flux<Version> execute(BinaryExecutor executor) {
        String versionPrefix = "Cryptotool version";
        return executor.execute("-v")
                .map(process -> parseValueWithPrefix(versionPrefix, process.getStdoutLines())
                        .orElseThrow(() -> new IllegalStateException("Cannot find version on stdout",
                                process.getException().orElse(null))))
                .map(version -> {
                    String[] majorMinorPatch = version.split("\\.");
                    return CryptotoolImpl.VersionImpl.builder()
                            .major(majorMinorPatch.length > 0 ? majorMinorPatch[0] : "0")
                            .minor(majorMinorPatch.length > 1 ? majorMinorPatch[1] : "0")
                            .patch(majorMinorPatch.length > 2 ? majorMinorPatch[2] : "0")
                            .build();
                });
    }
}
