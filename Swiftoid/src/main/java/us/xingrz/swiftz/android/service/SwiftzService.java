package us.xingrz.swiftz.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.amnoon.Socket;
import com.amnoon.crypto.Crypto;
import com.amnoon.crypto.Hdefcbag;
import com.amnoon.crypto.ICrypto;
import com.amnoon.proto.Action;
import com.amnoon.proto.Field;
import com.amnoon.proto.Packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
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

    private final static int START_INDEX = 0x01000000;

    private OnBreathedListener onBreathedListener;
    private OnDisconnectedListener onDisconnectedListener;

    private String session = null;

    private InetAddress server = null;
    private String entry = null;

    private int index = START_INDEX;

    public void setup(OnSetupCompletedListener onSetupCompletedListener) {
        //server = InetAddress.getByAddress(packet.getBytes(Field.SERVER, null));
        //String[] entries = packet.getStringList(Field.ENTRY, null);
        //onSetupCompletedListener.onSetupCompleted(server, entries);
    }

    public void login(String username, String password, String entry, final OnLoginListener onLoginListener) {
        if (session != null) {
            return;
        }

        Packet packet = new Packet(Action.LOGIN);
        packet.putBytes(Field.MAC, new byte[] { 0x00 });
        packet.putString(Field.USERNAME, username);
        packet.putString(Field.PASSWORD, password);
        packet.putString(Field.IP, "");
        packet.putString(Field.ENTRY, entry);
        packet.putBoolean(Field.DHCP, true);
        packet.putString(Field.VERSION, "3.7.8");

        Socket.send(packet, 3848, server, 3848, new Hdefcbag(), new Socket.OnResponseListener() {
            @Override
            public void onResponse(Packet packet) {
                if (packet != null && packet.getAction() == Action.LOGIN_RESULT) {
                    session = packet.getString(Field.SESSION, null);

                    startBreathing();

                    if (onLoginListener != null) {
                        boolean success = packet.getBoolean(Field.SUCCESS, false);
                        String message = packet.getString(Field.MESSAGE, null);
                        String website = packet.getString(Field.WEBSITE, null);
                        onLoginListener.onLogin(success, message, website, session);
                    }
                }
            }
        });
    }

    public void logout(final OnLogoutListener onLogoutListener) {
        if (session == null) {
            return;
        }

        stopBreathing();

        Packet packet = new Packet(Action.LOGOUT);
        packet.putString(Field.SESSION, session);
        packet.putString(Field.IP, "");
        packet.putBytes(Field.MAC, new byte[]{0x00});
        packet.putInteger(Field.INDEX, index);
        packet.putBytes(Field.BLOCK2A, new byte[4]);
        packet.putBytes(Field.BLOCK2B, new byte[4]);
        packet.putBytes(Field.BLOCK2C, new byte[4]);
        packet.putBytes(Field.BLOCK2D, new byte[4]);
        packet.putBytes(Field.BLOCK2E, new byte[4]);
        packet.putBytes(Field.BLOCK2F, new byte[4]);

        Socket.send(packet, 3848, server, 3848, new Hdefcbag(), new Socket.OnResponseListener() {
            @Override
            public void onResponse(Packet packet) {
                if (packet != null && packet.getAction() == Action.LOGOUT_RESULT) {
                    if (packet.getBoolean(Field.SUCCESS, false)) {
                        session = null;
                        if (onLogoutListener != null) {
                            onLogoutListener.onLogout();
                        }
                    }
                }
            }
        });
    }

    public void setOnBreathedListener(OnBreathedListener onBreathedListener) {
        this.onBreathedListener = onBreathedListener;
    }

    public void removeOnBreathedListener() {
        this.onBreathedListener = null;
    }

    public void setOnDisconnectedListener(OnDisconnectedListener onDisconnectedListener) {
        this.onDisconnectedListener = onDisconnectedListener;
    }

    public void removeOnDisconnectedListener() {
        this.onDisconnectedListener = null;
    }

    public boolean isOnline() {
        return (this.session != null);
    }

    private void startBreathing() {
        /*int breathedIndex = packet.getInteger(Field.INDEX, START_INDEX);

        if (packet.getBoolean(Field.SUCCESS, false)) {
            index = breathedIndex + 3;
        }

        if (onBreathedListener != null) {
            onBreathedListener.onBreathed(session, index);
        }
        break;*/
    }

    private void stopBreathing() {

    }

    public interface OnSetupCompletedListener {
        public void onSetupCompleted(InetAddress server, String[] entries);
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
        DEAD((byte)0x00),
        KILLED((byte)0x01),
        DRAINED((byte)0x02);

        DisconnectedReason(byte value) {}
    }

}
