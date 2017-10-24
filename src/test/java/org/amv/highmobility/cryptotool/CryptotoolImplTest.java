package org.amv.highmobility.cryptotool;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.amv.highmobility.cryptotool.CryptotoolUtils.SecureRandomUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.event.Level;

import java.time.LocalDateTime;

import static com.google.common.base.Preconditions.checkArgument;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class CryptotoolImplTest {
    static {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, Level.DEBUG.name());
    }

    private static CryptotoolOptions cryptotoolOptions;

    private Cryptotool sut;

    @BeforeClass
    public static void setUpClass() {
        CryptotoolImplTest.cryptotoolOptions = CryptotoolOptionsImpl.createDefault();
    }

    @Before
    public void setUp() {
        this.sut = new CryptotoolImpl(cryptotoolOptions);
    }

    @Test
    public void itShouldHaveAnElegantConstructionMechanism() {
        Cryptotool cryptotool = new CryptotoolImpl(CryptotoolOptionsImpl.createDefault());

        assertThat(cryptotool, Matchers.is(Matchers.notNullValue()));
    }

    @Test
    public void itShouldShowVersion() {
        Cryptotool.Version version = this.sut.version()
                .block();

        assertThat(version, is(notNullValue()));
        assertThat(version.getMajor(), is(notNullValue()));
        assertThat(version.getMinor(), is(notNullValue()));
        assertThat(version.getPatch(), is(notNullValue()));
        assertThat(version.getFullVersionString(), is(notNullValue()));
    }

    @Test
    @Parameters({ "1", "2", "3" })
    public void itShouldGenerateKeys(int index) {
        Cryptotool.Keys keys = this.sut.generateKeys()
                .block();

        assertThat(keys, is(notNullValue()));
        assertThat(keys.getPrivateKey(), is(notNullValue()));
        assertThat(keys.getPrivateKey().length(), is(64));
        assertThat(keys.getPublicKey(), is(notNullValue()));
        assertThat(keys.getPublicKey().length(), is(128));
    }

    @Test
    @Parameters({ "1","42", "256" })
    public void itShouldGenerateSignatures(int byteCount) {
        Cryptotool.Keys keys = this.sut.generateKeys()
                .block();

        String anyMessage = SecureRandomUtils.generateRandomHexString(byteCount);
        Cryptotool.Signature signature = this.sut.generateSignature(anyMessage, keys.getPrivateKey())
                .block();

        assertThat(signature, is(notNullValue()));
        assertThat(signature.getSignature(), is(notNullValue()));
    }

    @Test
    @Parameters({ "1","42", "256" })
    public void itShouldVerifySignature(int byteCount) {
        Cryptotool.Keys keys = this.sut.generateKeys()
                .block();

        String anyMessage = SecureRandomUtils.generateRandomHexString(byteCount);
        Cryptotool.Signature signature = this.sut.generateSignature(anyMessage, keys.getPrivateKey())
                .block();

        Cryptotool.Validity validity = this.sut.verifySignature(anyMessage, signature.getSignature(), keys.getPublicKey())
                .block();

        assertThat(validity, is(notNullValue()));
        assertThat(validity, is(Cryptotool.Validity.VALID));
    }


    @Test(expected = IllegalStateException.class)
    public void itShouldVerifySignatureFailureWithOverlongMessage() {
        Cryptotool.Keys keys = this.sut.generateKeys()
                .block();

        String anyMessage = SecureRandomUtils.generateRandomHexString(512);
        Cryptotool.Signature signature = this.sut.generateSignature(anyMessage, keys.getPrivateKey())
                .block();

        Assert.fail("Should have thrown exception when generating signature for overlong message.");
    }

    @Test
    @Parameters({ "1","42", "255" })
    public void itShouldFailVerifyingSignaturesWithMismatchingMessage(int byteCount) {
        checkArgument(byteCount <= 255);

        Cryptotool.Keys keys = this.sut.generateKeys()
                .block();

        String anyMessage = SecureRandomUtils.generateRandomHexString(byteCount);
        Cryptotool.Signature signature = this.sut.generateSignature(anyMessage, keys.getPrivateKey())
                .block();

        String mismatchingMessage = anyMessage + SecureRandomUtils.generateRandomHexString(1);
        Cryptotool.Validity validity = this.sut.verifySignature(mismatchingMessage, signature.getSignature(), keys.getPublicKey())
                .block();

        assertThat(validity, is(notNullValue()));
        assertThat(validity, is(Cryptotool.Validity.INVALID));
    }

    @Test
    @Parameters({ "1", "2", "42", "128", "200", "256" })
    public void itShouldFailVerifyingSignaturesWithMismatchingKey(int byteCount) {
        Cryptotool.Keys keys = this.sut.generateKeys()
                .block();

        String anyMessage = SecureRandomUtils.generateRandomHexString(byteCount);
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
    @Parameters({ "1", "2", "42", "128", "200", "256" })
    public void itShouldGenerateHmac(int byteCount) {
        String key = SecureRandomUtils.generateRandomHexString(32);
        String anyMessage = SecureRandomUtils.generateRandomHexString(byteCount);
        Cryptotool.Hmac hmac = this.sut.generateHmac(anyMessage, key)
                .block();

        assertThat(hmac, is(notNullValue()));
        assertThat(hmac.getHmac(), is(notNullValue()));
    }

    @Test
    @Parameters({ "1", "2", "42", "128", "200", "256" })
    public void itShouldVerifyHmac(int byteCount) {
        String key = SecureRandomUtils.generateRandomHexString(32);
        String anyMessage = SecureRandomUtils.generateRandomHexString(byteCount);

        Cryptotool.Hmac hmac = this.sut.generateHmac(anyMessage, key)
                .block();
        Cryptotool.Validity validity = this.sut.verifyHmac(anyMessage, key, hmac.getHmac())
                .block();

        assertThat(validity, is(notNullValue()));
        assertThat(validity, is(Cryptotool.Validity.VALID));
    }

    @Test
    @Parameters({ "1","42", "255" })
    public void itShouldFailVerifyingHmacWithMismatchingMessage(int byteCount) {
        checkArgument(byteCount <= 255);

        String key = SecureRandomUtils.generateRandomHexString(32);
        String anyMessage = SecureRandomUtils.generateRandomHexString(255);

        Cryptotool.Hmac hmac = this.sut.generateHmac(anyMessage, key)
                .block();

        String mismatchingMessage = anyMessage + SecureRandomUtils.generateRandomHexString(1);
        Cryptotool.Validity validity = this.sut.verifyHmac(mismatchingMessage, key, hmac.getHmac())
                .block();

        assertThat(validity, is(notNullValue()));
        assertThat(validity, is(Cryptotool.Validity.INVALID));
    }

    @Test
    @Parameters({ "1","42", "255" })
    public void itShouldFailVerifyingHmacWithMismatchingKey(int byteCount) {
        checkArgument(byteCount <= 255);

        String key = SecureRandomUtils.generateRandomHexString(32);
        String anyMessage = SecureRandomUtils.generateRandomHexString(byteCount);

        Cryptotool.Hmac hmac = this.sut.generateHmac(anyMessage, key)
                .block();

        String mismatchingKey = key.substring(0, 32) + SecureRandomUtils.generateRandomHexString(16);
        Cryptotool.Validity validity = this.sut.verifyHmac(anyMessage, mismatchingKey, hmac.getHmac())
                .block();

        assertThat(validity, is(notNullValue()));
        assertThat(validity, is(Cryptotool.Validity.INVALID));
    }

    @Test
    @Parameters({ "1", "2", "3"})
    public void itShouldCreateDeviceCertificate(int index) {
        Cryptotool.Keys keys = this.sut.generateKeys()
                .block();

        String issuer = SecureRandomUtils.generateRandomIssuerInHex();
        String appId = SecureRandomUtils.generateRandomAppId();
        String serial = SecureRandomUtils.generateRandomSerial();
        String publicKey = keys.getPublicKey();

        Cryptotool.DeviceCertificate deviceCertificate = this.sut.createDeviceCertificate(issuer, appId, serial, publicKey)
                .block();
        assertThat(deviceCertificate, is(notNullValue()));
        assertThat(deviceCertificate.getDeviceCertificate(), is(notNullValue()));
    }

    @Test
    @Parameters({ "1", "2", "3"})
    public void itShouldCreateAccessCertificate(int index) {
        Cryptotool.Keys keys = this.sut.generateKeys()
                .block();

        String gainingSerial = SecureRandomUtils.generateRandomSerial();
        String publicKey = keys.getPublicKey();
        String providingSerial = SecureRandomUtils.generateRandomSerial();
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusYears(1);

        Cryptotool.AccessCertificate accessCertificate = this.sut.createAccessCertificate(gainingSerial, publicKey, providingSerial, startDate, endDate, "10001F08000040")
                .block();
        assertThat(accessCertificate, is(notNullValue()));
        assertThat(accessCertificate.getAccessCertificate(), is(notNullValue()));
        assertThat(accessCertificate.getValidityStartDate(), is(startDate));
        assertThat(accessCertificate.getValidityEndDate(), is(endDate));
    }

}
