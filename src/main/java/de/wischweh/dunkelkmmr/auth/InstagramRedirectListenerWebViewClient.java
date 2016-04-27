package de.wischweh.dunkelkmmr.auth;

import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import de.wischweh.dunkelkmmr.R;

/**
 * Created by Jan D.S. Wischweh <mail@wischweh.de> on 26.04.16.
 */
public class InstagramRedirectListenerWebViewClient extends WebViewClient {

    OnTokenParsed listener;

    public InstagramRedirectListenerWebViewClient(OnTokenParsed listener) {
        this.listener = listener;
    }



    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        String needle=view.getContext().getString(R.string.instagram_redirect_uri)+"#access_token=";
        if (url.startsWith(needle)) {
            String token=url.substring(needle.length());
            if (!TextUtils.isEmpty(token)) {
                listener.onTokenParsed(token);
                return true;
            }
        }
        return false;
    }

    public interface OnTokenParsed {

        void onTokenParsed(String token);

    }

}
