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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.meruvian.midas.database.Columns.PrayerColumns;
import org.meruvian.midas.database.Columns.PrayerColumns.Reminder;
import org.meruvian.midas.util.ConnectionUtil;
import org.mutiaraiman.droid.R;
import org.mutiaraiman.droid.provider.PrayerContentProvider;
import org.mutiaraiman.droid.provider.db.PrayersDbAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

/**
 * @author Dian Aditya
 * 
 */
public class PrayerSyncHelper {
	private Context context;
	private static PrayerSyncHelper instance = null;

	private PrayerSyncHelper(Context context) {
		this.context = context;
	}

	public static PrayerSyncHelper getInstace(Context context) {
		if (instance == null) {
			instance = new PrayerSyncHelper(context);
		}

		return instance;
	}

	public void synchronize(ResultReceiver receiver, Bundle extras) {
		final Intent intent = new Intent(Intent.ACTION_SYNC, null, context,
				PrayerSyncService.class);

		intent.setAction(extras.getString("action"));
		intent.putExtra("receiver", receiver);
		context.startService(intent);
	}

	public void close() {
		instance = null;
	}
	
	public static String getModuleByType(int type){
	    String typeString = "";
        switch (type) {
        case 0:
            typeString = "prayers";
            break;
        case 1:
            typeString = "reflections";
            break;
        case 2:
            typeString = "stories";
            break;
        case 3:
            typeString = "quote";
            break;
        case 4:
            typeString = "worship";
            break;
        }
        return typeString;
	}
	
	private static void jsonToDb(JSONObject jsonObject, Context context, int type){
	    SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
	    try {
            JSONArray prayerArrayList = jsonObject.getJSONArray("entityList");
            for (int i = 0; i < prayerArrayList.length(); i++) {
                JSONObject prayer = prayerArrayList.getJSONObject(i);

                ContentValues values = new ContentValues();
                values.put(PrayerColumns.TITLE, prayer.getString("title"));
                values.put(PrayerColumns.CONTENT, prayer.getString("content"));
                values.put(PrayerColumns.ON_SERVER_ID, prayer.getString("id"));
                values.put(PrayerColumns.TYPE, type);
                values.put(PrayerColumns.SYNC_DATE,
                        dateFormat.format(new Date(System.currentTimeMillis())));
                values.put(PrayerColumns.REMINDER,
                        Reminder.get(prayer.getString("reminder")).ordinal());

                long createDateMillis = prayer.getJSONObject("logInformation")
                        .getLong("createDate");

                Date createDate = new Date(createDateMillis);
                values.put(PrayerColumns.CREATE_DATE, dateFormat.format(createDate));

                Log.d("Data",
                        values.getAsString(PrayerColumns.TYPE) + " "
                                + values.getAsString(PrayerColumns.CREATE_DATE)
                                + " " + createDateMillis + " "
                                + values.getAsString(PrayerColumns.TITLE));

                if (values.getAsString(PrayerColumns.TITLE) != null)
                    if (!values.getAsString(PrayerColumns.TITLE).equalsIgnoreCase(
                            "null"))
                        context.getContentResolver().insert(
                                Uri.parse(PrayerContentProvider.CONTENT_PATH
                                        + PrayerContentProvider.TABLES[0]), values);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
	}
	
	public static boolean syncPage(Context context, int type, long page) throws NotFoundException, IOException, JSONException {
	    PrayersDbAdapter dbAdapter = new PrayersDbAdapter(context);
        JSONObject prayerJsonObject = ConnectionUtil.get(context.getResources().getString(R.string.prayer_datasource_url)
            + "module/" + getModuleByType(type) + ".json?max=10&page=" + page);
        Log.d("prayerJsonObject", prayerJsonObject.toString());
        
        long totalPageLocal = dbAdapter.getTotalPage(type, 10);
        long totalDataLocal = dbAdapter.getAllPrayersByPage((int) page, type).size();
        long totalPageServer = 0;
        long totalDataServer = 0;
        try {
           totalPageServer = prayerJsonObject.getLong("totalPage");
           totalDataServer = prayerJsonObject.getJSONArray("entityList").length();
        } catch (JSONException e) {
           e.printStackTrace();
        }
        Log.d("PrayerSyncHelper", "totalPageServer: "+totalPageServer+", totalPageLocal: "+totalPageLocal+", Loaded Page: "+page);
        if (totalPageServer > totalPageLocal || totalDataServer > totalDataLocal) {
            jsonToDb(prayerJsonObject, context, type);
        } else {
            return false;
        }
        return true;
	} 
	
	
	public static void sync(Context context, String datasourceUrl, int type, boolean newest) throws Exception {
		PrayersDbAdapter dbAdapter = new PrayersDbAdapter(context);
		long sync = dbAdapter.getSyncDate(type, newest);
		long from = 0;
		String criteria = "";

		if (!newest) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(sync);

			calendar.add(Calendar.MINUTE, -2);
			sync = calendar.getTimeInMillis();

			calendar.add(Calendar.DAY_OF_MONTH, -7);
			from = calendar.getTimeInMillis();

			criteria = "date&from=" + from + "&to=" + (sync);
			Log.d("FROM", new Date(from).toString());
			Log.d("TO", new Date(sync - 1).toString());
		} else {
			criteria = "date&from=" + (sync + 1) + "&to="
					+ System.currentTimeMillis();
		}
		JSONObject json = ConnectionUtil.get(datasourceUrl + "module/" + getModuleByType(type)
				+ ".json?" + criteria);
		jsonToDb(json, context, type);
	}
}
