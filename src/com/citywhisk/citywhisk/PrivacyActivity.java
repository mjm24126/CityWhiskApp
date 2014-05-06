/*
 * Copyright 2014 CityWhisk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.citywhisk.citywhisk;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class PrivacyActivity extends Activity {
	
	private ActionBar actionBar;

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.privacyandterms);
		
        //creating ActionBar navigation Bar and setting up properties for it
		actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
    
		if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH ) {
			actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#646363")));
		}
		else {
			actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#646363")));
		}
		
		//Setting up the Tab bars 
		Tab tab = actionBar.newTab()
				.setText(R.string.privacy_tab_1)
				.setTabListener(new PrivTabListener());
		actionBar.addTab(tab);

		tab = actionBar.newTab()
				.setText(R.string.privacy_tab_2)
				.setTabListener(new PrivTabListener());
		actionBar.addTab(tab);
	}
	
	class PrivTabListener implements ActionBar.TabListener {
		
		PrivacyFragment pf;
		TermsFragment tf;
		
		/** Constructor used each time a new tab is created.
		 * activity  The host Activity, used to instantiate the fragment
		 * The identifier tag for the fragment
		 * The fragment's Class, used to instantiate the fragment
		 */
		public PrivTabListener() {
			pf = new PrivacyFragment();
			tf = new TermsFragment();
		}
		
		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			switch (tab.getPosition()) {
		    case 0:
		        ft.add(R.id.pandtcontainer, pf, null);
		        break;
		    case 1:
		        ft.add(R.id.pandtcontainer, tf, null);
		        break;
			}
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			switch (tab.getPosition()) {
		    case 0:
		        ft.remove(pf);
		        break;
		    case 1:
		        ft.remove(tf);
		        break;
		    }
		}

	}
}
