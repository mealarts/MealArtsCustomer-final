package com.mealarts;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.net.http.SslCertificate;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mealarts.Helpers.CheckExtras;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import okhttp3.internal.Version;

public class PolicyTermsActivity extends AppCompatActivity {

    ImageView ivBack, ivLoader, ivPolicyTerms;
    LinearLayout layoutLoader;
    TextView tvHeader;
    WebView wvAboutUs;
    String Content;
    CheckExtras connection;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy_terms);

        Content = getIntent().getStringExtra("Content");
        connection = new CheckExtras(PolicyTermsActivity.this);

        ivBack = findViewById(R.id.ivBack);
        tvHeader = findViewById(R.id.tvHeader);
        ivBack.setOnClickListener(v -> onBackPressed());

        layoutLoader = findViewById(R.id.layoutLoader);
        ivLoader = findViewById(R.id.ivLoader);
        ivPolicyTerms = findViewById(R.id.ivPolicyTerms);
        Glide.with(PolicyTermsActivity.this).asGif().load(R.drawable.mealarts_loader).into(ivLoader);
        //layoutLoader.setVisibility(View.VISIBLE);

        wvAboutUs = findViewById(R.id.wvAboutUs);
        /*wvAboutUs.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(PolicyTermsActivity.this, description, Toast.LENGTH_SHORT).show();
            }
            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                layoutLoader.setVisibility(View.GONE);
            }
        });
        wvAboutUs.getSettings().setJavaScriptEnabled(true);*/

        if(Content.equals("Policy")) {
            tvHeader.setText("Privacy Policy of MealArts");
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                Log.d("/*aboutus","build version > P, PrivacyPolicy");
                wvAboutUs.setVisibility(View.GONE);
                ivPolicyTerms.setImageResource(R.drawable.policy);
            }else{
                Log.d("/*aboutus","build version < P, PrivacyPolicy");
                ivPolicyTerms.setVisibility(View.GONE);
                wvAboutUs.loadUrl("https://www.mealarts.com/Cust_json/policy.php");
            }

        }else{
            tvHeader.setText("Terms of Mealarts");
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Log.d("/*aboutus","build version > P, Terms");
                wvAboutUs.setVisibility(View.GONE);
                ivPolicyTerms.setImageResource(R.drawable.terms);
            }else{
                Log.d("/*aboutus","build version < P, Terms");
                ivPolicyTerms.setVisibility(View.GONE);
                wvAboutUs.loadUrl("https://www.mealarts.com/Cust_json/terms.php");
            }
        }
        //setContentView(wvAboutUs);
    }

//    private class SSLTolerentWebViewClient extends WebViewClient {
//        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(PolicyTermsActivity.this);
//            AlertDialog alertDialog = builder.create();
//            String message = "SSL Certificate error.";
//            switch (error.getPrimaryError()) {
//                case SslError.SSL_UNTRUSTED:
//                    message = "The certificate authority is not trusted.";
//                    handler.proceed();
//                    break;
//                case SslError.SSL_EXPIRED:
//                    message = "The certificate has expired.";
//                    break;
//                case SslError.SSL_IDMISMATCH:
//                    message = "The certificate Hostname mismatch.";
//                    break;
//                case SslError.SSL_NOTYETVALID:
//                    message = "The certificate is not yet valid.";
//                    break;
//            }
//
//            message += " Do you want to continue anyway?";
//            alertDialog.setTitle("SSL Certificate Error");
//            alertDialog.setMessage(message);
//            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", (dialog, which) -> {
//                // Ignore SSL certificate errors
//                handler.proceed();
//            });
//
//            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> handler.cancel());
//            if(error.getPrimaryError() != SslError.SSL_UNTRUSTED)
//                alertDialog.show();
//        }
//    }
}
