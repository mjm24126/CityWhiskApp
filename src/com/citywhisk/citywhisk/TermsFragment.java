package com.citywhisk.citywhisk;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TermsFragment extends Fragment{
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceSate){
		return inflater.inflate(R.layout.terms, container, false);
	}

}
