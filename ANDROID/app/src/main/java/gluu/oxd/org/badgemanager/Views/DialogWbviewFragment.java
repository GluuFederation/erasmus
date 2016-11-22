package gluu.oxd.org.badgemanager.Views;
/**
 * Created by lcom75 on 8/8/16.
 */

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;

import gluu.oxd.org.badgemanager.R;
import gluu.oxd.org.badgemanager.Statics.Values;


/**
 * Created by lcom15 on 22/3/16.
 */
public class DialogWbviewFragment extends DialogFragment {
    private static String ARG_URl = "URl";
    WebView mWebView;
    String url;
    private static final String TAG = "DialogWbviewFragment";
//    ProgressBar progressBar;
    AuthDialogListner authDialogListner;

    public DialogWbviewFragment() {
    }

    public static DialogWbviewFragment newInstance(String param1) {
        DialogWbviewFragment fragment = new DialogWbviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URl, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(ARG_URl);
        }
        View view = inflater.inflate(R.layout.dialog_webview, container, false);
        this.setCancelable(false);

        bindView(view);

        CookieSyncManager.createInstance(getActivity());

        return view;
    }

    private void bindView(View view) {
        mWebView = (WebView) view.findViewById(R.id.webView);
//        progressBar = (ProgressBar) view.findViewById(R.id.Pb_loading);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setAllowContentAccess(true);

        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.clearView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.setWebContentsDebuggingEnabled(true);
        }

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                progressBar.setVisibility(View.VISIBLE);
                if (url.startsWith("https://127.0.0.1:3000/auth.html")) {
                    Uri uri = Uri.parse(url);

                    HashMap<String, String> map = new HashMap<>();
                    for (String paramName : uri.getQueryParameterNames()) {
                        if (paramName != null) {
                            String paramValue = uri.getQueryParameter(paramName);
                            if (paramValue != null) {
                                map.put(paramName, paramValue);
                            }
                        }
                    }
                    if (map.size() > 0) {
                        Values.AuthValues = map;
                    }
                    Log.d(TAG, "onPageStarted() called with: " + "view = [" + view + "], url = [" + url + "], favicon = [" + favicon + "]");

                    if (authDialogListner != null) {
                        authDialogListner.onSuccess(url);
                    }

                }

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "onPageFinished() called with: " + "view = [" + view + "], url = [" + url + "]");
                CookieSyncManager.getInstance().sync();
//                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.d(TAG, "onReceivedError() called with: " + "view = [" + view + "], request = [" + request + "], error = [" + error + "]");
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                Log.d(TAG, "onReceivedSslError() called with: " + "view = [" + view + "], handler = [" + handler + "], error = [" + error + "]");
                handler.proceed();

            }


        });
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.loadUrl(url);
        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {

                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            if (mWebView.canGoBack()) {
                                mWebView.goBack();
                                return true;
                            } else {
                                dismissAllowingStateLoss();
                            }
                            break;
                    }
                }

                return false;
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        android.webkit.CookieManager.getInstance().removeAllCookie();
        CookieSyncManager.getInstance().resetSync();


    }

    @Override
    public void onPause() {
        super.onPause();
        CookieSyncManager.getInstance().stopSync();

    }

    public interface AuthDialogListner {

        void onSuccess(String url);

        void onError(String error);
    }

    public AuthDialogListner getAuthDialogListner() {
        return authDialogListner;
    }

    public void setAuthDialogListner(AuthDialogListner authDialogListner) {
        this.authDialogListner = authDialogListner;
    }
}
