package us.xingrz.common;

import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UdpInputStream extends InputStream {

    private final WifiManager.MulticastLock lock;

    private final DatagramSocket socket;

    public UdpInputStream(WifiManager wifiManager, int port, String tag) throws SocketException {
        lock = wifiManager.createMulticastLock(tag);

        socket = new DatagramSocket(port);
        socket.setBroadcast(true);
    }

    @Override
    public int read() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int read(@NonNull byte[] buffer) throws IOException {
        return read(buffer, 0, buffer.length);
    }

    @Override
    public int read(@NonNull byte[] buffer, int byteOffset, int byteCount) throws IOException {
        lock.acquire();
        DatagramPacket packet = new DatagramPacket(buffer, byteOffset, byteCount);
        socket.receive(packet);
        lock.release();
        return packet.getLength();
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

}
