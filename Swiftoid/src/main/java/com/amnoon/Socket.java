package com.amnoon;

import android.os.Handler;

import com.amnoon.crypto.ICrypto;
import com.amnoon.proto.Packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Socket {

    public final static int PACKET_SIZE = 0xFF;
    public final static int LOCAL_PORT_RANDOM = 0;

    public static void send(final Packet packet, final int localPort,
                            final InetAddress remoteAddress, final int remotePort,
                            final ICrypto crypto, final OnResponseListener onResponseListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket socket = (localPort == LOCAL_PORT_RANDOM)
                            ? new DatagramSocket()
                            : new DatagramSocket(localPort);

                    byte[] request = crypto.encrypt(packet.getBytes());
                    socket.send(new DatagramPacket(request, request.length, remoteAddress, remotePort));

                    DatagramPacket received = new DatagramPacket(new byte[PACKET_SIZE], PACKET_SIZE);
                    socket.receive(received);

                    socket.close();

                    final Packet response = Packet.fromBytes(crypto.decrypt(received.getData()));

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (onResponseListener != null) {
                                onResponseListener.onResponse(response);
                            }
                        }
                    });
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public interface OnResponseListener {
        public void onResponse(Packet packet);
    }

}
