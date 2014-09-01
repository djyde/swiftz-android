package com.amnoon;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;

public class AmnoonPacket extends LinkedHashMap<Byte, byte[]> {

    public static AmnoonPacket readFrom(InputStream in) throws IOException {
        byte[] buffer = new byte[1024];
        int read = in.read(buffer, 0, buffer.length);

        AmnoonPacket packet = new AmnoonPacket(buffer[0]);

        // TODO: 检查长度
        // TODO: 检查签名
        // TODO: 解析

        return packet;
    }


    private final int action;

    public AmnoonPacket(int action) {
        this.action = action;
    }

    public void writeTo(OutputStream out) throws IOException {
        SignedOutputStream stream = new SignedOutputStream(out);

        // 包头
        stream.write(action);
        stream.write(length());

        // md5 占位
        stream.holdChecksum();

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

        public void holdChecksum() throws IOException {
            write(new byte[16]);
        }

        @Override
        public synchronized void flush() throws IOException {
            // TODO: 计算 md5 并填充 buf
            super.flush();
        }
    }

}
