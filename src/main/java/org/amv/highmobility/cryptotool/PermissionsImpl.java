package org.amv.highmobility.cryptotool;

import lombok.Builder;
import lombok.Getter;
import org.apache.commons.codec.binary.Hex;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkState;

@Getter
@Builder(builderClassName = "Builder")
public class PermissionsImpl implements Cryptotool.Permissions {
    private static final byte PERMISSIONS_IDENTIFIER_IN_HEX = 0x10;

    public static PermissionsImpl none() {
        return PermissionsImpl.builder().build();
    }

    private boolean certificatesRead; // Allowed to read list of stored certificates (trusted devices)
    private boolean certificatesWrite;    //Allowed to revoke access certificates
    private boolean resetWrite; //	Allowed to reset the Car SDK

    private boolean capabilitiesRead; //	Allowed to get car capabilities
    private boolean vehicleStatusRead; //	Allowed to get vehicle status
    private boolean diagnosticsRead; //	Allowed to get diagnostics and maintenance state
    private boolean doorLocksRead; //	Allowed to get the lock state
    private boolean doorLocksWrite; //	Allowed to lock or unlock the car
    private boolean engineRead; //	Allowed to get the ignition state
    private boolean engineWrite; //	Allowed to turn on/off the engine
    private boolean trunkAccessRead; //	Allowed to get the trunk state

    private boolean trunkAccessWrite; //	Allowed to open/close or lock/unlock the trunk
    private boolean trunkAccessLimited; //	If the access to the trunk is limited to one time, whereas a 0 means unlimited
    private boolean wakeUpWrite; //	Allowed to wake up the car
    private boolean chargeRead; //	Allowed to get the charge state
    private boolean chargeWrite; //	Allowed to start/stop charging or set the charge limit
    private boolean climateRead; //	Allowed to get the climate state
    private boolean climateWrite; //	Allowed to set climate profile and start/stop HVAC
    private boolean lightsRead; //	Allowed to get the lights state

    private boolean lightsWrite; //	Allowed to control lights
    private boolean windowsWrite; //	Allowed to open/close windows
    private boolean rooftopControlRead; //	Allowed to get the rooftop state
    private boolean rooftopControlWrite; //	Allowed to control the rooftop
    private boolean windscreenRead; //	Allowed to get the windscreen state
    private boolean windscreenWrite; //	Allowed to set the windscreen damage
    private boolean honkHornFlashLightsWrite; //	Allowed to honk the horn and flash lights and activate emergency flasher
    private boolean headunitWrite; //	Allowed to send notifications, messages, videos, URLs, images and text input to the headunit

    private boolean remoteControlRead; //	Allowed to get the control mode
    private boolean remoteControlWrite; //	Allowed to remote control the car
    private boolean valetModeRead; //	Allowed to get the valet mode
    private boolean valetModeWrite; //	Allowed to set the valet mode
    private boolean valetModeActive;//	Only allowed to use the car in valet mode
    private boolean fuelingWrite; //	Allowed to open the car gas flap
    private boolean heartRateWrite; //	Allowed to send the heart rate to the car
    private boolean driverFatigueRead; //	Allowed to get driver fatigue warnings

    private boolean vehicleLocationRead; //	Allowed to get the vehicle location
    private boolean naviDestinationWrite; //	Allowed to set the navigation destination
    private boolean theftAlarmRead; //	Allowed to get the theft alarm state
    private boolean theftAlarmWrite; //	Allowed to set the theft alarm
    private boolean parkingTicketRead; //	Allowed to get the parking ticket
    private boolean parkingTicketWrite; //	Allowed to start/end parking
    private boolean keyfobPositionRead; //	Allowed to get the keyfob position
    private boolean headunitRead; //	Allowed to receive notifications and messages from the headunit

