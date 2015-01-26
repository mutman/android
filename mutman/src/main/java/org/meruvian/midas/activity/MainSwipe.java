/*
 * Copyright 2012 Meruvian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.meruvian.midas.activity;

import java.util.ArrayList;
import java.util.List;

import org.meruvian.midas.core.defaults.DefaultActivity;
import org.mutiaraiman.droid.R;
import org.meruvian.midas.activity.page.AsyncTaskPage;
import org.meruvian.midas.activity.page.ButtonPage;
import org.meruvian.midas.ui.FlingLayout;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


/**
 * @author "Dias Nurul Arifin"
 * 
 */
public class MainSwipe extends DefaultActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_swipe);

		FlingLayout layout = (FlingLayout) findViewById(R.id.awesomepager);
		List<View> views = new ArrayList<View>();
		views.add(new ButtonPage(this));
		views.add(new AsyncTaskPage(this));
		layout.addViewList(views);
	}

    @Override
    public int theme() {
        return org.meruvian.midas.core.R.style.DarkTheme_Mutman;
    }

    // @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// MenuInflater menuInflater = getMenuInflater();
	// menuInflater.inflate(R.menu.main, menu);
	// return super.onCreateOptionsMenu(menu);
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// if (item.getItemId() == android.R.id.home) {
	// Toast.makeText(this, "Tapped home", Toast.LENGTH_SHORT).show();
	// } else if (item.getItemId() == R.id.menu_refresh) {
	// Toast.makeText(this, "Fake refreshing...", Toast.LENGTH_SHORT)
	// .show();
	// getActionBarHelper().setRefreshActionItemState(true);
	// getWindow().getDecorView().postDelayed(new Runnable() {
	// @Override
	// public void run() {
	// getActionBarHelper().setRefreshActionItemState(false);
	// }
	// }, 1000);
	// } else if (item.getItemId() == R.id.menu_search) {
	// Toast.makeText(this, "Tapped search", Toast.LENGTH_SHORT).show();
	// } else if (item.getItemId() == R.id.menu_invite) {
	// Toast.makeText(this, "Tapped share", Toast.LENGTH_SHORT).show();
	// }
	// return super.onOptionsItemSelected(item);
	// }
}
