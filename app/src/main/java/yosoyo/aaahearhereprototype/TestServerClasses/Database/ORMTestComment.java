package yosoyo.aaahearhereprototype.TestServerClasses.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import yosoyo.aaahearhereprototype.TestServerClasses.TestComment;
import yosoyo.aaahearhereprototype.TestServerClasses.TestCommentUser;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPostFullProcess;

/**
 * Created by adam on 02/03/16.
 */
public class ORMTestComment {

	private static final String TAG = "ORMTestComment";

	public static final String	TABLE_NAME = 				"testcomment";

	private static final String	COMMA_SEP = 				", ";

	public static final String	COLUMN_ID_NAME = 			"_id";
	private static final String	COLUMN_ID_TYPE = 			"INTEGER PRIMARY KEY";

	public static final String	COLUMN_POST_ID_NAME = 		"post_id";
	private static final String	COLUMN_POST_ID_TYPE = 		"INTEGER";

	public static final String	COLUMN_USER_ID_NAME = 		"user_id";
	private static final String	COLUMN_USER_ID_TYPE = 		"INTEGER";

	public static final String	COLUMN_MESSAGE_NAME = 		"message";
	private static final String	COLUMN_MESSAGE_TYPE = 		"TEXT";

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
		COLUMN_MESSAGE_NAME 	+ " " 	+ COLUMN_MESSAGE_TYPE 		+ COMMA_SEP	+
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
		Log.d(TAG, "Cleared TestComment table");

		database.close();
	}

	private static ContentValues testCommentToContentValues(TestComment testComment){
		ContentValues contentValues = new ContentValues();
		contentValues.put(COLUMN_ID_NAME, 			testComment.getID());
		contentValues.put(COLUMN_POST_ID_NAME, 		testComment.getPostID());
		contentValues.put(COLUMN_USER_ID_NAME, testComment.getUserID());
		contentValues.put(COLUMN_MESSAGE_NAME, testComment.getMessage());
		contentValues.put(COLUMN_CREATED_AT_NAME, String.valueOf(testComment.getCreatedAt()));
		contentValues.put(COLUMN_UPDATED_AT_NAME, String.valueOf(testComment.getUpdatedAt()));
		return contentValues;
	}

	public static void insertComment(Context context, TestComment testComment, DBTestCommentInsertTask.DBTestCommentInsertTaskCallback callbackTo){
		new DBTestCommentInsertTask(context, testComment, callbackTo).execute();
	}

	public static class DBTestCommentInsertTask extends AsyncTask<Void, Void, Long> {

		private Context context;
		private TestComment testComment;
		private DBTestCommentInsertTaskCallback callbackTo;

		public interface DBTestCommentInsertTaskCallback {
			void returnInsertedComment(Long commentID, TestComment comment);
		}

		public DBTestCommentInsertTask(Context context, TestComment testComment, DBTestCommentInsertTaskCallback callbackTo){
			this.context = context;
			this.testComment = testComment;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Long doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			ContentValues values = testCommentToContentValues(testComment);
			Long commentID = database.insert(TABLE_NAME, "null", values);

			Log.d(TAG, "Inserted new Comment with ID:" + commentID);

			database.close();

			return commentID;
		}

		@Override
		protected void onPostExecute(Long commentID){
			callbackTo.returnInsertedComment(commentID, testComment);
		}
	}

	public static void insertCommentsFromPosts(Context context, List<TestPostFullProcess> testPosts, DBTestCommentInsertManyFromPostsTask.DBTestCommentInsertManyFromPostsTaskCallback callbackTo){
		new DBTestCommentInsertManyFromPostsTask(context, testPosts, callbackTo).execute();
	}

	public static class DBTestCommentInsertManyFromPostsTask extends AsyncTask<Void, Void, Boolean> {

		private Context context;
		private List<TestPostFullProcess> testPosts;
		private DBTestCommentInsertManyFromPostsTaskCallback callbackTo;

		public interface DBTestCommentInsertManyFromPostsTaskCallback {
			void returnInsertedManyComments(List<TestPostFullProcess> testPosts);
		}

		public DBTestCommentInsertManyFromPostsTask(Context context, List<TestPostFullProcess> testPosts, DBTestCommentInsertManyFromPostsTaskCallback callbackTo){
			this.context = context;
			this.testPosts = testPosts;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			//List<Long> postIDs = new ArrayList<>(testPosts.size());
			Long commentID;
			for (int i = 0; i < testPosts.size(); i++) {
				TestPostFullProcess testPost = testPosts.get(i);
				for (int j = 0; j < testPost.getComments().size(); j++) {
					TestCommentUser testComment = testPost.getComments().get(j);
					ContentValues values = testCommentToContentValues(testComment.getComment());
					commentID = database.insert(TABLE_NAME, "null", values);


					Log.d(TAG, "Inserted new Comment with ID:" + commentID);
				}
				testPost.setCommentsProcessed(true);
			}

			database.close();

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result){
			callbackTo.returnInsertedManyComments(testPosts);
		}
	}

}
