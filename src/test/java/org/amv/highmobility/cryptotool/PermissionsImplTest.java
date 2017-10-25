package org.amv.highmobility.cryptotool;

import org.junit.Test;

import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class PermissionsImplTest {
    private static final String PERMISSIONS_IDENTIFIER_HEX = "10";
    private static final String ALL_PERMISSIONS_HEX = "1007FFFFFFFFFF";
    private static final String STANDARD_PERMISSIONS_HEX = "10001F08000040";
    private static final String EMPTY_PERMISSIONS_HEX = "10000000000000";

    @Test
    public void none() throws Exception {
        PermissionsImpl permissions = PermissionsImpl.none();

        String permissionsHex = permissions.getPermissions();
        assertThat(permissionsHex, is(notNullValue()));
        assertThat(permissionsHex.startsWith(PERMISSIONS_IDENTIFIER_HEX), is(true));
        assertThat(permissionsHex, is(equalToIgnoringCase(EMPTY_PERMISSIONS_HEX)));
    }

    @Test
    public void standard() throws Exception {
        PermissionsImpl permissions = PermissionsImpl.builder()
                .diagnosticsRead(true)
                .doorLocksRead(true)
                .doorLocksWrite(true)
                .keyfobPositionRead(true)
                .capabilitiesRead(true)
                .vehicleStatusRead(true)
                .chargeRead(true)
                .build();

        String permissionsHex = permissions.getPermissions();
        assertThat(permissionsHex, is(notNullValue()));
        assertThat(permissionsHex.startsWith(PERMISSIONS_IDENTIFIER_HEX), is(true));
        assertThat(permissionsHex, is(equalToIgnoringCase(STANDARD_PERMISSIONS_HEX)));
    }

    @Test
    public void all() throws Exception {
        PermissionsImpl permissions = PermissionsImpl.builder()
                .certificatesRead(true)
                .certificatesWrite(true)
                .resetWrite(true)

                .capabilitiesRead(true)
                .vehicleStatusRead(true)
                .diagnosticsRead(true)
                .doorLocksRead(true)
                .doorLocksWrite(true)
                .engineRead(true)
                .engineWrite(true)
                .trunkAccessRead(true)

                .trunkAccessWrite(true)
                .trunkAccessLimited(true)
                .wakeUpWrite(true)
                .chargeRead(true)
                .chargeWrite(true)
                .climateRead(true)
                .climateWrite(true)
                .lightsRead(true)

                .lightsWrite(true)
                .windowsWrite(true)
                .rooftopControlRead(true)
                .rooftopControlWrite(true)
                .windscreenRead(true)
                .windscreenWrite(true)
                .honkHornFlashLightsWrite(true)
                .headunitWrite(true)

                .remoteControlRead(true)
                .remoteControlWrite(true)
                .valetModeRead(true)
                .valetModeWrite(true)
                .valetModeActive(true)
                .fuelingWrite(true)
                .heartRateWrite(true)
                .driverFatigueRead(true)

                .vehicleLocationRead(true)
                .naviDestinationWrite(true)
                .theftAlarmRead(true)
                .theftAlarmWrite(true)
                .parkingTicketRead(true)
                .parkingTicketWrite(true)
                .keyfobPositionRead(true)
                .headunitRead(true)
                .build();

        String permissionsHex = permissions.getPermissions();
        assertThat(permissionsHex, is(notNullValue()));
        assertThat(permissionsHex.startsWith(PERMISSIONS_IDENTIFIER_HEX), is(true));
        assertThat(permissionsHex, is(equalToIgnoringCase(ALL_PERMISSIONS_HEX)));
    }


}