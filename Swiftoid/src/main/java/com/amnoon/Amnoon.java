package com.amnoon;

public class Amnoon {

    private static Amnoon ourInstance = new Amnoon();

    public static Amnoon getInstance() {
        return ourInstance;
    }

    private Amnoon() {
    }

    public interface OnRecievedServerListener {
        public void onRecievedServer(String server);
    }

    public interface OnRecievedEntriesListener {
        public void onRecievedEntries(String[] entries);
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

}
