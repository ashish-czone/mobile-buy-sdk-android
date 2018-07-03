package com.shopify.sample.view.cart;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.shopify.sample.R;
import com.shopify.sample.util.Util;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckOutWebView extends AppCompatActivity {

    private static final String TAG = CheckOutWebView.class.getSimpleName();

    @BindView(R.id.web_view_payment)
    WebView mWebViewPayment;

    private boolean mToggle = true;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_checkoutwebview);
        ButterKnife.bind(this);
        Util.showLoading(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptThirdPartyCookies(mWebViewPayment, true);
        }
        WebSettings webSettings = mWebViewPayment.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebViewPayment.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(getJavascript());
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl(getJavascript());
                mToggle = false;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Util.hideLoading();
                    }
                }, 6000);
                view.loadUrl("javascript:$(document).ajaxComplete(function (event, request, settings) { " +
                        "var d = document.getElementsByClassName(\"link--small\")[1];" +
                        "var f = document.getElementsByClassName('step__footer__previous-link')[0];" +
                        "if(d) d.style.display = 'none';" +
                        "if(f) f.style.display = 'none';" +
                        "});");
            }
        });
        Bundle extras = getIntent().getExtras();
        if ( extras != null) {
            String url = extras.getString("URL", null);
            // String token = extras.getString("ACCESS_TOKEN", null);
            if(url != null) {
               // Map<String, String> additionalHttpHeaders = new HashMap<>();
               // additionalHttpHeaders.put("X-Shopify-Customer-Access-Token", token);
               // mWebViewPayment.loadUrl(url, additionalHttpHeaders);
                mWebViewPayment.loadUrl(url);
            } else {
                onBackPressed();
            }
        }
    }

    private class WebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }

    private String getJavascript() {
        return "javascript:(function(){" +
                "var a = document.getElementsByClassName(\"section--billing-address\")[0];" +
                "var b = document.getElementsByClassName(\"section--optional\")[0];" +
                "var c = document.getElementsByClassName(\"link--small\")[0];" +
                "var d = document.getElementsByClassName(\"link--small\")[1];" +
                "var e = document.getElementsByClassName('step__footer__continue-btn')[0];" +
                "var f = document.getElementsByClassName('step__footer__previous-link')[0];" +
                "if(a) a.style.display = 'none';" +
                "if(b) b.style.display = 'none';" +
                "if(c) c.style.display = 'none';" +
                "if(d) d.style.display = 'none';" +
                (mToggle?"if(e) e.click();" : "") +
                "if(f) f.style.display = 'none';" +
                "})();";
    }
}