package us.xingrz.swiftz.android.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import us.xingrz.swiftz.android.SwiftzContact;
import us.xingrz.swiftz.android.util.ExtendedBroadcastReceiver;

public class SwiftzService extends Service {

    private ExtendedBroadcastReceiver wifiStateReceiver = new WifiConnectionStateReceiver();

    @Override
    public void onCreate() {
        super.onCreate();
        wifiStateReceiver.register(this);
    }

    @Override
    public void onDestroy() {
        wifiStateReceiver.unregister(this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        if (SwiftzContact.ACTION_CONNECT.equals(action)) {
            connect(intent.getStringExtra(SwiftzContact.EXTRA_ACCOUNT));
            return START_STICKY;
        }

        if (SwiftzContact.ACTION_DISCONNECT.equals(action)) {
            disconnect();
            return START_STICKY;
        }

        return START_STICKY;
    }

    private void connect(String account) {

    }

    private void disconnect() {

    }

    private void maybeEstablishConnection() {

    }

    private void indicateConnected() {

    }

    private void indicateDisconnected() {
        sendBroadcast(new Intent(SwiftzContact.UPDATE_CONNECTION_STATE));
    }

    private final class WifiConnectionStateReceiver extends ExtendedBroadcastReceiver {

        public WifiConnectionStateReceiver() {
            super(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {
                maybeEstablishConnection();
            } else {
                indicateDisconnected();
            }
        }

    }

}
