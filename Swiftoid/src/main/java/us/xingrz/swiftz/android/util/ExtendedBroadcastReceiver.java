package us.xingrz.swiftz.android.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

public abstract class ExtendedBroadcastReceiver extends BroadcastReceiver {

    private final String[] actions;

    public ExtendedBroadcastReceiver(String ... actions) {
        this.actions = actions;
    }

    public void register(Context context) {
        IntentFilter filter = new IntentFilter();
        for (String action : actions) {
            filter.addAction(action);
        }

        context.registerReceiver(this, filter);
    }

    public void unregister(Context context) {
        context.unregisterReceiver(this);
    }

}
