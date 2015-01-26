package org.meruvian.midas.activity.page;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.meruvian.midas.util.ConnectionUtil;
import org.mutiaraiman.droid.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AsyncTaskPage extends LinearLayout {
	private Context context;

	public AsyncTaskPage(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public AsyncTaskPage(Context context) {
		super(context);
		this.context = context;
		init();
	}

	private void init() {
		LayoutInflater.from(context).inflate(R.layout.get_person, this, true);
		Button button = (Button) findViewById(R.id.getPersonBtn);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new AsyncGetPerson().execute("http://10.0.2.2:8888/person");
			}
		});
	}
	
	private class AsyncGetPerson extends AsyncTask<String, JSONObject, JSONObject>{
		
		private ProgressDialog Dialog = new ProgressDialog(context);
		
		@Override
		protected void onPreExecute() {
			Dialog.setMessage("loading data person..");
			Dialog.show();
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			Dialog.dismiss();
			TextView textView = (TextView) findViewById(R.id.nameTextView);
			textView.setText("Data Loaded, Person: "+result.toString());
		}
		
		@Override
		protected JSONObject doInBackground(String... params) {
			try {
                return ConnectionUtil.get(params[0]);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
			return null;
		}
		
	}
}
