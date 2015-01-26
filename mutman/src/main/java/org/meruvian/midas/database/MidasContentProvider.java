package org.meruvian.midas.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MidasContentProvider extends ContentProvider {

	public static final String CONTENT_AUTHORITY = "org.meruvian.midas";
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://"
			+ CONTENT_AUTHORITY);

	private static final String PATH_PERSON = "person";

	public static final Uri PERSON_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
			.appendPath(PATH_PERSON).build();

	private MidasHelper databaseHelper;
	private static final UriMatcher uriMatcher = buildMatcher();

	private static final int PERSON = 100;

	private static UriMatcher buildMatcher() {
		UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		matcher.addURI(CONTENT_AUTHORITY, "person", PERSON);
		return matcher;

	}

	@Override
	public boolean onCreate() {
		databaseHelper = new MidasHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		return null;
	}

	@Override
	public String getType(Uri uri) {

		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		int match = uriMatcher.match(uri);

		switch (match) {
		case PERSON: {
			// long _id = db.insertOrThrow(MidasHelper.PERSON_TABLE, null,
			// values);
			// getContext().getContentResolver().notifyChange(uri, null);
			// return buildUri(PERSON_CONTENT_URI, String.valueOf(_id));
		}

		default: {
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		return 0;
	}

	public static Uri buildUri(Uri uri, String id) {
		return uri.buildUpon().appendPath(id).build();
	}
}
