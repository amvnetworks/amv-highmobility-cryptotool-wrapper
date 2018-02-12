package org.amv.highmobility.cryptotool.command;

import org.amv.OperationSystemHelper;
import org.amv.highmobility.cryptotool.Cryptotool;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class KeysJniCommandTest {

    @BeforeClass
    public static void skipWindowsEnvironment() {
        Assume.assumeTrue(!OperationSystemHelper.isWindows());
    }

    @Test
    public void execute() {
        Cryptotool.Keys keys = KeysJniCommand.builder()
                .build()
                .execute(null)
                .blockFirst();

        String privateKey = keys.getPrivateKey();
        String publicKey = keys.getPublicKey();

        assertThat(privateKey, is(notNullValue()));
        assertThat(publicKey, is(notNullValue()));
    }
}