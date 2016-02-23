package yosoyo.aaahearhereprototype.TestServerClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.DatabaseHelper;

/**
 * Created by adam on 22/02/16.
 */
public class ORMTestPost {

	private static final String TAG = "ORMTestPost";

	private static final String TABLE_NAME = "testpost";

	private static final String COMMA_SEP = ", ";

	private static final String COLUMN_ID_TYPE = "INTEGER PRIMARY KEY";
	public static final String COLUMN_ID_NAME = "_id";

	private static final String COLUMN_USER_ID_TYPE = "INTEGER";
	public static final String COLUMN_USER_ID_NAME = "user_id";

	private static final String COLUMN_TRACK_TYPE = "TEXT";
	public static final String COLUMN_TRACK_NAME = "track";

	private static final String COLUMN_LAT_TYPE = "REAL";
	public static final String COLUMN_LAT_NAME = "lat";

	private static final String COLUMN_LON_TYPE = "REAL";
	public static final String COLUMN_LON_NAME = "lon";

	private static final String COLUMN_MESSAGE_TYPE = "TEXT";
	public static final String COLUMN_MESSAGE_NAME = "message";

	private static final String COLUMN_CREATED_AT_TYPE = "TEXT";
	public static final String COLUMN_CREATED_AT_NAME = "created_at";

	private static final String COLUMN_UPDATED_AT_TYPE = "TEXT";
	public static final String COLUMN_UPDATED_AT_NAME = "updated_at";

	public static final String SQL_CREATE_TABLE =
		"CREATE TABLE " + TABLE_NAME + " (" +
			COLUMN_ID_NAME + " " + COLUMN_ID_TYPE + COMMA_SEP +
			COLUMN_USER_ID_NAME + " " + COLUMN_USER_ID_TYPE + COMMA_SEP +
			COLUMN_TRACK_NAME + " " + COLUMN_TRACK_TYPE + COMMA_SEP +
			COLUMN_LAT_NAME + " " + COLUMN_LAT_TYPE + COMMA_SEP +
			COLUMN_LON_NAME + " " + COLUMN_LON_TYPE + COMMA_SEP +
			COLUMN_MESSAGE_NAME + " " + COLUMN_MESSAGE_TYPE + COMMA_SEP +
			COLUMN_CREATED_AT_NAME + " " + COLUMN_CREATED_AT_TYPE + COMMA_SEP +
			COLUMN_UPDATED_AT_NAME + " " + COLUMN_UPDATED_AT_TYPE +
		");";

	public static final String SQL_DROP_TABLE =
		"DROP TABLE IF EXISTS " + TABLE_NAME;

	public static void resetTable(Context context){
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();

		database.execSQL(SQL_DROP_TABLE);
		database.execSQL(SQL_CREATE_TABLE);
		Log.d(TAG, "Cleared TestPost table");

		database.close();
	}

	public static void insertPost(Context context, TestPost testPost){
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();

		ContentValues values = testPostToContentValuse(testPost);
		long postID = database.insert(ORMTestPost.TABLE_NAME, "null", values);
		Log.d(TAG, "Inserted new TestPost with ID:" + postID);

		database.close();
	}

	private static ContentValues testPostToContentValuse(TestPost testPost){
		ContentValues contentValues = new ContentValues();
		contentValues.put(ORMTestPost.COLUMN_ID_NAME, testPost.id);
		contentValues.put(ORMTestPost.COLUMN_USER_ID_NAME, testPost.user_id);
		contentValues.put(ORMTestPost.COLUMN_TRACK_NAME, testPost.track);
		contentValues.put(ORMTestPost.COLUMN_LAT_NAME, testPost.lat);
		contentValues.put(ORMTestPost.COLUMN_LON_NAME, testPost.lon);
		contentValues.put(ORMTestPost.COLUMN_MESSAGE_NAME, testPost.message);
		contentValues.put(ORMTestPost.COLUMN_CREATED_AT_NAME, testPost.created_at);
		contentValues.put(ORMTestPost.COLUMN_UPDATED_AT_NAME, testPost.updated_at);
		return contentValues;
	}

	public static List<TestPost> getTestPosts(Context context){
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		SQLiteDatabase database = databaseHelper.getReadableDatabase();

		Cursor cursor = database.rawQuery("SELECT * FROM " + ORMTestPost.TABLE_NAME, null);

		int numPosts = cursor.getCount();
		Log.d(TAG, "Loaded " + numPosts + " TestPosts...");
		List<TestPost> testPostList = new ArrayList<TestPost>(numPosts);

		if (numPosts > 0){
			cursor.moveToFirst();
			while (!cursor.isAfterLast()){
				TestPost testPost = new TestPost(cursor);
				testPostList.add(testPost);
				cursor.moveToNext();
			}
			Log.d(TAG, "TestPosts loaded successfully");
		}

		cursor.close();
		database.close();

		return testPostList;
	}

}
