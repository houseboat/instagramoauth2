package org.houseboat.instagramoauth2.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;

import org.houseboat.instagramoauth2.R;

import de.wischweh.dunkelkmmr.auth.InstagramRedirectListenerWebViewClient;


/**
 * Created by Jan D.S. Wischweh <mail@wischweh.de> on 26.04.16.
 */

public class CredentialsActivity extends AppCompatActivity implements InstagramRedirectListenerWebViewClient.OnTokenParsed {

    public static final String KEY_TOKEN="token";
    WebView authWebView;

    @NonNull Uri getAuthUri() {
        Uri authUri=Uri.parse(getString(R.string.instagram_auth_url)).buildUpon()
                .appendQueryParameter("client_id",getString(R.string.instagram_client_id))
                .appendQueryParameter("redirect_uri",getString(R.string.instagram_redirect_uri))
                .appendQueryParameter("response_type","token").build();
        return authUri;
    }

    // TODO: transform to a popup overlay
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentials);
        authWebView=(WebView) findViewById(R.id.oauth_webview);
        authWebView.setWebViewClient(new InstagramRedirectListenerWebViewClient(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Uri authUri=getAuthUri();
        Log.d("AUTH","loading ww with "+authUri);
        authWebView.clearCache(true);
        authWebView.loadUrl(authUri.toString());
    }

    @Override
    public void onTokenParsed(String token) {
        Intent result=new Intent();
        result.putExtra(KEY_TOKEN,token);
        setResult(RESULT_OK,result);
        finish();
    }
}
