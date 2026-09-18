package com.hizlydighapara.halalrokto;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * হালাল রক্ত দান v2.0 — Native shell (WebView + JS bridge)
 * Org: হিজলি দিঘাপাড়া যুব সংঘ • Since 2026 • বাগাতিপাড়া, নাটোর
 */
public class MainActivity extends Activity {

    private WebView web;
    private SharedPreferences prefs;
    private final ExecutorService netPool = Executors.newFixedThreadPool(2);

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("halal_rokto_dan", MODE_PRIVATE);

        web = new WebView(this);
        web.setBackgroundColor(0xFF9E1710);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setSupportZoom(false);
        s.setDisplayZoomControls(false);
        s.setMediaPlaybackRequiresUserGesture(true);
        s.setGeolocationEnabled(false);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        s.setJavaScriptCanOpenWindowsAutomatically(false);
        s.setSupportMultipleWindows(false);

        web.setOverScrollMode(View.OVER_SCROLL_NEVER);
        web.setHorizontalScrollBarEnabled(false);
        web.setVerticalScrollBarEnabled(false);

        web.addJavascriptInterface(new Bridge(), "Android");

        web.setWebViewClient(new AppWebClient(this));
        web.setWebChromeClient(new AppChromeClient());

        setContentView(web);

        if (savedInstanceState == null) {
            web.loadUrl("file:///android_asset/www/index.html");
        } else {
            web.restoreState(savedInstanceState);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        web.saveState(outState);
    }

    @Override
    public void onBackPressed() {
        if (web != null) {
            // delegate to the web router: closes sheets first, then navigates back
            web.evaluateJavascript("(function(){try{window.dispatchEvent(new Event('androidback'));}catch(e){}})()", null);
        } else {
            super.onBackPressed();
        }
    }

    private void openExternal(String url) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, "এই লিংক খোলার কোনো অ্যাপ পাওয়া যায়নি", Toast.LENGTH_SHORT).show();
        }
    }

    private void vibrate(int ms) {
        try {
            Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (v == null) return;
            if (android.os.Build.VERSION.SDK_INT >= 26) {
                v.vibrate(VibrationEffect.createOneShot(Math.max(1, ms), VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(ms);
            }
        } catch (Exception ignored) {
        }
    }

    /* ── Named clients (avoid anonymous classes: d8 build-tools 34 NPE) ── */

    private static class AppWebClient extends WebViewClient {
        private final MainActivity host;

        AppWebClient(MainActivity host) { this.host = host; }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Uri u = request.getUrl();
            String scheme = u.getScheme() == null ? "" : u.getScheme();
            if ("http".equals(scheme) || "https".equals(scheme)) {
                host.openExternal(u.toString());
                return true;
            }
            if ("tel".equals(scheme) || "mailto".equals(scheme) || "sms".equals(scheme)
                    || "whatsapp".equals(scheme) || "geo".equals(scheme) || "market".equals(scheme) || "intent".equals(scheme)) {
                host.openExternal(u.toString());
                return true;
            }
            return false; // file:// loads inside
        }
    }

    private static class AppChromeClient extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(android.webkit.ConsoleMessage cm) {
            return true; // swallow logs in release
        }
    }

    /* ── JavaScript bridge ─────────────────────────────────── */
    private class Bridge {

        @JavascriptInterface
        public void toast(final String msg) {
            runOnUiThread(() -> Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show());
        }

        @JavascriptInterface
        public void share(final String text, final String title) {
            runOnUiThread(() -> {
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_TEXT, text == null ? "" : text);
                    String t = (title == null || title.isEmpty()) ? "শেয়ার করুন" : title;
                    i.putExtra(Intent.EXTRA_TITLE, t);
                    i.putExtra(Intent.EXTRA_SUBJECT, t);
                    startActivity(Intent.createChooser(i, t));
                } catch (Exception ignored) {
                }
            });
        }

        @JavascriptInterface
        public void vibrate(int ms) {
            MainActivity.this.vibrate(ms);
        }

        @JavascriptInterface
        public String getPref(String key) {
            return prefs.getString(key, null);
        }

        @JavascriptInterface
        public void setPref(String key, String value) {
            prefs.edit().putString(key, value == null ? "" : value).apply();
        }

        @JavascriptInterface
        public String appVersion() {
            return "2.0.0";
        }

        @JavascriptInterface
        public String orgName() {
            return "হিজলি দিঘাপাড়া যুব সংঘ";
        }

        @JavascriptInterface
        public void exitApp() {
            runOnUiThread(() -> finish());
        }

        @JavascriptInterface
        public void http(final String method, final String urlStr, final String body, final String cbId) {
            netPool.execute(() -> {
                int status = 0;
                String resp = "";
                HttpURLConnection conn = null;
                try {
                    URL url = new URL(urlStr);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    conn.setRequestMethod(method == null ? "GET" : method.toUpperCase());
                    conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setInstanceFollowRedirects(true);
                    String m = conn.getRequestMethod();
                    if (body != null && !body.isEmpty() && !("GET".equals(m) || "HEAD".equals(m))) {
                        conn.setDoOutput(true);
                        try (OutputStream os = conn.getOutputStream()) {
                            os.write(body.getBytes(StandardCharsets.UTF_8));
                        }
                    }
                    status = conn.getResponseCode();
                    java.io.InputStream in = (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream();
                    if (in == null) {
                        resp = "";
                    } else {
                        Scanner sc = new Scanner(in, "UTF-8").useDelimiter("\\A");
                        resp = sc.hasNext() ? sc.next() : "";
                    }
                } catch (Exception e) {
                    status = -1;
                    try {
                        resp = new JSONObject().put("error", String.valueOf(e.getMessage())).toString();
                    } catch (Exception ignored) {
                        resp = "{}";
                    }
                } finally {
                    if (conn != null) conn.disconnect();
                }
                final int st = status;
                final String rp = resp;
                runOnUiThread(() -> {
                    try {
                        String safe = JSONObject.quote(rp == null ? "" : rp);
                        web.evaluateJavascript(
                                "(function(){try{window.__syncDone('" + cbId + "'," + st + "," + safe + ")}catch(e){}})()", null);
                    } catch (Exception ignored) {
                    }
                });
            });
        }
    }

    @Override
    protected void onDestroy() {
        netPool.shutdown();
        if (web != null) {
            web.removeJavascriptInterface("Android");
            web.destroy();
            web = null;
        }
        super.onDestroy();
    }
}
