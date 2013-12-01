package com.amnoon.proto;

public class Packet {
    public static Packet fromBytes(byte[] bytes) {
        return new Packet();
    }

    public Action getAction() {
        return Action.LOGIN;
    }

    public String getString(Field field) {
        return null;
    }

    public byte getByte(Field field) {
        return 0;
    }

    public int getInteger(Field field) {
        return 0;
    }

    public boolean getBoolean(Field field) {
        return false;
    }

    public byte[] getBytes(Field field) {
        return null;
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