package com.amnoon;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class AmnoonPacket extends LinkedHashMap<Byte, byte[]> {

    public static AmnoonPacket readFrom(InputStream in) throws IOException {
        byte[] buffer = new byte[1024];
        int read = in.read(buffer, 0, buffer.length);
        if (read < 18 || read != buffer[1]) {
            throw new IOException("Illegal packet");
        }

        ByteBuffer bytes = ByteBuffer.wrap(buffer, 0, read);

        AmnoonPacket packet = new AmnoonPacket(bytes.get(0));

        // 读出 MD5 部分
        byte[] checksum = new byte[16];
        bytes.position(2);
        bytes.get(checksum);

        // 将 MD5 部分清零
        bytes.position(2);
        bytes.put(new byte[16]);

        try {
            byte[] calculated = MessageDigest.getInstance("MD5").digest(bytes.array());
            if (!Arrays.equals(checksum, calculated)) {
                return null;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        while (bytes.remaining() > 2) {
            byte field = bytes.get();
            int length = bytes.getInt() - 2;

            // 理论上不会出现长度不匹配的情况
            // 如果有，说明数据不完整，那就不读了
            if (length > bytes.remaining()) {
                break;
            }

            byte[] value = new byte[length];
            bytes.get(value);

            packet.put(field, value);
        }

        return packet;
    }


    private final int action;

    public AmnoonPacket(int action) {
        this.action = action;
    }

    public byte get(int field, byte defaultValue) {
        return containsKey(field)
                ? get(field)[0]
                : defaultValue;
    }

    public boolean getBoolean(int field, boolean defaultValue) {
        return containsKey(field)
                ? (get(field)[0] != 0)
                : defaultValue;
    }

    public int getInt(int field, int defaultValue) {
        return containsKey(field)
                ? ByteBuffer.wrap(get(field)).getInt()
                : defaultValue;
    }

    public String getString(int field) {
        return containsKey(field)
                ? new String(get(field), Charset.forName("GBK"))
                : null;
    }

    public void writeTo(OutputStream out) throws IOException {
        SignedOutputStream stream = new SignedOutputStream(out);

        // 包头
        stream.write(action);
        stream.write(length());

        // md5 占位
        stream.checksum();

        for (Entry<Byte, byte[]> entry : entrySet()) {
            stream.write(entry.getKey().byteValue());
            stream.write(entry.getValue());
        }

        stream.flush();
    }

    public int length() {
        int length = 0;

        length += 1;    // action
        length += 16;   // md5

        for (byte[] value : values()) {
            length += 1;            // field key
            length += value.length; // field value
        }

        return length;
    }


    private class SignedOutputStream extends BufferedOutputStream {

        public SignedOutputStream(OutputStream out) {
            super(out, length());
        }

        public void checksum() throws IOException {
            write(new byte[16]);
        }

        @Override
        public synchronized void flush() throws IOException {
            try {
                byte[] checksum = MessageDigest.getInstance("MD5").digest(buf);
                System.arraycopy(checksum, 0, buf, 2, 16);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            super.flush();
        }
    }

    @SuppressWarnings("unused")
    public static final class Action {

        public static final int LOGIN_REQ = 1;
        public static final int LOGIN_RES = 2;

        public static final int PING = 3;
        public static final int PONG = 4;

        public static final int LOGOUT_REQ = 5;
        public static final int LOGOUT_RES = 6;

        public static final int QUERY_ENTRIES_REQ = 7;
        public static final int QUERY_ENTRIES_RES = 8;

        public static final int DISCONNECT = 9;

        public static final int ACL_REQ = 10;
        public static final int ACL_RES = 11;

        public static final int FIND_SERVER_REQ = 12;
        public static final int FIND_SERVER_RES = 13;

    }

    @SuppressWarnings("unused")
    public static final class Field {

        public static final int USERNAME = 1;

        public static final int PASSWORD = 2;

        public static final int SUCCESS = 3;

        public static final int MAC = 7;

        public static final int SESSION = 8;

        public static final int IP = 9;

        public static final int ENTRY = 10;

        public static final int MESSAGE = 11;

        public static final int SERVER_PRIMARY = 12;
        public static final int SERVER_SECONDARY = 13;

        public static final int DHCP = 14;

        public static final int WEBSITE = 19;

        public static final int INDEX = 20;

        public static final int VERSION = 31;

        public static final int REASON = 36;

        public static final int BLOCK2A = 42;
        public static final int BLOCK2B = 43;
        public static final int BLOCK2C = 44;
        public static final int BLOCK2D = 45;
        public static final int BLOCK2E = 46;
        public static final int BLOCK2F = 47;

        public static final int QUOTA_TRAFFIC_IN = 52;
        public static final int QUOTA_TRAFFIC_OUT = 53;
        public static final int QUOTA_TRAFFIC_TOTAL = 54;
        public static final int QUOTA_TRAFFIC_ACL = 55;
        public static final int QUOTA_DURATION = 56;

    }

}
