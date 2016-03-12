package yosoyo.aaahearhereprototype.HHServerClasses.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import yosoyo.aaahearhereprototype.HHServerClasses.HHFriendship;
import yosoyo.aaahearhereprototype.HHServerClasses.HHFriendshipUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUserFullProcess;

/**
 * Created by adam on 02/03/16.
 */
public class ORMFriendship {

	private static final String TAG = "ORMFriendship";

	public static final String	TABLE_NAME = 					"friendships";

	private static final String	COMMA_SEP = 					", ";

	public static final String	COLUMN_ID_NAME = 				"_id";
	private static final String	COLUMN_ID_TYPE = 				"INTEGER PRIMARY KEY";

	public static final String	COLUMN_USER_ID_NAME = 			"user_id";
	private static final String	COLUMN_USER_ID_TYPE = 			"INTEGER";

	public static final String	COLUMN_FRIEND_USER_ID_NAME = 	"post_id";
	private static final String	COLUMN_FRIEND_USER_ID_TYPE =	"INTEGER";

	public static final String	COLUMN_CREATED_AT_NAME = 		"created_at";
	private static final String	COLUMN_CREATED_AT_TYPE = 		"TIMESTAMP";

	public static final String	COLUMN_UPDATED_AT_NAME = 		"updated_at";
	private static final String	COLUMN_UPDATED_AT_TYPE = 		"TIMESTAMP";

	public static final String	COLUMN_CACHED_AT_NAME = 		"cached_at";
	private static final String	COLUMN_CACHED_AT_TYPE = 		"TIMESTAMP";
	public static final String	COLUMN_CACHED_AT_DEFAULT =		"DEFAULT CURRENT_TIMESTAMP NOT NULL";


	public static final String SQL_CREATE_TABLE =
		"CREATE TABLE " + TABLE_NAME + " (" +
		COLUMN_ID_NAME 				+ " " 	+ COLUMN_ID_TYPE 				+ COMMA_SEP	+
		COLUMN_USER_ID_NAME 		+ " " 	+ COLUMN_USER_ID_TYPE 			+ COMMA_SEP	+
		COLUMN_FRIEND_USER_ID_NAME	+ " "	+ COLUMN_FRIEND_USER_ID_TYPE	+ COMMA_SEP	+
		COLUMN_CREATED_AT_NAME 		+ " " 	+ COLUMN_CREATED_AT_TYPE 		+ COMMA_SEP	+
		COLUMN_UPDATED_AT_NAME 		+ " " 	+ COLUMN_UPDATED_AT_TYPE 		+ COMMA_SEP	+
		COLUMN_CACHED_AT_NAME 		+ " " 	+ COLUMN_CACHED_AT_TYPE 		+ " " 		+ COLUMN_CACHED_AT_DEFAULT	+
	");";

	public static final String SQL_DROP_TABLE =
		"DROP TABLE IF EXISTS " + TABLE_NAME;

	public static void resetTable(Context context){
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();

		database.execSQL(SQL_DROP_TABLE);
		database.execSQL(SQL_CREATE_TABLE);
		Log.d(TAG, "Cleared HHFriendship table");

		database.close();
	}

	private static ContentValues friendshipToContentValues(HHFriendship friendship){
		ContentValues contentValues = new ContentValues();
		contentValues.put(COLUMN_ID_NAME, 				friendship.getID());
		contentValues.put(COLUMN_USER_ID_NAME, 			friendship.getUserID());
		contentValues.put(COLUMN_FRIEND_USER_ID_NAME,	friendship.getFriendUserID());
		contentValues.put(COLUMN_CREATED_AT_NAME, 		String.valueOf(friendship.getCreatedAt()));
		contentValues.put(COLUMN_UPDATED_AT_NAME,		String.valueOf(friendship.getUpdatedAt()));
		return contentValues;
	}

	public static void insertFriendship(Context context, HHFriendship friendship, DBFriendshipInsertTask.DBFriendshipInsertTaskCallback callbackTo){
		new DBFriendshipInsertTask(context, friendship, callbackTo).execute();
	}

