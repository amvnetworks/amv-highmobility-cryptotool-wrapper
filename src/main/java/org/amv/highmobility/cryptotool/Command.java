package org.amv.highmobility.cryptotool;

enum Command {
    VERSION("-v"),
    KEYS("keys"),
    CREATE_SIGNATURE("sign"),
    VERIFY_SIGNATURE("verify"),
    CREATE_HMAC("hmac"),
    VERIFY_HMAC("hmacver"),
    CREATE_ACCESS_CERTIFICATE("access"),
    CREATE_DEVICE_CERTIFICATE("device");

    private String command;

    Command(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
