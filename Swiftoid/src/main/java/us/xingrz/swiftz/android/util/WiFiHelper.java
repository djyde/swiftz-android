package us.xingrz.swiftz.android.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class WiFiHelper {
    private static final int WALLED_GARDEN_SOCKET_TIMEOUT_MS = 10 * 1000;

    private boolean isWalledGardenConnection() {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL("http://clients3.google.com/generate_204"); // "http://clients3.google.com/generate_204"
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setInstanceFollowRedirects(false);
            urlConnection.setConnectTimeout(WALLED_GARDEN_SOCKET_TIMEOUT_MS);
            urlConnection.setReadTimeout(WALLED_GARDEN_SOCKET_TIMEOUT_MS);
            urlConnection.setUseCaches(false);
            urlConnection.getInputStream();
            return urlConnection.getResponseCode() != 204;
        } catch (IOException e) {
            return false;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}
