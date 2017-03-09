package com.everykindred.ravensfood;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class BlogActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar)findViewById(R.id.progressBarWeb);

        String blogTitle = getIntent().getStringExtra("blogTitle");
        getSupportActionBar().setTitle("Blog - " + blogTitle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        WebView webView = (WebView) findViewById(R.id.webViewBlog);

//        webView.setWebChromeClient(new WebChromeClient());

        //set client so link clicks do not load external browser
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //return true to disable all links, users should use float button to send email
                //return true;
                return false;
            }
            @Override
            public void onPageFinished(WebView view, String url){
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        //get the url passed in from activity bundle, load web page
        String blogUrl = getIntent().getStringExtra("blogUrl");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(blogUrl);
    }

}
