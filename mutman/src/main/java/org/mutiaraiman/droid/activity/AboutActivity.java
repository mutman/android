package org.mutiaraiman.droid.activity;

import org.meruvian.midas.core.defaults.DefaultActivity;
import org.mutiaraiman.droid.R;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

public class AboutActivity extends DefaultActivity {
    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        webView = (WebView) findViewById(R.id.aboutWebview);
        webView.loadUrl("file:///android_asset/about.html");
    }

    @Override
    public int theme() {
        return org.meruvian.midas.core.R.style.DarkTheme_Mutman;
    }

    @Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		boolean itemSelected = super.onMenuItemSelected(featureId, item);
		if (item.getItemId() == android.R.id.home) {
		    super.onBackPressed();
		}
		return itemSelected;
	}
}