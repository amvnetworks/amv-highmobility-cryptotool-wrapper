package org.amv.highmobility.cryptotool;

import org.amv.highmobility.cryptotool.CryptotoolUtils.TestUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.event.Level;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class CryptotoolWithIssuerImplTest {
    static {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, Level.DEBUG.name());
    }

    private static CryptotoolOptions cryptotoolOptions;

    private CryptotoolWithIssuer sut;

    @BeforeClass
    public static void setUpClass() {
        CryptotoolWithIssuerImplTest.cryptotoolOptions = CryptotoolOptionsImpl.createDefault();
    }

    @Before
    public void setUp() {
        Cryptotool cryptotool = new CryptotoolImpl(cryptotoolOptions);

        CryptotoolWithIssuer.CertificateIssuer certificateIssuer = CryptotoolWithIssuer.CertificateIssuerImpl.builder()
                .name(TestUtils.generateRandomIssuer())
                .keys(cryptotool.generateKeys().block())
                .build();

        this.sut = new CryptotoolWithIssuerImpl(cryptotoolOptions, certificateIssuer);
    }

    @Test
    public void itShouldCreateAndVerifySignaturesWithSuccess() {
        String anyMessage = TestUtils.generateRandomHexString(256);

        Cryptotool.Signature signature = this.sut.generateSignature(anyMessage)
                .block();
        Cryptotool.Validity validity = this.sut.verifySignature(anyMessage, signature.getSignature())
                .block();

        assertThat(validity, is(notNullValue()));
        assertThat(validity, is(Cryptotool.Validity.VALID));
    }

    @Test
    public void itShouldCreateDeviceCertificate() {
        Cryptotool.Keys keys = this.sut.generateKeys().block();
        String appId = TestUtils.generateRandomAppId();
        String serial = TestUtils.generateRandomSerial();

        Cryptotool.DeviceCertificate deviceCertificate = this.sut.createDeviceCertificate(appId, serial, keys.getPublicKey())
                .block();

        assertThat(deviceCertificate, is(notNullValue()));
        assertThat(deviceCertificate.getDeviceCertificate(), is(notNullValue()));
    }
}
