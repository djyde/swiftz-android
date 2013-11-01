package com.amnoon.protocal;

public enum Action {

    LOGIN((char)0x01),

    LOGIN_RESULT((char)0x02),

    BREATHE((char)0x03),

    BREATHE_RESULT((char)0x04),

    LOGOUT((char)0x05),

    LOGOUT_RESULT((char)0x06),

    ENTRIES((char)0x07),

    ENTRIES_RESULT((char)0x08),

    DISCONNECT((char)0x09),

    CONFIRM((char)0x0A),

    CONFIRM_RESULT((char)0x0B),

    SERVER((char)0X0C),

    SERVER_RESULT((char)0x0D);

    Action(char value) {}

}
