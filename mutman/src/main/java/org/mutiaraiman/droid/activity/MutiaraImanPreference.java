package org.mutiaraiman.droid.activity;

import org.meruvian.midas.core.defaults.DefaultPreferenceActivity;
import org.mutiaraiman.droid.R;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

public class MutiaraImanPreference extends DefaultPreferenceActivity {
    private Preference about;

	@Override @TargetApi(11)
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}

        about = (Preference) findPreference("about");
        about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(MutiaraImanPreference.this, AboutActivity.class));
                return false;
            }
        });

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            about.setSummary("Version : " + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

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
