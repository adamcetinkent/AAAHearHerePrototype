package yosoyo.aaahearhereprototype.HHServerClasses.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHComment;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHCommentUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPostFullProcess;

/**
 * Created by adam on 02/03/16.
 */
public class ORMComment {

	private static final String TAG = "ORMComment";

	private static final String	TABLE_NAME = 				"comments";
	public static String TABLE() {return TABLE_NAME;}

	private static final String	COMMA_SEP = 				", ";

	private static final String	COLUMN_ID_NAME = 			"_id";
	private static final String	COLUMN_ID_TYPE = 			"INTEGER PRIMARY KEY";
	public static String ID() { return TABLE_NAME + "." + COLUMN_ID_NAME; }

	private static final String	COLUMN_POST_ID_NAME = 		"post_id";
	private static final String	COLUMN_POST_ID_TYPE = 		"INTEGER";
	public static String POST_ID() { return TABLE_NAME + "." + COLUMN_POST_ID_NAME; }

	private static final String	COLUMN_USER_ID_NAME = 		"user_id";
	private static final String	COLUMN_USER_ID_TYPE = 		"INTEGER";
	public static String USER_ID() { return TABLE_NAME + "." + COLUMN_USER_ID_NAME; }

	private static final String	COLUMN_MESSAGE_NAME = 		"message";
	private static final String	COLUMN_MESSAGE_TYPE = 		"TEXT";
	public static String MESSAGE() { return TABLE_NAME + "." + COLUMN_MESSAGE_NAME; }

	private static final String	COLUMN_CREATED_AT_NAME = 	"created_at";
	private static final String	COLUMN_CREATED_AT_TYPE = 	"TIMESTAMP";
	public static String CREATED_AT() { return TABLE_NAME + "." + COLUMN_CREATED_AT_NAME; }

	private static final String	COLUMN_UPDATED_AT_NAME = 	"updated_at";
	private static final String	COLUMN_UPDATED_AT_TYPE = 	"TIMESTAMP";
	public static String UPDATED_AT() { return TABLE_NAME + "." + COLUMN_UPDATED_AT_NAME; }

	private static final String	COLUMN_CACHED_AT_NAME = 	"cached_at";
	private static final String	COLUMN_CACHED_AT_TYPE = 	"TIMESTAMP";
	private static final String	COLUMN_CACHED_AT_DEFAULT =	"DEFAULT CURRENT_TIMESTAMP NOT NULL";
	public static String CACHED_AT() { return TABLE_NAME + "." + COLUMN_CACHED_AT_NAME; }


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
		Log.d(TAG, "Cleared HHComment table");

		database.close();
	}

	private static ContentValues commentToContentValues(HHComment comment){
		ContentValues contentValues = new ContentValues();
		contentValues.put(COLUMN_ID_NAME, 			comment.getID());
		contentValues.put(COLUMN_POST_ID_NAME, 		comment.getPostID());
		contentValues.put(COLUMN_USER_ID_NAME, 		comment.getUserID());
		contentValues.put(COLUMN_MESSAGE_NAME, 		comment.getMessage());
		contentValues.put(COLUMN_CREATED_AT_NAME, 	String.valueOf(comment.getCreatedAt()));
		contentValues.put(COLUMN_UPDATED_AT_NAME, 	String.valueOf(comment.getUpdatedAt()));
		return contentValues;
	}

	public static void insertComment(Context context, HHComment comment, DBCommentInsertTask.Callback callbackTo){
		new DBCommentInsertTask(context, comment, callbackTo).execute();
	}

	public static class DBCommentInsertTask extends AsyncTask<Void, Void, Long> {

		private final Context context;
		private final HHComment comment;
		private final Callback callbackTo;

		public interface Callback {
			void returnInsertedComment(Long commentID, HHComment comment);
		}

		public DBCommentInsertTask(Context context, HHComment comment, Callback callbackTo){
			this.context = context;
			this.comment = comment;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Long doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			ContentValues values = commentToContentValues(comment);
			Long commentID = database.insert(TABLE_NAME, "null", values);

			Log.d(TAG, "Inserted new Comment with ID:" + commentID);

			database.close();

			return commentID;
		}

		@Override
		protected void onPostExecute(Long commentID){
			callbackTo.returnInsertedComment(commentID, comment);
		}
	}

	public static void insertCommentsFromPosts(Context context, List<HHPostFullProcess> posts, DBCommentInsertManyFromPostsTask.Callback callbackTo){
		new DBCommentInsertManyFromPostsTask(context, posts, callbackTo).execute();
	}

	public static class DBCommentInsertManyFromPostsTask extends AsyncTask<Void, Void, Boolean> {

		private final Context context;
		private final List<HHPostFullProcess> posts;
		private final Callback callbackTo;

		public interface Callback {
			void returnInsertedManyComments(List<HHPostFullProcess> postsToProcess);
		}

		public DBCommentInsertManyFromPostsTask(Context context, List<HHPostFullProcess> posts, Callback callbackTo){
			this.context = context;
			this.posts = posts;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			Long commentID;
			for (int i = 0; i < posts.size(); i++) {
				HHPostFullProcess post = posts.get(i);
				for (int j = 0; j < post.getComments().size(); j++) {
					HHCommentUser comment = post.getComments().get(j);
					ContentValues values = commentToContentValues(comment.getComment());
					commentID = database.insertWithOnConflict(TABLE_NAME, "null", values, SQLiteDatabase.CONFLICT_REPLACE);


					Log.d(TAG, "Inserted new Comment with ID:" + commentID);
				}
				post.setCommentsProcessed(true);
			}

			database.close();

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result){
			callbackTo.returnInsertedManyComments(posts);
		}
	}

}
