package com.amnoon.proto;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class Packet {

    private Action action;
    private ArrayList<Field> fields = new ArrayList<Field>();
    private ArrayList<byte[]> values = new ArrayList<byte[]>();

    public Packet(Action action) {
        this.action = action;
    }

    public static Packet fromBytes(byte[] bytes) {
        if (bytes.length < 18) {
            return null;
        }

        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        byte[] checksum = new byte[16];
        buffer.get(checksum, 2, 16);

        buffer.put(new byte[16], 2, 16);
        byte[] verifies = new byte[16];

        try {
            verifies = MessageDigest.getInstance("MD5").digest(buffer.array());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if (!Arrays.equals(verifies, checksum)) {
            return null;
        }

        Action action = Action.fromByte(bytes[0]);

        if (action == null) {
            return null;
        }

        Packet packet = new Packet(action);
        int position = 18;

        while (position < bytes.length) {
            Field field = Field.fromByte(bytes[position]);

            int length = bytes[position + 1];
            byte[] value = new byte[length - 2];
            buffer.get(value, position + 2, length - 2);

            packet.fields.add(field);
            packet.values.add(value);

            position += length;
        }

        return packet;
    }

    public byte[] getBytes() {
        byte length = 18;
        for (byte[] item : values) {
            length += item.length + 2;
        }

        ByteBuffer buffer = ByteBuffer.allocate(length);

        buffer.put(action.value());
        buffer.put(length);
        buffer.position(18);

        for (int i = 0; i < fields.size(); i++) {
            buffer.put(fields.get(i).value());

            byte[] value = values.get(i);

            buffer.put((byte) value.length);
            buffer.put(value);
        }

        byte[] checksum = new byte[16];

        try {
            checksum = MessageDigest.getInstance("MD5").digest(buffer.array());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        buffer.position(2);
        buffer.put(checksum);

        return buffer.array();
    }

    public Action getAction() {
        return this.action;
    }

    public String getString(Field field, String alternative) {
        if (!fields.contains(field)) {
            return alternative;
        }

        Object value = values.get(fields.indexOf(field));

        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof byte[]) {
            return String.valueOf(value);
        } else {
            return alternative;
        }
    }

    public String[] getStringList(Field field, String[] alternative) {
        ArrayList<String> result = new ArrayList<String>();

        for (int i = 0; i < this.fields.size(); i++) {
            if (this.fields.get(i) == field) {
                Object value = this.values.get(i);

                if (value instanceof String) {
                    result.add((String) value);
                } else if (value instanceof byte[]) {
                    result.add(String.valueOf(value));
                }
            }
        }

        return (result.size() == 0) ? alternative : (String[]) result.toArray();
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
        this.fields.add(field);
        //this.values.add(value);
    }

    public void putByte(Field field, byte value) {
        this.fields.add(field);
        //this.values.add(value);
    }

    public void putInteger(Field field, int value) {
        this.fields.add(field);
        //this.values.add(value);
    }

    public void putBoolean(Field field, boolean value) {
        this.fields.add(field);
        //this.values.add(value);
    }

    public void putBytes(Field field, byte[] value) {
        this.fields.add(field);
        this.values.add(value);
    }
}