    /**
     * Permissions specification
     * <p>
     * Per the Access Certificate format, the permissions field consists of up to 16 bytes of data.
     * For the Auto API, the first 8 bytes are used to bitwise manifest the user permissions.
     * Unless otherwise specified, the bit is set to 1 if the user has permission to use the API, otherwise 0.
     * <p>
     * Data[0]		Permissions Identifier
     * 0x10		Auto API permissions
     * Data[1]		Bitwise values
     * Bit 0	{@link #certificatesRead}
     * Bit 1	{@link #certificatesWrite}
     * Bit 2	{@link #resetWrite}
     * Data[2]		Bitwise values
     * Bit 0	{@link #capabilitiesRead}
     * Bit 1	{@link #vehicleStatusRead}
     * Bit 2	{@link #diagnosticsRead}
     * Bit 3	{@link #doorLocksRead}
     * Bit 4	{@link #doorLocksWrite}
     * Bit 5	{@link #engineRead}
     * Bit 6	{@link #engineWrite}
     * Bit 7	{@link #trunkAccessRead}
     * Data[3]		Bitwise values
     * Bit 0	{@link #trunkAccessWrite}
     * Bit 1	{@link #trunkAccessLimited}
     * Bit 2	{@link #wakeUpWrite}
     * Bit 3	{@link #chargeRead}
     * Bit 4	{@link #chargeWrite}
     * Bit 5	{@link #climateRead}
     * Bit 6	{@link #climateWrite}
     * Bit 7	{@link #lightsRead}
     * Data[4]		Bitwise values
     * Bit 0	{@link #lightsWrite}
     * Bit 1	{@link #windowsWrite}
     * Bit 2	{@link #rooftopControlRead}
     * Bit 3	{@link #rooftopControlWrite}
     * Bit 4	{@link #windscreenRead}
     * Bit 5	{@link #windscreenWrite}
     * Bit 6	{@link #honkHornFlashLightsWrite}
     * Bit 7	{@link #headunitWrite}
     * Data[5]		Bitwise values
     * Bit 0	{@link #certificatesRead}remoteControlRead
     * Bit 1	{@link #certificatesRead}remoteControlWrite
     * Bit 2	{@link #certificatesRead}valetModeRead
     * Bit 3	{@link #certificatesRead}valetModeWrite
     * Bit 4	{@link #certificatesRead}valetMode.active
     * Bit 5	{@link #certificatesRead}fuelingWrite
     * Bit 6	{@link #certificatesRead}heartRateWrite
     * Bit 7	{@link #certificatesRead}driverFatigueRead
     * Data[6]		Bitwise values
     * Bit 0	{@link #certificatesRead}vehicleLocationRead
     * Bit 1	{@link #certificatesRead}naviDestinationWrite
     * Bit 2	{@link #certificatesRead}theftAlarmRead
     * Bit 3	{@link #certificatesRead}theftAlarmWrite
     * Bit 4	{@link #certificatesRead}parkingTicketRead
     * Bit 5	{@link #certificatesRead}parkingTicketWrite
     * Bit 6	{@link #certificatesRead}keyfobPositionRead
     * Bit 7	{@link #certificatesRead}headunitRead
     */
    @Override
    public String getPermissions() {
        return Hex.encodeHexString(asByteArray());
    }

    private byte[] asByteArray() {
        return new byte[]{
                PERMISSIONS_IDENTIFIER_IN_HEX,
                asByte(new Boolean[]{false, false, false, false, false, resetWrite, certificatesWrite, certificatesRead}),
                asByte(new Boolean[]{trunkAccessRead, engineWrite, engineRead, doorLocksWrite, doorLocksRead, diagnosticsRead, vehicleStatusRead, capabilitiesRead}),
                asByte(new Boolean[]{lightsRead, climateWrite, climateRead, chargeWrite, chargeRead, wakeUpWrite, trunkAccessLimited, trunkAccessWrite}),
                asByte(new Boolean[]{headunitWrite, honkHornFlashLightsWrite, windscreenWrite, windscreenRead, rooftopControlWrite, rooftopControlRead, windowsWrite, lightsWrite}),
                asByte(new Boolean[]{driverFatigueRead, heartRateWrite, fuelingWrite, valetModeActive, valetModeWrite, valetModeRead, remoteControlWrite, remoteControlRead}),
                asByte(new Boolean[]{headunitRead, keyfobPositionRead, parkingTicketWrite, parkingTicketRead, theftAlarmWrite, theftAlarmRead, naviDestinationWrite, vehicleLocationRead}),
        };
    }

    private static byte asByte(Boolean[] bool) {
        checkState(bool.length == 8);

        return (byte) ((bool[0] ? 1 << 7 : 0) +
                (bool[1] ? 1 << 6 : 0) +
                (bool[2] ? 1 << 5 : 0) +
                (bool[3] ? 1 << 4 : 0) +
                (bool[4] ? 1 << 3 : 0) +
                (bool[5] ? 1 << 2 : 0) +
                (bool[6] ? 1 << 1 : 0) +
                (bool[7] ? 1 : 0));
    }
}
