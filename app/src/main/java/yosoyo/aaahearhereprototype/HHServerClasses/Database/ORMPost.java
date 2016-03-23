package yosoyo.aaahearhereprototype.HHServerClasses.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPost;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPostFullProcess;

/**
 * Created by adam on 22/02/16.
 */
public class ORMPost {

	private static final String TAG = "ORMPost";

	public static final String TABLE_NAME = 					"posts";

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

	private static final String COLUMN_CACHED_AT_NAME =			"cached_at";
	private static final String COLUMN_CACHED_AT_TYPE =			"TIMESTAMP";
	private static final String COLUMN_CACHED_AT_DEFAULT =		"DEFAULT CURRENT_TIMESTAMP NOT NULL";

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
		Log.d(TAG, "Cleared HHPost table");

		database.close();
	}

	public static void insertPost(Context context, HHPost post, DBPostInsertTask.Callback callbackTo){
		new DBPostInsertTask(context, post, callbackTo).execute();
	}

	public static void insertPosts(Context context, List<HHPostFullProcess> posts, DBPostInsertManyTask.Callback callbackTo){
		new DBPostInsertManyTask(context, posts, callbackTo).execute();
	}

	private static ContentValues postToContentValues(HHPost post){
		ContentValues contentValues = new ContentValues();
		contentValues.put(COLUMN_ID_NAME, 				post.getID());
		contentValues.put(COLUMN_USER_ID_NAME, 			post.getUserID());
		contentValues.put(COLUMN_TRACK_NAME, 			post.getTrack());
		contentValues.put(COLUMN_LAT_NAME, 				post.getLat());
		contentValues.put(COLUMN_LON_NAME, 				post.getLon());
		contentValues.put(COLUMN_MESSAGE_NAME, 			post.getMessage());
		contentValues.put(COLUMN_PLACE_NAME_NAME, 		post.getPlaceName());
		contentValues.put(COLUMN_GOOGLE_PLACE_ID_NAME, 	post.getGooglePlaceID());
		contentValues.put(COLUMN_CREATED_AT_NAME,	 	String.valueOf(post.getCreatedAt()));
		contentValues.put(COLUMN_UPDATED_AT_NAME, 		String.valueOf(post.getUpdatedAt()));
		return contentValues;
	}

	public static void getPosts(Context context, DBPostSelectTask.Callback callbackTo){
		new DBPostSelectTask(context, callbackTo).execute();
	}

	protected static class DBPostSelectTask extends AsyncTask<Void, Void, List<HHPost> > {

		private final Context context;
		private final Callback callbackTo;

		public interface Callback {
			void returnPosts(List<HHPost> posts);
		}

		public DBPostSelectTask(Context context, Callback callbackTo){
			this.context = context;
			this.callbackTo = callbackTo;
		}

		@Override
		protected List<HHPost> doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getReadableDatabase();

			Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);

			int numTracks = cursor.getCount();
			Log.d(TAG, "Loaded " + numTracks + " Posts...");
			List<HHPost> posts = new ArrayList<>(numTracks);

			if (numTracks > 0){
				cursor.moveToFirst();
				while (!cursor.isAfterLast()){
					HHPost post = new HHPost(cursor);
					posts.add(post);
					cursor.moveToNext();
				}
				Log.d(TAG, "PostUsers loaded successfully");
			}

			cursor.close();
			database.close();

			return posts;
		}

		@Override
		protected void onPostExecute(List<HHPost> posts){
			callbackTo.returnPosts(posts);
		}
	}

	protected static class DBPostInsertTask extends AsyncTask<Void, Void, Long> {

		private final Context context;
		private final HHPost post;
		private final Callback callbackTo;

		public interface Callback {
			void returnInsertedPostUserID(Long postID, HHPost post);
		}

		public DBPostInsertTask(Context context, HHPost post, Callback callbackTo){
			this.context = context;
			this.post = post;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Long doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			ContentValues values = postToContentValues(post);
			long postID = database.insert(ORMPost.TABLE_NAME, "null", values);
			Log.d(TAG, "Inserted new HHPost with ID:" + postID);

			database.close();

			return postID;
		}

		@Override
		protected void onPostExecute(Long postID){
			callbackTo.returnInsertedPostUserID(postID, post);
		}
	}

	protected static class DBPostInsertManyTask extends AsyncTask<Void, Void, Boolean> {

		private final Context context;
		private final List<HHPostFullProcess> posts;
		private final Callback callbackTo;

		public interface Callback {
			void returnInsertedManyPosts(List<HHPostFullProcess> postsToProcess);
		}

		public DBPostInsertManyTask(Context context, List<HHPostFullProcess> posts, Callback callbackTo){
			this.context = context;
			this.posts = posts;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			Long postID;
			for (int i = 0; i < posts.size(); i++) {
				HHPostFullProcess post = posts.get(i);
				ContentValues values = postToContentValues(post.getPost());
				postID = database.insertWithOnConflict(TABLE_NAME, "null", values,
													   SQLiteDatabase.CONFLICT_REPLACE);

				post.setPostProcessed(true);

				Log.d(TAG, "Inserted new HHPost with ID:" + postID);
			}

			database.close();

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result){
			callbackTo.returnInsertedManyPosts(posts);
		}
	}

	public static void getUserPostCount(Context context, final long userID, DBUserPostCountTask.Callback callbackTo){
		new DBUserPostCountTask(context, userID, callbackTo).execute();
	}

	protected static class DBUserPostCountTask extends AsyncTask<Void, Void, Integer> {

		private final Context context;
		private final long userID;
		private final Callback callbackTo;

		public interface Callback {
			void returnPostCount(final int postCount);
		}

		public DBUserPostCountTask(Context context, final long userID, Callback callbackTo){
			this.context = context;
			this.userID = userID;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Integer doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getReadableDatabase();

			Cursor cursor = database.rawQuery(
				"SELECT ? FROM " + TABLE_NAME +
					" WHERE " + COLUMN_USER_ID_NAME + "=?"
				, new String[]{
					COLUMN_USER_ID_NAME,
					String.valueOf(userID)
				});

			int postCount = cursor.getCount();

			cursor.close();
			database.close();

			return postCount;
		}

		@Override
		protected void onPostExecute(Integer postCount){
			callbackTo.returnPostCount(postCount);
		}
	}

}
