package yosoyo.aaahearhereprototype.TestServerClasses.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import yosoyo.aaahearhereprototype.TestServerClasses.TestCommentUser;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPostFullProcess;
import yosoyo.aaahearhereprototype.TestServerClasses.TestUser;

/**
 * Created by adam on 02/03/16.
 */
public class ORMTestUser {

	private static final String TAG = "ORMTestUser";

	public static final String	TABLE_NAME = 				"testuser";

	private static final String	COMMA_SEP = 				", ";

	public static final String	COLUMN_ID_NAME = 			"_id";
	private static final String	COLUMN_ID_TYPE = 			"INTEGER PRIMARY KEY";

	public static final String	COLUMN_FIRST_NAME_NAME =	"first_name";
	private static final String	COLUMN_FIRST_NAME_TYPE =	"TEXT";

	public static final String	COLUMN_LAST_NAME_NAME =		"last_name";
	private static final String	COLUMN_LAST_NAME_TYPE =		"TEXT";

	public static final String	COLUMN_EMAIL_NAME = 		"email";
	private static final String	COLUMN_EMAIL_TYPE = 		"TEXT";

	public static final String	COLUMN_FB_USER_ID_NAME = 	"fb_user_id";
	private static final String	COLUMN_FB_USER_ID_TYPE = 	"TEXT";

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
		COLUMN_FIRST_NAME_NAME 	+ " " 	+ COLUMN_FIRST_NAME_TYPE 	+ COMMA_SEP	+
		COLUMN_LAST_NAME_NAME 	+ " " 	+ COLUMN_LAST_NAME_TYPE 	+ COMMA_SEP	+
		COLUMN_EMAIL_NAME 		+ " " 	+ COLUMN_EMAIL_TYPE 		+ COMMA_SEP	+
		COLUMN_FB_USER_ID_NAME 	+ " " 	+ COLUMN_FB_USER_ID_TYPE 	+ COMMA_SEP	+
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
		Log.d(TAG, "Cleared TestUser table");

		database.close();
	}

	private static ContentValues testUserToContentValues(TestUser testUser){
		ContentValues contentValues = new ContentValues();
		contentValues.put(COLUMN_ID_NAME, 			testUser.getID());
		contentValues.put(COLUMN_FIRST_NAME_NAME, 	testUser.getFirstName());
		contentValues.put(COLUMN_LAST_NAME_NAME, 	testUser.getLastName());
		contentValues.put(COLUMN_EMAIL_NAME, 		testUser.getEmail());
		contentValues.put(COLUMN_FB_USER_ID_NAME, 	testUser.getFBUserID());
		contentValues.put(COLUMN_CREATED_AT_NAME, 	String.valueOf(testUser.getCreatedAt()));
		contentValues.put(COLUMN_UPDATED_AT_NAME, String.valueOf(testUser.getUpdatedAt()));
		return contentValues;
	}

	public static void insertUsersFromPosts(Context context, List<TestPostFullProcess> testPosts, DBTestUserInsertManyFromPostsTask.DBTestUserInsertManyFromPostsTaskCallback callbackTo){
		new DBTestUserInsertManyFromPostsTask(context, testPosts, callbackTo).execute();
	}

	public static class DBTestUserInsertManyFromPostsTask extends AsyncTask<Void, Void, Boolean> {

		private Context context;
		private List<TestPostFullProcess> testPosts;
		private DBTestUserInsertManyFromPostsTaskCallback callbackTo;

		public interface DBTestUserInsertManyFromPostsTaskCallback {
			void returnInsertedManyUsers(List<TestPostFullProcess> testPosts);
		}

		public DBTestUserInsertManyFromPostsTask(Context context, List<TestPostFullProcess> testPosts, DBTestUserInsertManyFromPostsTaskCallback callbackTo){
			this.context = context;
			this.testPosts = testPosts;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			long userID;
			for (int i = 0; i < testPosts.size(); i++) {
				TestPostFullProcess testPost = testPosts.get(i);
				ContentValues values = testUserToContentValues(testPost.getUser());
				userID = database.insert(TABLE_NAME, "null", values);
				Log.d(TAG, "Inserted new TestUser with ID:" + userID);

				List<TestCommentUser> comments = testPost.getComments();
				for (int j = 0; j < comments.size(); j++){
					TestCommentUser comment = comments.get(j);
					values = testUserToContentValues(comment.getUser());
					userID = database.insert(TABLE_NAME, "null", values);
					Log.d(TAG, "Inserted new TestUser with ID:" + userID);
				}

				testPost.setUsersProcessed(true);
			}

			database.close();

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result){
			callbackTo.returnInsertedManyUsers(testPosts);
		}
	}

}
