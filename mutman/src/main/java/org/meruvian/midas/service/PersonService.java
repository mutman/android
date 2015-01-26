package org.meruvian.midas.service;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.meruvian.midas.processor.ProcessorFactory;
import org.meruvian.midas.util.ConnectionUtil;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;


public class PersonService extends IntentService {
	
	public PersonService() {
		super("Person Service");
	}
	
	protected void onHandleIntent(Intent intent) {
		JSONObject person = null;
        try {
            person = ConnectionUtil.get("http://10.0.2.2:8888/person");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
		String action = intent.getExtras().getString("action");
		ProcessorFactory.createProcessor(getApplicationContext(), action).parse(person);
		
		Intent broadCastIntent = new Intent();
		broadCastIntent.setAction(action);
		broadCastIntent.putExtra("action", action);
		Log.i("SERVICE", "send broadcast "+action);
		this.sendBroadcast(broadCastIntent);
	};
}
