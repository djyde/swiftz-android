package com.amnoon.proto;

public class Packet {
    public static Packet fromBytes(byte[] bytes) {
        return new Packet();
    }

    public Action getAction() {
        return Action.LOGIN;
    }

    public String getString(Field field, String alternative) {
        return alternative;
    }

    public String[] getStringList(Field field, String[] alternative) {
        return alternative;
    }

    public byte getByte(Field field, byte alternative) {
        return alternative;
    }

    public int getInteger(Field field, int alternative) {
        return alternative;
    }

    public boolean getBoolean(Field field, boolean alternative) {
        return alternative;
    }

    public byte[] getBytes(Field field, byte[] alternative) {
        return alternative;
    }

    public void putString(Field field, String value) {

    }

    public void putByte(Field field, byte value) {

    }

    public void putInteger(Field field, int value) {

    }

    public void putBoolean(Field field, boolean value) {

    }

    public void putBytes(Field field, byte[] value) {

    }
}