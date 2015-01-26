/*
 * Copyright 2013 The Android Open Source Project
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

package org.meruvian.midas.core.drawer;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.meruvian.midas.core.R;
import org.meruvian.midas.core.defaults.DefaultActivity;


public abstract class NavigationDrawer extends DefaultActivity {
    private DrawerLayout drawerLayout;
    private RelativeLayout drawer;
    private ListView mainMenu;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationDrawerAdapter navigationAdapter;
    private RelativeLayout preference;

    private CharSequence drawerTitle, title;

    public abstract Fragment mainFragment();
    public abstract void navigationAdapter(NavigationDrawerAdapter adapter);
    public abstract void selectedItem(int position);
    public abstract void onClickPreference();
    public abstract int iconHome();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setIcon(iconHome());
        setContentView(R.layout.activity_main);

        title = drawerTitle = getTitle();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer = (RelativeLayout) findViewById(R.id.left_drawer);
        mainMenu = (ListView) findViewById(R.id.main_menu);
        preference = (RelativeLayout) findViewById(R.id.preference);
        navigationAdapter = new NavigationDrawerAdapter(this);
        navigationAdapter(navigationAdapter);

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mainMenu.setAdapter(navigationAdapter);
        mainMenu.setOnItemClickListener(new DrawerItemClickListener());

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.drawable.ic_drawer_white,
                R.string.drawer_open,
                R.string.drawer_close
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(title);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mainFragment()).commit();

        preference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPreference();
            }
        });

        onViewCreated();
    }

    public abstract void onViewCreated();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
            
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
        getActionBar().setTitle(title);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public void closeDrawer() {
    	drawerLayout.closeDrawer(drawer);
    }
    
    public void openDrawer() {
    	drawerLayout.openDrawer(drawer);
    }
    
    public void replaceFragment(Fragment fragment, String tag) {
    	FragmentManager fragmentManager = getSupportFragmentManager();
    	FragmentTransaction transaction = fragmentManager.beginTransaction();
    	
    	transaction.replace(R.id.content_frame, fragment, tag).addToBackStack(null).commit();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener{

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectedItem(position);
		}
    };
}