package org.meruvian.midas.activity.fragment;

import org.mutiaraiman.droid.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;

public class WebViewFragment extends Fragment {
    private WebView viewer = null;
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        viewer = (WebView) inflater
                .inflate(R.layout.view, container, false);
        WebSettings settings = viewer.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDefaultZoom(ZoomDensity.FAR);
        
        return viewer;
    }
    
    @Override
    public void onPause() {
       if (viewer != null) {
           viewer.onPause();
       }
       super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewer != null) {
            viewer.onResume();
        }
    }

    public void updateUrl(String newUrl) {
        if (viewer != null) {
            viewer.loadUrl(newUrl);
        }
    }
}
