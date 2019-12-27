package com.mealarts.AsyncTask;

import android.content.Context;
import android.util.Log;

import com.android.volley.toolbox.HurlStack;
import com.mealarts.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class SSLCertification {

    private Context context;

    public SSLCertification(Context context) {
        this.context = context;
    }

    public HurlStack getHurlStack(){
        return new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(java.net.URL url)
                    throws IOException {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super
                        .createConnection(url);
                try {
                    httpsURLConnection
                            .setSSLSocketFactory(getSSLSocketFactory(context));
                    // httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;
            }
        };
    }

    private SSLSocketFactory getSSLSocketFactory(Context context)
            throws CertificateException, KeyStoreException, IOException,
            NoSuchAlgorithmException, KeyManagementException {

// the certificate file will be stored in \app\src\main\res\raw folder path
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = context.getResources().openRawResource(
                R.raw.cert);

        Certificate ca = cf.generateCertificate(caInput);
        caInput.close();

        KeyStore keyStore = KeyStore.getInstance("BKS");

        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        TrustManager[] wrappedTrustManagers = getWrappedTrustManagers(tmf
                .getTrustManagers());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, wrappedTrustManagers, null);

        return sslContext.getSocketFactory();
    }

    private HostnameVerifier getHostnameVerifier() {
        return (hostname, session) -> {
            //return true; // verify always returns true, which could cause insecure network traffic due to trusting TLS/SSL server certificates for wrong hostnames
            HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
            return hv.verify("www.mealarts.com", session);
        };
    }

    private static TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
        return new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return originalTrustManager.getAcceptedIssuers();
            }

            public void checkClientTrusted(X509Certificate[] certs,
                                           String authType) {
                try {
                    if (certs != null && certs.length > 0) {
                        certs[0].checkValidity();
                    } else {
                        originalTrustManager
                                .checkClientTrusted(certs, authType);
                    }
                } catch (CertificateException e) {
                    Log.w("checkClientTrusted", e.toString());
                }
            }

            public void checkServerTrusted(X509Certificate[] certs,
                                           String authType) {
                try {
                    if (certs != null && certs.length > 0) {
                        certs[0].checkValidity();
                    } else {
                        originalTrustManager
                                .checkServerTrusted(certs, authType);
                    }
                } catch (CertificateException e) {
                    Log.w("checkServerTrusted", e.toString());
                }
            }
        } };
    }

    public static OkHttpClient getOkHttpClient(Context context) {
        try {
            // Create a trust manager that does not validate certificate chains

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = context.getResources().openRawResource(
                    R.raw.cert);

            Certificate ca = cf.generateCertificate(caInput);
            caInput.close();
            KeyStore keyStore = KeyStore.getInstance("BKS");

            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
            TrustManager[] wrappedTrustManagers = getWrappedTrustManagers(tmf
                    .getTrustManagers());
            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, wrappedTrustManagers, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)wrappedTrustManagers[0]);
            builder.hostnameVerifier((hostname, session) -> {
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                return true;
            });

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
