/*
 * Copyright 2012 Meruvian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meruvian.midas.activity.receiver;

import org.mutiaraiman.droid.service.AlarmHelper;
import org.mutiaraiman.droid.service.PrayerSyncHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;
import android.util.Log;

/**
 * @author "Dias Nurul Arifin"
 * @author Dian Aditya
 * 
 */
public class MidasReceiver extends BroadcastReceiver {
	public static final String EXTRA_RECEIVER = "EXTRA_RECEIVER";
	public static final String ACTION_FIRST_LOAD = "ACTION_FIRST_LOAD";
	public static final String ACTION_REFRESH = "ACTION_REFRESH";
	public static final String ACTION_LOAD_MORE = "ACTION_LOAD_MORE";
	public static final String ACTION_NOTIFY = "ACTION_NOTIFY";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("Receiver received", "Received " + intent.getAction());

		PrayerSyncHelper syncHelper = PrayerSyncHelper.getInstace(context);
		ResultReceiver receiver = intent.getParcelableExtra(EXTRA_RECEIVER);

		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {			
			AlarmHelper.setNotifyPrayer(context);
		}

		if (ACTION_REFRESH.equals(intent.getAction())
				|| ACTION_LOAD_MORE.equals(intent.getAction())) {
			intent.putExtra("action", intent.getAction());
			syncHelper.synchronize(receiver, intent.getExtras());
		}
	}
	
}
