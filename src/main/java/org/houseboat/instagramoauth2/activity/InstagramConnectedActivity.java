package org.houseboat.instagramoauth2.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.houseboat.instagramoauth2.R;

import java.io.IOException;


/**
 * Created by Jan D.S. Wischweh <mail@wischweh.de> on 26.04.16.
 */
public abstract class InstagramConnectedActivity extends AppCompatActivity implements AccountManagerCallback<Bundle> {

    public static final int CODE_AUTHINTERACTION=2000;

    private final String TAG="[Connecting]";
    private final int MAX_AUTH_RETRIES=3;
    private int retryCount=0;

    AccountManager accountManager;
    Account instagramAccount;
    String currentAuthtoken = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountManager=AccountManager.get(this);
        instagramAccount=accountManager.getAccountsByType(getString(R.string.instagram_account_type))[0];
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void aquireAuthToken() {
       accountManager.getAuthToken(instagramAccount,
                    getString(R.string.instagram_auth_scope),
                    null,
                    this,
                    this,
                    new Handler());
    }

    private void retryAquireAuthTokenOrDisplayError(AuthenticatorException e) {
        if (retryCount<MAX_AUTH_RETRIES) {
            retryCount++;
            aquireAuthToken();
        } else {
            displayAuthError(e);
        }
    }

    private void displayAuthError(AuthenticatorException e) {
        String msg=getString(R.string.error_auth_generic);
        Log.e(TAG,msg,e);
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void run(AccountManagerFuture<Bundle> future) {
        try {
            Bundle result = future.getResult();
            Intent launchForUserInteraction=(Intent) result.get(AccountManager.KEY_INTENT);
            if (launchForUserInteraction != null) {
                // The API needs to interact with the user.
                Log.w(TAG,"recieved KEY_INTENT, will go into user interaction");
                startActivityForResult(launchForUserInteraction,CODE_AUTHINTERACTION);
            } else {
                currentAuthtoken = result.getString(AccountManager.KEY_AUTHTOKEN);
                retryCount = 0;
                onAuthenticated();
            }

        } catch (AuthenticatorException e) {
            retryAquireAuthTokenOrDisplayError(e);
        } catch (IOException e) {
            aquireAuthToken();
        } catch (OperationCanceledException e) {
            Log.w(TAG,"Authentication was canceled");
            // TODO: handle this
        }

    }

    /**
     * returns the auth token from the last succesful authentication from the AccountManager
     * @return
     */

    protected String getCurrentAuthtoken() {
        return currentAuthtoken;
    }

    protected abstract void onAuthenticated();
}
