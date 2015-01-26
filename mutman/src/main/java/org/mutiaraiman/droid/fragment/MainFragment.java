package org.mutiaraiman.droid.fragment;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.meruvian.midas.activity.receiver.MidasReceiver;
import org.meruvian.midas.core.defaults.DefaultFragment;
import org.meruvian.midas.lib.ViewUtils;
import org.meruvian.midas.util.ConnectionUtil;
import org.mutiaraiman.droid.Prayer;
import org.mutiaraiman.droid.R;
import org.mutiaraiman.droid.activity.PrayersActivity;
import org.mutiaraiman.droid.activity.SinglePrayerActivity;
import org.mutiaraiman.droid.activity.adapter.HomePrayerAdapter;
import org.mutiaraiman.droid.provider.db.PrayersDbAdapter;
import org.mutiaraiman.droid.service.AlarmHelper;

import roboguice.inject.InjectResource;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class MainFragment extends DefaultFragment {
	private static final String LOG = MainFragment.class.getName();
	private View mainLayout;
	private ViewFlipper mFlipper;
	private ListView prayerListView;
	private PrayersDbAdapter dbAdapter;
	private LayoutInflater inflater;
	private ArrayList<Prayer> prayers;
	private Prayer p;
    private BroadcastReceiver syncBroadcastReceiver;
	private TextView baseTitle,baseDesc, mainContentTab, mainTitleTab, contentDoa, titleDoa, contentLagu, titleLagu, contentQuote, titleQuote, contentKisah, titleKisah;
    private LinearLayout mainContent, doa, lagu, quote, kisah;

    private View view;

    private Activity activity;
	
	@InjectResource(R.string.prayer_datasource_url)
	private String datasourceUrl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        activity = getActivity();
        setHasOptionsMenu(true);
        datasourceUrl = getString(R.string.prayer_datasource_url);
        AlarmHelper.setNotifyPrayer(getActivity());

        inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		dbAdapter = new PrayersDbAdapter(getActivity());
	}

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.main, menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.menu_refresh) {
//            Intent intent = new Intent(getActivity(), MidasReceiver.class);
//            intent.putExtra("type", getActivity().getIntent().getIntExtra("type", 0));
//            intent.putExtra(MidasReceiver.EXTRA_RECEIVER, receiver);
//            intent.setAction(MidasReceiver.ACTION_REFRESH);
//            getActivity().sendBroadcast(intent);
//        } else if (item.getItemId() == R.id.menu_invite){
//            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//            sharingIntent.setType("text/plain");
//            String shareBody = "Coba sekarang aplikasi Mutiara Iman Catholic di Android http://www.mutiara-iman.org/android";
//            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
//            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Rekomendasikan ke teman");
//            startActivity(Intent.createChooser(sharingIntent, "Share via"));
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    @Override
    public void onResume() {
        new GetTodayStory().execute("");
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.view = view;

        IntentFilter intentFilter = new IntentFilter("org.mutiaraiman.droid.SYNC_COMPLETE");
        syncBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                initPrayerList();
                p = getLatestPrayerByType(1);
                if (ViewUtils.isTablet(getActivity())) {
                    try {
                        initTablet(MainFragment.this.view);
                    } catch (Exception e) {
                        initFlipHeaderBase();
                    }
                } else {
                    try {
                        initFlipHeaderBase();
                    } catch (Exception e) {
                        initTablet(MainFragment.this.view);
                    }
                }
            }

            private void initFlipHeaderBase() {
                baseTitle.setText(p.getTitle());
                baseDesc.setText(p
                        .getContent().length() > 100 ? p.getContent()
                        .substring(0, 100).replaceAll("\n", " ")
                        + "..." : p.getContent().replaceAll("\n", " "));
                mFlipper.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newActivity(p);
                    }
                });
            }
        };

        getActivity().getApplicationContext().registerReceiver(syncBroadcastReceiver, intentFilter);

        p = getLatestPrayerByType(1);
        if (ViewUtils.isTablet(getActivity())) {
            try {
                initTablet(view);
            } catch (Exception e) {
                initBase(view);
            }
        } else {
            try {
                initBase(view);
            } catch (Exception e) {
                initTablet(view);
            }
        }

        prayerListView = (ListView) view.findViewById(R.id.prayer_list);
    }

    private void initTablet(View view) {
		mainContentTab = (TextView) view.findViewById(R.id.mainContentTab);
        mainContentTab.setText(p.getContent()
                .length() > 200 ? p.getContent().substring(0, 200)
                .replaceAll("\n", " ")
                + "..." : p.getContent().replaceAll("\n", " "));
		mainTitleTab = (TextView) view.findViewById(R.id.mainTittleTab);
        mainTitleTab.setText(p.getTitle());

		if (!p.getTitle().equals("Data kosong")) {
			mainContent = (LinearLayout) view.findViewById(R.id.maincontetn);
			mainContent.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    newActivityFragment(1);
                }
            });
		}

		p = getLatestPrayerByType(0);
		contentDoa = (TextView) view.findViewById(R.id.contentDoa);
        contentDoa.setText(p.getContent()
                .length() > 200 ? p.getContent().substring(0, 200)
                .replaceAll("\n", " ")
                + "..." : p.getContent().replaceAll("\n", " "));
		titleDoa = (TextView) view.findViewById(R.id.titleDoa);
        titleDoa.setText(p.getTitle());
		if (!p.getTitle().equals("Data kosong")) {
			doa = (LinearLayout) view.findViewById(R.id.doa);
		    doa.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    newActivityFragment(0);
                }
            });
		}

		p = getLatestPrayerByType(2);
		System.err.println(p.getTitle()+"|"+p.getType());
		contentLagu = (TextView) view.findViewById(R.id.contentLagu);
        contentLagu.setText(p.getContent()
                .length() > 200 ? p.getContent().substring(0, 200)
                .replaceAll("\n", " ")
                + "..." : p.getContent().replaceAll("\n", " "));
		titleLagu = (TextView) view.findViewById(R.id.titleLagu);
        titleLagu.setText(p.getTitle());
		if (!p.getTitle().equals("Data kosong")) {
			lagu = (LinearLayout) view.findViewById(R.id.lagu);
			lagu.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    newActivityFragment(2);
                }
            });
		}

		p = getLatestPrayerByType(4);
		contentQuote = (TextView) view.findViewById(R.id.contentQuote);
        contentQuote.setText(p.getContent()
                .length() > 200 ? p.getContent().substring(0, 200)
                .replaceAll("\n", " ")
                + "..." : p.getContent().replaceAll("\n", " "));
		titleQuote = (TextView) view.findViewById(R.id.tittleQuote);
        titleQuote.setText(p.getTitle());
		if (!p.getTitle().equals("Data kosong")) {
			quote = (LinearLayout) view.findViewById(R.id.quote);
			quote.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    newActivityFragment(4);
                }
            });
		}
	}

	private void setToday(View view, Prayer p){
		contentKisah = (TextView) view.findViewById(R.id.contentKisah);
        contentKisah.setText(p.getContent()
                .length() > 200 ? p.getContent().substring(0, 200)
                .replaceAll("\n", " ")
                + "..." : p.getContent().replaceAll("\n", " "));
		titleKisah = (TextView) view.findViewById(R.id.titleKisah);
        titleKisah.setText(p.getTitle());
		if (!p.getTitle().equals("Data kosong")) {
			kisah = (LinearLayout) view.findViewById(R.id.kisah);
			kisah.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    newActivityFragment(4);
                }
            });
		}
	}
	private void initBase(View view) {
		initBaseHeader(view);

		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		boolean sync = sharedPrefs.getBoolean("sync_enable", true);
		Log.d(LOG, "Data Synchronization: " + sync);

		/*
		 * mFlipper.startFlipping(); mFlipper.setFlipInterval(5000);
		 * mFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
		 * R.anim.push_left_in));
		 * mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
		 * R.anim.push_left_out));
		 */

		prayerListView = (ListView) view.findViewById(R.id.prayer_list);
		
	}

	private void initBaseHeader(View view) {
		mFlipper = ((ViewFlipper) view.findViewById(R.id.flipper));
		if (p != null) {
			View flippedItem = inflater.inflate(R.layout.flipped_item, null);
			baseTitle = ((TextView) flippedItem.findViewById(R.id.flipTitle));
			baseTitle.setText(p.getTitle());
			baseDesc = (TextView) flippedItem.findViewById(R.id.flipContent);
			baseDesc.setText(p
					.getContent().length() > 100 ? p.getContent()
					.substring(0, 100).replaceAll("\n", " ")
					+ "..." : p.getContent().replaceAll("\n", " "));
			mFlipper.addView(flippedItem);
		}
		if (!p.getTitle().equals("Data kosong")) {
			mFlipper.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					newActivity(p);
				}
			});
		}
	}

	private void initPrayerList() {
		prayers = new ArrayList<Prayer>();
		for (int i = 0; i <= 4; i++) {
			if (i != 2) {
				Prayer prayer = dbAdapter.getLatestPrayerByType(i);
				if (prayer != null)
					prayers.add(prayer);
			}
		}
		if(!ViewUtils.isTablet(getActivity())){
			prayerListView.setAdapter(new HomePrayerAdapter(getActivity(),
					R.layout.main_prayer_list, prayers, false));	
		}
	}

	private Prayer getLatestPrayerByType(int type) {
		Prayer prayer = dbAdapter.getLatestPrayerByType(type);
		if (prayer == null) {
			prayer = new Prayer("Data kosong",
					"silahkan synchronize data terlebih dahulu untuk mengambil dari server");
		}
		return prayer;
	}

	private void newActivityFragment(int type) {
		Intent intent = new Intent(getActivity(), PrayersActivity.class);
		intent.putExtra("type", type);
		startActivity(intent);
	}

	private void newActivity(Prayer prayer) {
		Intent intent = new Intent(getActivity(), SinglePrayerActivity.class);
		System.err.println(prayer.getId()+"xxxxxxxxx");
		if (prayer.getId() == 0) {
			intent.putExtra(BaseColumns._ID, prayer.getId());
			intent.putExtra("prayer", prayer);
			intent.putExtra("index", 0);
			startActivity(intent);
		}else{
			intent.putExtra(BaseColumns._ID, prayer.getId());
			intent.putExtra("index", 0);
			startActivity(intent);
		}
	}
	
	private class GetTodayStory extends AsyncTask<String, Integer, Prayer> {
		protected Prayer doInBackground(String... x) {
			try {
				JSONObject json = ConnectionUtil.get(datasourceUrl
						+ "module/today.json");
				Prayer prayer = new Prayer();
				if (json != null) {
					prayer.setId(0);
					prayer.setTitle(json.getString("title"));
					prayer.setContent(json.getString("content"));
					prayer.setType(2);
					return prayer;
				} else {
					return null;
				}

			} catch (IOException e) {
				e.printStackTrace();
				return new Prayer();
			} catch (JSONException e) {
				e.printStackTrace();
				return new Prayer();
			}
		}

		protected void onPostExecute(Prayer result) {
			if(result!=null){
				if (ViewUtils.isTablet(activity.getApplicationContext())) {
					setToday(view, result);
				} else {
					initPrayerList();
					if (result != null) {
						prayers.add(result);
					}
					prayerListView
							.setOnItemClickListener(new OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> av, View v,
										int i, long l) {
									newActivity(prayers.get(i));
								}
							});
				}
			}
		}
	}
}
