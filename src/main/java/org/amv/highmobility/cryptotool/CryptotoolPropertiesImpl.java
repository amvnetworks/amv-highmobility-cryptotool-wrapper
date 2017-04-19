package org.amv.highmobility.cryptotool;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(builderClassName = "Builder")
public class CryptotoolPropertiesImpl implements CryptotoolProperties{
    private String privateKey;
    private String publicKey;
}