package org.meruvian.midas.service;

import org.meruvian.midas.util.ConnectionUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class ServiceHelper {

	private Context context;
	private static ServiceHelper serviceHelper;

	public ServiceHelper(Context context) {
		this.context = context;
	}

	public static ServiceHelper getInstance(Context context) {
		if (serviceHelper == null)
			serviceHelper = new ServiceHelper(context);
		serviceHelper.setContext(context);
		return serviceHelper;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public boolean startService(String action) {
		return startService(action, null);
	}

	public boolean startService(String action, Bundle bundle) {
		if (ConnectionUtil.isInternetAvailable(context)) {
			Intent intent = null;

			if (action.equals(MidasActions.PERSONS_ACTION)) {
				intent = new Intent(context, PersonService.class);
				intent.putExtra("action", action);
			}
			context.startService(intent);

		} else {
			Toast.makeText(context, "Connection Unavailable", Toast.LENGTH_LONG)
					.show();
			return false;
		}
		return true;
	}
}
