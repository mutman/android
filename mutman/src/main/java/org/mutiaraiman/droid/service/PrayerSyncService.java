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

import org.meruvian.midas.activity.receiver.MidasReceiver;
import org.meruvian.midas.util.ConnectionUtil;
import org.mutiaraiman.droid.R;

import roboguice.inject.InjectResource;
import roboguice.service.RoboIntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

/**
 * @author Dian Aditya
 * 
 */
public class PrayerSyncService extends RoboIntentService {

	public static final int STATUS_STARTED = 0;
	public static final int STATUS_FINISHED = 1;
	public static final int STATUS_INTERNET_NOT_AVAILABLE = 2;
	public static final int STATUS_CONNECTION_TIMEOUT = 3;

	/**
	 * @param name
	 */
	public PrayerSyncService(String name) {
		super(name);
	}

	public PrayerSyncService() {
		super("");
	}

	@InjectResource(R.string.prayer_datasource_url)
	private String datasourceUrl;


	public void onCreate() {
		super.onCreate();
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		int command = super.onStartCommand(intent, flags, startId);
		return command;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		ResultReceiver receiver = intent.getParcelableExtra("receiver");
		if (receiver == null) {
			return;
		}

		if (ConnectionUtil.isInternetAvailable(this)) {
			try {
				receiver.send(STATUS_STARTED, Bundle.EMPTY);

				if (MidasReceiver.ACTION_LOAD_MORE.equals(intent.getAction())) {
					PrayerSyncHelper.sync(this, datasourceUrl, intent.getIntExtra("type" ,0), false);
					Log.i(PrayerSyncService.class.getSimpleName(), "Action Load More :"+ intent.getIntExtra("type" ,0));
				} else {
				    Log.i(PrayerSyncService.class.getSimpleName(), "Action Refresh");
					PrayerSyncHelper.sync(this, datasourceUrl, 0, true);
					PrayerSyncHelper.sync(this, datasourceUrl, 1, true);
					PrayerSyncHelper.sync(this, datasourceUrl, 2, true);
                    PrayerSyncHelper.sync(this, datasourceUrl, 3, true);
                    PrayerSyncHelper.sync(this, datasourceUrl, 4, true);
				}

				receiver.send(STATUS_FINISHED, Bundle.EMPTY);
				Intent i = new Intent("org.mutiaraiman.droid.SYNC_COMPLETE");
				this.sendBroadcast(i);
			} catch (Exception e) {
				receiver.send(STATUS_CONNECTION_TIMEOUT, Bundle.EMPTY);
			}
		} else {
			receiver.send(STATUS_INTERNET_NOT_AVAILABLE, Bundle.EMPTY);
		}

		stopSelf();
	}
}
