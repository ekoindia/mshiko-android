package in.co.eko.fundu;
/*
 * Created by Bhuvnesh
 */

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.constants.V1API;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.utils.Fog;
import io.fabric.sdk.android.Fabric;

import static in.co.eko.fundu.constants.Constants.debug;
//import com.icicibank.isdk.listner.ISDKInitializationListner;
/**
 * ICICI Bank UPI
 */

public class FunduApplication extends Application  /*implements ISDKInitializationListner*/{
    private static final String TAG = FunduApplication.class.getSimpleName();
    private RequestQueue thridPartyRequestQueue;
    private RequestQueue mRequestQueue;
    private static Context context;
    private static FunduApplication mInstance;
    Tracker tracker;
    GoogleAnalytics analytics;
    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Fabric.with(this, new Crashlytics());
        context = getApplicationContext();
        FunduUser.initialize(this);
        mInstance = this;
        upiInitialization();
        if(debug){
            Stetho.initializeWithDefaults(this);
            VolleyLog.DEBUG = true;
        }
        else
            VolleyLog.DEBUG = false;

    }
    //TODO: Look for a way to do initialization only for India numbers
    private void upiInitialization(){

        if(Constants.upiProvider == Constants.UPI_PROVIDER.ICICI) {
            //ICICI Bank UPI Initialization
            //ISDK.initSDK(this, "661086f1261a811c1e4bff96e4f31c03", "MID001", "1001", this);
        }
        else{
            /**Yes bank UPI Initialization*/
//            SharedPreferenceHelper.initSharedPreferenceHelper(this);
//            UiUtil.initLogos();
//
            //  add firebase config code below
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApplicationId("your_id")
                    .setApiKey("your_key")
                    .setDatabaseUrl("https://firedemo-49b5d384-your-37847sjdhsjd.firebaseio.com/")
                    .build();
            FirebaseApp.initializeApp(this, options,"<Your app Name>");

        }

    }
    public static synchronized FunduApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {

            HurlStack hurlStack = new HurlStack() {
                @Override
                protected HttpURLConnection createConnection(URL url) throws IOException {
                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                    try {
                          //httpsURLConnection.setSSLSocketFactory(getSSLSocketFactory());
                          httpsURLConnection.setSSLSocketFactory(getDefaultSSLSocketFactory());
                          //httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return httpsURLConnection;
                }
            };
            if(V1API.BASE_URL.contains("https")){
                mRequestQueue  = Volley.newRequestQueue(getApplicationContext(), hurlStack);
            }
            else{
                mRequestQueue = Volley.newRequestQueue(getApplicationContext());
            }

        }
        return mRequestQueue;
    }

    private RequestQueue getThridPartyRequestQueue(){
        thridPartyRequestQueue = Volley.newRequestQueue(getApplicationContext());
        return thridPartyRequestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        if(req.getUrl().toString().contains(V1API.BASE_URL))
            getRequestQueue().add(req);
        else{
            getThridPartyRequestQueue().add(req);
        }
    }

    public static Context getAppContext() {
        return context;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        addToRequestQueue(req,null);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public Tracker getTracker() {
        if(tracker == null){
            analytics = GoogleAnalytics.getInstance(this);
            tracker = analytics.newTracker(getString(R.string.ga_trackingId));
        }
        return tracker;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Fog.e(TAG, "Low Memory");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


    private SSLSocketFactory getDefaultSSLSocketFactory(){
        try{
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
            String fileName = "fundu.text";
                if(V1API.BASE_URL.contains("uatapi"))
                    fileName = "fundu_uat.text";

                InputStream caInput = new BufferedInputStream(getAssets().open(fileName));
                Certificate ca;
                try {
                    ca = cf.generateCertificate(caInput);
                } finally {
                    caInput.close();
                }
                String keyStoreType = KeyStore.getDefaultType();
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", ca);
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(keyStore);
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, tmf.getTrustManagers(), null);
                return context.getSocketFactory();

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    /**
     *  ICICI Bank UPI
     */

//    @Override
//    public void initSuccess() {
//        if(Constants.debug)
//            Toast.makeText(this,"init success",Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void initFailed(int i) {
//        if(Constants.debug)
//           Toast.makeText(this,"initFailed errorcode "+i,Toast.LENGTH_LONG).show();
//
//    }


}
