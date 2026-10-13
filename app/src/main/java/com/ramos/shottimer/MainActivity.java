package com.ramos.shottimer;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.webkit.WebViewAssetLoader;

public class MainActivity extends Activity {

    private static final int REQ_AUDIO = 1001;
    private static final String HOST = "appassets.androidplatform.net";

    private WebView web;
    private PermissionRequest pendingRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        web = new WebView(this);
        setContentView(web);
        applyBars(isNight());

        final WebViewAssetLoader loader = new WebViewAssetLoader.Builder()
                .setDomain(HOST)
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setAllowFileAccess(false);
        s.setAllowContentAccess(false);
        s.setSupportZoom(false);

        web.addJavascriptInterface(new Bridge(), "AndroidBridge");
        web.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return loader.shouldInterceptRequest(request.getUrl());
            }
        });
        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                runOnUiThread(() -> handleWebPermission(request));
            }
        });

        web.loadUrl("https://" + HOST + "/assets/index.html?sys=" + (isNight() ? "dark" : "light"));
    }

    private void handleWebPermission(PermissionRequest request) {
        boolean wantsAudio = false;
        for (String r : request.getResources()) {
            if (PermissionRequest.RESOURCE_AUDIO_CAPTURE.equals(r)) wantsAudio = true;
        }
        if (!wantsAudio || !HOST.equals(request.getOrigin().getHost())) {
            request.deny();
            return;
        }
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            request.grant(new String[]{PermissionRequest.RESOURCE_AUDIO_CAPTURE});
        } else {
            if (pendingRequest != null) pendingRequest.deny();
            pendingRequest = request;
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQ_AUDIO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int code, String[] perms, int[] results) {
        super.onRequestPermissionsResult(code, perms, results);
        if (code != REQ_AUDIO || pendingRequest == null) return;
        if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
            pendingRequest.grant(new String[]{PermissionRequest.RESOURCE_AUDIO_CAPTURE});
        } else {
            pendingRequest.deny();
        }
        pendingRequest = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (web != null) {
            web.evaluateJavascript("window.setSystemTheme && setSystemTheme('" + (isNight() ? "dark" : "light") + "')", null);
        }
    }

    private boolean isNight() {
        int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return mode == Configuration.UI_MODE_NIGHT_YES;
    }

    @SuppressWarnings("deprecation")
    private void applyBars(boolean dark) {
        int bg = dark ? 0xFF121613 : 0xFFE8EAE5;
        Window w = getWindow();
        w.setStatusBarColor(bg);
        w.setNavigationBarColor(bg);
        int flags = dark ? 0 : (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        w.getDecorView().setSystemUiVisibility(flags);
        if (web != null) web.setBackgroundColor(bg);
    }

    private class Bridge {
        @JavascriptInterface
        public void setTheme(final boolean dark) {
            runOnUiThread(() -> applyBars(dark));
        }
    }

    @Override
    protected void onDestroy() {
        if (web != null) {
            web.removeJavascriptInterface("AndroidBridge");
            web.destroy();
        }
        super.onDestroy();
    }
}
