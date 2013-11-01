package us.xingrz.swiftz.android;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

public class SettingsActivity
        extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    EditTextPreference mServerPref;
    ListPreference mEntryPref;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_network);

        mServerPref = (EditTextPreference) findPreference("network_server");
        mEntryPref = (ListPreference) findPreference("network_entry");

        mEntryPref.setEntries(new CharSequence[] { "internet", "local" });
        mEntryPref.setEntryValues(new CharSequence[] { "internet", "local" });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        findPreference(key).setSummary(sharedPreferences.getString(key, "xxx"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh_network:
                attemptRefresh();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private FetchPropsTask mFetchTask = null;
    private ProgressDialog progressDialog;

    public void attemptRefresh() {
        if (mFetchTask != null) {
            return;
        }

        progressDialog = ProgressDialog.show(this, null, getString(R.string.progress_fetching), true, true, new ProgressDialog.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                mFetchTask.cancel(true);
            }
        });

        mFetchTask = new FetchPropsTask();
        mFetchTask.execute((Void) null);
    }

    public class FetchPropsTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            mEntryPref.setEntries(new CharSequence[] { "internet", "local" });
            mEntryPref.setEntryValues(new CharSequence[] { "internet", "local" });

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mFetchTask = null;
            progressDialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            mFetchTask = null;
            progressDialog.dismiss();
        }
    }
}
