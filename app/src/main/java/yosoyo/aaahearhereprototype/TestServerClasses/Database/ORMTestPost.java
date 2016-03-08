package yosoyo.aaahearhereprototype.TestServerClasses.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.TestServerClasses.TestPost;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPostFullProcess;

/**
 * Created by adam on 22/02/16.
 */
public class ORMTestPost {

	private static final String TAG = "ORMTestPost";

	public static final String TABLE_NAME = 					"testpost";

	private static final String COMMA_SEP = 					", ";

	public static final String 	COLUMN_ID_NAME = 				"_id";
	private static final String COLUMN_ID_TYPE = 				"INTEGER PRIMARY KEY";

	public static final String 	COLUMN_USER_ID_NAME = 			"user_id";
	private static final String COLUMN_USER_ID_TYPE = 			"INTEGER";

	public static final String 	COLUMN_TRACK_NAME =		 		"track";
	private static final String COLUMN_TRACK_TYPE = 			"TEXT";

	public static final String 	COLUMN_LAT_NAME = 				"lat";
	private static final String COLUMN_LAT_TYPE = 				"REAL";

	public static final String 	COLUMN_LON_NAME = 				"lon";
	private static final String COLUMN_LON_TYPE = 				"REAL";

	public static final String 	COLUMN_MESSAGE_NAME = 			"message";
	private static final String COLUMN_MESSAGE_TYPE = 			"TEXT";

	public static final String 	COLUMN_PLACE_NAME_NAME = 		"place_name";
	private static final String COLUMN_PLACE_NAME_TYPE = 		"TEXT";

	public static final String 	COLUMN_GOOGLE_PLACE_ID_NAME =	"google_place_id";
	private static final String COLUMN_GOOGLE_PLACE_ID_TYPE =	"TEXT";

	public static final String 	COLUMN_CREATED_AT_NAME = 		"created_at";
	private static final String COLUMN_CREATED_AT_TYPE = 		"TIMESTAMP";

	public static final String 	COLUMN_UPDATED_AT_NAME = 		"updated_at";
	private static final String COLUMN_UPDATED_AT_TYPE = 		"TIMESTAMP";

	public static final String 	COLUMN_CACHED_AT_NAME =			"cached_at";
	private static final String COLUMN_CACHED_AT_TYPE =			"TIMESTAMP";
	public static final String 	COLUMN_CACHED_AT_DEFAULT =		"DEFAULT CURRENT_TIMESTAMP NOT NULL";

	public static final String SQL_CREATE_TABLE =
		"CREATE TABLE " + TABLE_NAME + " (" +
			COLUMN_ID_NAME				+ " "	+ COLUMN_ID_TYPE				+ COMMA_SEP	+
			COLUMN_USER_ID_NAME			+ " "	+ COLUMN_USER_ID_TYPE			+ COMMA_SEP	+
			COLUMN_TRACK_NAME			+ " "	+ COLUMN_TRACK_TYPE				+ COMMA_SEP	+
			COLUMN_LAT_NAME				+ " "	+ COLUMN_LAT_TYPE 				+ COMMA_SEP	+
			COLUMN_LON_NAME				+ " "	+ COLUMN_LON_TYPE				+ COMMA_SEP	+
			COLUMN_MESSAGE_NAME			+ " "	+ COLUMN_MESSAGE_TYPE 			+ COMMA_SEP	+
			COLUMN_PLACE_NAME_NAME		+ " "	+ COLUMN_PLACE_NAME_TYPE 		+ COMMA_SEP	+
			COLUMN_GOOGLE_PLACE_ID_NAME	+ " "	+ COLUMN_GOOGLE_PLACE_ID_TYPE	+ COMMA_SEP	+
			COLUMN_CREATED_AT_NAME		+ " "	+ COLUMN_CREATED_AT_TYPE		+ COMMA_SEP	+
			COLUMN_UPDATED_AT_NAME		+ " "	+ COLUMN_UPDATED_AT_TYPE		+ COMMA_SEP	+
			COLUMN_CACHED_AT_NAME		+ " "	+ COLUMN_CACHED_AT_TYPE 		+ " " 		+ COLUMN_CACHED_AT_DEFAULT	+
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

	public static void insertPost(Context context, TestPost testPost, DBTestPostInsertTask.DBTestPostInsertTaskCallback callbackTo){
		new DBTestPostInsertTask(context, testPost, callbackTo).execute();
	}

	public static void insertPosts(Context context, List<TestPostFullProcess> testPosts, DBTestPostInsertManyTask.DBTestPostInsertManyTaskCallback callbackTo){
		new DBTestPostInsertManyTask(context, testPosts, callbackTo).execute();
	}

