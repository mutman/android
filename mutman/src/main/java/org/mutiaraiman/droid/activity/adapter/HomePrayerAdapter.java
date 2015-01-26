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
package org.mutiaraiman.droid.activity.adapter;

import java.util.List;

import org.mutiaraiman.droid.Prayer;
import org.mutiaraiman.droid.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Dian Aditya
 * 
 */
public class HomePrayerAdapter extends ArrayAdapter<Prayer> {

    private List<Prayer> prayers;

    public HomePrayerAdapter(Context context, int textViewResourceId,
        List<Prayer> items, boolean isLandFragment) {
        super(context, textViewResourceId, items);
        this.prayers = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.main_prayer_list, null);
        }

        Prayer prayer = prayers.get(position);

        if (prayer != null) {
            TextView title = (TextView) v.findViewById(R.id.prayer_title);
            title.setText(prayer.getTitle());
            TextView content = (TextView) v.findViewById(R.id.prayer_content);
            content.setText(prayer.getContent().length() > 50? prayer.getContent().substring(0, 50).replaceAll("\n", " ")+"..." : prayer.getContent().replaceAll("\n", " "));
            ImageView categoryIcon = (ImageView) v.findViewById(R.id.categoryIcon);
            switch(prayer.getType()){
	            case 0:
	            	categoryIcon.setImageResource(R.drawable.ic_list_doa);
	            	break;
	            case 1:
	            	categoryIcon.setImageResource(R.drawable.ic_list_renungan);
	            	break;
	            case 2:
	            	categoryIcon.setImageResource(R.drawable.ic_list_kisah);
	            	break;
	            case 3:
	            	categoryIcon.setImageResource(R.drawable.ic_list_quote);
	            	break;
	            case 4:
	            	categoryIcon.setImageResource(R.drawable.ic_list_lagu_rohani);
	            	break;
            }
        }

        return v;
    }

    public void setPrayers(List<Prayer> prayers) {
        this.prayers = prayers;
    }

}
