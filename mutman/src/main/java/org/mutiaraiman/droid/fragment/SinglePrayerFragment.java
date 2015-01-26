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

import java.util.ArrayList;
import java.util.List;

import org.meruvian.midas.ui.FlingLayout;
import org.mutiaraiman.droid.Prayer;
import org.mutiaraiman.droid.R;
import org.mutiaraiman.droid.provider.db.PrayersDbAdapter;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Dian Aditya
 * 
 */
public class SinglePrayerFragment extends RoboFragment implements
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

	private int type;

	private String count;

	private List<Prayer> prayers;

    private LayoutInflater inflater;

    private int index;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    this.inflater = inflater;
		View root = inflater.inflate(R.layout.mik_prayer_single, container);
		dbAdapter = new PrayersDbAdapter(getActivity());

		Intent intent = getActivity().getIntent();
		index = intent.getIntExtra("index", 0);

		type = intent.getIntExtra("type", 0);
		count = dbAdapter.getPrayerCount(type) + "";

		txtType = (TextView) root.findViewById(R.id.prayer_type_txt);
		String prayerTitle = "";
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

		txtPage = (TextView) root.findViewById(R.id.prayer_page_txt);
		txtPage.setText((index + 1) + "/" + count);

		frameLayout = inflater.inflate(R.layout.main_swipe, null);
		flingLayout = (FlingLayout) frameLayout.findViewById(R.id.awesomepager);
		flingLayout.setOnPageChangeListener(this);

		initializeData();
		
		flingLayout.setCurrentItem(index, true);

		contentLayout = (LinearLayout) root.findViewById(R.id.prayer_content);
		contentLayout.addView(frameLayout, -1, -1);

		return root;
	}

	public void setCurrentPage(int index) {
		flingLayout.setCurrentItem(index);
	}

	public void initializeData(){
	    prayers = dbAdapter.getAllPrayers(type);
	    List<View> pager = new ArrayList<View>();
	    for (Prayer p : prayers) {
            View layout = inflater.inflate(R.layout.mik_prayer_content, null);
            TextView title = (TextView) layout.findViewById(R.id.prayer_title_txt);
            TextView content = (TextView) layout.findViewById(R.id.prayer_content_txt);
            title.setText(Html.fromHtml("<u>" + p.getTitle() + "</u>"));
            content.setText(p.getContent());
            pager.add(layout);
        }
	    flingLayout.addViewList(pager);
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
