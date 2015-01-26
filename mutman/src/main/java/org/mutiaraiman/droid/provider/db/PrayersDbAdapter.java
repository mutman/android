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
package org.mutiaraiman.droid.provider.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.meruvian.midas.database.Columns.PrayerColumns;
import org.meruvian.midas.database.Columns.PrayerColumns.Reminder;
import org.mutiaraiman.droid.Prayer;
import org.mutiaraiman.droid.provider.PrayerContentProvider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * @author Dian Aditya
 * 
 */
public class PrayersDbAdapter {
	private Context context;
	private Uri dbUri = Uri.parse(PrayerContentProvider.CONTENT_PATH
			+ PrayerContentProvider.TABLES[0]);

	public PrayersDbAdapter(Context context) {
		this.context = context;
	}
	
	public long getTotalPage(int type, int limit){
	    long rowcount = getPrayerCount(type);
	    long page = rowcount / limit;
        return rowcount % limit > 0 ? page + 1 : page;
	}
	
	public Prayer getLatestPrayerByType(int type){
	    Cursor cursor = context.getContentResolver().query(dbUri, null, PrayerColumns.TYPE + "= ?", 
	        new String[] { type + "" }, PrayerColumns.CREATE_DATE + " DESC" + " LIMIT "+ 1);
	    Prayer prayer = new Prayer() {

            private static final long serialVersionUID = -1013207117501325010L;
            
            @Override
            public String toString() {
                return getTitle();
            }
        };

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                prayer.setId(cursor.getInt(cursor
                        .getColumnIndexOrThrow(BaseColumns._ID)));
                prayer.setTitle(cursor.getString(cursor
                        .getColumnIndexOrThrow(PrayerColumns.TITLE)));
                prayer.setContent(cursor.getString(cursor
                        .getColumnIndexOrThrow(PrayerColumns.CONTENT)));
                prayer.setCreateDate(cursor.getString(cursor
                        .getColumnIndexOrThrow(PrayerColumns.CREATE_DATE)));
                prayer.setReminder(cursor.getString(cursor
                        .getColumnIndexOrThrow(PrayerColumns.REMINDER)));
                prayer.setType(cursor.getInt(cursor
                        .getColumnIndexOrThrow(PrayerColumns.TYPE)));
            } else {
                cursor.close();
                return null;
            }
            cursor.close();
        }
        return prayer;
	}
	
	public List<Prayer> getAllPrayersByPage(int page, int type){
	    Cursor cursor = context.getContentResolver().query(dbUri, null, 
	            PrayerColumns.TYPE + "= ?", new String[] { type + ""}, 
	            PrayerColumns.CREATE_DATE + " DESC" + " LIMIT " + page*10 + ", 10");
	    List<Prayer> prayers = new ArrayList<Prayer>();
	    if (cursor != null) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Prayer prayer = new Prayer() {
                        private static final long serialVersionUID = -7565771922753501470L;
                        public String toString() {
                            return getTitle();
                        }
                    };
                    prayer.setId(cursor.getInt(cursor
                            .getColumnIndexOrThrow(BaseColumns._ID)));
                    prayer.setTitle(cursor.getString(cursor
                            .getColumnIndexOrThrow(PrayerColumns.TITLE)));
                    prayer.setContent(cursor.getString(cursor
                            .getColumnIndexOrThrow(PrayerColumns.CONTENT)));
                    prayer.setCreateDate(cursor.getString(cursor
                            .getColumnIndexOrThrow(PrayerColumns.CREATE_DATE)));
                    prayer.setReminder(cursor.getString(cursor
                            .getColumnIndexOrThrow(PrayerColumns.REMINDER)));
                    prayer.setType(cursor.getInt(cursor
                            .getColumnIndexOrThrow(PrayerColumns.TYPE)));
                    prayer.setAlreadyRead(cursor.getInt(cursor
                            .getColumnIndexOrThrow(PrayerColumns.ALREADY_READ)) > 0);
                    prayers.add(prayer);
                }
            }
            cursor.close();
        }
	    Log.d("PrayersDbAdapter", "Query: page="+page+",type="+type+",Found: "+prayers.size());
	    return prayers;
	}
	
	public List<Prayer> getAllPrayers(int type) {
		Cursor cursor = context.getContentResolver().query(dbUri, null,
				PrayerColumns.TYPE + "= ?", new String[] { type + "" },
				PrayerColumns.CREATE_DATE + " DESC");

		List<Prayer> prayers = new ArrayList<Prayer>();

		if (cursor != null) {
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					Prayer prayer = new Prayer() {
						
						private static final long serialVersionUID = -7565771922753501470L;

						public String toString() {
							return getTitle();
						}
					};

					prayer.setId(cursor.getInt(cursor
							.getColumnIndexOrThrow(BaseColumns._ID)));
					prayer.setTitle(cursor.getString(cursor
							.getColumnIndexOrThrow(PrayerColumns.TITLE)));
					prayer.setContent(cursor.getString(cursor
							.getColumnIndexOrThrow(PrayerColumns.CONTENT)));
					prayer.setCreateDate(cursor.getString(cursor
							.getColumnIndexOrThrow(PrayerColumns.CREATE_DATE)));
					prayer.setReminder(cursor.getString(cursor
							.getColumnIndexOrThrow(PrayerColumns.REMINDER)));
					prayer.setType(cursor.getInt(cursor
							.getColumnIndexOrThrow(PrayerColumns.TYPE)));
					prayer.setAlreadyRead(cursor.getInt(cursor
							.getColumnIndexOrThrow(PrayerColumns.ALREADY_READ)) > 0);

					prayers.add(prayer);
				}
			}

			cursor.close();
		}

		return prayers;
	}

	public long getPrayerCount(int type) {
		Cursor cursor = context.getContentResolver().query(dbUri,
				new String[] { "count(_id)" }, "type = ?",
				new String[] { type + "" }, null);

		long count = 0;

		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				count = cursor.getLong(0);
			}
			cursor.close();
		}

		return count;
	}

	public long getSyncDate(int type, boolean last) {
		Cursor cursor = context.getContentResolver().query(
				dbUri,
				new String[] { (last ? "max(" : "min(")
						+ PrayerColumns.SYNC_DATE + ")" },
				PrayerColumns.TYPE + "= ?", new String[] { type + "" }, null);

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");

		long lastSync = last ? System.currentTimeMillis() : 0;

		if (cursor != null) {
			String date;
			if (cursor.moveToFirst() && ((date = cursor.getString(0)) != null)) {
				try {
					lastSync = dateFormat.parse(date).getTime();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else {
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_MONTH, -10);

				lastSync = calendar.getTimeInMillis();

			}

			cursor.close();
		}

		return lastSync;
	}

	public Prayer getPrayerById(int id) {
		Cursor cursor = context.getContentResolver().query(dbUri, null,
				BaseColumns._ID + "= ?", new String[] { id + "" }, null);

		Prayer prayer = new Prayer() {

			private static final long serialVersionUID = -1013207117501325010L;

			public String toString() {
				return getTitle();
			}
		};

		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();

				prayer.setId(cursor.getInt(cursor
						.getColumnIndexOrThrow(BaseColumns._ID)));
				prayer.setTitle(cursor.getString(cursor
						.getColumnIndexOrThrow(PrayerColumns.TITLE)));
				prayer.setContent(cursor.getString(cursor
						.getColumnIndexOrThrow(PrayerColumns.CONTENT)));
				prayer.setCreateDate(cursor.getString(cursor
						.getColumnIndexOrThrow(PrayerColumns.CREATE_DATE)));
				prayer.setReminder(cursor.getString(cursor
						.getColumnIndexOrThrow(PrayerColumns.REMINDER)));
				prayer.setType(cursor.getInt(cursor
						.getColumnIndexOrThrow(PrayerColumns.TYPE)));
			} else {
				cursor.close();
				return null;
			}

			cursor.close();
		}

		return prayer;
	}

	public Prayer getNewestToday(Reminder time) {
		Uri uri = dbUri.buildUpon().appendQueryParameter("LIMIT", "1").build();

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		calendar.set(Calendar.HOUR_OF_DAY, 18);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		String criteria = PrayerColumns.ALREADY_READ + " = '0' AND "
				+ PrayerColumns.CREATE_DATE + " > '"+dateFormat.format(calendar.getTime())+"' AND "
				+ PrayerColumns.REMINDER + " = "+time.ordinal();
		// " AND " + PrayerColumns.TYPE + " = ?";

		Cursor cursor = context.getContentResolver().query(
				uri,
				null,
				criteria,null/*
				new String[] { "0", dateFormat.format(calendar.getTime()),
						"" + time.ordinal() , "1" }*/, null);
	    Log.d("ngux", "{0},{"+dateFormat.format(calendar.getTime())+"},{"+ time.ordinal()+"}");
		Prayer prayer = new Prayer();

		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();

				prayer.setId(cursor.getInt(cursor
						.getColumnIndexOrThrow(BaseColumns._ID)));
				prayer.setTitle(cursor.getString(cursor
						.getColumnIndexOrThrow(PrayerColumns.TITLE)));
				prayer.setContent(cursor.getString(cursor
						.getColumnIndexOrThrow(PrayerColumns.CONTENT)));
				prayer.setCreateDate(cursor.getString(cursor
						.getColumnIndexOrThrow(PrayerColumns.CREATE_DATE)));
				prayer.setReminder(cursor.getString(cursor
						.getColumnIndexOrThrow(PrayerColumns.REMINDER)));
				prayer.setType(cursor.getInt(cursor
						.getColumnIndexOrThrow(PrayerColumns.TYPE)));
			} else {
				cursor.close();
				return null;
			}

			cursor.close();
		}

		return prayer;
	}

	public int setPrayerAlreadyRead(int id) {
		ContentValues values = new ContentValues();
		values.put(PrayerColumns.ALREADY_READ, 1);

		int update = context.getContentResolver().update(dbUri, values,
				BaseColumns._ID + "= ?", new String[] { id + "" });

		return update;
	}
	
	public int getPrayerUnreadCount(int type){
		Cursor cursor = context.getContentResolver().query(dbUri,
				new String[] { "count(_id)" }, "type = ? AND "+PrayerColumns.ALREADY_READ+" = '0'",
				new String[] { type + "" }, null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		return count;
	}
}