	public static class DBFriendshipInsertTask extends AsyncTask<Void, Void, Long> {

		private Context context;
		private HHFriendship friendship;
		private DBFriendshipInsertTaskCallback callbackTo;

		public interface DBFriendshipInsertTaskCallback {
			void returnInsertedFriendship(Long friendshipID, HHFriendship friendship);
		}

		public DBFriendshipInsertTask(Context context, HHFriendship friendship, DBFriendshipInsertTaskCallback callbackTo){
			this.context = context;
			this.friendship = friendship;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Long doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			ContentValues values = friendshipToContentValues(friendship);
			Long friendshipID = database.insertWithOnConflict(TABLE_NAME, "null", values, SQLiteDatabase.CONFLICT_REPLACE);

			Log.d(TAG, "Inserted new Friendship with ID:" + friendshipID);

			database.close();

			return friendshipID;
		}

		@Override
		protected void onPostExecute(Long friendshipID){
			callbackTo.returnInsertedFriendship(friendshipID, friendship);
		}
	}

	/*public static void deleteFriendship(Context context, HHFriendship friendship, DBFriendshipDeleteTask.DBFriendshipDeleteTaskCallback callbackTo){
		new DBFriendshipDeleteTask(context, friendship, callbackTo).execute();
	}

	public static class DBFriendshipDeleteTask extends AsyncTask<Void, Void, Boolean> {

		private Context context;
		private HHFriendship friendship;
		private DBFriendshipDeleteTaskCallback callbackTo;

		public interface DBFriendshipDeleteTaskCallback {
			void returnDeletedFriendship(boolean success);
		}

		public DBFriendshipDeleteTask(Context context, HHFriendship friendship, DBFriendshipDeleteTaskCallback callbackTo){
			this.context = context;
			this.friendship = friendship;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			int deletedRows = database.delete(
				TABLE_NAME, COLUMN_POST_ID_NAME + "=? AND " +COLUMN_USER_ID_NAME + "=?",
				new String[]{String.valueOf(friendship.getPostID()), String.valueOf(friendship.getUserID())});

			Log.d(TAG, "Deleted "+deletedRows +" rows with ID:" + friendship.getID());

			database.close();

			return (deletedRows > 0);
		}

		@Override
		protected void onPostExecute(Boolean success){
			callbackTo.returnDeletedFriendship(success);
		}
	}*/

	public static void insertFriendshipsFromUser(Context context, HHUserFullProcess user, DBFriendshipInsertManyFromUserTask.DBFriendshipInsertManyFromUserTaskCallback callbackTo){
		new DBFriendshipInsertManyFromUserTask(context, user, callbackTo).execute();
	}

	public static class DBFriendshipInsertManyFromUserTask extends AsyncTask<Void, Void, Boolean> {

		private Context context;
		private HHUserFullProcess user;
		private DBFriendshipInsertManyFromUserTaskCallback callbackTo;

		public interface DBFriendshipInsertManyFromUserTaskCallback {
			void returnInsertedManyFriendships(HHUserFullProcess user);
		}

		public DBFriendshipInsertManyFromUserTask(Context context, HHUserFullProcess user, DBFriendshipInsertManyFromUserTaskCallback callbackTo){
			this.context = context;
			this.user = user;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			Long friendshipID;
			for (int j = 0; j < user.getFriendships().size(); j++) {
				HHFriendshipUser friendship = user.getFriendships().get(j);
				ContentValues values = friendshipToContentValues(friendship.getFriendship());
				friendshipID = database.insertWithOnConflict(TABLE_NAME, "null", values, SQLiteDatabase.CONFLICT_REPLACE);

				Log.d(TAG, "Inserted new Friendship with ID:" + friendshipID);
			}
			user.setFriendshipsProcessed(true);

			database.close();

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result){
			callbackTo.returnInsertedManyFriendships(user);
		}
	}

}
