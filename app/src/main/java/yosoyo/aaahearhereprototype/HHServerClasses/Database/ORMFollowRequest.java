package yosoyo.aaahearhereprototype.HHServerClasses.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import yosoyo.aaahearhereprototype.HHServerClasses.HHFollowRequest;
import yosoyo.aaahearhereprototype.HHServerClasses.HHFollowRequestUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUserFullProcess;

/**
 * Created by adam on 02/03/16.
 */
class ORMFollowRequest {

	private static final String TAG = "ORMFollowRequest";

	private static final String	TABLE_NAME = 					"follow_requests";

	private static final String	COMMA_SEP = 					", ";

	private static final String	COLUMN_ID_NAME = 				"_id";
	private static final String	COLUMN_ID_TYPE = 				"INTEGER PRIMARY KEY";

	private static final String	COLUMN_USER_ID_NAME = 			"user_id";
	private static final String	COLUMN_USER_ID_TYPE = 			"INTEGER";

	private static final String COLUMN_REQUESTED_USER_ID_NAME = "requested_user_id";
	private static final String COLUMN_REQUESTED_USER_ID_TYPE =	"INTEGER";

	private static final String	COLUMN_CREATED_AT_NAME = 		"created_at";
	private static final String	COLUMN_CREATED_AT_TYPE = 		"TIMESTAMP";

	private static final String	COLUMN_UPDATED_AT_NAME = 		"updated_at";
	private static final String	COLUMN_UPDATED_AT_TYPE = 		"TIMESTAMP";

	private static final String	COLUMN_CACHED_AT_NAME = 		"cached_at";
	private static final String	COLUMN_CACHED_AT_TYPE = 		"TIMESTAMP";
	private static final String	COLUMN_CACHED_AT_DEFAULT =		"DEFAULT CURRENT_TIMESTAMP NOT NULL";


	public static final String SQL_CREATE_TABLE =
		"CREATE TABLE " + TABLE_NAME + " (" +
		COLUMN_ID_NAME 					+ " " 	+ COLUMN_ID_TYPE 				+ COMMA_SEP	+
		COLUMN_USER_ID_NAME 			+ " " 	+ COLUMN_USER_ID_TYPE 			+ COMMA_SEP	+
		COLUMN_REQUESTED_USER_ID_NAME	+ " "	+ COLUMN_REQUESTED_USER_ID_TYPE + COMMA_SEP	+
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
		Log.d(TAG, "Cleared HHFollowRequest table");

		database.close();
	}

	private static ContentValues followRequestToContentValues(HHFollowRequest followRequest){
		ContentValues contentValues = new ContentValues();
		contentValues.put(COLUMN_ID_NAME, 				followRequest.getID());
		contentValues.put(COLUMN_USER_ID_NAME, followRequest.getUserID());
		contentValues.put(COLUMN_REQUESTED_USER_ID_NAME, followRequest.getRequestedUserID());
		contentValues.put(COLUMN_CREATED_AT_NAME, String.valueOf(followRequest.getCreatedAt()));
		contentValues.put(COLUMN_UPDATED_AT_NAME, String.valueOf(followRequest.getUpdatedAt()));
		return contentValues;
	}

	public static void insertFollowRequestsFromUser(Context context, HHUserFullProcess user, DBFollowRequestInsertManyFromUserTask.Callback callbackTo){
		new DBFollowRequestInsertManyFromUserTask(context, user, callbackTo).execute();
	}

	public static class DBFollowRequestInsertManyFromUserTask extends AsyncTask<Void, Void, Boolean> {

		private final Context context;
		private final HHUserFullProcess user;
		private final Callback callbackTo;

		public interface Callback {
			void returnInsertedManyFollowRequests(HHUserFullProcess user);
		}

		public DBFollowRequestInsertManyFromUserTask(Context context, HHUserFullProcess user, Callback callbackTo){
			this.context = context;
			this.user = user;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			Long followRequestID;
			for (int j = 0; j < user.getFollowOutRequests().size(); j++) {
				HHFollowRequestUser follow = user.getFollowOutRequests().get(j);
				ContentValues values = followRequestToContentValues(follow.getFollowRequest());
				followRequestID = database.insertWithOnConflict(TABLE_NAME, "null", values, SQLiteDatabase.CONFLICT_REPLACE);

				Log.d(TAG, "Inserted new FollowRequest with ID:" + followRequestID);
			}
			for (int j = 0; j < user.getFollowInRequests().size(); j++) {
				HHFollowRequestUser follow = user.getFollowInRequests().get(j);
				ContentValues values = followRequestToContentValues(follow.getFollowRequest());
				followRequestID = database.insertWithOnConflict(TABLE_NAME, "null", values, SQLiteDatabase.CONFLICT_REPLACE);

				Log.d(TAG, "Inserted new FollowRequest with ID:" + followRequestID);
			}
			user.setFollowRequestsProcessed(true);

			database.close();

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result){
			callbackTo.returnInsertedManyFollowRequests(user);
		}
	}

	public static void deleteFollowRequest(Context context, HHFollowRequest followRequest, DBFollowRequestDelete.Callback callbackTo){
		new DBFollowRequestDelete(context, followRequest, callbackTo).execute();
	}

	public static class DBFollowRequestDelete extends AsyncTask<Void, Void, Boolean> {

		private final Context context;
		private final HHFollowRequest followRequest;
		private final Callback callbackTo;

		public interface Callback {
			void returnDeletedLike(boolean success);
		}

		public DBFollowRequestDelete(Context context, HHFollowRequest followRequest, Callback callbackTo){
			this.context = context;
			this.followRequest = followRequest;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			int deletedRows = database.delete(
				TABLE_NAME, COLUMN_USER_ID_NAME + "=? AND " +COLUMN_REQUESTED_USER_ID_NAME + "=?",
				new String[]{
					String.valueOf(followRequest.getUserID()),
					String.valueOf(followRequest.getRequestedUserID())
				});

			Log.d(TAG, "Deleted " + deletedRows + " rows with ID:" + followRequest.getID());

			database.close();

			return (deletedRows > 0);
		}

		@Override
		protected void onPostExecute(Boolean success){
			callbackTo.returnDeletedLike(success);
		}
	}

}
