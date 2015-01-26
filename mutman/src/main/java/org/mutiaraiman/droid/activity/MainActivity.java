package org.mutiaraiman.droid.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.splunk.mint.Mint;

import org.meruvian.midas.activity.receiver.MidasReceiver;
import org.meruvian.midas.core.drawer.Navigation;
import org.meruvian.midas.core.drawer.NavigationDrawer;
import org.meruvian.midas.core.drawer.NavigationDrawerAdapter;
import org.mutiaraiman.droid.R;
import org.mutiaraiman.droid.fragment.MainFragment;
import org.mutiaraiman.droid.provider.db.PrayersDbAdapter;
import org.mutiaraiman.droid.service.AlarmHelper;
import org.mutiaraiman.droid.service.PrayerSyncService;

/**
 * Created by ludviantoovandi on 28/10/14.
 */
public class MainActivity extends NavigationDrawer {
    private ResultReceiver receiver;

    private ProgressDialog dialog;

    @Override
    public Fragment mainFragment() {
        return new MainFragment();
    }

    @Override
    public void navigationAdapter(NavigationDrawerAdapter adapter) {
        PrayersDbAdapter dbAdapter = new PrayersDbAdapter(this);

        adapter.addNavigation(new Navigation("Kategori Tulisan", Navigation.NavigationType.TITLE));
        adapter.addNavigation(new Navigation("Doa", dbAdapter.getPrayerUnreadCount(0) + "", R.drawable.ic_action_prayer, Navigation.NavigationType.MENU));
        adapter.addNavigation(new Navigation("Renungan", dbAdapter.getPrayerUnreadCount(1) + "", R.drawable.ic_action_reflection, Navigation.NavigationType.MENU));
        adapter.addNavigation(new Navigation("Lagu Rohani", dbAdapter.getPrayerUnreadCount(4) + "", R.drawable.ic_action_worship, Navigation.NavigationType.MENU));
        adapter.addNavigation(new Navigation("Kisah Santo dan Santa", dbAdapter.getPrayerUnreadCount(2) + "", R.drawable.ic_action_stories, Navigation.NavigationType.MENU));
        adapter.addNavigation(new Navigation("Catholic Quote", dbAdapter.getPrayerUnreadCount(3) + "", R.drawable.ic_action_quote, Navigation.NavigationType.MENU));
    }

    @Override
    public void selectedItem(int position) {
        Intent intent = new Intent(this, PrayersActivity.class);
        switch (position) {
            case 0:
                break;
            case 1:
                intent.putExtra("type", 0);
                startActivity(intent);
                break;
            case 2:
                intent.putExtra("type", 1);
                startActivity(intent);
                break;
            case 3:
                intent.putExtra("type", 4);
                startActivity(intent);
                break;
            case 4:
                intent.putExtra("type", 2);
                startActivity(intent);
                break;
            case 5:
                intent.putExtra("type", 3);
                startActivity(intent);
                break;
            case 8:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onClickPreference() {
        startActivity(new Intent(this, MutiaraImanPreference.class));
    }

    @Override
    public int iconHome() {
        return R.drawable.ic_mik_logo_2d_white;
    }

    @Override
    public void onViewCreated() {
        Mint.initAndStartSession(this, "59c60b9e");

        receiver = new ResultReceiver(new Handler()) {
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                MainActivity.this.onReceiveResult(resultCode, resultData);
            }
        };

        dialog = new ProgressDialog(this);
        dialog.setTitle("Synchronizing");
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");

        PackageInfo pInfo;
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(),
                    PackageManager.GET_META_DATA);
            if (prefs.getLong("lastRunVersionCode", 0) < pInfo.versionCode) {
                Log.d(MainFragment.class.getSimpleName(), "Initializing new version");
                initSetup();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong("lastRunVersionCode", pInfo.versionCode);
                editor.commit();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
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

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public int theme() {
        return R.style.DarkTheme_Mutman;
    }

    private void initSetup() {
        Log.d("Mutman", "inititalization first run");
        Intent intent = new Intent(this, MidasReceiver.class);
        intent.putExtra(MidasReceiver.EXTRA_RECEIVER, receiver);
        intent.setAction(MidasReceiver.ACTION_REFRESH);
        sendBroadcast(intent);
        AlarmHelper.setNotifyPrayer(this);
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
