package us.xingrz.swiftz.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.amnoon.Amnoon;

public class LoginActivity extends Activity {

    private UserLoginTask mAuthTask = null;

    private ProgressDialog progressDialog;

    private EditText mUsernameText;
    private EditText mPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mUsernameText = (EditText) findViewById(R.id.username);

        mPasswordText = (EditText) findViewById(R.id.password);
        mPasswordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.action_login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

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
                attemptLogin();
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

    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        mUsernameText.setError(null);
        mPasswordText.setError(null);

        String mUsername = mUsernameText.getText().toString();
        String mPassword = mPasswordText.getText().toString();

        if (TextUtils.isEmpty(mUsername)) {
            mUsernameText.setError(getString(R.string.error_field_required));
            mUsernameText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(mPassword)) {
            mPasswordText.setError(getString(R.string.error_field_required));
            mPasswordText.requestFocus();
            return;
        }

        progressDialog = ProgressDialog.show(this, null, getString(R.string.progress_connecting), true, true, new ProgressDialog.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                mAuthTask.cancel(true);
            }
        });

        mAuthTask = new UserLoginTask();
        mAuthTask.execute();
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                // Simulate network access.
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            progressDialog.dismiss();

            mPasswordText.setError(getString(R.string.error_incorrect_account));
            mPasswordText.requestFocus();
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            progressDialog.dismiss();
        }
    }
}
