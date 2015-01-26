package org.meruvian.midas.processor;

import org.json.JSONException;
import org.json.JSONObject;
import org.meruvian.midas.database.MidasContentProvider;
import org.meruvian.midas.database.MidasHelper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

public class PersonProcessor implements Processor {
	private Context context;

	public PersonProcessor(Context context) {
		this.context = context;
	}

	@Override
	public void parse(JSONObject jsonObject) {
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		// try {
		// // values.put(MidasHelper.PERSON_NAME_COL,
		// // jsonObject.getString("name"));
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		resolver.insert(MidasContentProvider.PERSON_CONTENT_URI, values);
	}

}
