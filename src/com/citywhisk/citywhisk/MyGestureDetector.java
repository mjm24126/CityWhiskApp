package com.citywhisk.citywhisk;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public abstract class MyGestureDetector extends SimpleOnGestureListener {
	private static final int SWIPE_MIN_DISTANCE = 50;
	private static final int SWIPE_THRESHOLD_VELOCITY = 25;
	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	private Context con;
	View view;

	public MyGestureDetector(Context c, View v) {
		con = c;
		view = v;
	    gestureDetector = new GestureDetector(this.con, this, null);
	    gestureListener = new View.OnTouchListener() {
	        public boolean onTouch(View v, MotionEvent event) {
	            return gestureDetector.onTouchEvent(event);
	        }
	    };
	}

	public abstract void onRightToLeftSwipe();

	public abstract void onLeftToRightSwipe();

	public abstract void onTopToBottomSwipe();

	public abstract void onBottomToTopSwipe();

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velX,
	        float velY) {
	    DisplayMetrics dm = con.getResources().getDisplayMetrics();
	    int REL_SWIPE_MIN_DISTANCE = (int) (SWIPE_MIN_DISTANCE * dm.densityDpi / 160.0f);
	    int REL_SWIPE_THRESHOLD_VELOCITY = (int) (SWIPE_THRESHOLD_VELOCITY
	            * dm.densityDpi / 160.0f);
	    float deltaX = e1.getX() - e2.getX();
	    float deltaY = e1.getY() - e2.getY();
	    try {
	        // swipe horizontal?
	        if ((Math.abs(deltaX) > REL_SWIPE_MIN_DISTANCE)
	                && (Math.abs(velX) > REL_SWIPE_THRESHOLD_VELOCITY)
	                && (Math.abs(deltaX) > Math.abs(deltaY))) {
	            // left or right
	            if (deltaX < 0) {
	                this.onLeftToRightSwipe();
	                return true;
	            }
	            if (deltaX > 0) {
	                this.onRightToLeftSwipe();
	                return true;
	            }
	        } else if ((Math.abs(deltaY) > REL_SWIPE_MIN_DISTANCE)
	                && (Math.abs(velY) > REL_SWIPE_THRESHOLD_VELOCITY)) { // swipe
	        	return false;  // only listen for horizontal swipe
	            // vertical
	            // top or down
	            /*if (deltaY < 0) {
	                this.onTopToBottomSwipe();
	                return true;
	            }
	            if (deltaY > 0) {
	                this.onBottomToTopSwipe();
	                return true;
	            }*/
	        } else {
	            return false; // We don't consume the event
	        }

	    } catch (Exception e) {
	        // nothing
	    }
	    return false;
	}

	public View.OnTouchListener getGestureListener() {
	    return gestureListener;
	}

	public void setGestureListener(View.OnTouchListener gestureListener) {
	    this.gestureListener = gestureListener;
	}
}