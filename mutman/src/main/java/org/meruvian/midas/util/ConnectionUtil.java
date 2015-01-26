package org.meruvian.midas.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;

public class ConnectionUtil {

	private static HttpParams getHttpParams(int connectionTimeout,
			int socketTimeout) {
		HttpParams params = new BasicHttpParams();

		HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
		HttpConnectionParams.setSoTimeout(params, socketTimeout);

		return params;
	}

	public static boolean isInternetAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm.getActiveNetworkInfo() != null)
			return cm.getActiveNetworkInfo().isConnectedOrConnecting();
		else
			return false;
	}

	public static JSONObject get(String url) throws IOException, JSONException {
		JSONObject json = null;
		HttpClient httpClient = new DefaultHttpClient(getHttpParams(15000,
				15000));
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Content-Type", "application/json");
		try {
			HttpResponse response = httpClient.execute(httpGet);
			json = new JSONObject(convertEntityToString(response.getEntity()));
			json.put("status", "success");
			return json;	
		} catch (UnknownHostException e) {
			return null;
		}
	}

	public static JSONObject post(String url, List<NameValuePair> nameValuePairs) {
		JSONObject json = null;
		HttpClient httpClient = new DefaultHttpClient(getHttpParams(15000,
				15000));
		HttpPost httpPost = new HttpPost(url);
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpClient.execute(httpPost);
			json = new JSONObject(convertEntityToString(response.getEntity()));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	public static String convertEntityToString(HttpEntity entity) {
		InputStream instream;
		StringBuilder total = null;
		try {
			instream = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					instream));
			total = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				total.append(line);
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return total.toString();
	}
}
