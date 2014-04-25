package com.citywhisk.citywhisk;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

import com.citywhisk.citywhisk.MainPage.GetDistance;
import com.google.android.gms.internal.r;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.BadTokenException;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailClass extends BaseActionBar {

	//minimum swiping distance for List header, responsible for navigating between different itinerary
	private static final int SWIPE_MIN_DISTANCE = 50;
	private static final int SWIPE_THRESHOLD_VELOCITY = 100;

	private int tourIndex;

	private SharedPreferences settings;

	private ExpandableListView listViewObj;
	private DetailListAdapter listAdaptorObj;

	//holds an entity when view details has been selected from search list
	private Entity detailEntity;
	
	//boolean to determine if details page coming from itinerary item or search page
	private boolean cameFromItinerary;
	
	//image handler
	ImageView image;
	
	//tracks the image being displayed
	int imageIndex = 0;
	
	//OnSearchSelectedListener mListener;
	
	// layout to set background of 
	RelativeLayout rl;
	
	private LruCache<String, Bitmap> mMemoryCache;
	BitmapDrawable d;
	
//	public interface OnAddFromDetailsListener {
//		public void onAddFromDetails( Entity obj );
//	}
//
//	public interface OnSearchSelectedListener {
//		public void onSearchSelected( Entity obj );
//		public void onViewDetailsSelected( Entity obj );
//	}
	
    @SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu; this adds items to the action bar if it is present.
	    getMenuInflater().inflate(R.menu.detailsmenu, menu);
	    
	    // Locate MenuItem with ShareActionProvider
	    /*MenuItem item = menu.findItem(R.id.menu_item_share);
	    // Fetch and store ShareActionProvider
	    shareActionProvider = (ShareActionProvider) item.getActionProvider();
	    shareActionProvider.setShareIntent(createShareIntent());*/
	    
	    int positionOfMenuItem = 0; 
        MenuItem item = menu.getItem(positionOfMenuItem);
        SpannableString s = new SpannableString("Privacy and Terms");
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
        item.setTitle(s);
	    
	    return true;
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 * Based on button clicked on ActionBar menu, action is performed
	 */
	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
			case R.id.actionbar8:
				Intent i = new Intent(getBaseContext(), PrivacyActivity.class);
		        startActivity(i);
				//Log.i("onOptionsItemSelected","finished");
				return true;
		
			default:
				//Log.i("onOptionsItemSelected","finished");
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detaillay);
		Intent intent = getIntent();
		int index = intent.getIntExtra("detailIndex", 0);
		
		settings =  getSharedPreferences("CITYWHISK", 0);
		
		tourIndex = index;
		
		// check if an item from search list has been chosen so we know whether we need add to itinerary button
		if ( searchEntity != null ) {
			detailEntity = searchEntity;
			//Log.i("detail entity",searchEntity.toString());
			// reset back to null
			searchEntity = null;
			// set boolean to false
			cameFromItinerary = false;
		}
		else {
			
		 	detailEntity = currentItin.itin.get( index );
		 	//Log.i("tour index "+index,detailEntity.toString());
		 	// set boolean to true
		 	cameFromItinerary = true;
		}
		
		// setting up the bitmap memory cache
		// Get max available VM memory, exceeding this amount will throw an
	    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
	    // int in its constructor.
	    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

	    // Use 1/8th of the available memory for this memory cache.
	    final int cacheSize = maxMemory / 8;
	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
			@Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            // The cache size will be measured in kilobytes rather than number of items.
	            return bitmap.getByteCount() / 1024;
	        }
	    };

		ActionBar actionBar = getActionBar();

		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);

		rl = (RelativeLayout) findViewById(R.id.detailImageContainer);
		loadBitmap(detailEntity.getTourId());
		currentItin.print("detials");
		loadItinBitmaps(currentItin);
	}


	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		generateView();

	}

	public void generateView() {

		TextView listHeader = (TextView) findViewById(R.id.detailName);
		
		
		ImageView rightArrow = (ImageView) findViewById(R.id.rightArrow);
		ImageView leftArrow = (ImageView) findViewById(R.id.leftArrow);
		// set arrows to move through tours if coming from itinerary
		if ( cameFromItinerary ) {
			
			
			//Right Arrow
			if(tourIndex == (currentItin.itin.size()-1)){
				rightArrow.setVisibility(View.GONE);
			}
			else{
				rightArrow.setVisibility(View.VISIBLE);
				rightArrow.setImageResource(R.drawable.imgarrow_right_white);
			}
	
			rightArrow.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(tourIndex == 0){
						tourIndex += 2;
						detailEntity = currentItin.itin.get(tourIndex);
						loadBitmap(detailEntity.getTourId());
						generateView();
					}
					else if(tourIndex < (currentItin.itin.size()-1)){
						tourIndex++;
						detailEntity = currentItin.itin.get(tourIndex);
						loadBitmap(detailEntity.getTourId());
						generateView();
					}
					else{
						Toast toast = Toast.makeText(DetailClass.this, "End of list", Toast.LENGTH_SHORT);
						toast.show();
					}
	
				}
			});
	
			//Left Arrow
			if(tourIndex <= 1){
				leftArrow.setVisibility(View.INVISIBLE);
			}
			else{
				leftArrow.setVisibility(View.VISIBLE);
				leftArrow.setImageResource(R.drawable.imgarrow_left_white);
			}
	
			leftArrow.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(tourIndex == 1){
						Toast toast = Toast.makeText(DetailClass.this, "At start of list", Toast.LENGTH_SHORT);
						toast.show();
					}
					else if(tourIndex>1)
					{
						tourIndex--;
						detailEntity = currentItin.itin.get(tourIndex);
						loadBitmap(detailEntity.getTourId());
						generateView();
					}
				}
			});
		}
		// came from search list
		else {
			rightArrow.setVisibility(View.GONE);
			leftArrow.setVisibility(View.GONE);
			
			Intent i = getIntent();
			boolean inItin = i.getExtras().getBoolean("isInItin");
			
			if( !inItin ){
				ImageView addBtn = (ImageView) findViewById(R.id.detailPlusImage);
				addBtn.setVisibility(View.VISIBLE);
			
				addBtn.setOnClickListener( new OnClickListener() {

					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						currentItin.itin.add(1, detailEntity);
						cl.notifyDataSetChanged();
						setResult(0, new Intent());
						finish();
					}
					
				});
			}
		}
		

		//Typeface typeFaceBold = Typeface.createFromAsset(getApplicationContext().getAssets(),
				//"fonts/GOTHICB.TTF");
		//listHeader.setTypeface(typeFaceBold);
		listHeader.setText(detailEntity.getName());
		
		
		//Typeface typeFaceReg = Typeface.createFromAsset(getApplicationContext().getAssets(),
			//	"fonts/GOTHIC.TTF");
		ImageView detailAddress = (ImageView) findViewById(R.id.detailLocationImages);
		detailAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/maps?" + "saddr="+ settings.getString("user_latitude", "") + "," + settings.getString("user_longitude", "") + "&daddr=" + detailEntity.getLatitude() + "," + detailEntity.getLongitude()));
				intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
				startActivity(intent);

			}
		});

		listViewObj = (ExpandableListView) findViewById(R.id.detailListView);
		listAdaptorObj = new DetailListAdapter(DetailClass.this, detailEntity);
		listViewObj.setAdapter(listAdaptorObj);
		listViewObj.expandGroup(0);
		listViewObj.setDivider(null);

		ImageView detailPhone = (ImageView) findViewById(R.id.detailPhoneImage);
		detailPhone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String url = "tel:"+detailEntity.getPhoneNum();
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse(url));
				startActivity(callIntent);
			}
		});
		
		/*
		 * UNCOMMENT FOR PROMOTION ROW ----  promotion
		 */
		
		/*TextView promotion = (TextView) findViewById(R.id.promotion);
		promotion.setTypeface(typeFace);
		ImageView detailSaleImage = (ImageView) findViewById(R.id.detailSaleImage);
		if( detailEntity.getPromo() ){
			promotion.setText("Sale : $ "+detailEntity.getSavings());
			detailSaleImage.setImageResource(R.drawable.salestag_green);
			
			promotion.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
				}
			});
			
		}
		else{
			promotion.setText("");
			detailSaleImage.setImageDrawable(null);
		}*/
		

		RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		ratingBar.setRating((float)detailEntity.getRating());

		TextView ratingText = (TextView) findViewById(R.id.ratingText);
		ratingText.setText((detailEntity.getRating()+"").substring(0, 3));
		
		TextView activityText = (TextView) findViewById(R.id.activityText);
		if( detailEntity.getActivities() != null ){
			String detailEntActs = detailEntity.getActivities().replace(" | ", ", ");
			activityText.setText(detailEntActs+"\n");
		}
		activityText.bringToFront();

	}
	
	public static ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
                dialog.show();
        } catch (BadTokenException e) {

        }
        dialog.setCancelable(false);
        dialog.setIndeterminate(false);
        dialog.setContentView(R.layout.progressdialog);
        // dialog.setMessage(Message);
        return dialog;
	}
	
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
	    if (getBitmapFromMemCache(key) == null) {
	        mMemoryCache.put(key, bitmap);
	    }
	}

	public Bitmap getBitmapFromMemCache(String key) {
	    return mMemoryCache.get(key);
	}
	
	public void loadBitmap(int resId) {
	    final Bitmap bitmap = getBitmapFromMemCache(String.valueOf(resId));
	    d = new BitmapDrawable(getResources(), bitmap);
		d.setAlpha(190);
	    if (bitmap != null) {
	        rl.setBackgroundDrawable(d);
	    } else {
	    	rl.setBackgroundDrawable(null);
	        new GetThisBitmap().execute(detailEntity.getImages());
	    }
	}
	
	public void loadItinBitmaps(Itinerary it) {
		for( int i=1; i<currentItin.itin.size(); i++ ){
			if( getBitmapFromMemCache(String.valueOf(currentItin.itin.get(i).getTourId())) == null ){
				new GetBitmaps().execute(currentItin.itin.get(i));
			}
		}
	}

	protected class GetThisBitmap extends AsyncTask<String, String, String> {
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
    	int width = dm.widthPixels;
    	int height = dm.heightPixels;
    	//set max image size for bitmpas based on 150dp height in detail view * screen width
    	int maxImgSize = width * 150;
    	Bitmap bit;
    	ProgressDialog pDialog;
    	
    	@Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pDialog == null) {
            	pDialog = createProgressDialog(DetailClass.this);
            	pDialog.show();
            } else {
                pDialog.show();
            }
        }
    	
        /**
         * getting search results from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            //for(int i=1; i < itinerary.itin.size(); i++ ){
            	decodeImages(args[0]);
            //}
 
            return null;
        }
        
	    public void decodeImages(String str){
			//ArrayList<String> imgs = new ArrayList<String>();
			//setImgbitmaps(new ArrayList<Bitmap>());
			//for( String img : imgs ) {
			
			if( str != null )
			{
				byte[] decodedString = Base64.decode(str, Base64.DEFAULT);
				BitmapFactory.Options o = new BitmapFactory.Options();
				o.inJustDecodeBounds = true;
				bit = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, o);
				
				o.inSampleSize = calculateInSampleSize(o, width, 150);
				
				o.inJustDecodeBounds = false;
			    bit = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, o);
			    addBitmapToMemoryCache(String.valueOf(detailEntity.getTourId()), bit);
			    //e.setBit(bit);
			    //e.getImgbitmaps().add(bit);	
			}
		}
		
		public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		    // Raw height and width of image
		    final int height = options.outHeight;
		    final int width = options.outWidth;
		    int inSampleSize = 1;
		
		    if (height > reqHeight || width > reqWidth) {   
		
		        final int halfHeight = height / 2;
		        final int halfWidth = width / 2;
		
		        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
		        // height and width larger than the requested height and width.
		        while ((halfHeight / inSampleSize) > reqHeight
		                && (halfWidth / inSampleSize) > reqWidth) {
		            inSampleSize *= 2;
		        }
		    }
		
		    return inSampleSize;
		}
		
		protected void onPostExecute(String file_url) {
			d = new BitmapDrawable(getResources(), bit);
			d.setAlpha(190);
            rl.setBackgroundDrawable(d);
            //Log.i("post execute","done");
            pDialog.dismiss();
        }
 
    }
	
	protected class GetBitmaps extends AsyncTask<Entity, String, String> {
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
    	int width = dm.widthPixels;
    	int height = dm.heightPixels;
    	//set max image size for bitmpas based on 150dp height in detail view * screen width
    	int maxImgSize = width * 150;
    	Bitmap bit;
    	ProgressDialog pDialog;
    	
        /**
         * getting search results from url
         * */
        protected String doInBackground(Entity... args) {
            // Building Parameters
            //for(int i=1; i < itinerary.itin.size(); i++ ){
            	decodeImages(args[0]);
            //}
 
            return null;
        }
        
	    public void decodeImages(Entity ent){
			//ArrayList<String> imgs = new ArrayList<String>();
			//setImgbitmaps(new ArrayList<Bitmap>());
			//for( String img : imgs ) {
			
			if( ent != null )
			{
				byte[] decodedString = Base64.decode(ent.getImages(), Base64.DEFAULT);
				BitmapFactory.Options o = new BitmapFactory.Options();
				o.inJustDecodeBounds = true;
				bit = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, o);
				
				o.inSampleSize = calculateInSampleSize(o, width, 150);
				
				o.inJustDecodeBounds = false;
			    bit = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, o);
			    addBitmapToMemoryCache(String.valueOf(ent.getTourId()), bit);
			}
		}
		
		public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		    // Raw height and width of image
		    final int height = options.outHeight;
		    final int width = options.outWidth;
		    int inSampleSize = 1;
		
		    if (height > reqHeight || width > reqWidth) {   
		
		        final int halfHeight = height / 2;
		        final int halfWidth = width / 2;
		
		        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
		        // height and width larger than the requested height and width.
		        while ((halfHeight / inSampleSize) > reqHeight
		                && (halfWidth / inSampleSize) > reqWidth) {
		            inSampleSize *= 2;
		        }
		    }
		
		    return inSampleSize;
		}

    }

}
