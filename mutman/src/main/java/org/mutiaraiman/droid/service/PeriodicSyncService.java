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
package org.mutiaraiman.droid.service;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.meruvian.midas.activity.receiver.MidasReceiver;
import org.meruvian.midas.database.Columns.PrayerColumns.Reminder;
import org.mutiaraiman.droid.Prayer;
import org.mutiaraiman.droid.activity.MainActivity;
import org.mutiaraiman.droid.provider.db.PrayersDbAdapter;

import roboguice.service.RoboService;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;

/**
 * @author Dian Aditya
 * 
 */
@Deprecated
public class PeriodicSyncService extends RoboService {

	private PrayersDbAdapter adapter;

	private static int counter = 0;

	private static int getCounter() {
		if (counter > 1000)
			counter = 0;

		return counter++;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		adapter = new PrayersDbAdapter(this);

		doSync();
		checkNewest();
	}

	private void doSync() {
		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.HOUR_OF_DAY, 4);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		if (((Long) System.currentTimeMillis()).compareTo(calendar
				.getTimeInMillis()) > 0) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}

		Log.d("Start alarm at",
				"Start alarm at "
						+ DateFormat.format("dd MMMM yyyy hh:mm:ss", calendar)
								.toString());

		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Service service = PeriodicSyncService.this;

				Intent intent = new Intent(service, MidasReceiver.class);
				intent.setAction(MidasReceiver.ACTION_REFRESH);
				service.sendBroadcast(intent);
			}
		};

		final Handler handler = new Handler();
		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				handler.post(runnable);
			}
		}, calendar.getTime(), AlarmManager.INTERVAL_DAY);
	}

	private static final Date getTimeFromNow(int hour, int minute, int second) {
		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);

		if (((Long) System.currentTimeMillis()).compareTo(calendar
				.getTimeInMillis()) > 0) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}

		return calendar.getTime();
	}

	private void checkNewest() {
        final Handler handler = new Handler();
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        remind(Reminder._6_AM);
                    }
                });
            }
        }, getTimeFromNow(6, 0, 0), AlarmManager.INTERVAL_DAY);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        remind(Reminder._12_PM);
                    }
                });
            }
        }, getTimeFromNow(12, 0, 0), AlarmManager.INTERVAL_DAY);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        remind(Reminder._6_PM);
                    }
                });
            }
        }, getTimeFromNow(18, 0, 0), AlarmManager.INTERVAL_DAY);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        remind(Reminder.TEST);
                    }
                });
            }
        }, getTimeFromNow(10, 0, 0), AlarmManager.INTERVAL_DAY);
    }

	private void remind(Reminder reminder) {
		Prayer prayer = adapter.getNewestToday(reminder);
		Log.d("prayer", "Prayer " + prayer);
		if (prayer != null) {
			showNotification(prayer);
		}
	}

	private void showNotification(Prayer prayer) {
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification(
				android.R.drawable.star_on, new Date().toString(),
				System.currentTimeMillis());

		String type = prayer.getType() == 0 ? "Doa" : "Renungan";

		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.vibrate = new long[] { 0, 100, 200, 300 };
		notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("type", prayer.getType());

		PendingIntent pendingIntent = PendingIntent.getActivity(this,
				getCounter(), intent, 0);
		notification.setLatestEventInfo(this, type + " baru",
				"Periksa " + type.toLowerCase() + " baru hari ini!",
				pendingIntent);

		manager.notify(getCounter(), notification);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
