package org.amv.highmobility.cryptotool;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.time.Duration;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class CryptotoolOptionsImplTest {

    @Test
    public void itShouldCreateInstanceWithReasonableDefaults() {
        CryptotoolOptions cryptotoolOptions = CryptotoolOptionsImpl.createDefault();

        assertThat(cryptotoolOptions, is(notNullValue()));
        assertThat(cryptotoolOptions.getBinaryExecutor(), is(notNullValue()));
        assertThat(cryptotoolOptions.getCommandTimeout(), is(Duration.ofSeconds(3L)));
    }

    @Test
    public void itShouldHaveAnElegantConstructionMechanism() {
        long commandTimeoutInSeconds = RandomUtils.nextLong();
        CryptotoolOptions cryptotoolOptions = CryptotoolOptionsImpl.builder()
                .binaryExecutor(BinaryExecutorImpl.createDefault())
                .commandTimeout(Duration.ofSeconds(commandTimeoutInSeconds))
                .build();

        assertThat(cryptotoolOptions, is(notNullValue()));
        assertThat(cryptotoolOptions.getBinaryExecutor(), is(notNullValue()));
        assertThat(cryptotoolOptions.getCommandTimeout(), is(Duration.ofSeconds(commandTimeoutInSeconds)));
    }
}