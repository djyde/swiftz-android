package com.amnoon.proto;

public enum Action {

    LOGIN((byte)0x01),

    LOGIN_RESULT((byte)0x02),

    BREATHE((byte)0x03),

    BREATHE_RESULT((byte)0x04),

    LOGOUT((byte)0x05),

    LOGOUT_RESULT((byte)0x06),

    ENTRIES((byte)0x07),

    ENTRIES_RESULT((byte)0x08),

    DISCONNECT((byte)0x09),

    CONFIRM((byte)0x0A),

    CONFIRM_RESULT((byte)0x0B),

    SERVER((byte)0X0C),

    SERVER_RESULT((byte)0x0D);

    Action(byte value) {}

    public static Action fromByte(byte action) {
        return null;
    }

    public byte value() {
        return 0;
    }

}
