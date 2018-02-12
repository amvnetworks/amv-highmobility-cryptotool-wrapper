package org.amv.highmobility.cryptotool;

import org.apache.commons.codec.binary.Hex;

import java.util.Arrays;
import java.util.Base64;

public class KeysImpl implements Cryptotool.Keys {
    private final byte[] privateKey;
    private final byte[] publicKey;

    public KeysImpl(byte[] privateKey, byte[] publicKey) {
        this.privateKey = Arrays.copyOf(privateKey, privateKey.length);
        this.publicKey = Arrays.copyOf(publicKey, publicKey.length);
    }

    public static Builder builder() {
        return new Builder();
    }

    public byte[] getPublicKeyBytes() {
        return Arrays.copyOf(publicKey, publicKey.length);
    }

    public byte[] getPrivateKeyBytes() {
        return Arrays.copyOf(privateKey, privateKey.length);
    }

    public String getPrivateKey() {
        return Hex.encodeHexString(privateKey);
    }

    public String getPublicKey() {
        return Hex.encodeHexString(publicKey);
    }

    public String getPublicKeyBase64() {
        return Base64.getEncoder().encodeToString(publicKey);
    }

    public String getPrivateKeyBase64() {
        return Base64.getEncoder().encodeToString(privateKey);
    }

    public static class Builder {
        private byte[] privateKey;
        private byte[] publicKey;

        Builder() {
        }

        public Builder privateKey(byte[] privateKey) {
            this.privateKey = Arrays.copyOf(privateKey, privateKey.length);
            return this;
        }

        public Builder publicKey(byte[] publicKey) {
            this.publicKey = Arrays.copyOf(publicKey, publicKey.length);
            return this;
        }

        public KeysImpl build() {
            return new KeysImpl(privateKey, publicKey);
        }

        public String toString() {
            return "KeysImpl.Builder(privateKey=" + Arrays.toString(this.privateKey) + ", publicKey=" + Arrays.toString(this.publicKey) + ")";
        }
    }
}
