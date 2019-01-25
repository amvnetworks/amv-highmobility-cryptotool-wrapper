package org.amv.highmobility.cryptotool;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.amv.highmobility.cryptotool.CryptotoolUtils.SecureRandomUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.event.Level;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
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
                .name(SecureRandomUtils.generateRandomIssuer())
                .keys(cryptotool.generateKeys().block())
                .build();

        this.sut = new CryptotoolWithIssuerImpl(cryptotoolOptions, certificateIssuer);
    }

    @Test
    public void itShouldCreateAndVerifySignaturesWithSuccess() {
        String anyMessage = SecureRandomUtils.generateRandomHexString(256);

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
        String appId = SecureRandomUtils.generateRandomAppId();
        String serial = SecureRandomUtils.generateRandomSerial();

        Cryptotool.DeviceCertificate deviceCertificate = this.sut.createDeviceCertificate(appId, serial, keys.getPublicKey())
                .block();

        assertThat(deviceCertificate, is(notNullValue()));
        assertThat(deviceCertificate.getDeviceCertificate(), is(notNullValue()));
    }

    @Test
    @Parameters({"1", "2", "3"})
    public void itShouldCreateAccessCertificateV1(int index) {
        Cryptotool.Keys keys = this.sut.generateKeys()
                .block();

        String providingSerial = SecureRandomUtils.generateRandomSerial();
        String gainingSerial = SecureRandomUtils.generateRandomSerial();
        String gainingPublicKey = keys.getPublicKey();
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusYears(1);
        String expectedPermissions = "10001F08000040";
        PermissionsImpl permissions = PermissionsImpl.builder()
                .diagnosticsRead(true)
                .doorLocksRead(true)
                .doorLocksWrite(true)
                .keyfobPositionRead(true)
                .capabilitiesRead(true)
                .vehicleStatusRead(true)
                .chargeRead(true)
                .build();

        assertThat("sanity check", permissions.getPermissions(), is(equalToIgnoringCase(expectedPermissions)));

        Cryptotool.AccessCertificate accessCertificate = this.sut
                .createAccessCertificateV1(providingSerial, gainingSerial, gainingPublicKey, startDate, endDate, permissions)
                .block();
        assertThat(accessCertificate, is(notNullValue()));
        assertThat(accessCertificate.getAccessCertificate(), is(notNullValue()));
        assertThat(accessCertificate.getValidityStartDate(), is(startDate));
        assertThat(accessCertificate.getValidityEndDate(), is(endDate));
    }
}
