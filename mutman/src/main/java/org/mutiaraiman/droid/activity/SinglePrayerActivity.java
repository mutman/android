package org.mutiaraiman.droid.activity;

import java.util.ArrayList;
import java.util.List;

import org.meruvian.midas.core.defaults.DefaultActivity;
import org.meruvian.midas.ui.FlingLayout;
import org.mutiaraiman.droid.Prayer;
import org.mutiaraiman.droid.R;
import org.mutiaraiman.droid.fragment.MainFragment;
import org.mutiaraiman.droid.provider.db.PrayersDbAdapter;

import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SinglePrayerActivity extends DefaultActivity implements
		OnPageChangeListener {

	@InjectView(R.id.prayer_content)
	private LinearLayout contentLayout;

	@InjectView(R.id.prayer_type_txt)
	private TextView txtType;

	@InjectView(R.id.prayer_page_txt)
	private TextView txtPage;

	private View frameLayout;
	private FlingLayout flingLayout;

	private PrayersDbAdapter dbAdapter;
	private long count;

	private int type;

	private List<Prayer> prayers = new ArrayList<Prayer>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mik_prayer_single);
		dbAdapter = new PrayersDbAdapter(this);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Intent intent = getIntent();
		int id = intent.getIntExtra(BaseColumns._ID, -1);
		int index = intent.getIntExtra("index", 0);
		Prayer prayer = new Prayer();
		if (id == 0) {
			prayer = (Prayer) intent.getSerializableExtra("prayer");
		} else {
			prayer = dbAdapter.getPrayerById(id);
		}
		type = prayer.getType();
		count = dbAdapter.getPrayerCount(type);
		if (id == 0)
			count = count + 1;
		LayoutInflater inflater = getLayoutInflater();
		String prayerTitle = "";
		Log.i(SinglePrayerActivity.class.getSimpleName(), "type: "+ type);
		switch (type) {
        case 0:
            prayerTitle = "Doa dan Devosi";
            break;
        case 1:
            prayerTitle = "Renungan";
            break;
        case 2:
            prayerTitle = "Kisah Santo dan Santa";
            break;
        case 3:
            prayerTitle = "Catholic Quote";
            break;
        case 4:
            prayerTitle = "Lagu Rohani";
            break;
        }
		txtType.setText(prayerTitle);
		txtPage.setText((index + 1) + "/" + count);

		frameLayout = inflater.inflate(R.layout.main_swipe, null);
		flingLayout = (FlingLayout) frameLayout.findViewById(R.id.awesomepager);
		flingLayout.setOnPageChangeListener(this);

		prayers = dbAdapter.getAllPrayers(type);
		prayers.add(0, prayer);
		List<View> pager = new ArrayList<View>();
		for (Prayer p : prayers) {
			View layout = inflater.inflate(R.layout.mik_prayer_content, null);
//			TextView updateDate = (TextView) layout
//					.findViewById(R.id.prayer_updatedate_txt);
			TextView title = (TextView) layout
					.findViewById(R.id.prayer_title_txt);
			TextView content = (TextView) layout
					.findViewById(R.id.prayer_content_txt);
			
//			if(type==3){
//			    updateDate.setVisibility(View.GONE);
//			} else {
//			    updateDate.setText(p.getCreateDate());
//			}
			
			title.setText(Html.fromHtml("<u>" + p.getTitle() + "</u>"));
			content.setText(p.getContent());

			pager.add(layout);
		}

		flingLayout.addViewList(pager);
		if (index == 0) {
			dbAdapter.setPrayerAlreadyRead(prayers.get(0).getId());
		}
		flingLayout.setCurrentItem(index, true);

		contentLayout.addView(frameLayout, -1, -1);
	}

    @Override
    public int theme() {
        return org.meruvian.midas.core.R.style.DarkTheme_Mutman;
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
		boolean itemSelected = super.onMenuItemSelected(featureId, item);
		if (item.getItemId() == android.R.id.home) {
//		    startActivity(new Intent(SinglePrayerActivity.this, MainFragment.class));
            finish();
		}
		return itemSelected;
	}
	
	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		txtPage.setText((position + 1) + "/" + count);

		dbAdapter.setPrayerAlreadyRead(prayers.get(position).getId());
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}
}
