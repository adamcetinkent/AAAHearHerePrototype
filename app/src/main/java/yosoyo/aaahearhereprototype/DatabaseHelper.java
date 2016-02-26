package yosoyo.aaahearhereprototype;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import yosoyo.aaahearhereprototype.TestServerClasses.ORMCachedSpotifyTrack;
import yosoyo.aaahearhereprototype.TestServerClasses.ORMTestPostUser;

/**
 * Created by adam on 22/02/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = "DatabaseHelper";
	private static final String DB_NAME = "AAAHereHerePrototype";
	private static final int DB_VERSION = 4;

	public DatabaseHelper(Context context){
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "Creating database [" + DB_NAME + " v." + DB_VERSION + "]...");

		db.execSQL(ORMTestPostUser.SQL_CREATE_TABLE);
		db.execSQL(ORMCachedSpotifyTrack.SQL_CREATE_TABLE);
		//db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "Updating database [" + DB_NAME + " v." + oldVersion + "] to [" + DB_NAME + " v." + newVersion + "]...");

		db.execSQL(ORMTestPostUser.SQL_DROP_TABLE);
		db.execSQL(ORMCachedSpotifyTrack.SQL_DROP_TABLE);
		onCreate(db);
	}

	public static void reset(Context context){
		Log.d(TAG, "Resetting database...");

		ORMTestPostUser.resetTable(context);
		ORMCachedSpotifyTrack.resetTable(context);

		Log.d(TAG, "Database reset");
	}
}
