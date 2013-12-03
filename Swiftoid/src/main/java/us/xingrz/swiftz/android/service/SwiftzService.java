package us.xingrz.swiftz.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.amnoon.Socket;
import com.amnoon.crypto.Hdefcbag;
import com.amnoon.proto.Action;
import com.amnoon.proto.Field;
import com.amnoon.proto.Packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class SwiftzService extends Service {

    /**
     * Binding
     */

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

    /**
     * Message Handling
     */

    private final static int MSG_SERVER     = 0x00;
    private final static int MSG_ENTRIES    = 0x01;
    private final static int MSG_LOGIN      = 0x02;
    private final static int MSG_CONFIRM    = 0x03;
    private final static int MSG_BREATHE    = 0x04;
    private final static int MSG_LOGOUT     = 0x05;
    private final static int MSG_DISCONNECT = 0x06;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DISCONNECT:
                    if (msg.obj != null && onDisconnectedListener != null) {
                        DisconnectedReason reason = (DisconnectedReason) msg.obj;
                        onDisconnectedListener.onDisconnected(session, reason);
                    }
                    session = null;
                    break;
            }
        }
    };

    /**
     * Event Listeners
     */

    private OnBreathedListener onBreathedListener;
    private OnDisconnectedListener onDisconnectedListener;

    public interface OnSetupCompletedListener {
        public void onSetupCompleted(InetAddress server, String[] entries);
    }

    public interface OnLoginListener {
        public void onLogin(boolean success, String message, String website, String session);
    }

    public interface OnLogoutListener {
        public void onLogout(String session);
    }

    public interface OnBreathedListener {
        public void onBreathed(String session, int index);
    }

    public interface OnDisconnectedListener {
        public void onDisconnected(String session, DisconnectedReason reason);
    }

    /**
     * Public Methods
     */

    private final static int START_INDEX = 0x01000000;
    private final static byte[] SOCKET_INIT = "info sock ini".getBytes();
    private final static String INIT_SERVER = "1.1.1.8";
    private final static String FAKE_SESSION = "0123456789";

    private String session = null;

    private InetAddress server = null;
    private String entry = null;

    private InetAddress ip = null;
    private byte[] mac = new byte[4];

    private int index = START_INDEX;

    private Timer breathing;
    private NotificationListener notification;

    public void setup(OnSetupCompletedListener onSetupCompletedListener) {
        try {
            new DatagramSocket(3848).send(new DatagramPacket(SOCKET_INIT, SOCKET_INIT.length,
                    InetAddress.getByName(INIT_SERVER), 3850));

            // TODO: init device info

            setupServer(onSetupCompletedListener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupServer(final OnSetupCompletedListener onSetupCompletedListener) throws UnknownHostException {
        Packet serverSend = new Packet(Action.SERVER);
        serverSend.putString(Field.SESSION, FAKE_SESSION);
        serverSend.putString(Field.IP, ip.toString());
        serverSend.putBytes(Field.MAC, mac);

        Socket.send(serverSend, 3848, InetAddress.getByName(INIT_SERVER), 3850, new Hdefcbag(), new Socket.OnResponseListener() {
            @Override
            public void onResponse(Packet serverReceived) {
                if (serverReceived != null && serverReceived.getAction() == Action.SERVER_RESULT) {
                    try {
                        server = InetAddress.getByAddress(serverReceived.getBytes(Field.SERVER, new byte[4]));
                        setupEntries(onSetupCompletedListener);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setupEntries(final OnSetupCompletedListener onSetupCompletedListener) {
        Packet entrySend = new Packet(Action.ENTRIES);
        entrySend.putString(Field.SESSION, FAKE_SESSION);
        entrySend.putBytes(Field.MAC, mac);

        Socket.send(entrySend, 3848, server, 3848, new Hdefcbag(), new Socket.OnResponseListener() {
            @Override
            public void onResponse(Packet entryReceived) {
                if (entryReceived != null && entryReceived.getAction() == Action.ENTRIES_RESULT) {
                    String[] entries = entryReceived.getStringList(Field.ENTRY, new String[0]);

                    if (onSetupCompletedListener != null) {
                        onSetupCompletedListener.onSetupCompleted(server, entries);
                    }
                }
            }
        });
    }

    public void login(String username, String password, String entry, final OnLoginListener onLoginListener) {
        if (session != null) {
            return;
        }

        Packet packet = new Packet(Action.LOGIN);
        packet.putBytes(Field.MAC, mac);
        packet.putString(Field.USERNAME, username);
        packet.putString(Field.PASSWORD, password);
        packet.putString(Field.IP, ip.getHostAddress());
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
        packet.putString(Field.IP, ip.getHostAddress());
        packet.putBytes(Field.MAC, mac);
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
                        if (onLogoutListener != null) {
                            onLogoutListener.onLogout(session);
                        }
                        session = null;
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
        try {
            notification = new NotificationListener(server);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        breathing = new Timer();
        breathing.schedule(new Breathing(), 0, 30 * 1000);
    }

    private void stopBreathing() {
        breathing.cancel();
        breathing = null;

        notification.finish();
        notification = null;
    }

    /**
     * Inner Classes
     */

    public enum DisconnectedReason {
        DEAD((byte)0x00),
        KILLED((byte)0x01),
        DRAINED((byte)0x02);

        DisconnectedReason(byte value) {}
    }

    private class Breathing extends TimerTask {
        @Override
        public void run() {
            Packet packet = new Packet(Action.BREATHE);
            packet.putString(Field.SESSION, session);
            packet.putString(Field.IP, ip.getHostAddress());
            packet.putBytes(Field.MAC, mac);
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
                            index = packet.getInteger(Field.INDEX, START_INDEX) + 3;

                            if (onBreathedListener != null) {
                                onBreathedListener.onBreathed(session, index);
                            }
                        }
                    }
                }
            });
        }
    }

    private class NotificationListener extends Thread {
        private boolean running = true;
        private InetAddress server;
        private DatagramSocket socket;

        NotificationListener(InetAddress server) throws SocketException {
            this.server = server;
            this.socket = new DatagramSocket(4999);
        }

        public void finish() {
            running = false;
            this.socket.close();
            this.server = null;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    DatagramPacket received = new DatagramPacket(new byte[0xFF], 0xFF);
                    socket.receive(received);

                    if (received.getAddress() != this.server) {
                        // ignore those not from our server
                        continue;
                    }

                    Packet packet = Packet.fromBytes(received.getData());

                    if (packet == null || packet.getAction() != Action.DISCONNECT) {
                        // ignore invalid packets
                        continue;
                    }

                    switch (packet.getByte(Field.REASON, (byte) 0x00)) {
                        case 0x00:
                            handler.obtainMessage(MSG_DISCONNECT, DisconnectedReason.DEAD);
                            break;
                        case 0x01:
                            handler.obtainMessage(MSG_DISCONNECT, DisconnectedReason.KILLED);
                            break;
                        case 0x02:
                            handler.obtainMessage(MSG_DISCONNECT, DisconnectedReason.DRAINED);
                            break;
                        default:
                            handler.obtainMessage(MSG_DISCONNECT);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