	private static ContentValues testPostToContentValues(TestPost testPost){
		ContentValues contentValues = new ContentValues();
		contentValues.put(COLUMN_ID_NAME, 				testPost.getID());
		contentValues.put(COLUMN_USER_ID_NAME, 			testPost.getUserID());
		contentValues.put(COLUMN_TRACK_NAME, 			testPost.getTrack());
		contentValues.put(COLUMN_LAT_NAME, 				testPost.getLat());
		contentValues.put(COLUMN_LON_NAME, 				testPost.getLon());
		contentValues.put(COLUMN_MESSAGE_NAME, 			testPost.getMessage());
		contentValues.put(COLUMN_PLACE_NAME_NAME, 		testPost.getPlaceName());
		contentValues.put(COLUMN_GOOGLE_PLACE_ID_NAME, 	testPost.getGooglePlaceID());
		contentValues.put(COLUMN_CREATED_AT_NAME,	 	String.valueOf(testPost.getCreatedAt()));
		contentValues.put(COLUMN_UPDATED_AT_NAME, 		String.valueOf(testPost.getUpdatedAt()));
		return contentValues;
	}

	public static void getTestPosts(Context context, DBTestPostSelectTask.DBTestPostSelectTaskCallback callbackTo){
		new DBTestPostSelectTask(context, callbackTo).execute();
	}

	public static class DBTestPostSelectTask extends AsyncTask<Void, Void, List<TestPost> > {

		private Context context;
		private DBTestPostSelectTaskCallback callbackTo;

		public interface DBTestPostSelectTaskCallback {
			void returnTestPosts(List<TestPost> testPosts);
		}

		public DBTestPostSelectTask(Context context, DBTestPostSelectTaskCallback callbackTo){
			this.context = context;
			this.callbackTo = callbackTo;
		}

		@Override
		protected List<TestPost> doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getReadableDatabase();

			Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);

			int numTracks = cursor.getCount();
			Log.d(TAG, "Loaded " + numTracks + " TestPosts...");
			List<TestPost> testPosts = new ArrayList<>(numTracks);

			if (numTracks > 0){
				cursor.moveToFirst();
				while (!cursor.isAfterLast()){
					TestPost testPost = new TestPost(cursor);
					testPosts.add(testPost);
					cursor.moveToNext();
				}
				Log.d(TAG, "TestPostUsers loaded successfully");
			}

			cursor.close();
			database.close();

			return testPosts;
		}

		@Override
		protected void onPostExecute(List<TestPost> testPosts){
			callbackTo.returnTestPosts(testPosts);
		}
	}

	public static class DBTestPostInsertTask extends AsyncTask<Void, Void, Long> {

		private Context context;
		private TestPost testPost;
		private DBTestPostInsertTaskCallback callbackTo;

		public interface DBTestPostInsertTaskCallback {
			void returnInsertedPostUserID(Long postID, TestPost testPost);
		}

		public DBTestPostInsertTask(Context context, TestPost testPost, DBTestPostInsertTaskCallback callbackTo){
			this.context = context;
			this.testPost = testPost;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Long doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			ContentValues values = testPostToContentValues(testPost);
			long postID = database.insert(ORMTestPost.TABLE_NAME, "null", values);
			Log.d(TAG, "Inserted new TestPost with ID:" + postID);

			database.close();

			return postID;
		}

		@Override
		protected void onPostExecute(Long postID){
			callbackTo.returnInsertedPostUserID(postID, testPost);
		}
	}

	public static class DBTestPostInsertManyTask extends AsyncTask<Void, Void, Boolean> {

		private Context context;
		private List<TestPostFullProcess> testPosts;
		private DBTestPostInsertManyTaskCallback callbackTo;

		public interface DBTestPostInsertManyTaskCallback {
			void returnInsertedManyPosts(List<TestPostFullProcess> testPosts);
		}

		public DBTestPostInsertManyTask(Context context, List<TestPostFullProcess> testPosts, DBTestPostInsertManyTaskCallback callbackTo){
			this.context = context;
			this.testPosts = testPosts;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			//List<Long> postIDs = new ArrayList<>(testPosts.size());
			Long postID;
			for (int i = 0; i < testPosts.size(); i++) {
				TestPostFullProcess testPost = testPosts.get(i);
				ContentValues values = testPostToContentValues(testPost.getPost());
				postID = database.insert(TABLE_NAME, "null", values);

				testPost.setPostProcessed(true);

				Log.d(TAG, "Inserted new TestPost with ID:" + postID);
			}

			database.close();

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result){
			callbackTo.returnInsertedManyPosts(testPosts);
		}
	}

}
