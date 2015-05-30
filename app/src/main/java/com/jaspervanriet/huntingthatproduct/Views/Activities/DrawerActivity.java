/*
 * Copyright (C) 2015 Jasper van Riet
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspervanriet.huntingthatproduct.Views.Activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.jaspervanriet.huntingthatproduct.Data.Settings.AppSettings;
import com.jaspervanriet.huntingthatproduct.R;
import com.jaspervanriet.huntingthatproduct.Views.Activities.Settings.SettingsActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Activity that handles common tasks for activities in the drawer
 */
public class DrawerActivity extends AppCompatActivity {

	public static final int NAVDRAWER_ITEM_TODAYS_PRODUCTS = R.id.navigation_item_main;
	public static final int NAVDRAWER_ITEM_COLLECTIONS = R.id.navigation_item_collections;
	public static final int NAVDRAWER_ITEM_SETTINGS = R.id.navigation_item_prefs;
	public static final int NAVDRAWER_ITEM_ABOUT = R.id.navigation_item_about;
	public static final int NAVDRAWER_ITEM_INVALID = -1;

	private static final int NAVDRAWER_LAUNCH_DELAY = 250;

	private ActionBarDrawerToggle mDrawerToggle;
	private Handler mHandler;

	@InjectView (R.id.drawer_layout)
	DrawerLayout mDrawer;
	@InjectView (R.id.toolbar)
	Toolbar mToolBar;
	@InjectView (R.id.nav_drawer)
	NavigationView mDrawerView;

	@Override
	public void setContentView (int layoutResID) {
		super.setContentView (layoutResID);
		ButterKnife.inject (this);
		setToolBar ();
		setupDrawer ();
		mHandler = new Handler ();
	}

	@Override
	public void onDestroy () {
		PicassoTools.clearCache (Picasso.with (this));
		super.onDestroy ();
	}

	private void setupDrawer () {
		setupDrawerList ();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			mDrawer.setElevation (10);
		}
		mDrawerToggle = new ActionBarDrawerToggle (this, mDrawer,
				mToolBar, R.string.open, R.string.close);
		mDrawerToggle.setDrawerIndicatorEnabled (true);
		mDrawer.setDrawerListener (mDrawerToggle);
	}

	private void setupDrawerList () {
		mDrawerView.setNavigationItemSelectedListener (menuItem -> {
			onDrawerItemClicked (menuItem);
			return true;
		});
	}

	private void onDrawerItemClicked (MenuItem item) {
		if (!(item.getItemId () == getSelfNavDrawerItem ())) {
			// Wait for drawer to close before starting activity
			mHandler.postDelayed (() -> goToNavDrawerItem (item), NAVDRAWER_LAUNCH_DELAY);
		}
		closeNavDrawer ();
	}

	public Toolbar getToolbar () {
		return mToolBar;
	}

	private void goToNavDrawerItem (MenuItem item) {
		Intent intent;
		switch (item.getItemId ()) {
			case NAVDRAWER_ITEM_TODAYS_PRODUCTS:
				intent = new Intent (this, MainActivity.class);
				intent.putExtra ("toolbar_animation", false);
				startActivity (intent);
				overridePendingTransition (0, 0);
				finish ();
				break;
			case NAVDRAWER_ITEM_COLLECTIONS:
				intent = new Intent (this, CollectionsListActivity.class);
				startActivity (intent);
				overridePendingTransition (0, 0);
				finish ();
				break;
			case NAVDRAWER_ITEM_SETTINGS:
				intent = new Intent (this, SettingsActivity.class);
				startActivity (intent);
				overridePendingTransition (0, 0);
				finish ();
				break;
			case NAVDRAWER_ITEM_ABOUT:
				intent = new Intent (this, AboutActivity.class);
				startActivity (intent);
				overridePendingTransition (0, 0);
				finish ();
				break;
		}
	}

	protected void closeNavDrawer () {
		if (mDrawer != null) {
			mDrawer.closeDrawer (GravityCompat.START);
		}
	}

	protected int getSelfNavDrawerItem () {
		return NAVDRAWER_ITEM_INVALID;
	}

	protected void setToolBar () {
		String[] drawerData = {
				getString (R.string.drawer_main),
				getString (R.string.drawer_collections),
				getString (R.string.drawer_prefs),
				getString (R.string.drawer_about)
		};
		setSupportActionBar (mToolBar);
		ActionBar actionBar = getSupportActionBar ();
		actionBar.setElevation (5);
		switch (getSelfNavDrawerItem ()) {
			case NAVDRAWER_ITEM_TODAYS_PRODUCTS:
				actionBar.setTitle ("Today\'s " + drawerData[0]);
				break;
			case NAVDRAWER_ITEM_COLLECTIONS:
				actionBar.setTitle ("Featured " + drawerData[1]);
				break;
			case NAVDRAWER_ITEM_SETTINGS:
				actionBar.setTitle (drawerData[2]);
				break;
			case NAVDRAWER_ITEM_ABOUT:
				actionBar.setTitle (drawerData[3]);
				break;
		}
	}

	protected boolean sendCrashData () {
		return AppSettings.getCrashDataPref (this);
	}

	protected void setActionBarTitle (String title) {
		ActionBar actionBar = getSupportActionBar ();
		actionBar.setTitle (title);
	}

	protected LinearLayoutManager getLayoutManager () {
		LinearLayoutManager layoutManager = new LinearLayoutManager (this);
		layoutManager.setOrientation (LinearLayoutManager.VERTICAL);
		return layoutManager;
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		return mDrawerToggle.onOptionsItemSelected (item) || super.onOptionsItemSelected (item);
	}

	@Override
	protected void onPostCreate (Bundle savedInstanceState) {
		super.onPostCreate (savedInstanceState);
		mDrawerToggle.syncState ();
	}

	@Override
	public void onConfigurationChanged (Configuration newConfig) {
		super.onConfigurationChanged (newConfig);
		mDrawerToggle.onConfigurationChanged (newConfig);
	}
}