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

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailListAdapter extends BaseExpandableListAdapter  {

	Context con;
	Itinerary tours;
	int indexForDestination;
	Entity currentDestinationInTours;
	boolean isPromo;
	boolean hasTestimonials;
	
	public static final int DESC_GROUP = 0;
	public static final int PROMO_GROUP = 1;
	public static final int TEST_GROUP = 2;
	
	private static final int[] EMPTY_STATE_SET = {};
    private static final int[] GROUP_EXPANDED_STATE_SET =
            {android.R.attr.state_expanded};
    private static final int[][] GROUP_STATE_SETS = {
         EMPTY_STATE_SET, // 0
         GROUP_EXPANDED_STATE_SET // 1
	};
	

	public DetailListAdapter(Context con, Entity obj) {
		this.con = con;
		//this.tours = tours;
		//this.indexForDestination = indexForDestination;
		currentDestinationInTours = obj;
		isPromo = false;
		hasTestimonials = false;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		if( groupPosition == DESC_GROUP ){
			return currentDestinationInTours.getDescription();
		}
		else {
			return "Best day ever";
		}

	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = convertView;
		if (view == null) {
			LayoutInflater infalInflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = infalInflater.inflate(R.layout.expandlistviewchild, null);
		}
		TextView tv = (TextView) view.findViewById(R.id.childDescription);
		Typeface typeFace = Typeface.createFromAsset(con.getAssets(),
				"fonts/GOTHIC.TTF");
		//tv.setTypeface(typeFace);

		if( getGroupCount() == 2 ){
			if( groupPosition == DESC_GROUP ){
				tv.setText(currentDestinationInTours.getDescription());
			}
			else {
				tv.setText("No testimonials yet.");
			}
		}
		else {
			if( groupPosition == DESC_GROUP ){
				tv.setText(currentDestinationInTours.getDescription());
			}
			else if( groupPosition == PROMO_GROUP ){
				tv.setText("Promotion is: " + currentDestinationInTours );
			}
			else {
				tv.setText("Best thing ever!");

			}
		}
		

		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return currentDestinationInTours;
	}

	@Override
	public int getGroupCount() {
		if( currentDestinationInTours.getSavings() == 0 ){
			isPromo = false;
			return 1;
		}
		else {
			isPromo = true;
			return 1;
		}
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.expandablelistgrouplayout, null);
		}
		
		View ind = v.findViewById( R.id.explist_indicator);
		if( ind != null ) {
			ImageView indicator = (ImageView)ind;
			if( getChildrenCount( groupPosition ) == 0 ) {
				indicator.setVisibility( View.INVISIBLE );
			} else {
				indicator.setVisibility( View.VISIBLE );
				int stateSetIndex = ( isExpanded ? 0 : 1) ;
				Drawable drawable = indicator.getDrawable();
				drawable.setState(GROUP_STATE_SETS[stateSetIndex]);
			}
		}
		
	
		TextView groupTextView = (TextView) v.findViewById(R.id.groupDescription);
		//Typeface typeFace = Typeface.createFromAsset(con
			//	.getAssets(), "fonts/GOTHICBI.TTF");
		//groupTextView.setTypeface(typeFace);
		if( getGroupCount() == 1 ) {
			groupTextView.setText("Description");
		}
		if( getGroupCount() == 2 ) {
			if( groupPosition == DESC_GROUP )
			{
				groupTextView.setText("Description");
			}
			else {
				groupTextView.setText("Testimonials");
			}
		}
		else {
			if( groupPosition == DESC_GROUP)
			{
				groupTextView.setText("Description");
			}
			else if( groupPosition == PROMO_GROUP )
			{
				groupTextView.setText("Promotion");
			}
			else {
				groupTextView.setText("Testimonials");
			}
		}
		return v;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
