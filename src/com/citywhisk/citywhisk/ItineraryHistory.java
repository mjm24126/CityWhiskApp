package com.citywhisk.citywhisk;

import java.util.ArrayList;

import android.util.Log;

public class ItineraryHistory {

	private ArrayList<Itinerary> itineraryList;
	private static final String TAG = "ITIN HISTORY";
	private int currentIndex;
	
	public ItineraryHistory(){
		itineraryList = new ArrayList<Itinerary>();
		setCurrentIndex(0);
	}

	public ArrayList<Itinerary> getItineraryList() {
		return itineraryList;
	}

	public void setItineraryList(ArrayList<Itinerary> itineraryList) {
		this.itineraryList = itineraryList;
	}
	
	public void printHistory() {
		////Log.i(TAG, "size is "+itineraryList.size());
		for( int i=0; i<itineraryList.size(); i++  ){
			////Log.i(TAG, "index " + i);
			for( Entity e : itineraryList.get(i).itin ){
				////Log.i(TAG,e.getName() + " - " + e.getLatitude() + ", " + e.getLongitude());
			}
		}
	}
	
	public Itinerary getPreviousItin(){
		if( getCurrentIndex() > 0 ) {
			////Log.i(TAG,"in > 0");
			Itinerary it = itineraryList.get(0);
			for( Entity e : itineraryList.get(0).itin ){
				////Log.i(TAG,e.getName() + " - " + e.getLatitude() + ", " + e.getLongitude());
			}
			////Log.i(TAG,(it == null)+"");
			currentIndex--;
			return it;
		}
		return null;
	}
	
	public Itinerary getNextItin() {
		if( getCurrentIndex() < itineraryList.size()-1 ){
			Itinerary it = itineraryList.get(getCurrentIndex()+1);
			setCurrentIndex(getCurrentIndex() + 1);
			return it;
		}
		return null;
	}
	
	public int addToHistory(Itinerary itin){
		itineraryList.add(itin);
		setCurrentIndex(itineraryList.size() - 1);
		return getCurrentIndex();
	}
	
	public int updateCurrentItin(Itinerary itin){
		itineraryList.set(getCurrentIndex(), itin);
		return getCurrentIndex();
	}
	
	public int getSize(){
		return itineraryList.size();
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}
	
	public boolean hasRightItin(){
		if( currentIndex < getSize() - 1 ){
			return true;
		}
		return false;
	}
}
