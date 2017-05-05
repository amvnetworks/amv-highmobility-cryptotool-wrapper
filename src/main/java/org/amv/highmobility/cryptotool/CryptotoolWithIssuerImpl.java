package org.amv.highmobility.cryptotool;

import static java.util.Objects.requireNonNull;

public class CryptotoolWithIssuerImpl extends CryptotoolImpl implements CryptotoolWithIssuer {

    private final CertificateIssuer certificateIssuer;

    public CryptotoolWithIssuerImpl(CryptotoolOptions options, CertificateIssuer certificateIssuer)
            throws IllegalArgumentException {
        super(options);
        this.certificateIssuer = requireNonNull(certificateIssuer, "`certificateIssuer` must not be null");
    }

    @Override
    public CertificateIssuer getCertificateIssuer() {
        return this.certificateIssuer;
    }
}