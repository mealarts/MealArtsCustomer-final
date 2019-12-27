package com.mealarts;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.compiler.GlideIndexer_GlideModule_com_bumptech_glide_integration_okhttp3_OkHttpLibraryGlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpGlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule;
import com.mealarts.AsyncTask.SSLCertification;


public class AboutUsActivity extends AppCompatActivity {

    ImageView ivBack, ivLoader, ivAboutUs;
    LinearLayout layoutLoader;
    WebView wvAboutUs;
    SSLCertification sslCertification = new SSLCertification(AboutUsActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> onBackPressed());

        layoutLoader = findViewById(R.id.layoutLoader);
        ivLoader = findViewById(R.id.ivLoader);
        Glide.with(AboutUsActivity.this).asGif().load(R.drawable.mealarts_loader).into(ivLoader);
        //layoutLoader.setVisibility(View.VISIBLE);

        wvAboutUs = findViewById(R.id.wvAboutUs);
        ivAboutUs = findViewById(R.id.ivAboutUs);

        //wvAboutUs.setWebViewClient(new SSLTolerentWebViewClient());
        /*wvAboutUs.setWebViewClient(new SSLTolerentWebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(AboutUsActivity.this, description, Toast.LENGTH_SHORT).show();
                Log.e("Error", String.valueOf(description));
            }
            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError error) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, error.getErrorCode(), error.getDescription().toString(), req.getUrl().toString());
                Log.e("Error", String.valueOf(error.getDescription()));
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                layoutLoader.setVisibility(View.GONE);
            }
        });*/

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            wvAboutUs.setVisibility(View.GONE);
            ivAboutUs.setImageResource(R.drawable.about_us_temp);
        }else
            ivAboutUs.setVisibility(View.GONE);
            wvAboutUs.loadUrl("https://www.mealarts.com/Cust_json/about.php");

        /*wvAboutUs.setWebViewClient(new WebViewClient());
        wvAboutUs.getSettings().setJavaScriptEnabled(true);
        wvAboutUs.getSettings().setDomStorageEnabled(true);
        wvAboutUs.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        RequestQueue requestQueue = Volley.newRequestQueue(AboutUsActivity.this, sslCertification.getHurlStack());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://www.mealarts.com/Cust_json/about.php", response -> {
            Log.e("Rest_About", response);
            wvAboutUs.loadDataWithBaseURL("", response, "image/png", "UTF-8", "");
        }, error -> {

        });
        requestQueue.add(stringRequest);*/

    }

    private class SSLTolerentWebViewClient extends WebViewClient {
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {

            AlertDialog.Builder builder = new AlertDialog.Builder(AboutUsActivity.this);
            AlertDialog alertDialog = builder.create();
            String message = "SSL Certificate error.";
            switch (error.getPrimaryError()) {
                case SslError.SSL_UNTRUSTED:
                    message = "The certificate authority is not trusted.";
                    handler.proceed();
                    break;
                case SslError.SSL_EXPIRED:
                    message = "The certificate has expired.";
                    break;
                case SslError.SSL_IDMISMATCH:
                    message = "The certificate Hostname mismatch.";
                    break;
                case SslError.SSL_NOTYETVALID:
                    message = "The certificate is not yet valid.";
                    break;
            }

            message += " Do you want to continue anyway?";
            alertDialog.setTitle("SSL Certificate Error");
            alertDialog.setMessage(message);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", (dialog, which) -> {
                // Ignore SSL certificate errors
                handler.proceed();
            });

            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> handler.cancel());
            if(error.getPrimaryError() != SslError.SSL_UNTRUSTED)
                alertDialog.show();
        }
    }
}
