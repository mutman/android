package org.mutiaraiman.droid.service;

import java.util.Date;

import org.meruvian.midas.database.Columns.PrayerColumns.Reminder;
import org.mutiaraiman.droid.Prayer;
import org.mutiaraiman.droid.R;
import org.mutiaraiman.droid.fragment.MainFragment;
import org.mutiaraiman.droid.provider.db.PrayersDbAdapter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class NotifyPrayerReceiver extends BroadcastReceiver {

    private static final String LOG = NotifyPrayerReceiver.class.getName();

    private PrayersDbAdapter adapter;
	private String datasourceUrl;
    private Bundle bundle;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        bundle = intent.getExtras();
        adapter = new PrayersDbAdapter(context);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean sync = sharedPrefs.getBoolean("sync_enable", true);
		if (sync) {
			new SyncPrayersTask().execute(context);
		} else {
			Log.d(LOG, "Data Synchronization: " + sync);
		}
    }

    private void remind(Context ctx, Reminder reminder) {
        Log.d("prayer", "remind" + reminder);
        Prayer prayer = adapter.getNewestToday(reminder);
        Log.d("prayer", "Prayer " + prayer);
        if (prayer != null) {
            showNotification(ctx, prayer, reminder);
        }
    }

    private void showNotification(Context ctx, Prayer prayer, Reminder reminder) {
        NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification =
            new Notification(R.drawable.ic_status_bible, new Date().toString(), System.currentTimeMillis());
        
        String type = "";
        switch (prayer.getType()) {
        case 0:
            type = "Doa";
            break;
        case 1:
            type = "Renungan";
            break;
        case 2:
            type = "Kisah Santo dan Santa";
            break;
        case 3:
            type = "Catholic Quote";
            break;
        case 4:
            type = "Lagu Rohani";
            break;
        }

        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.vibrate = new long[] { 0, 100, 200, 300 };
        notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        Intent intent = new Intent(ctx, MainFragment.class);
        intent.putExtra("type", prayer.getType());

        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, reminder.ordinal(), intent, 0);
        notification.setLatestEventInfo(ctx, type + " baru", "Periksa " + type.toLowerCase() + " baru hari ini!",
            pendingIntent);

        manager.notify(reminder.ordinal(), notification);
    }
    
    class SyncPrayersTask extends AsyncTask<Context, Void, Context> {
		@Override
		protected Context doInBackground(Context... context) {
			try {
				datasourceUrl = context[0].getResources().getString(R.string.prayer_datasource_url);
				PrayerSyncHelper.sync(context[0], datasourceUrl, 0, true);
				PrayerSyncHelper.sync(context[0], datasourceUrl, 1, true);
				PrayerSyncHelper.sync(context[0], datasourceUrl, 2, true);
                PrayerSyncHelper.sync(context[0], datasourceUrl, 3, true);
                PrayerSyncHelper.sync(context[0], datasourceUrl, 4, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return context[0];
		}
		
    	@Override
    	protected void onPostExecute(Context result) {
    		int notifId = bundle.getInt("notifId");
            Log.d(LOG, "received event -> " + notifId);
            switch (notifId) {
                case 6:
                    remind(result, Reminder._6_AM);
                    break;
                case 12:
                    remind(result, Reminder._12_PM);
                    break;
                case 18:
                    remind(result, Reminder._6_PM);
                    break;
                case 11:
                    remind(result, Reminder.TEST);
                    break;
            }
    	}
    }
}
