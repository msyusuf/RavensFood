package com.everykindred.ravensfood;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WebFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WebFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_URL= "url";

    private String url;
    private ProgressBar progressBar;


    public WebFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WebFragment.
     */
    public static WebFragment newInstance(String url) {
        WebFragment fragment = new WebFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(ARG_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_web, container, false);

        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBarWeb);
        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_email);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] addresses = {getString(R.string.contact_email)};
                composeEmail(addresses,"");
            }
        });

        WebView webView = (WebView) rootView.findViewById(R.id.webView);

        //set client so link clicks do not load external browser
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //disable all links, users should use float button to send email
                //return true;
                return false;
            }
            @Override
            public void onPageFinished(WebView view, String url){
                //show float email button after page has loaded
                if (url.equals(getString(R.string.urlContact))) {
                    fab.show();
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });


        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);

        // Inflate the layout for this fragment
        return rootView;
    }

    public void composeEmail(String[] address, String subject) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, address);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}
