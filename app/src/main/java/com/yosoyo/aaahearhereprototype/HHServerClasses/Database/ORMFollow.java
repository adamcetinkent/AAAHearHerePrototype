package com.yosoyo.aaahearhereprototype.HHServerClasses.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollow;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUserFullProcess;

/**
 * Created by adam on 02/03/16.
 *
 * Object-Relational Mapping for {@link HHFollow}
 */
public class ORMFollow {

	private static final String TAG = "ORMFollow";

	private static final String	TABLE_NAME = 					"follows";
	public static String TABLE() {return TABLE_NAME;}

	private static final String	COMMA_SEP = 					", ";

	private static final String	COLUMN_ID_NAME = 				"_id";
	private static final String	COLUMN_ID_TYPE = 				"INTEGER PRIMARY KEY";
	public static String ID() { return COLUMN_ID_NAME; }
	public static String dotID() { return TABLE_NAME + "." + COLUMN_ID_NAME; }

	private static final String	COLUMN_USER_ID_NAME = 			"user_id";
	private static final String	COLUMN_USER_ID_TYPE = 			"INTEGER";
	public static String USER_ID() { return COLUMN_USER_ID_NAME; }
	public static String dotUSER_ID() { return TABLE_NAME + "." + COLUMN_USER_ID_NAME; }

	private static final String	COLUMN_FOLLOWED_USER_ID_NAME = 	"followed_user_id";
	private static final String	COLUMN_FOLLOWED_USER_ID_TYPE =	"INTEGER";
	public static String FOLLOWED_USER_ID() { return COLUMN_FOLLOWED_USER_ID_NAME; }
	public static String dotFOLLOWED_USER_ID() { return TABLE_NAME + "." + COLUMN_FOLLOWED_USER_ID_NAME; }

	private static final String	COLUMN_CREATED_AT_NAME = 		"created_at";
	private static final String	COLUMN_CREATED_AT_TYPE = 		"TIMESTAMP";
	public static String CREATED_AT() { return COLUMN_CREATED_AT_NAME; }
	public static String dotCREATED_AT() { return TABLE_NAME + "." + COLUMN_CREATED_AT_NAME; }

	private static final String	COLUMN_UPDATED_AT_NAME = 		"updated_at";
	private static final String	COLUMN_UPDATED_AT_TYPE = 		"TIMESTAMP";
	public static String UPDATED_AT() { return COLUMN_UPDATED_AT_NAME; }
	public static String dotUPDATED_AT() { return TABLE_NAME + "." + COLUMN_UPDATED_AT_NAME; }

	private static final String	COLUMN_CACHED_AT_NAME = 		"cached_at";
	private static final String	COLUMN_CACHED_AT_TYPE = 		"TIMESTAMP";
	private static final String	COLUMN_CACHED_AT_DEFAULT =		"DEFAULT CURRENT_TIMESTAMP NOT NULL";
	public static String CACHED_AT() { return COLUMN_CACHED_AT_NAME; }
	public static String dotCACHED_AT() { return TABLE_NAME + "." + COLUMN_CACHED_AT_NAME; }


	public static final String SQL_CREATE_TABLE =
		"CREATE TABLE " + TABLE_NAME + " (" +
		COLUMN_ID_NAME 					+ " " 	+ COLUMN_ID_TYPE 				+ COMMA_SEP	+
		COLUMN_USER_ID_NAME 			+ " " 	+ COLUMN_USER_ID_TYPE 			+ COMMA_SEP	+
		COLUMN_FOLLOWED_USER_ID_NAME	+ " "	+ COLUMN_FOLLOWED_USER_ID_TYPE	+ COMMA_SEP	+
		COLUMN_CREATED_AT_NAME 			+ " " 	+ COLUMN_CREATED_AT_TYPE 		+ COMMA_SEP	+
		COLUMN_UPDATED_AT_NAME 			+ " " 	+ COLUMN_UPDATED_AT_TYPE 		+ COMMA_SEP	+
		COLUMN_CACHED_AT_NAME 			+ " " 	+ COLUMN_CACHED_AT_TYPE 		+ " " 		+ COLUMN_CACHED_AT_DEFAULT	+
	");";

	public static final String SQL_DROP_TABLE =
		"DROP TABLE IF EXISTS " + TABLE_NAME;

	public static void resetTable(Context context){
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();

		database.execSQL(SQL_DROP_TABLE);
		database.execSQL(SQL_CREATE_TABLE);
		Log.d(TAG, "Cleared HHFollow table");

		database.close();
	}

	private static ContentValues followToContentValues(HHFollow follow){
		ContentValues contentValues = new ContentValues();
		contentValues.put(COLUMN_ID_NAME, 				follow.getID());
		contentValues.put(COLUMN_USER_ID_NAME, follow.getUserID());
		contentValues.put(COLUMN_FOLLOWED_USER_ID_NAME, follow.getFollowedUserID());
		contentValues.put(COLUMN_CREATED_AT_NAME, String.valueOf(follow.getCreatedAt()));
		contentValues.put(COLUMN_UPDATED_AT_NAME, String.valueOf(follow.getUpdatedAt()));
		return contentValues;
	}

