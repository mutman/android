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
package org.mutiaraiman.droid.fragment;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.meruvian.midas.util.ConnectionUtil;
import org.mutiaraiman.droid.Prayer;
import org.mutiaraiman.droid.R;
import org.mutiaraiman.droid.activity.adapter.PrayerAdapter;
import org.mutiaraiman.droid.provider.db.PrayersDbAdapter;
import org.mutiaraiman.droid.service.PrayerSyncHelper;

import roboguice.fragment.RoboFragment;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Dian Aditya
 * 
 */
public class PrayerFragment extends RoboFragment {

	private PrayersDbAdapter dbAdapter;
	private PrayerAdapter adapter;
	private ListView prayerList;
	private OnListSelectedListener listSelectedListener;
	private LinearLayout mLoadLayout;
	private TextView mTipContent, mLoading;
	private ProgressBar progressBar;
	
	private List<Prayer> prayers = new ArrayList<Prayer>();
	private int type = 0;
	private int page = 0;
	private long totalPage = 0;
	boolean sync = false;
	int pageToLoad = 0;
    boolean initEmpty = false;
	private final LayoutParams mTipContentLayoutParams = new LayoutParams(
        LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT);

	private SharedPreferences prefs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbAdapter = new PrayersDbAdapter(getActivity());
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		Activity activity = getActivity();
		type = activity.getIntent().getExtras().getInt("type");
		Log.i(PrayerFragment.class.getSimpleName(), "type: "+ type);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View root = inflater.inflate(R.layout.mik_prayer_list, container);
		TextView prayerLastUpdate = (TextView) root.findViewById(R.id.prayer_last_update);

		prayerLastUpdate.setText("Last Updated " + DateFormat.format("dd/MM/yyyy", System.currentTimeMillis()));

		prayerList = (ListView) root.findViewById(R.id.prayer_list);
		totalPage = dbAdapter.getTotalPage(type, 10);
		Log.d("PrayerFragment", "Total Page: "+ totalPage+", Total Data in Local: "+dbAdapter.getPrayerCount(type));
		
		prayers = dbAdapter.getAllPrayers(type);
		int rounded = (int) Math.floor(prayers.size() / 10);
        page = rounded;
        initEmpty = prayers.size() == 0 ? true : false;
		Log.d("PrayerFragment", "Last Page: "+ page);
        adapter = new PrayerAdapter(getActivity(), R.layout.mik_prayer_list_comp,
                prayers, false);
        
        if(prefs.getBoolean("showLoadmore"+type, true)){
            //initialize load more button
            mLoadLayout = new LinearLayout(getActivity());
            mLoadLayout.setGravity(Gravity.CENTER);
            mLoadLayout.setPadding(0, 10, 0, 10);
            mLoadLayout.setBackgroundResource(R.color.actionbar_background_color);
            mLoadLayout.setOrientation(LinearLayout.HORIZONTAL);
            mTipContent = new TextView(getActivity());
            mTipContent.setText("Load More...");
            mTipContent.setTextSize(18);
            mTipContent.setTypeface(Typeface.DEFAULT_BOLD);
            mTipContent.setTextColor(Color.parseColor("#FFFFFF"));
            mLoadLayout.addView(mTipContent, mTipContentLayoutParams);
            progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleSmall);
            progressBar.setVisibility(View.GONE);
            mLoadLayout.addView(progressBar, mTipContentLayoutParams);
            mLoading = new TextView(getActivity());
            mLoading.setText("Loading...");
            mLoading.setTextSize(18);
            mLoading.setPadding(3, 0, 0, 0);
            mLoading.setTextColor(Color.parseColor("#FFFFFF"));
            mLoading.setVisibility(View.GONE);
            mLoadLayout.addView(mLoading, mTipContentLayoutParams);
            mLoadLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AsyncLoadMore().execute();
                }
            });
        prayerList.addFooterView(mLoadLayout);
        }
        
		prayerList.setAdapter(adapter);
		prayerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int i, long l) {
					listSelectedListener.onListSelected(prayers.get(i), i);
				}
			});
		return root;
	}
	
	private class AsyncLoadMore extends AsyncTask<Void, Void, Void> {
	    
        @Override
        protected void onPreExecute() {
            mTipContent.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            mLoading.setVisibility(View.VISIBLE);
        }
        
        @Override
        protected Void doInBackground(Void... arg) {
            if (prayers.size() % 10 != 0) {
                if(pageToLoad != 0)
                    pageToLoad = page - 1;
            } else {
                pageToLoad = page;
            }
            
            Log.d("PrayerFragment: ", "Page to load: " + pageToLoad);
            if(ConnectionUtil.isInternetAvailable(getActivity())){
                try {
                    sync = PrayerSyncHelper.syncPage(getActivity(), type, pageToLoad);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!sync) {
                                Toast.makeText(getActivity(), "No more data available", Toast.LENGTH_LONG).show();
                                prayerList.removeFooterView(mLoadLayout);
                                Editor editor = prefs.edit();
                                editor.putBoolean("showLoadmore"+type, false);
                                editor.commit();
                            } else {
                                Log.d("PrayerFragment", "Prayers Size: "+ prayers.size()+", "+page);
                                if(prayers.size() < 10){                                   
                                    prayers.clear();
                                    prayers.addAll(dbAdapter.getAllPrayersByPage(page , type));
                                    if(initEmpty)
                                        page++;
                                } else {                                
                                    prayers.addAll(dbAdapter.getAllPrayersByPage(page , type));
                                    page++;
                                }
                                Log.d("PrayerFragment", "Prayers Size: "+ prayers.size()+", "+page);
                                adapter.setPrayers(prayers);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                } catch (UnknownHostException e){ 
                    showMessage("Connection timeout: "+ e.getMessage());
                    e.printStackTrace();
                } catch (Exception e) {
                    showMessage("An error occured: "+ e.getMessage());
                    e.printStackTrace();
                }
            } else {
                showMessage("No internet connection available");
            }
            return null;
        }
        
	    private void showMessage(final String message){
	        getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show(); 
                }
            });
	    }
	    
        @Override
        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);
            mLoading.setVisibility(View.GONE);
            mTipContent.setVisibility(View.VISIBLE);
        }
	}
	
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listSelectedListener = (OnListSelectedListener) activity;
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
	}

	public void onResume() {
		super.onResume();
		adapter.setPrayers(dbAdapter.getAllPrayers(type));
		prayerList.setAdapter(adapter);
	}

	public List<Prayer> getPrayers() {
		return prayers;
	}

	public int getPrayersCount() {
		return prayers.size();
	}

	public interface OnListSelectedListener {
		public void onListSelected(Prayer prayer, int index);
	}
}
