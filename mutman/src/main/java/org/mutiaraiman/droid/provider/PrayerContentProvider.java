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
package org.mutiaraiman.droid.provider;

import org.meruvian.midas.database.MidasHelper;
import org.meruvian.midas.database.MidasHelper.Tables;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * @author Dian Aditya
 * 
 */
public class PrayerContentProvider extends ContentProvider {

	private MidasHelper dbHelper;

	public static final String TABLES[] = { Tables.PRAYERS, Tables.PRAYERS_CAT };

	private UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH) {
		{
			int i = 0;
			for (String table : TABLES) {
				addURI(AUTHORITY, table, i);
				i++;
			}
		}
	};

	public static final String AUTHORITY = PrayerContentProvider.class
			.getName().toLowerCase();
	public static final String CONTENT_PATH = "content://" + AUTHORITY + "/";

	@Override
	public boolean onCreate() {
		dbHelper = new MidasHelper(getContext());

		return false;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();

		int deleted = database.delete(TABLES[matcher.match(uri)], selection,
				selectionArgs);

		getContext().getContentResolver().notifyChange(uri, null);

		return deleted;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();

		long id = database.insert(TABLES[matcher.match(uri)], null, values);

		getContext().getContentResolver().notifyChange(uri, null);

		return Uri.parse(TABLES[matcher.match(uri)] + "/" + id);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		SQLiteDatabase database = dbHelper.getWritableDatabase();

		builder.setTables(TABLES[matcher.match(uri)]);
		Cursor cursor = builder.query(database, projection, selection,
				selectionArgs, null, null, sortOrder);
		
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		SQLiteDatabase database = dbHelper.getWritableDatabase();
		int updated = database.update(TABLES[matcher.match(uri)], values,
				selection, selectionArgs);

		getContext().getContentResolver().notifyChange(uri, null);

		return updated;
	}

	@Override
	@TargetApi(11)
	public void shutdown() {
		dbHelper.close();
		super.shutdown();
	}

}
