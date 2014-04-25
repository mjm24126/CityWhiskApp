package com.citywhisk.citywhisk;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "itins.sqlite";
	private static final int VERSION = 1;
	
	private static final String TABLE_ITIN = "itinerary";
	private static final String COLUMN_ITIN_ID = "itin_id";
	private static final String COLUMN_ITIN_NAME = "itin_name";
	private static final String COLUMN_ITIN_DATE = "itin_date";
	private static final String COLUMN_ITIN_DEMOG = "itin_demog";
	
	private static final String TABLE_ENT = "entity";
	private static final String COLUMN_ENT_ID = "ent_id";
	private static final String COLUMN_ENT_SERVER_ID = "ent_server_id";
	private static final String COLUMN_ENT_NAME = "ent_name";
	private static final String COLUMN_ENT_ADDRESS = "ent_address";
	private static final String COLUMN_ENT_LAT = "ent_latitude";
	private static final String COLUMN_ENT_LON = "ent_longitude";
	private static final String COLUMN_ENT_PHONE = "ent_phone";
	private static final String COLUMN_ENT_RATING = "ent_rating";
	private static final String COLUMN_ENT_LOWP = "ent_low_price";
	private static final String COLUMN_ENT_HIGHP = "ent_high_price";
	private static final String COLUMN_ENT_DESC = "ent_description";
	private static final String COLUMN_ENT_CAT = "ent_category";
	private static final String COLUMN_ENT_TIME = "ent_time";
	private static final String COLUMN_ENT_LINK = "ent_link";
	private static final String COLUMN_ENT_IS_FAV = "ent_is_favorite";
	private static final String COLUMN_ENT_IS_PROMO = "ent_is_promo";
	
	private static final String TABLE_IMG = "image";
	private static final String COLUMN_IMG_ID = "img_id";
	private static final String COLUMN_IMG_ENT_ID = "img_ent_id";
	private static final String COLUMN_IMG_IMAGE = "img_image";
	
	private static final String TABLE_ITIN_STOPS = "itinerary_stops";
	private static final String COLUMN_ITIN_STOPS_ID = "is_id";
	private static final String COLUMN_ITIN_STOPS_ITIN_ID = "is_itin_id";
	private static final String COLUMN_ITIN_STOPS_ENT_ID = "is_ent_id";
	private static final String COLUMN_ITIN_STOPS_IS_FIRST = "is_is_first";
	private static final String COLUMN_ITIN_STOPS_IS_LAST = "is_is_last";
	private static final String COLUMN_ITIN_STOPS_STOP_NUM = "is_stop_number";
	
	public DatabaseHelper( Context context ){
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("PRAGMA foreign_keys=ON;");
		// create the itinerary table
		db.execSQL("create table itinerary (" +
				"itin_id integer primary key autoincrement," +
				"itin_name varchar(100)," +
				"itin_date integer," +
				"itin_demog integer," +
				"UNIQUE( itin_name ) ON CONFLICT REPLACE )");
		
		// create entity table
		db.execSQL("create table entity (" +
				"ent_id integer primary key autoincrement," +
				"ent_server_id integer," +        // entity server id to match the id on the server
				"ent_name varchar(100)," +
				"ent_address varchar(100)," +
				"ent_latitude real," +
				"ent_longitude real," +
				"ent_phone varchar(20)," +
				"ent_rating real," +
				"ent_low_price real," +
				"ent_high_price real," +
				"ent_description varchar(500)," +
				"ent_category varchar(30)," +
				"ent_time real," +
				"ent_link varchar(40)," +
				"ent_is_favorite integer," +  // use 0/1 for boolean value
				"ent_is_promo integer," +
				"UNIQUE( ent_server_id ) ON CONFLICT REPLACE )");  // use 0/1 for boolean value
		
		db.execSQL("create table image (" +
				"img_id integer primary key autoincrement," +
				"img_ent_id integer," +
				"img_image blob," +
				"FOREIGN KEY(img_ent_id) REFERENCES entity(ent_server_id) ON DELETE CASCADE)");
		
		db.execSQL("create table itinerary_stops (" +
				"is_id integer primary key autoincrement," +
				"is_itin_id integer references itinerary(itin_id)," +
				"is_ent_id integer references entity(ent_server_id)," +
				"is_is_first integer," +  // use 0/1 for boolean value
				"is_is_last integer," +   // use 0/1 for boolean value
				"is_stop_number integer," +
				"FOREIGN KEY(is_itin_id) REFERENCES itinerary(itin_id) ON DELETE CASCADE," +
				"FOREIGN KEY(is_ent_id) REFERENCES entity(ent_server_id) ON DELETE CASCADE)");
		
		/*db.execSQL("create table activity_search (" +
				"_id INTEGER PRIMARY KEY," +
				"activity TEXT" +
				")");
		insertActivities(db);*/
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}
	
	public long insertItinerary(Itinerary itin) {
		ContentValues cvItin = new ContentValues();
		
		// insert into itinerary table first 
		cvItin.put(COLUMN_ITIN_NAME, itin.getItinName());
		Date date = new Date();
		Timestamp time =  new Timestamp(date.getTime());
		cvItin.put(COLUMN_ITIN_DATE, time.toString());
		cvItin.put(COLUMN_ITIN_DEMOG, itin.getDemoID());
		long itinID = getWritableDatabase().insert(TABLE_ITIN, null, cvItin);
		
		// go through itinerary and handle each entity
		int counter = 0;
		for( Entity ent : itin.itin ) {
			int isFirst = 0; int isLast = 0;
			if( counter == 0 ) isFirst = 1;
			if( counter == itin.itin.size()-1 ) isLast = 1;
			ContentValues cvEnt = new ContentValues();
			
			ContentValues cvItinStops = new ContentValues();
			// if tour id is not in db yet add it to entities table
			if( counter!=0 && !getEntitiesInDB().contains( ent.getTourId() ) ){
				cvEnt.put(COLUMN_ENT_SERVER_ID, ent.getTourId());
				cvEnt.put(COLUMN_ENT_NAME, ent.getName());
				cvEnt.put(COLUMN_ENT_ADDRESS, ent.getAddress());
				cvEnt.put(COLUMN_ENT_LAT, ent.getLatitude());
				cvEnt.put(COLUMN_ENT_LON, ent.getLongitude());
				cvEnt.put(COLUMN_ENT_PHONE, ent.getPhoneNum());
				cvEnt.put(COLUMN_ENT_RATING, ent.getRating());
				cvEnt.put(COLUMN_ENT_LOWP, ent.getLowPrice());
				cvEnt.put(COLUMN_ENT_HIGHP, ent.getHighPrice());
				cvEnt.put(COLUMN_ENT_DESC, ent.getDescription());
				cvEnt.put(COLUMN_ENT_CAT, ent.getCatagory());
				cvEnt.put(COLUMN_ENT_TIME, ent.getTime());
				cvEnt.put(COLUMN_ENT_LINK, ent.getLink());
				cvEnt.put(COLUMN_ENT_IS_FAV, 0);
				
				// store images for the entity
				/*for( Uri uri : ent.getImages() ){
					ContentValues cvImg = new ContentValues();
					cvImg.put(COLUMN_IMG_ENT_ID, ent.getTourId());
					cvImg.put(COLUMN_IMG_IMAGE, uri.toString());
					getWritableDatabase().insert(TABLE_IMG, null, cvImg);
				}*/
				
				getWritableDatabase().insert(TABLE_ENT, null, cvEnt);
			}
			// add entity to tour stops regardless
			cvItinStops.put(COLUMN_ITIN_STOPS_ITIN_ID, itinID);
			cvItinStops.put(COLUMN_ITIN_STOPS_ENT_ID, ent.getTourId());
			cvItinStops.put(COLUMN_ITIN_STOPS_IS_FIRST, isFirst);
			cvItinStops.put(COLUMN_ITIN_STOPS_IS_LAST, isLast);
			cvItinStops.put(COLUMN_ITIN_STOPS_STOP_NUM, counter);
			
			
			// insert into itin stops
			SQLiteDatabase db = getWritableDatabase();
			db.insert(TABLE_ITIN_STOPS, null, cvItinStops);
			db.close();
			
			counter++;
		}
		return itinID;
	}
	
	private ArrayList<Integer> getEntitiesInDB() {
		Cursor entResults = getReadableDatabase().rawQuery("SELECT "+COLUMN_ENT_SERVER_ID+" FROM "+TABLE_ENT, null);
		ArrayList<Integer> entities = new ArrayList<Integer>();
		
		while( !entResults.isBeforeFirst() && !entResults.isAfterLast() ){
			entities.add(entResults.getInt(0));
			entResults.moveToNext();
		}
		return entities;
	}
	
	private void setFavoriteEntity( Entity ent ){
		ContentValues cvEnt = new ContentValues();
		// if the entity is not in db yet, add everything
		if( !getEntitiesInDB().contains( ent.getTourId() ) ) {
			cvEnt.put(COLUMN_ENT_SERVER_ID, ent.getTourId());
			cvEnt.put(COLUMN_ENT_NAME, ent.getName());
			cvEnt.put(COLUMN_ENT_ADDRESS, ent.getAddress());
			cvEnt.put(COLUMN_ENT_LAT, ent.getLatitude());
			cvEnt.put(COLUMN_ENT_LON, ent.getLongitude());
			cvEnt.put(COLUMN_ENT_PHONE, ent.getPhoneNum());
			cvEnt.put(COLUMN_ENT_RATING, ent.getRating());
			cvEnt.put(COLUMN_ENT_LOWP, ent.getLowPrice());
			cvEnt.put(COLUMN_ENT_HIGHP, ent.getHighPrice());
			cvEnt.put(COLUMN_ENT_DESC, ent.getDescription());
			cvEnt.put(COLUMN_ENT_CAT, ent.getCatagory());
			cvEnt.put(COLUMN_ENT_TIME, ent.getTime());
			cvEnt.put(COLUMN_ENT_LINK, ent.getLink());
			cvEnt.put(COLUMN_ENT_IS_FAV, 1);
			// store images for the entity
			/*for( Uri uri : ent.getImages() ){
				ContentValues cvImg = new ContentValues();
				cvImg.put(COLUMN_IMG_ENT_ID, ent.getTourId());
				cvImg.put(COLUMN_IMG_IMAGE, uri.toString());
				getWritableDatabase().insert(TABLE_IMG, null, cvImg);
			}*/
			
			getWritableDatabase().insert(TABLE_ENT, null, cvEnt);
		}
		// if it is, just change it to a favorite
		else{
			cvEnt.put(COLUMN_ENT_IS_FAV, 1);
			getWritableDatabase().update(TABLE_ENT, cvEnt, COLUMN_ENT_SERVER_ID+" = "+ent.getTourId(),null);
		}
	}
	
	private ArrayList<Entity> getFavoriteEntities() {
		ArrayList<Entity> favs = new ArrayList<Entity>();
		
		return favs;
	}

	public ArrayList<String> getSavedItineraries() {
		Cursor itinCursor = getReadableDatabase().rawQuery("SELECT * FROM itinerary",null);
		ArrayList<String> saved = new ArrayList<String>();
		
		// check if empty 
		if( itinCursor.getCount() == 0 ){
			//Log.i("dbhelper","curser empty");
			return saved;
		}
		itinCursor.moveToFirst();
		for( int i=0; i<itinCursor.getCount(); i++ ){
			if( itinCursor != null && !itinCursor.isAfterLast()){
				saved.add(itinCursor.getString(1));
				itinCursor.moveToNext();
			}
		}
		return saved;
	}

	public Itinerary getSavedItinerary(String name) {
		String[] arg = new String[1];
		arg[0] = name;
		SQLiteDatabase db = getReadableDatabase();
		Cursor itinCursor = db.rawQuery("SELECT * from itinerary_stops, entity, itinerary " +
														   "WHERE itinerary.itin_id = itinerary_stops.is_itin_id " +
														   "AND itinerary_stops.is_ent_id = entity.ent_server_id " +
														   "AND itinerary.itin_name = ? " +
														   "ORDER BY itinerary_stops.is_stop_number", arg);
		// check if empty 
		if( itinCursor.getCount() == 0 ){
			//Log.i("dbhelper","curser empty");
			return new Itinerary();
		}
		
		// create empty itinerary to add each object to
		Itinerary i = new Itinerary();
		i.itin.clear();
		i.addStart();
		i.setItinName(arg[0]);
		itinCursor.moveToFirst();
		if( itinCursor != null && !itinCursor.isAfterLast()){
			//Log.i("dbhelper",itinCursor.getInt(1)+"");
			i.setItinID(itinCursor.getInt(1));
		}
		try{
			for( int x=0; x<itinCursor.getCount(); x++ ){
				if( itinCursor != null && !itinCursor.isAfterLast()){ 
					Entity e = new Entity();
					e.setTourId(itinCursor.getInt(7));
					e.setName(itinCursor.getString(8));
					e.setAddress(itinCursor.getString(9));
					e.setLatitude(itinCursor.getFloat(10)+"");
					e.setLongitude(itinCursor.getFloat(11)+"");
					e.setPhoneNum(itinCursor.getString(12));
					e.setRating(itinCursor.getFloat(13));
					e.setLowPrice(itinCursor.getFloat(14));
					e.setHighPrice(itinCursor.getFloat(15));
					e.setDescription(itinCursor.getString(16));
					e.setCatagory(itinCursor.getString(17));
					e.setTime(itinCursor.getFloat(18));
					e.setLink(itinCursor.getString(19));
					if( itinCursor.getInt(21) == 1 ) e.setPromo(true); else e.setPromo(false);
					
					i.itin.add(e);
					
					itinCursor.moveToNext();
				}
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}
		itinCursor.close();
		db.close();
		return i;
	}

	public void removeSavedItin(String name) {
		String arg[] = new String[1];
		arg[0] = name;
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("DELETE FROM itinerary WHERE itin_name = ?", arg);
		db.close();
	}
	
	public void insertActivities(SQLiteDatabase db){
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Amateur Sports Teams')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Amusement Parks')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Aquariums')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Archery')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Badminton')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Beaches')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Bike Rentals')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Boating')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Bowling')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Climbing')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Disc Golf')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Diving')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Fishing')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Fitness & Instruction')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Go Karts')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Golf')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Gun/Rifle Ranges')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Gymnastics')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Hang Gliding')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Hiking')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Horse Racing')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Horseback Riding')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Hot Air Ballonons')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Kiteboarding')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Lakes')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Laser Tag')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Leisure Centers')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Mini Golf')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Mountain Biking')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Paddleboarding')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Paintball')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Parks')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Playgrounds')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Rafting/Kayaking')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Recreation Centers')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Rock Climbing')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Skating Rinks')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Skydiving')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Soccer')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Spin Classes')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Sports Clubs')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Squash')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Summer Camps')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Surfing')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Swimming Pools')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Tennis')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Trampoline Parks')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Tubing')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Zoos')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Arcades')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Art Galleries')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Botanical Gardens')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Casinos')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Cinema')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Cultural Centers')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Festivals')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Jazz & Blues')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Museums')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Music Venues')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Opera & Ballet')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Performing Arts')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Professional Sports Teams')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Psychics & Astrologers')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Race Tracks')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Social Clubs')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Stadiums & Arenas')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Ticket Sales')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Wineries')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Barbers')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Cosmetic & Beauty Supplies')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Day Spas')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Eyelash Service')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Hair Extensions')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Hair Removal')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Hair Salons')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Makeup Artists')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Massage')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Medical Spas')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Nail Salons')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Permanent Makeup')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Piercings')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Rolfing')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Skin Care')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Tanning')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Tattoos')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Bagels')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Bakeries')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Beer, Wine, & Spirits')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Breweries')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Bubble Tea')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Coffee & Tea')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Convenience Stores')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Desserts')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Do-It-Yourself Food')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Donuts')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Farmers Market')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Food Trucks')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Gelato')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Grocery')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Ice Cream & Frozen Yogurt')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Internet Cafes')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Juice Bars & Smoothies')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Pretzels')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Shaved Ice')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Specialty Food')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Street Vendors')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Tea Rooms')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Wineries')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Airports')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Bed & Breakfast')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Campgrounds')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Car Rental')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Guest Houses')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Hostels')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Hotels')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Motorcycle Parks')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('RV Parks')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('RV Rental')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Resorts')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Ski Resorts')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Tours')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Train Stations')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Transportation')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Vacation Rentals')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Adult Entertainment')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Bars')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Comedy Clubs')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Country Dance Halls')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Dance Clubs')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Jazz & Blues')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Karaoke')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Music Venues')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Piano Bars')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Pool Halls')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Animal Shelter')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Horse Boarding')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Pet Services')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Pet Stores')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Veterinarians')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Court Houses')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Embassy')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Fire Departments')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Landmarks & Historical Buildings')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Libraries')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Police Departments')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Post Offices')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Buddhist Temples')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Churches')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Hindu Temples')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Mosques')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Synagogues')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('African')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('American')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Arabian')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Argentine')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Armenian')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Asian Fusion')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Australian')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Austrian')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Bangladeshi')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Barbeque')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Basque')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Belgian')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Brasseries')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Brazilian')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Breakfast & Brunch')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('British')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Buffets')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Burgers')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Burmese')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Cafes')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Cafeterias')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Cajun/Creole')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Cambodian')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Carribean')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Catalan')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Cheesesteaks')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Chicken Wings')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Chinese')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Comfort Food')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Creperies')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Cuban')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Czech/Slovakian')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Delis')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Diners')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Ethiopian')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Fast Food')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Filipino')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Fish & Chips')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Fondue')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Food Court')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Food Stands')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('French')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Gastropubs')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('German')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Gluten-free')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Greek')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Halal')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Hawaiian')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Himalayan/Nepalese')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Hot Dogs')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Hot Pot')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Hungarian')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Iberian')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Indian')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Indonesian')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Irish')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Italian')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Japanese')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Korean')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Kosher')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Laotian')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Latin American')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Live/Raw Food')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Malaysian')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Mediterranean')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Mexicna')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Middle Eastern')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Modern European')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Mongolian')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Moroccan')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Pakistani')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Persian/Iranian')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Peruvian')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Pizza')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Polish')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Portuguese')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Russian')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Salad')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Sandwiches')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Scandinavians')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Scottish')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Seafood')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Singaporean')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Soul Food')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Soup')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Southern')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Spanish')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Steakhouses')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Sushi Bars')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Taiwanese')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Tapas Bars')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Tapas/Small Plates')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Tex-Mex')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Thai')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Turkish')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Ukranian')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Vegan')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Vegitarian')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Vietnamese')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Adult')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Antiques')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Art Museums')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Arts & Crafts')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Auction Houses')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Baby Gear & Furniture')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Bespoke Clothing')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Books, Mags, Music & Video')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Bridal')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Computers')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Cosmetics & Beauty Supply')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Department Stores')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Discount Stores')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Drugstores')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Electronics')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Eyewear & Opticians')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Fashion')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Fireworks')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Flea Markets')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Flowers & Gifts')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Golf Equipment Shops')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Guns & Ammo')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Hobby Shops')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Home & Garden')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Jewelry')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Knitting Supplies')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Luggage')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Medical Supplies')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Mobile Phones')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Motorcycle Gear')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Musical Instruments & Teachers')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Office Equipment')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Outlet Stores')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Pawn Shops')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Personal Shopping')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Photography Stores & Services')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Pop-up Shops')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Shopping Centers')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Sporting Goods')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Thrift Stores')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Tobacco Shops')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Toy Stores')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Trophy Shops')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Uniforms')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Watches')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Wholesale Stores')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Wigs')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Card and Stationary')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Gift Shops')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Childrens Clothing')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Mens Clothing')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Womens Clothing')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Accessories')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Shoe Stores')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Leather Goods')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Yoga')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Pubs')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Gyms')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Food Market')");
		db.execSQL("INSERT INTO activity_search(ACTIVITY) VALUES('Music & DVDs')");
	}

}
