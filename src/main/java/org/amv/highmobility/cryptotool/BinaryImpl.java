package org.amv.highmobility.cryptotool;

import lombok.Builder;
import lombok.Value;

import java.io.File;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

@Value
@Builder(builderClassName = "Builder")
public class BinaryImpl implements Binary {
    private final File file;

    public BinaryImpl(File file) {
        requireNonNull(file, "`file` must not be null");
        checkArgument(file.exists(), "`file` does not exist");
        this.file = file;
    }
}
