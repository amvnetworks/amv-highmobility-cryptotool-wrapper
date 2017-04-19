package org.amv.highmobility.cryptotool;

import com.google.common.io.Files;
import org.amv.highmobility.cryptotool.CryptotoolUtils.TestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.event.Level;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class CryptotoolImplTest {
    static {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, Level.DEBUG.name());
    }

    private Cryptotool sut;

    @Before
    public void setUp() throws URISyntaxException, IOException {
        CryptotoolOptions cryptotoolOptions = CryptotoolOptionsImpl.builder()
                .pathToExecutable(BinaryHelper.getCryptotoolBinary())
                .workingDirectory(Files.createTempDir())
                .build();

        this.sut = new CryptotoolImpl(cryptotoolOptions);
    }

    @Test
    public void itShouldShowVersion() throws IOException {
        Cryptotool.Version version = this.sut.version()
                .block();

        assertThat(version, is(notNullValue()));
        assertThat(version.getMajor(), is(notNullValue()));
        assertThat(version.getMinor(), is(notNullValue()));
        assertThat(version.getPatch(), is(notNullValue()));
        assertThat(version.getFullVersionString(), is(notNullValue()));
    }

    @Test
    public void itShouldGenerateKeys() throws IOException {
        Cryptotool.Keys keys = this.sut.generateKeys()
                .block();

        assertThat(keys, is(notNullValue()));
        assertThat(keys.getPrivateKey(), is(notNullValue()));
        assertThat(keys.getPublicKey(), is(notNullValue()));
    }

    @Test
    public void itShouldGenerateSignatures() throws IOException {
        Cryptotool.Keys keys = this.sut.generateKeys()
                .block();

        String anyMessage = RandomStringUtils.randomAlphabetic(RandomUtils.nextInt(1, 500));
        Cryptotool.Signature signature = this.sut.generateSignature(anyMessage, keys.getPrivateKey())
                .block();

        assertThat(signature, is(notNullValue()));
        assertThat(signature.getSignature(), is(notNullValue()));
    }

    @Test
    public void itShouldVerifySignature() throws IOException {
        Cryptotool.Keys keys = this.sut.generateKeys()
                .block();

        String anyMessage = RandomStringUtils.randomAlphabetic(RandomUtils.nextInt(1, 500));
        Cryptotool.Signature signature = this.sut.generateSignature(anyMessage, keys.getPrivateKey())
                .block();

        Cryptotool.Validity validity = this.sut.verifySignature(anyMessage, signature.getSignature(), keys.getPublicKey())
                .block();

        assertThat(validity, is(notNullValue()));
        assertThat(validity, is(Cryptotool.Validity.VALID));
    }

    @Test
    public void itShouldFailVerifyingSignaturesWithMismatchingMessage() throws IOException {
        Cryptotool.Keys keys = this.sut.generateKeys()
                .block();

        String anyMessage = RandomStringUtils.randomAlphabetic(RandomUtils.nextInt(1, 500));
        Cryptotool.Signature signature = this.sut.generateSignature(anyMessage, keys.getPrivateKey())
                .block();

        String mismatchingMessage = anyMessage + RandomStringUtils.randomAlphabetic(10);
        Cryptotool.Validity validity = this.sut.verifySignature(mismatchingMessage, signature.getSignature(), keys.getPublicKey())
                .block();

        assertThat(validity, is(notNullValue()));
        assertThat(validity, is(Cryptotool.Validity.INVALID));
    }

    @Test
    public void itShouldFailVerifyingSignaturesWithDifferentKey() throws IOException {
        Cryptotool.Keys keys = this.sut.generateKeys()
                .block();

        String anyMessage = RandomStringUtils.randomAlphabetic(RandomUtils.nextInt(1, 500));
        Cryptotool.Signature signature = this.sut.generateSignature(anyMessage, keys.getPrivateKey())
                .block();

        Cryptotool.Keys differentKeys = this.sut.generateKeys()
                .block();
        Cryptotool.Validity validity = this.sut.verifySignature(anyMessage, signature.getSignature(),
                differentKeys.getPublicKey())
                .block();

        assertThat(validity, is(notNullValue()));
        assertThat(validity, is(Cryptotool.Validity.INVALID));
    }

    @Test
    public void itShouldGenerateHmac() throws IOException {
        String key = RandomStringUtils.randomNumeric(64);
        String anyMessage = RandomStringUtils.randomAlphabetic(RandomUtils.nextInt(1, 500));
        Cryptotool.Hmac hmac = this.sut.generateHmac(anyMessage, key)
                .block();

        assertThat(hmac, is(notNullValue()));
        assertThat(hmac.getHmac(), is(notNullValue()));
    }

    @Test
    public void itShouldVerifyHmac() throws IOException {
        String key = RandomStringUtils.randomNumeric(64);
        String anyMessage = RandomStringUtils.randomAlphabetic(RandomUtils.nextInt(1, 500));

        Cryptotool.Hmac hmac = this.sut.generateHmac(anyMessage, key)
                .block();
        Cryptotool.Validity validity = this.sut.verifyHmac(anyMessage, key, hmac.getHmac())
                .block();

        assertThat(validity, is(notNullValue()));
        assertThat(validity, is(Cryptotool.Validity.VALID));
    }

    @Test
    public void itShouldFailVerifyingHmacWithMismatchingMessage() throws IOException {
        String key = RandomStringUtils.randomNumeric(64);
        String anyMessage = RandomStringUtils.randomAlphabetic(RandomUtils.nextInt(1, 10));

        Cryptotool.Hmac hmac = this.sut.generateHmac(anyMessage, key)
                .block();

        String mismatchingMessage = anyMessage + RandomStringUtils.randomAlphabetic(10);
        Cryptotool.Validity validity = this.sut.verifyHmac(mismatchingMessage, key, hmac.getHmac())
                .block();

        assertThat(validity, is(notNullValue()));
        assertThat(validity, is(Cryptotool.Validity.INVALID));
    }

    @Test
    public void itShouldFailVerifyingHmacWithDifferentKey() throws IOException {
        String key = RandomStringUtils.randomNumeric(64);
        String anyMessage = RandomStringUtils.randomAlphabetic(RandomUtils.nextInt(1, 10));

        Cryptotool.Hmac hmac = this.sut.generateHmac(anyMessage, key)
                .block();

        String mismatchingKey = key.substring(0, 32) + RandomStringUtils.randomAlphabetic(32);
        Cryptotool.Validity validity = this.sut.verifyHmac(anyMessage, mismatchingKey, hmac.getHmac())
                .block();

        assertThat(validity, is(notNullValue()));
        assertThat(validity, is(Cryptotool.Validity.INVALID));
    }

    @Test
    public void itShouldCreateDeviceCertificate() throws IOException {
        Cryptotool.Keys keys = this.sut.generateKeys()
                .block();

        String issuer = TestUtils.generateRandomIssuer();
        String appId = TestUtils.generateRandomAppId();
        String serial = TestUtils.generateRandomSerial();
        String publicKey = keys.getPublicKey();

        Cryptotool.DeviceCertificate deviceCertificate = this.sut.createDeviceCertificate(issuer, appId, serial, publicKey)
                .block();
        assertThat(deviceCertificate, is(notNullValue()));
        assertThat(deviceCertificate.getDeviceCertificate(), is(notNullValue()));
    }

    @Test
    public void itShouldCreateAccessCertificate() throws IOException {
        Cryptotool.Keys keys = this.sut.generateKeys()
                .block();

        String gainingSerial = TestUtils.generateRandomSerial();
        String publicKey = keys.getPublicKey();
        String providingSerial = TestUtils.generateRandomSerial();
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusYears(1);

        Cryptotool.AccessCertificate accessCertificate = this.sut.createAccessCertificate(gainingSerial, publicKey, providingSerial, startDate, endDate)
                .block();
        assertThat(accessCertificate, is(notNullValue()));
        assertThat(accessCertificate.getAccessCertificate(), is(notNullValue()));
    }

}
