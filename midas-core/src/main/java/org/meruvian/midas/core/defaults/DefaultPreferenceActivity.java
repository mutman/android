package org.meruvian.midas.core.defaults;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import org.meruvian.midas.core.R;

/**
 * Created by ludviantoovandi on 29/10/14.
 */
public abstract class DefaultPreferenceActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getThemes());
    }

    private int getThemes() {
        SharedPreferences sessionPreference = PreferenceManager.getDefaultSharedPreferences(this);

        if (theme() == 0) {
            return sessionPreference.getInt("themes", sessionPreference.getInt("themes", R.style.DarkTheme_Midas));
        } else {
            return theme();
        }
    }

    public abstract int theme();
}
