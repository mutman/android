/*
 * Copyright 2012 Meruvian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mutiaraiman.droid.activity;

import org.meruvian.midas.activity.receiver.MidasReceiver;
import org.meruvian.midas.core.defaults.DefaultActivity;
import org.mutiaraiman.droid.Prayer;
import org.mutiaraiman.droid.R;
import org.mutiaraiman.droid.fragment.PrayerFragment.OnListSelectedListener;
import org.mutiaraiman.droid.fragment.SinglePrayerFragment;
import org.mutiaraiman.droid.service.PrayerSyncService;

import roboguice.inject.InjectFragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.BaseColumns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.inject.internal.Nullable;

/**
 * @author Dian Aditya
 * 
 */
public class PrayersActivity extends DefaultActivity implements
		OnListSelectedListener {

    @Nullable
	@InjectFragment(R.id.single_prayer_fragment)
	private SinglePrayerFragment singlePrayerFragment;

    private ProgressDialog dialog;

    private ResultReceiver receiver;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mik_prayer_list_fragment);
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setIcon(R.drawable.ic_mik_logo_2d_white);

        receiver = new ResultReceiver(new Handler()) {
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                PrayersActivity.this.onReceiveResult(resultCode, resultData);
            }
        };

        dialog = new ProgressDialog(this);
        dialog.setTitle("Synchronizing");
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");
	}

    @Override
    public int theme() {
        return org.meruvian.midas.core.R.style.DarkTheme_Mutman;
    }

    public void onListSelected(Prayer prayer, int index) {

		if (singlePrayerFragment != null) {
			if (singlePrayerFragment.isInLayout()){
			    singlePrayerFragment.initializeData();
				singlePrayerFragment.setCurrentPage(index);
			} else {
				newActivity(prayer, index);
			}
		} else {
			newActivity(prayer, index);
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.main, menu);
//
//        return false;
//    }

    @Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		boolean itemSelected = super.onMenuItemSelected(featureId, item);
		if (item.getItemId() == android.R.id.home) {
		    super.onBackPressed();
		} else if (item.getItemId() == R.id.menu_refresh) {
            final Intent intent = new Intent(this, MidasReceiver.class);
            intent.putExtra("type", getIntent().getIntExtra("type", 0));
            intent.putExtra(MidasReceiver.EXTRA_RECEIVER, receiver);
            intent.setAction(MidasReceiver.ACTION_REFRESH);
            sendBroadcast(intent);
        } else if (item.getItemId() == R.id.menu_invite){
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Coba sekarang aplikasi Mutiara Iman Catholic di Android http://www.mutiara-iman.org/android";
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Rekomendasikan ke teman");
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }
		return itemSelected;
	}

    private void newActivity(Prayer prayer, int index) {
        Intent intent = new Intent(this, SinglePrayerActivity.class);

        intent.putExtra(BaseColumns._ID, prayer.getId());
        intent.putExtras(getIntent().getExtras());
        intent.putExtra("index", index);

        startActivity(intent);
    }

    private void alert(String title, String message) {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        dialog.setTitle(title);
        dialog.setMessage(message);

        dialog.show();
    }

    protected void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case PrayerSyncService.STATUS_STARTED:
                dialog.show();
                break;
            case PrayerSyncService.STATUS_FINISHED:
                if (dialog.isShowing())
                    dialog.dismiss();
                break;
            case PrayerSyncService.STATUS_CONNECTION_TIMEOUT:
                if (dialog.isShowing())
                    dialog.dismiss();
                alert("", "Connection timeout");
                break;
            case PrayerSyncService.STATUS_INTERNET_NOT_AVAILABLE:
                if (dialog.isShowing())
                    dialog.dismiss();
                alert("No internet connection available",
                        "Please check your internet connection");
                break;
            default:
                if (dialog.isShowing())
                    dialog.dismiss();
                break;
        }
    }
}
