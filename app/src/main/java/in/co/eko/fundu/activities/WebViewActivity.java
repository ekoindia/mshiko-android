package in.co.eko.fundu.activities;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;

import in.co.eko.fundu.R;
import in.co.eko.fundu.utils.Utils;

public class WebViewActivity extends BaseActivity implements View.OnClickListener {

    private WebView webView;
    private ProgressDialog progressDialog;
    private ImageView btnFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        String url = getIntent().getStringExtra("url");

        webView = (WebView) findViewById(R.id.web_view);
        btnFeedback = (ImageView) findViewById(R.id.feedback);
        btnFeedback.setOnClickListener(this);

        webView.loadUrl(url);
        
        setWebView();


        webView.setWebViewClient(new MyWebViewClient());
    }

    private void setWebView() {
        WebSettings settings = webView.getSettings();
//        settings.setBuiltInZoomControls(true);
        settings.setLoadWithOverviewMode(true);
//        settings.setBuiltInZoomControls(true);
//        settings.setSupportZoom(false);
        settings.setUseWideViewPort(true);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progressDialog.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (progressDialog!=null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.feedback:
                View v = getWindow().getDecorView().getRootView();
                v.setDrawingCacheEnabled(true);
                Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
                v.setDrawingCacheEnabled(false);
                Utils.takeFeedback(bmp,WebViewActivity.this);
                break;
        }
    }
}
