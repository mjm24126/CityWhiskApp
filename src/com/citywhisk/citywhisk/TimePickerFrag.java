package com.citywhisk.citywhisk;

import java.util.Calendar;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

public class TimePickerFrag extends DialogFragment implements OnTimeSetListener {
	
	int hour;
	int minute;
	boolean useDefaultStartTime;

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		
		useDefaultStartTime = ((MainPage) getActivity()).isUseDefaultStartTime();
		
        // Use the current time as the default values for the picker if time has not been set yet
        if ( useDefaultStartTime ) {
			final Calendar c = Calendar.getInstance();
	        hour = c.get(Calendar.HOUR_OF_DAY);
	        minute = c.get(Calendar.MINUTE);
        }
        else {
        	hour = ((MainPage) getActivity()).getItinStartHour();
    		minute = ((MainPage) getActivity()).getItinStartMin();
        }

        // Create a new instance of TimePickerDialog and return it
        TimePickerDialog timepicker = new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
        timepicker.setTitle("Set start time");
        
        return timepicker;
    }

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// TODO Auto-generated method stub
		((MainPage) getActivity()).setItinStartHour( hourOfDay );
		((MainPage) getActivity()).setItinStartMin(minute );
		////Log.i("itin start time: ", hourOfDay + ":" + minute);
	}
	
}
