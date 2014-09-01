package us.xingrz.common;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UdpOutputStream extends OutputStream {

    private final int port;
    private final InetAddress address;

    private final DatagramSocket socket;

    public UdpOutputStream(int port, InetAddress address) throws SocketException {
        this.port = port;
        this.address = address;

        socket = new DatagramSocket();
    }

    @Override
    public void write(int oneByte) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(@NonNull byte[] buffer) throws IOException {
        write(buffer, 0, buffer.length);
    }

    @Override
    public void write(@NonNull byte[] buffer, int offset, int count) throws IOException {
        socket.send(new DatagramPacket(buffer, offset, count, address, port));
    }

    @Override
    public void close() throws IOException {
        super.close();
    }

}
