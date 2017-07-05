package org.amv.highmobility.cryptotool;


import com.google.common.base.Charsets;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.Base64;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.codec.binary.Base64.isBase64;

/**
 * Packaged with sources for easier development
 */
public final class CryptotoolUtils {
    private CryptotoolUtils() {
        throw new UnsupportedOperationException();
    }

    public static String decodeBase64AsHex(String key) {
        requireNonNull(key, "`key` must not be null");
        checkArgument(isBase64(key), "`key` must be base64");

        byte[] keyInBytes = Base64.getDecoder().decode(key);
        return Hex.encodeHexString(keyInBytes);
    }

    public static String encodeHexAsBase64(String key) {
        requireNonNull(key, "`key` must not be null");

        try {
            byte[] keyBase16 = Hex.decodeHex(key.toCharArray());
            return Base64.getEncoder().encodeToString(keyBase16);
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }
    }

    public static final class TestUtils {
        private TestUtils() {
            throw new UnsupportedOperationException();
        }

        public static String generateRandomHexString(int byteCount) {
            return Hex.encodeHexString(RandomUtils.nextBytes(byteCount));
        }

        public static String generateRandomSerial() {
            return generateRandomHexString(9);
        }

        public static String generateRandomAppId() {
            return generateRandomHexString(12);
        }

        public static String generateRandomIssuerInHex() {
            return Hex.encodeHexString(generateRandomIssuer().getBytes(Charsets.UTF_8));
        }

        public static String generateRandomIssuer() {
            return RandomStringUtils.randomAlphanumeric(4);
        }
    }


}
