package yosoyo.aaahearhereprototype.TestServerClasses.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import yosoyo.aaahearhereprototype.TestServerClasses.TestLike;
import yosoyo.aaahearhereprototype.TestServerClasses.TestLikeUser;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPostFullProcess;

/**
 * Created by adam on 02/03/16.
 */
public class ORMTestLike {

	private static final String TAG = "ORMTestLike";

	public static final String	TABLE_NAME = 				"testlike";

	private static final String	COMMA_SEP = 				", ";

	public static final String	COLUMN_ID_NAME = 			"_id";
	private static final String	COLUMN_ID_TYPE = 			"INTEGER PRIMARY KEY";

	public static final String	COLUMN_POST_ID_NAME = 		"post_id";
	private static final String	COLUMN_POST_ID_TYPE = 		"INTEGER";

	public static final String	COLUMN_USER_ID_NAME = 		"user_id";
	private static final String	COLUMN_USER_ID_TYPE = 		"INTEGER";

	public static final String	COLUMN_CREATED_AT_NAME = 	"created_at";
	private static final String	COLUMN_CREATED_AT_TYPE = 	"TIMESTAMP";

	public static final String	COLUMN_UPDATED_AT_NAME = 	"updated_at";
	private static final String	COLUMN_UPDATED_AT_TYPE = 	"TIMESTAMP";

	public static final String	COLUMN_CACHED_AT_NAME = 	"cached_at";
	private static final String	COLUMN_CACHED_AT_TYPE = 	"TIMESTAMP";
	public static final String	COLUMN_CACHED_AT_DEFAULT =	"DEFAULT CURRENT_TIMESTAMP NOT NULL";


	public static final String SQL_CREATE_TABLE =
		"CREATE TABLE " + TABLE_NAME + " (" +
		COLUMN_ID_NAME 			+ " " 	+ COLUMN_ID_TYPE 			+ COMMA_SEP	+
		COLUMN_POST_ID_NAME 	+ " " 	+ COLUMN_POST_ID_TYPE 		+ COMMA_SEP	+
		COLUMN_USER_ID_NAME 	+ " " 	+ COLUMN_USER_ID_TYPE 		+ COMMA_SEP	+
		COLUMN_CREATED_AT_NAME 	+ " " 	+ COLUMN_CREATED_AT_TYPE 	+ COMMA_SEP	+
		COLUMN_UPDATED_AT_NAME 	+ " " 	+ COLUMN_UPDATED_AT_TYPE 	+ COMMA_SEP	+
		COLUMN_CACHED_AT_NAME 	+ " " 	+ COLUMN_CACHED_AT_TYPE 	+ " " 		+ COLUMN_CACHED_AT_DEFAULT	+
	");";

	public static final String SQL_DROP_TABLE =
		"DROP TABLE IF EXISTS " + TABLE_NAME;

	public static void resetTable(Context context){
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();

		database.execSQL(SQL_DROP_TABLE);
		database.execSQL(SQL_CREATE_TABLE);
		Log.d(TAG, "Cleared TestLike table");

		database.close();
	}

	private static ContentValues testLikeToContentValues(TestLike testLike){
		ContentValues contentValues = new ContentValues();
		contentValues.put(COLUMN_ID_NAME, 			testLike.getID());
		contentValues.put(COLUMN_POST_ID_NAME, testLike.getPostID());
		contentValues.put(COLUMN_USER_ID_NAME, testLike.getUserID());
		contentValues.put(COLUMN_CREATED_AT_NAME, String.valueOf(testLike.getCreatedAt()));
		contentValues.put(COLUMN_UPDATED_AT_NAME, String.valueOf(testLike.getUpdatedAt()));
		return contentValues;
	}

	public static void insertLike(Context context, TestLike testLike, DBTestLikeInsertTask.DBTestLikeInsertTaskCallback callbackTo){
		new DBTestLikeInsertTask(context, testLike, callbackTo).execute();
	}

	public static class DBTestLikeInsertTask extends AsyncTask<Void, Void, Long> {

		private Context context;
		private TestLike testLike;
		private DBTestLikeInsertTaskCallback callbackTo;

		public interface DBTestLikeInsertTaskCallback {
			void returnInsertedLike(Long likeID, TestLike like);
		}

		public DBTestLikeInsertTask(Context context, TestLike testLike, DBTestLikeInsertTaskCallback callbackTo){
			this.context = context;
			this.testLike = testLike;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Long doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			ContentValues values = testLikeToContentValues(testLike);
			Long likeID = database.insert(TABLE_NAME, "null", values);

			Log.d(TAG, "Inserted new Like with ID:" + likeID);

			database.close();

			return likeID;
		}

		@Override
		protected void onPostExecute(Long likeID){
			callbackTo.returnInsertedLike(likeID, testLike);
		}
	}

	public static void deleteLike(Context context, TestLike testLike, DBTestLikeDeleteTask.DBTestLikeDeleteTaskCallback callbackTo){
		new DBTestLikeDeleteTask(context, testLike, callbackTo).execute();
	}

	public static class DBTestLikeDeleteTask extends AsyncTask<Void, Void, Boolean> {

		private Context context;
		private TestLike testLike;
		private DBTestLikeDeleteTaskCallback callbackTo;

		public interface DBTestLikeDeleteTaskCallback {
			void returnDeletedLike(boolean success);
		}

		public DBTestLikeDeleteTask(Context context, TestLike testLike, DBTestLikeDeleteTaskCallback callbackTo){
			this.context = context;
			this.testLike = testLike;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			int deletedRows = database.delete(TABLE_NAME, COLUMN_ID_NAME + "=?", new String[]{String.valueOf(
				testLike.getID())});

			Log.d(TAG, "Deleted "+deletedRows +" rows with ID:" + testLike.getID());

			database.close();

			return (deletedRows > 0);
		}

		@Override
		protected void onPostExecute(Boolean success){
			callbackTo.returnDeletedLike(success);
		}
	}

	public static void insertLikesFromPosts(Context context, List<TestPostFullProcess> testPosts, DBTestLikeInsertManyFromPostsTask.DBTestLikeInsertManyFromPostsTaskCallback callbackTo){
		new DBTestLikeInsertManyFromPostsTask(context, testPosts, callbackTo).execute();
	}

	public static class DBTestLikeInsertManyFromPostsTask extends AsyncTask<Void, Void, Boolean> {

		private Context context;
		private List<TestPostFullProcess> testPosts;
		private DBTestLikeInsertManyFromPostsTaskCallback callbackTo;

		public interface DBTestLikeInsertManyFromPostsTaskCallback {
			void returnInsertedManyLikes(List<TestPostFullProcess> testPosts);
		}

		public DBTestLikeInsertManyFromPostsTask(Context context, List<TestPostFullProcess> testPosts, DBTestLikeInsertManyFromPostsTaskCallback callbackTo){
			this.context = context;
			this.testPosts = testPosts;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			Long likeID;
			for (int i = 0; i < testPosts.size(); i++) {
				TestPostFullProcess testPost = testPosts.get(i);
				for (int j = 0; j < testPost.getLikes().size(); j++) {
					TestLikeUser testLike = testPost.getLikes().get(j);
					ContentValues values = testLikeToContentValues(testLike.getLike());
					likeID = database.insert(TABLE_NAME, "null", values);

					Log.d(TAG, "Inserted new Like with ID:" + likeID);
				}
				testPost.setLikesProcessed(true);
			}

			database.close();

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result){
			callbackTo.returnInsertedManyLikes(testPosts);
		}
	}

}
