package us.xingrz.swiftz.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.amnoon.crypto.Hdefcbag;
import com.amnoon.proto.Packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class SwiftzService extends Service {

    private final Binder mBinder = new SwiftzBinder();

    public class SwiftzBinder extends Binder {
        public SwiftzService getService() {
            return SwiftzService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private OnLoginListener onLoginListener = null;
    private OnLogoutListener onLogoutListener = null;
    private OnBreathedListener onBreathedListener = null;
    private OnDisconnectedListener onDisconnectedListener = null;
    private OnSetupCompletedListener onSetupCompletedListener = null;

    private String session = null;

    public void setup(OnSetupCompletedListener onSetupCompletedListener) {
        this.onSetupCompletedListener = onSetupCompletedListener;

        // ...
    }

    public void login(String username, String password, String entry, OnLoginListener onLoginListener) {
        this.onLoginListener = onLoginListener;

        // ...
    }

    public void logout(OnLogoutListener onLogoutListener) {
        this.onLogoutListener = onLogoutListener;

        // ...
    }

    public void setOnBreathedListener(OnBreathedListener onBreathedListener) {
        this.onBreathedListener = onBreathedListener;
    }

    public void setOnDisconnectedListener(OnDisconnectedListener onDisconnectedListener) {
        this.onDisconnectedListener = onDisconnectedListener;
    }

    public boolean isOnline() {
        return this.session != null;
    }

    public interface OnSetupCompletedListener {
        public void onSetupCompleted(String server, String[] entries);
    }

    public interface OnLoginListener {
        public void onLogin(boolean success, String message, String website, String session);
    }

    public interface OnLogoutListener {
        public void onLogout();
    }

    public interface OnBreathedListener {
        public void onBreathed(String session, int index);
    }

    public interface OnDisconnectedListener {
        public void onDisconnected(String session, DisconnectedReason reason);
    }

    public enum DisconnectedReason {
        DEAD((char)0x00),
        KILLED((char)0x01),
        DRAINED((char)0x02);

        DisconnectedReason(char value) {}
    }

    private class ServerListener extends Thread {
        private boolean running = true;
        private InetAddress address;

        ServerSocket(InetAddress address) {
            this.address = address;
        }

        ServerSocket(String address) throws UnknownHostException {
            this.address = InetAddress.getByName(address);
        }

        ServerSocket(byte[] address) throws UnknownHostException {
            this.address = InetAddress.getByAddress(address);
        }

        public void finish() {
            running = false;
        }

        @Override
        public void run() {
            try {
                DatagramSocket listener = new DatagramSocket(3848, address);
                while (running) {
                    DatagramPacket answer = new DatagramPacket(new byte[255], 255);
                    listener.receive(answer);

                    Packet packet = Packet.fromBytes(Hdefcbag.decrypt(answer.getData()));

                    switch (packet.getAction()) {
                        case SERVER_RESULT:
                            
                            break;
                        case ENTRIES_RESULT:
                            break;
                        case LOGIN_RESULT:
                            if (onLoginListener != null) {

                            }
                            break;
                        case BREATHE_RESULT:
                            break;
                        case LOGOUT_RESULT:
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
