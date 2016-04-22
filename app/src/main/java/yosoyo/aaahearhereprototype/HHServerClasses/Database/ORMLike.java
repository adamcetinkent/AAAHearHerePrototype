package yosoyo.aaahearhereprototype.HHServerClasses.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHLike;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHLikeUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPostFullProcess;

/**
 * Created by adam on 02/03/16.
 *
 * Object-Relational Mapping for {@link HHLike}
 */
public class ORMLike {

	private static final String TAG = "ORMLike";

	private static final String	TABLE_NAME = 				"likes";
	public static String TABLE() {return TABLE_NAME;}

	private static final String	COMMA_SEP = 				", ";

	private static final String	COLUMN_ID_NAME = 			"_id";
	private static final String	COLUMN_ID_TYPE = 			"INTEGER PRIMARY KEY";
	public static String ID() { return COLUMN_ID_NAME; }
	public static String dotID() { return TABLE_NAME + "." + COLUMN_ID_NAME; }

	private static final String	COLUMN_POST_ID_NAME = 		"post_id";
	private static final String	COLUMN_POST_ID_TYPE = 		"INTEGER";
	public static String POST_ID() { return COLUMN_POST_ID_NAME; }
	public static String dotPOST_ID() { return TABLE_NAME + "." + COLUMN_POST_ID_NAME; }

	private static final String	COLUMN_USER_ID_NAME = 		"user_id";
	private static final String	COLUMN_USER_ID_TYPE = 		"INTEGER";
	public static String USER_ID() { return COLUMN_USER_ID_NAME; }
	public static String dotUSER_ID() { return TABLE_NAME + "." + COLUMN_USER_ID_NAME; }

	private static final String	COLUMN_CREATED_AT_NAME = 	"created_at";
	private static final String	COLUMN_CREATED_AT_TYPE = 	"TIMESTAMP";
	public static String CREATED_AT() { return COLUMN_CREATED_AT_NAME; }
	public static String dotCREATED_AT() { return TABLE_NAME + "." + COLUMN_CREATED_AT_NAME; }

	private static final String	COLUMN_UPDATED_AT_NAME = 	"updated_at";
	private static final String	COLUMN_UPDATED_AT_TYPE = 	"TIMESTAMP";
	public static String UPDATED_AT() { return COLUMN_UPDATED_AT_NAME; }
	public static String dotUPDATED_AT() { return TABLE_NAME + "." + COLUMN_UPDATED_AT_NAME; }

	private static final String	COLUMN_CACHED_AT_NAME = 	"cached_at";
	private static final String	COLUMN_CACHED_AT_TYPE = 	"TIMESTAMP";
	private static final String	COLUMN_CACHED_AT_DEFAULT =	"DEFAULT CURRENT_TIMESTAMP NOT NULL";
	public static String CACHED_AT() { return COLUMN_CREATED_AT_NAME; }
	public static String dotCACHED_AT() { return TABLE_NAME + "." + COLUMN_CREATED_AT_NAME; }


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
		Log.d(TAG, "Cleared HHLike table");

		database.close();
	}

	private static ContentValues likeToContentValues(HHLike like){
		ContentValues contentValues = new ContentValues();
		contentValues.put(COLUMN_ID_NAME, 			like.getID());
		contentValues.put(COLUMN_POST_ID_NAME, 		like.getPostID());
		contentValues.put(COLUMN_USER_ID_NAME, 		like.getUserID());
		contentValues.put(COLUMN_CREATED_AT_NAME, 	String.valueOf(like.getCreatedAt()));
		contentValues.put(COLUMN_UPDATED_AT_NAME,	String.valueOf(like.getUpdatedAt()));
		return contentValues;
	}

	public static void insertLike(Context context, HHLike like, DBLikeInsertTask.Callback callbackTo){
		new DBLikeInsertTask(context, like, callbackTo).execute();
	}

	public static class DBLikeInsertTask extends AsyncTask<Void, Void, Long> {

		private final Context context;
		private final HHLike like;
		private final Callback callbackTo;

		public interface Callback {
			void returnInsertedLike(Long likeID, HHLike like);
		}

		public DBLikeInsertTask(Context context, HHLike like, Callback callbackTo){
			this.context = context;
			this.like = like;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Long doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			ContentValues values = likeToContentValues(like);
			Long likeID = database.insertWithOnConflict(TABLE_NAME, "null", values, SQLiteDatabase.CONFLICT_REPLACE);

			Log.d(TAG, "Inserted new Like with ID:" + likeID);

			database.close();

			return likeID;
		}

		@Override
		protected void onPostExecute(Long likeID){
			callbackTo.returnInsertedLike(likeID, like);
		}
	}

	public static void deleteLike(Context context, HHLike like, DBLikeDeleteTask.Callback callbackTo){
		new DBLikeDeleteTask(context, like, callbackTo).execute();
	}

	public static class DBLikeDeleteTask extends AsyncTask<Void, Void, Boolean> {

		private final Context context;
		private final HHLike like;
		private final Callback callbackTo;

		public interface Callback {
			void returnDeleteLike(boolean success);
		}

		public DBLikeDeleteTask(Context context, HHLike like, Callback callbackTo){
			this.context = context;
			this.like = like;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			int deletedRows = database.delete(
				TABLE_NAME, COLUMN_POST_ID_NAME + "=? AND " +COLUMN_USER_ID_NAME + "=?",
				new String[]{String.valueOf(like.getPostID()), String.valueOf(like.getUserID())});

			Log.d(TAG, "Deleted "+deletedRows +" rows with ID:" + like.getID());

			database.close();

			return (deletedRows > 0);
		}

		@Override
		protected void onPostExecute(Boolean success){
			callbackTo.returnDeleteLike(success);
		}
	}

	public static void insertLikesFromPosts(Context context, List<HHPostFullProcess> posts, DBLikeInsertManyFromPostsTask.Callback callbackTo){
		new DBLikeInsertManyFromPostsTask(context, posts, callbackTo).execute();
	}

	public static class DBLikeInsertManyFromPostsTask extends AsyncTask<Void, Void, Boolean> {

		private final Context context;
		private final List<HHPostFullProcess> posts;
		private final Callback callbackTo;

		public interface Callback {
			void returnInsertedManyLikes(List<HHPostFullProcess> postsToProcess);
		}

		public DBLikeInsertManyFromPostsTask(Context context, List<HHPostFullProcess> posts, Callback callbackTo){
			this.context = context;
			this.posts = posts;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			Long likeID;
			for (int i = 0; i < posts.size(); i++) {
				HHPostFullProcess post = posts.get(i);
				for (int j = 0; j < post.getLikes().size(); j++) {
					HHLikeUser like = post.getLikes().get(j);
					ContentValues values = likeToContentValues(like.getLike());
					likeID = database.insertWithOnConflict(TABLE_NAME, "null", values, SQLiteDatabase.CONFLICT_REPLACE);

					Log.d(TAG, "Inserted new Like with ID:" + likeID);
				}
				post.setLikesProcessed(true);
			}

			database.close();

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result){
			callbackTo.returnInsertedManyLikes(posts);
		}
	}

}
