package us.xingrz.swiftz.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import us.xingrz.swiftz.android.service.SwiftzService;

public class LoginActivity extends Activity {

    private ProgressDialog progressDialog;

    private EditText mUsernameText;
    private EditText mPasswordText;

    private SwiftzService mService;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = ((SwiftzService.SwiftzBinder) iBinder).getService();
            initialize();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        progressDialog = ProgressDialog.show(this, null, getString(R.string.progress_initializing), true, true, new ProgressDialog.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finish();
            }
        });

        mUsernameText = (EditText) findViewById(R.id.username);
        mPasswordText = (EditText) findViewById(R.id.password);

        mPasswordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.action_login || id == EditorInfo.IME_NULL) {
                    login();
                    return true;
                } else {
                    return false;
                }
            }
        });

        bindService(new Intent(this, SwiftzService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this.mConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_login:
                login();
                break;
            case R.id.action_settings:
                startSettings();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, 0);
    }

    private void initialize() {
        if (mService.isOnline()) {
            progressDialog.dismiss();
            progressDialog = null;
            online();
            return;
        }

        this.mService.setup(new SwiftzService.OnSetupCompletedListener() {
            @Override
            public void onSetupCompleted(String server, String[] entries) {
                progressDialog.dismiss();
                progressDialog = null;
                online();
            }
        });
    }

    private void login() {
        if (progressDialog != null) {
            return;
        }

        if (mUsernameText.getText() == null || mPasswordText.getText() == null) {
            return;
        }

        mUsernameText.setError(null);
        mPasswordText.setError(null);

        String username = mUsernameText.getText().toString();
        String password = mPasswordText.getText().toString();

        if (TextUtils.isEmpty(username)) {
            mUsernameText.setError(getString(R.string.error_field_required));
            mUsernameText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordText.setError(getString(R.string.error_field_required));
            mPasswordText.requestFocus();
            return;
        }

        progressDialog = ProgressDialog.show(this, null, getString(R.string.progress_connecting), true, false);

        mService.login(username, password, "internet", new SwiftzService.OnLoginListener() {
            @Override
            public void onLogin(boolean success, String message, String website, String session) {
                progressDialog.dismiss();
                progressDialog = null;
                if (success) {
                    online();
                } else {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle(getString(R.string.alert_login_failed))
                            .show();
                }
            }
        });
    }

    private void online() {

    }
}
