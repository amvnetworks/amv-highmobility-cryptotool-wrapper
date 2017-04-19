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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class CryptotoolWithIssuerImplTest {
    static {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, Level.DEBUG.name());
    }

    private CryptotoolWithIssuer sut;

    @Before
    public void setUp() throws URISyntaxException, IOException {
        CryptotoolOptions cryptotoolOptions = CryptotoolOptionsImpl.builder()
                .pathToExecutable(BinaryHelper.getCryptotoolBinary())
                .workingDirectory(Files.createTempDir())
                .build();

        Cryptotool cryptotool = new CryptotoolImpl(cryptotoolOptions);

        CryptotoolWithIssuer.CertificateIssuer certificateIssuer = CryptotoolWithIssuer.CertificateIssuerImpl.builder()
                .name(TestUtils.generateRandomIssuer())
                .keys(cryptotool.generateKeys())
                .build();

        this.sut = new CryptotoolWithIssuerImpl(cryptotoolOptions, certificateIssuer);
    }

    @Test
    public void itShouldCreateAndVerifySignaturesWithSuccess() throws IOException {
        String anyMessage = RandomStringUtils.randomAlphabetic(RandomUtils.nextInt(1, 500));

        Cryptotool.Signature signature = this.sut.generateSignature(anyMessage);
        Cryptotool.Validity validity = this.sut.verifySignature(anyMessage, signature.getSignature());

        assertThat(validity, is(notNullValue()));
        assertThat(validity, is(Cryptotool.Validity.VALID));
    }

    @Test
    public void itShouldCreateDeviceCertificate() throws IOException {
        String appId = TestUtils.generateRandomAppId();
        String serial = TestUtils.generateRandomSerial();

        Cryptotool.DeviceCertificate deviceCertificate = this.sut.createDeviceCertificate(appId, serial);
        assertThat(deviceCertificate, is(notNullValue()));
        assertThat(deviceCertificate.getDeviceCertificate(), is(notNullValue()));
    }
}
