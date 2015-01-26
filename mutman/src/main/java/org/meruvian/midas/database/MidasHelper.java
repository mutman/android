package org.meruvian.midas.database;

import org.meruvian.midas.database.Columns.PrayerCategoryColumns;
import org.meruvian.midas.database.Columns.PrayerColumns;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class MidasHelper extends SQLiteOpenHelper {

	public static final String DATABASE = "mutiaraiman.db";
	private static final int VERSION_CODE = 1;
	private static final int VERSION = 1;

	public interface Tables {
		String PRAYERS = "mi_prayers";
		String PRAYERS_CAT = "mi_prayers_cat";
	}

	public MidasHelper(Context context) {
		super(context, DATABASE, null, VERSION_CODE);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + Tables.PRAYERS + " (" + BaseColumns._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ PrayerColumns.ON_SERVER_ID + " TEXT UNIQUE NOT NULL, "
				+ PrayerColumns.ALREADY_READ + " DEFAULT '0', "
				+ PrayerColumns.CATEGORY_ID + " TEXT, " + PrayerColumns.TITLE
				+ " TEXT NOT NULL, " + PrayerColumns.CONTENT + " TEXT, "
				+ PrayerColumns.CREATE_DATE + " DATETIME, "
				+ PrayerColumns.SYNC_DATE
				+ " DATETIME DEFAULT CURRENT_TIMESTAMP, "
				+ PrayerColumns.REMINDER + " INTEGER, " + PrayerColumns.TYPE
				+ " INTEGER DEFAULT 0)");

		db.execSQL("CREATE TABLE " + Tables.PRAYERS_CAT + " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ PrayerCategoryColumns.ON_SERVER_ID
				+ " TEXT UNIQUE NOT NULL, " + PrayerCategoryColumns.NAME
				+ " TEXT NOT NULL, " + PrayerCategoryColumns.DESCRIPTION
				+ " TEXT," + PrayerCategoryColumns.SYNC_DATE
				+ " DATETIME DEFAULT CURRENT_TIMESTAMP)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < newVersion) {
			// db.execSQL("DROP TABLE IF EXIST " + Tables.PRAYERS);
			// db.execSQL("DROP TABLE IF EXIST " + Tables.PRAYERS_CAT);
			//
			// onCreate(db);
		}
	}

}
