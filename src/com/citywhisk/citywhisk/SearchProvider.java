package com.citywhisk.citywhisk;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

public class SearchProvider extends ContentProvider{
	
	private DatabaseHelper dbHelper;
	
	private static final String DBNAME = "itins.sqlite";
	private SQLiteDatabase db;
	
	public static String AUTHORITY = "com.citywhisk.citywhisk.SearchProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/activity_search");
    
    public static final String WORDS_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
            "/vnd.citywhisk.citywhisk";

    // UriMatcher stuff
    private static final int SEARCH_WORDS = 0;
    private static final int GET_WORD = 1;
    private static final int SEARCH_SUGGEST = 2;
    private static final int REFRESH_SHORTCUT = 3;
    //private static final UriMatcher sURIMatcher = buildUriMatcher();

	@Override
	public boolean onCreate() {
		/*
         * Creates a new helper object. This method always returns quickly.
         * Notice that the database itself isn't created or opened
         * until SQLiteOpenHelper.getWritableDatabase is called
         */
        dbHelper = new DatabaseHelper(getContext());

        return true;
	}
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		return WORDS_MIME_TYPE;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		return null;
	}


	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		String query = uri.getLastPathSegment();
		if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(query)) {
			SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
	        builder.setTables("activity_search");
	        //builder.setProjectionMap(mColumnMap);

	        String[] columns = new String[] { "_id", "activity" };
	        
	        Cursor cursor = builder.query(dbHelper.getReadableDatabase(),
	        		columns, selection, selectionArgs, null, null, null);

	        if (cursor == null) {
	            return null;
	        } else if (!cursor.moveToFirst()) {
	            cursor.close();
	            return null;
	        }
	        return cursor;
		}
		return null;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

}
