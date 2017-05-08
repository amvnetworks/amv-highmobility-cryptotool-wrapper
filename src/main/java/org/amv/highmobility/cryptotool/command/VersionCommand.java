package org.amv.highmobility.cryptotool.command;

import lombok.Builder;
import lombok.Value;
import org.amv.highmobility.cryptotool.BinaryExecutor;
import org.amv.highmobility.cryptotool.Cryptotool.Version;
import org.amv.highmobility.cryptotool.CryptotoolImpl;
import reactor.core.publisher.Flux;

@Value
@Builder(builderClassName = "Builder")
public class VersionCommand implements Command<Version> {

    @Override
    public Flux<Version> execute(BinaryExecutor executor) {
        String versionPrefix = "Cryptotool version";
        return executor.execute("-v")
                .map(processResult -> CommandHelper.parseValueWithPrefix(versionPrefix, processResult.getCleanedOutput())
                        .orElseThrow(() -> new IllegalStateException("Cannot find version on stdout")))
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