	public static void insertFollow(Context context, HHFollowUser follow, DBFollowInsertTask.Callback callbackTo){
		new DBFollowInsertTask(context, follow, callbackTo).execute();
	}

	public static class DBFollowInsertTask extends AsyncTask<Void, Void, Long> {

		private final Context context;
		private final HHFollowUser follow;
		private final Callback callbackTo;

		public interface Callback {
			void returnInsertFollow(Long followID, HHFollowUser follow);
		}

		public DBFollowInsertTask(Context context, HHFollowUser follow, Callback callbackTo){
			this.context = context;
			this.follow = follow;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Long doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			ContentValues values = followToContentValues(follow.getFollow());
			Long followID = database.insertWithOnConflict(TABLE_NAME, "null", values,
																 SQLiteDatabase.CONFLICT_REPLACE);

			Log.d(TAG, "Inserted new Follow with ID:" + followID);

			database.close();

			return followID;
		}

		@Override
		protected void onPostExecute(Long followID){
			callbackTo.returnInsertFollow(followID, follow);
		}
	}

	public static void deleteFollow(Context context, HHFollowUser like, DBFollowDeleteTask.Callback callbackTo){
		new DBFollowDeleteTask(context, like, callbackTo).execute();
	}

	public static class DBFollowDeleteTask extends AsyncTask<Void, Void, Boolean> {

		private final Context context;
		private final HHFollowUser follow;
		private final Callback callbackTo;

		public interface Callback {
			void returnDeleteFollow(boolean success);
		}

		public DBFollowDeleteTask(Context context, HHFollowUser follow, Callback callbackTo){
			this.context = context;
			this.follow = follow;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			int deletedRows = database.delete(
				TABLE_NAME, COLUMN_USER_ID_NAME + "=? AND " + COLUMN_FOLLOWED_USER_ID_NAME + "=?",
				new String[]{String.valueOf(follow.getFollow().getUserID()), String
					.valueOf(follow.getFollow().getFollowedUserID())});

			Log.d(TAG, "Deleted " + deletedRows + " rows with ID:" + follow.getFollow().getID());

			database.close();

			return (deletedRows > 0);
		}

		@Override
		protected void onPostExecute(Boolean success){
			callbackTo.returnDeleteFollow(success);
		}
	}

	public static void insertFollowsFromUser(Context context, HHUserFullProcess user, DBFollowInsertManyFromUserTask.Callback callbackTo){
		new DBFollowInsertManyFromUserTask(context, user, callbackTo).execute();
	}

	public static class DBFollowInsertManyFromUserTask extends AsyncTask<Void, Void, Boolean> {

		private final Context context;
		private final HHUserFullProcess user;
		private final Callback callbackTo;

		public interface Callback {
			void returnInsertedManyFollows(HHUserFullProcess user);
		}

		public DBFollowInsertManyFromUserTask(Context context, HHUserFullProcess user, Callback callbackTo){
			this.context = context;
			this.user = user;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			Long followID;
			for (int j = 0; j < user.getFollowOuts().size(); j++) {
				HHFollowUser follow = user.getFollowOuts().get(j);
				ContentValues values = followToContentValues(follow.getFollow());
				followID = database.insertWithOnConflict(TABLE_NAME, "null", values, SQLiteDatabase.CONFLICT_REPLACE);

				Log.d(TAG, "Inserted new Follow with ID:" + followID);
			}
			for (int j = 0; j < user.getFollowIns().size(); j++) {
				HHFollowUser follow = user.getFollowIns().get(j);
				ContentValues values = followToContentValues(follow.getFollow());
				followID = database.insertWithOnConflict(TABLE_NAME, "null", values, SQLiteDatabase.CONFLICT_REPLACE);

				Log.d(TAG, "Inserted new Follow with ID:" + followID);
			}
			user.setFollowsProcessed(true);

			database.close();

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result){
			callbackTo.returnInsertedManyFollows(user);
		}
	}

	public static void getUserFollowersInCount(Context context, final long userID, DBUserFollowersInCountTask.Callback callbackTo){
		new DBUserFollowersInCountTask(context, userID, callbackTo).execute();
	}

	static class DBUserFollowersInCountTask extends AsyncTask<Void, Void, Integer> {

		private final Context context;
		private final long userID;
		private final Callback callbackTo;

		public interface Callback {
			void returnFollowersInCount(final int followersInCount);
		}

		public DBUserFollowersInCountTask(Context context, final long userID, Callback callbackTo){
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
					" WHERE " + COLUMN_FOLLOWED_USER_ID_NAME + "=?"
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
			callbackTo.returnFollowersInCount(postCount);
		}
	}

	public static void getUserFollowersOutCount(Context context, final long userID, DBUserFollowersOutCountTask.Callback callbackTo){
		new DBUserFollowersOutCountTask(context, userID, callbackTo).execute();
	}

	static class DBUserFollowersOutCountTask extends AsyncTask<Void, Void, Integer> {

		private final Context context;
		private final long userID;
		private final Callback callbackTo;

		public interface Callback {
			void returnFollowersOutCount(final int followersInCount);
		}

		public DBUserFollowersOutCountTask(Context context, final long userID, Callback callbackTo){
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
			callbackTo.returnFollowersOutCount(postCount);
		}
	}

}
