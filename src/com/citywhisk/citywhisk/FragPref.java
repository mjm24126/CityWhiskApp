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

import java.util.concurrent.ExecutionException;

import com.citywhisk.citywhisk.MainPage.GetDistance;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//Fragment for Pick menu
public class FragPref extends Fragment implements OnSeekBarChangeListener {

	//minimum swiping distance for List header, responsible for navigating between different ititnerary
	private static final int SWIPE_MIN_DISTANCE = 80;
	private static final int SWIPE_THRESHOLD_VELOCITY = 150;
	
	LinearLayout rl ;
	private MainPage parentActivity;
	
	private SeekBar budgetBar;
	private SeekBar distanceBar;
	private SeekBar ratingBar;
	private SeekBar timeBar;
	
	private TextView budgetLabel;
	private TextView distanceLabel;
	private TextView ratingLabel;
	private TextView timeLabel;
	
	private Button applyButton;
	private Button advancedButton;
	
	// integers to hold the linear value within the slider's constraints
	private int budgetBarVal;
	private int distanceBarVal;
	private int ratingBarVal;
	private int timeBarVal;
	
	// hold the calculated values based on slider's values
	private int budgetCalcVal;
	private double distanceCalcVal;
	private int ratingCalcVal;
	private int timeCalcVal;
	
	
	private View thisView;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceSate){
		
		parentActivity = (MainPage) getActivity();
		
		budgetBarVal = 50;
		timeBarVal = 3;
		distanceBarVal = 150;
		ratingBarVal = 3;
		
		budgetCalcVal = budgetBarVal;
		timeCalcVal = timeBarVal;
		distanceCalcVal = 20;
		ratingCalcVal = ratingBarVal + 1;
		
		thisView = inflater.inflate(R.layout.fragpref, container, false);
		
		budgetBar = (SeekBar) thisView.findViewById(R.id.budgetBar);
		budgetBar.setProgress(budgetBarVal);
		budgetBar.setOnSeekBarChangeListener(this);
		budgetLabel = (TextView) thisView.findViewById(R.id.budgetBarCurrent);
		if ( budgetBar != null ) {
			budgetLabel.setText("$" + budgetCalcVal);
		}
		
		distanceBar = (SeekBar) thisView.findViewById(R.id.distanceBar);
		distanceBar.setProgress(distanceBarVal);
		distanceBar.setOnSeekBarChangeListener(this);
		distanceLabel = (TextView) thisView.findViewById(R.id.distanceBarCurrent);
		if ( distanceBar != null ) {
			distanceLabel.setText(distanceCalcVal + " mi");
		}
		
		ratingBar = (SeekBar) thisView.findViewById(R.id.ratingBar);
		ratingBar.setProgress(ratingBarVal);
		ratingBar.setOnSeekBarChangeListener(this);
		ratingLabel = (TextView) thisView.findViewById(R.id.ratingBarCurrent);
		if ( ratingBar != null ) {
			ratingLabel.setText(ratingCalcVal + " stars");
		}
		
		timeBar = (SeekBar) thisView.findViewById(R.id.timeBar);
		timeBar.setProgress(timeBarVal);
		timeBar.setOnSeekBarChangeListener(this);
		timeLabel = (TextView) thisView.findViewById(R.id.timeBarCurrent);
		if ( timeBar != null ) {
			timeLabel.setText(timeCalcVal + " hrs");
		}
		
		applyButton = (Button) thisView.findViewById(R.id.applyPrefs);
		applyButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
				
				LinearLayout layout = new LinearLayout(parentActivity);
		        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		        layout.setOrientation(LinearLayout.VERTICAL);
		        layout.setLayoutParams(parms);
		        
		        TextView tv = new TextView(parentActivity);
		        tv.setText("Create new itinerary?");
		        tv.setTextColor(Color.parseColor("#646363"));
		        tv.setPadding(10, 25, 10, 25);
		        tv.setGravity(Gravity.CENTER);
		        tv.setTextSize(20);
		        LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		        layout.addView(tv,tv1Params);
				
				builder.setView(layout);
				builder.setPositiveButton(R.string.yes, null);
				builder.setNegativeButton(R.string.no, null);
				
				final AlertDialog ad = builder.create();
				ad.setOnShowListener(new OnShowListener() {

					@Override
					public void onShow(DialogInterface dia) {
						
						Button posBtn = ad.getButton(AlertDialog.BUTTON_POSITIVE);
							posBtn.setOnClickListener(new View.OnClickListener() {

					            @Override
					            public void onClick(View view) {
					            	parentActivity.setBudgetPref(budgetCalcVal);
					        		parentActivity.setDistancePref(distanceCalcVal);
					        		parentActivity.setTimePref(timeCalcVal * 60);  // turn hours to minutes
					        		parentActivity.setRatingPref(ratingCalcVal);
									parentActivity.new GetItinerary().execute();
									parentActivity.getActionBar().setSelectedNavigationItem(0);
									ad.dismiss();
					            }
					        });
				        
				        Button neg = ad.getButton(AlertDialog.BUTTON_NEGATIVE);
				        neg.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// revert preference progress bars
								ad.dismiss();
								
							}
						});
					}
					
				});
				ad.show();
			}
		});
		/*advancedButton = (Button) thisView.findViewById(R.id.advancedPrefs);
		advancedButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(thisView.getContext(), "Open Advanced", Toast.LENGTH_SHORT).show();
			}
		});*/
		
		return thisView;
	}
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Fragment#onStart()
	 * 
	 * 
	 * Set up the user preference in Shared preference.
	 * Can be passed to server to customize the XML results from server
	 */
	@Override
	public void onStart() {

		super.onStart();
		
		rl = (LinearLayout) getActivity().findViewById(R.id.prefPage);
		
		@SuppressWarnings("deprecation")
		final GestureDetector gdt = new GestureDetector(new GestureListener());
		rl.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				gdt.onTouchEvent(event);
				return true;
			}
		});
		
		
		
		//Button advancePref = (Button) getActivity().findViewById(R.id.advancedPrefs);
		//advancePref.setOnClickListener(new OnClickListener() {

			/*
			 * (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 * 
			 * Switch to advance preference screen shoot
			 */
			//@Override
			//public void onClick(View v) {
				// TODO Auto-generated method stub
				//rl.setBackgroundResource(R.drawable.adprefscreenshot);
				//Spinner spinner = new Spinner(getActivity());
				//ArrayAdapter<String> spinnerAA = new ArrayAdapter<String>()
				//getActivity().findViewById(R.id.)
			//}
		//});

	}
	
	
	@Override
	public void onProgressChanged(SeekBar v, int progress, boolean isUser) {
		if ( v == budgetBar ) {
			budgetBarVal = v.getProgress();
			if ( budgetBarVal <= 172 ) {
				budgetCalcVal = (int) Math.round(((double) budgetBarVal) * 1.1627906976744187 );
			}
			else if ( budgetBarVal <= 335  ) {
				budgetCalcVal = (int) Math.round((((double) budgetBarVal) * 4.9079754601226995 ) - 644.1717791411043 );
			}
			else {
				budgetCalcVal = (int) Math.round((((double) budgetBarVal) * 9.090909090909092 ) - 2045.4545454545455 );
			}
			budgetLabel.setText("$" + budgetCalcVal);
		}
		if ( v == distanceBar ) {
			distanceBarVal = v.getProgress();
			if ( distanceBarVal == 0 ) {
				distanceCalcVal = .25;
			}
			else if ( distanceBarVal <= 68 ) {
				distanceCalcVal = Math.round((((double) distanceBarVal) / 34)*4)/4f;
			}
			else if ( distanceBarVal <= 134  ) {
				distanceCalcVal = Math.round(((((double) distanceBarVal) * .12121212) - 6.242424 )*4)/4f;
			}
			else {
				distanceCalcVal = Math.round(((((double) distanceBarVal) * .6060606061) - 71.2121212 )*4)/4f;
			}
			distanceLabel.setText(distanceCalcVal + " mi");
		}
		if ( v == ratingBar ) {
			ratingBarVal = v.getProgress();
			ratingCalcVal = ratingBarVal + 1;
			ratingLabel.setText(ratingCalcVal + " stars");
		}
		if ( v == timeBar ) {
			timeBarVal = v.getProgress();
			timeCalcVal = timeBarVal;
			timeLabel.setText(timeCalcVal + " hrs");
		}
	}

	// Gesture Listener for list view Header
	private class GestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

				return false; // Right to left
			}  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

				return false; // Left to right
			}

			if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				return false; // Bottom to top
			}  else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				//rl.setBackgroundResource(R.drawable.preferencesscreenshot);
				return false; // Top to bottom
			}
			return false;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	} 
}
