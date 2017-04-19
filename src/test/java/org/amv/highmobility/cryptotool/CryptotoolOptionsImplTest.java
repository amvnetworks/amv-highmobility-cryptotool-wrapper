package org.amv.highmobility.cryptotool;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class CryptotoolOptionsImplTest {

    @Test
    public void itSouldHaveAnElegantConstructionMechanism() throws Exception {
        CryptotoolOptions cryptotoolOptions = CryptotoolOptionsImpl.createDefault();

        assertThat(cryptotoolOptions, is(notNullValue()));
    }

}