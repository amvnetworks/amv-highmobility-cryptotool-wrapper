package org.amv.highmobility.cryptotool;

import com.google.common.base.Charsets;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.util.Base64;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CryptotoolUtilsTest {

    @Test
    public void decodingAndEncodingFromAndToHexShouldBeTheSame() {
        String value = RandomStringUtils.randomAlphanumeric(1000);

        String valueBase64 = Base64.getEncoder().encodeToString(value.getBytes(Charsets.UTF_8));
        String valueHex = CryptotoolUtils.decodeBase64AsHex(valueBase64);
        String valueBase64Again = CryptotoolUtils.encodeHexAsBase64(valueHex);

        assertThat(valueBase64Again, is(equalTo(valueBase64)));

        String valueAgain = new String(Base64.getDecoder().decode(valueBase64Again.getBytes(Charsets.UTF_8)), Charsets.UTF_8);
        assertThat(valueAgain, is(equalTo(value)));
    }

    @Test
    public void decodeBase64AsHex() {
        String value = "ABCDEF";

        String valueBase64 = Base64.getEncoder().encodeToString(value.getBytes(Charsets.UTF_8));
        assertThat(valueBase64, is(equalTo("QUJDREVG")));

        String valueHex = CryptotoolUtils.decodeBase64AsHex(valueBase64);
        assertThat(valueHex, is(equalTo("414243444546")));
    }

    @Test
    public void encodeHexAsBase64() {
        String value = "ABCDEF";

        String valueHex = new String(Hex.encodeHex(value.getBytes(Charsets.UTF_8)));
        assertThat(valueHex, is(equalTo("414243444546")));

        String valueBase64 = CryptotoolUtils.encodeHexAsBase64(valueHex);

        assertThat(valueBase64, is(equalTo("QUJDREVG")));
    }
}