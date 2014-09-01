package us.xingrz.common;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class SecureStream {

    protected static final int BIT_1 = 0x80;
    protected static final int BIT_2 = 0x40;
    protected static final int BIT_3 = 0x20;
    protected static final int BIT_4 = 0x10;
    protected static final int BIT_5 = 0x08;
    protected static final int BIT_6 = 0x04;
    protected static final int BIT_7 = 0x02;
    protected static final int BIT_8 = 0x01;

    private final Object lock = new Object();

    private final SecureInputStream secureInputStream = new SecureInputStream();
    private final SecureOutputStream secureOutputStream = new SecureOutputStream();

    private InputStream rawInputStream;
    private OutputStream rawOutputStream;

    public SecureStream(InputStream rawInputStream, OutputStream rawOutputStream) {
        this.rawInputStream = rawInputStream;
        this.rawOutputStream = rawOutputStream;
    }

    public InputStream getInputStream() {
        return secureInputStream;
    }

    public OutputStream getOutputStream() {
        return secureOutputStream;
    }

    protected abstract void encrypt(@NonNull byte[] buffer, int offset, int count);

    protected abstract void decrypt(@NonNull byte[] buffer, int byteOffset, int byteCount);

    private class SecureInputStream extends InputStream {

        @Override
        public int read() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read(@NonNull byte[] buffer) throws IOException {
            synchronized (lock) {
                if (rawInputStream == null) {
                    return -1;
                }
            }

            int read = rawInputStream.read(buffer);
            if (read == -1) {
                return read;
            }

            decrypt(buffer, 0, buffer.length);
            return read;
        }

        @Override
        public int read(@NonNull byte[] buffer, int byteOffset, int byteCount) throws IOException {
            synchronized (lock) {
                if (rawInputStream == null) {
                    return -1;
                }
            }

            int read = rawInputStream.read(buffer, byteOffset, byteCount);
            if (read == -1) {
                return read;
            }

            decrypt(buffer, byteOffset, byteCount);
            return read;
        }

        @Override
        public void close() throws IOException {
            synchronized (lock) {
                if (rawInputStream == null) {
                    return;
                }
            }

            rawInputStream.close();
            rawInputStream = null;
        }

    }

    private class SecureOutputStream extends OutputStream {

        @Override
        public void write(int oneByte) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void write(@NonNull byte[] buffer) throws IOException {
            synchronized (lock) {
                if (rawOutputStream == null) {
                    throw new IOException("output stream closed");
                }
            }

            encrypt(buffer, 0, buffer.length);
            rawOutputStream.write(buffer);
        }

        @Override
        public void write(@NonNull byte[] buffer, int offset, int count) throws IOException {
            synchronized (lock) {
                if (rawOutputStream == null) {
                    throw new IOException("output stream closed");
                }
            }

            encrypt(buffer, offset, count);
            rawOutputStream.write(buffer, offset, count);
        }

        @Override
        public void close() throws IOException {
            synchronized (lock) {
                if (rawOutputStream == null) {
                    return;
                }
            }

            rawOutputStream.close();
            rawOutputStream = null;
        }

    }

}
