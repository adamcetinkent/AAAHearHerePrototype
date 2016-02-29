package yosoyo.aaahearhereprototype.TestServerClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.DatabaseHelper;

/**
 * Created by adam on 22/02/16.
 */
public class ORMTestPostUser {

	private static final String TAG = "ORMTestPostUser";

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

	private static final String COLUMN_USER_FIRST_NAME_TYPE = "TEXT";
	public static final String COLUMN_USER_FIRST_NAME_NAME = "user_first_name";

	private static final String COLUMN_USER_LAST_NAME_TYPE = "TEXT";
	public static final String COLUMN_USER_LAST_NAME_NAME = "user_last_name";

	private static final String COLUMN_USER_IMG_TYPE = "TEXT";
	public static final String COLUMN_USER_IMG_NAME = "user_img_url";

	private static final String COLUMN_FB_USER_ID_TYPE = "TEXT";
	public static final String COLUMN_FB_USER_ID_NAME = "fb_user_id";

	public static final String SQL_CREATE_TABLE =
		"CREATE TABLE " + TABLE_NAME + " (" +
			COLUMN_ID_NAME + " " + COLUMN_ID_TYPE + COMMA_SEP +
			COLUMN_USER_ID_NAME + " " + COLUMN_USER_ID_TYPE + COMMA_SEP +
			COLUMN_TRACK_NAME + " " + COLUMN_TRACK_TYPE + COMMA_SEP +
			COLUMN_LAT_NAME + " " + COLUMN_LAT_TYPE + COMMA_SEP +
			COLUMN_LON_NAME + " " + COLUMN_LON_TYPE + COMMA_SEP +
			COLUMN_MESSAGE_NAME + " " + COLUMN_MESSAGE_TYPE + COMMA_SEP +
			COLUMN_CREATED_AT_NAME + " " + COLUMN_CREATED_AT_TYPE + COMMA_SEP +
			COLUMN_UPDATED_AT_NAME + " " + COLUMN_UPDATED_AT_TYPE + COMMA_SEP +
			COLUMN_USER_FIRST_NAME_NAME + " " + COLUMN_USER_FIRST_NAME_TYPE +COMMA_SEP +
			COLUMN_USER_LAST_NAME_NAME + " " + COLUMN_USER_LAST_NAME_TYPE +COMMA_SEP +
			COLUMN_USER_IMG_NAME + " " + COLUMN_USER_IMG_TYPE + COMMA_SEP +
			COLUMN_FB_USER_ID_NAME + " " + COLUMN_FB_USER_ID_TYPE +
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

	public static void insertPost(Context context, TestPostUser testPostUser, int position, InsertDBTestPostUserTask.InsertDBTestPostUserCallback callbackTo){
		new InsertDBTestPostUserTask(context, testPostUser, position, callbackTo).execute();
	}

	public static void insertPost(Context context, TestPostUser testPostUser, InsertDBTestPostUserTask.InsertDBTestPostUserCallback callbackTo){
		new InsertDBTestPostUserTask(context, testPostUser, -1, callbackTo).execute();
	}

	private static ContentValues testPostToContentValuse(TestPostUser testPostUser){
		ContentValues contentValues = new ContentValues();
		contentValues.put(ORMTestPostUser.COLUMN_ID_NAME, testPostUser.getTestPost().id);
		contentValues.put(ORMTestPostUser.COLUMN_USER_ID_NAME, testPostUser.getTestPost().user_id);
		contentValues.put(ORMTestPostUser.COLUMN_TRACK_NAME, testPostUser.getTestPost().track);
		contentValues.put(ORMTestPostUser.COLUMN_LAT_NAME, testPostUser.getTestPost().lat);
		contentValues.put(ORMTestPostUser.COLUMN_LON_NAME, testPostUser.getTestPost().lon);
		contentValues.put(ORMTestPostUser.COLUMN_MESSAGE_NAME, testPostUser.getTestPost().message);
		contentValues.put(ORMTestPostUser.COLUMN_CREATED_AT_NAME, testPostUser.getTestPost().created_at);
		contentValues.put(ORMTestPostUser.COLUMN_UPDATED_AT_NAME, testPostUser.getTestPost().updated_at);
		contentValues.put(ORMTestPostUser.COLUMN_USER_FIRST_NAME_NAME, testPostUser.getTestUser().first_name);
		contentValues.put(ORMTestPostUser.COLUMN_USER_LAST_NAME_NAME, testPostUser.getTestUser().last_name);
		contentValues.put(ORMTestPostUser.COLUMN_USER_IMG_NAME, testPostUser.getTestUser().img_url);
		contentValues.put(ORMTestPostUser.COLUMN_FB_USER_ID_NAME, testPostUser.getTestUser().fb_user_id);
		return contentValues;
	}

	public static void getTestPosts(Context context, GetDBTestPostsTask.GetDBTestPostUsersCallback callbackTo){
		new GetDBTestPostsTask(context, callbackTo).execute();
	}

	public static class GetDBTestPostsTask extends AsyncTask<Void, Void, List<TestPostUser> > {

		private Context context;
		private GetDBTestPostUsersCallback callbackTo;

		public interface GetDBTestPostUsersCallback {
			void returnTestPostUsers(List<TestPostUser> testPostUsers);
		}

		public GetDBTestPostsTask(Context context, GetDBTestPostUsersCallback callbackTo){
			this.context = context;
			this.callbackTo = callbackTo;
		}

		@Override
		protected List<TestPostUser> doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getReadableDatabase();

			Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);

			int numTracks = cursor.getCount();
			Log.d(TAG, "Loaded " + numTracks + " CachedSpotifyTracks...");
			List<TestPostUser> testPostUsers = new ArrayList<>(numTracks);

			if (numTracks > 0){
				cursor.moveToFirst();
				while (!cursor.isAfterLast()){
					TestPostUser testPostUser = new TestPostUser(cursor);
					testPostUsers.add(testPostUser);
					cursor.moveToNext();
				}
				Log.d(TAG, "TestPostUsers loaded successfully");
			}

			cursor.close();
			database.close();

			return testPostUsers;
		}

		@Override
		protected void onPostExecute(List<TestPostUser> testPostUsers){
			callbackTo.returnTestPostUsers(testPostUsers);
		}
	}

	public static class InsertDBTestPostUserTask extends AsyncTask<Void, Void, Long> {

		private Context context;
		private TestPostUser testPostUser;
		private int position;
		private InsertDBTestPostUserCallback callbackTo;

		public interface InsertDBTestPostUserCallback {
			void returnInsertedPostUserID(Long postID, int position, TestPostUser testPostUser);
		}

		public InsertDBTestPostUserTask(Context context, TestPostUser testPostUser, int position, InsertDBTestPostUserCallback callbackTo){
			this.context = context;
			this.testPostUser = testPostUser;
			this.position = position;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Long doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			ContentValues values = testPostToContentValuse(testPostUser);
			long postID = database.insert(ORMTestPostUser.TABLE_NAME, "null", values);
			Log.d(TAG, "Inserted new TestPost with ID:" + postID);

			database.close();

			return postID;
		}

		@Override
		protected void onPostExecute(Long postID){
			callbackTo.returnInsertedPostUserID(postID, position, testPostUser);
		}
	}

}
