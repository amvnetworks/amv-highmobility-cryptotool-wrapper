package org.amv.highmobility.cryptotool;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;


public class BinariesTest {
    @Test
    public void defaultBinary() throws Exception {
        final Binary binary = Binaries.defaultBinary();
        assertThat(binary, is(notNullValue()));
    }

    @Test
    public void windowsBinary() throws Exception {
        final Binary binary = Binaries.windowsBinary();
        assertThat(binary, is(notNullValue()));
    }

    @Test
    public void unixBinary() throws Exception {
        final Binary binary = Binaries.unixBinary();
        assertThat(binary, is(notNullValue()));
    }

    @Test
    public void redhatBinary() throws Exception {
        final Binary binary = Binaries.redhatBinary();
        assertThat(binary, is(notNullValue()));
    }

}