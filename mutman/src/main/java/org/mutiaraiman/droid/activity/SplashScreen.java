package org.mutiaraiman.droid.activity;

import org.mutiaraiman.droid.R;
import org.mutiaraiman.droid.fragment.MainFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class SplashScreen extends Activity {

	private final int SPLASH_DISPLAY_LENGHT = 2000;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				Intent mainIntent =  new Intent(SplashScreen.this, MainActivity.class);
				SplashScreen.this.startActivity(mainIntent);
				SplashScreen.this.finish();
			}
		}, SPLASH_DISPLAY_LENGHT);
	}
}