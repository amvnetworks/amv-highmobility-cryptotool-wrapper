package org.amv.highmobility.cryptotool;

import org.apache.commons.codec.binary.Hex;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;

import static java.util.Objects.requireNonNull;

public class AccessCertificateImpl implements Cryptotool.AccessCertificate {
    private byte[] accessCertificate;
    private LocalDateTime validityStartDate;
    private LocalDateTime validityEndDate;

    AccessCertificateImpl(byte[] accessCertificate,
                          LocalDateTime validityStartDate,
                          LocalDateTime validityEndDate) {
        this.accessCertificate = accessCertificate;
        this.validityStartDate = requireNonNull(validityStartDate);
        this.validityEndDate = requireNonNull(validityEndDate);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String getAccessCertificateBase64() {
        return Base64.getEncoder().encodeToString(this.accessCertificate);
    }

    @Override
    public byte[] getAccessCertificateBytes() {
        return Arrays.copyOf(this.accessCertificate, this.accessCertificate.length);
    }

    @Override
    public String getAccessCertificate() {
        return Hex.encodeHexString(this.accessCertificate);
    }

    @Override
    public LocalDateTime getValidityStartDate() {
        return this.validityStartDate;
    }

    @Override
    public LocalDateTime getValidityEndDate() {
        return this.validityEndDate;
    }

    public static class Builder {
        private byte[] accessCertificate;
        private LocalDateTime validityStartDate;
        private LocalDateTime validityEndDate;

        Builder() {
        }

        public AccessCertificateImpl.Builder accessCertificate(byte[] accessCertificate) {
            this.accessCertificate = Arrays.copyOf(accessCertificate, accessCertificate.length);
            return this;
        }

        public AccessCertificateImpl.Builder validityStartDate(LocalDateTime validityStartDate) {
            this.validityStartDate = validityStartDate;
            return this;
        }

        public AccessCertificateImpl.Builder validityEndDate(LocalDateTime validityEndDate) {
            this.validityEndDate = validityEndDate;
            return this;
        }

        public AccessCertificateImpl build() {
            return new AccessCertificateImpl(accessCertificate, validityStartDate, validityEndDate);
        }

        public String toString() {
            return "AccessCertificateImpl.Builder(accessCertificate=" + java.util.Arrays.toString(this.accessCertificate) + ", validityStartDate=" + this.validityStartDate + ", validityEndDate=" + this.validityEndDate + ")";
        }
    }
}
