package com.yosoyo.aaahearhereprototype.HHServerClasses.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPostFullProcess;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHTag;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHTagUser;

import java.util.List;

/**
 * Created by adam on 10/03/16.
 *
 * Object-Relational Mapping for {@link HHTag}
 */
public class ORMTag {

	private static final String TAG = "ORMTag";

	private static final String	TABLE_NAME = 				"tags";
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
	public static String CACHED_AT() { return COLUMN_CACHED_AT_NAME; }
	public static String dotCACHED_AT() { return TABLE_NAME + "." + COLUMN_CACHED_AT_NAME; }


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
		Log.d(TAG, "Cleared HHTag table");

		database.close();
	}

	private static ContentValues tagToContentValues(HHTag tag){
		ContentValues contentValues = new ContentValues();
		contentValues.put(COLUMN_ID_NAME, 			tag.getID());
		contentValues.put(COLUMN_POST_ID_NAME, 		tag.getPostID());
		contentValues.put(COLUMN_USER_ID_NAME, 		tag.getUserID());
		contentValues.put(COLUMN_CREATED_AT_NAME, 	String.valueOf(tag.getCreatedAt()));
		contentValues.put(COLUMN_UPDATED_AT_NAME,	String.valueOf(tag.getUpdatedAt()));
		return contentValues;
	}

	public static void insertTag(Context context, HHTag tag, DBTagInsertTask.Callback callbackTo){
		new DBTagInsertTask(context, tag, callbackTo).execute();
	}

	public static class DBTagInsertTask extends AsyncTask<Void, Void, Long> {

		private final Context context;
		private final HHTag tag;
		private final Callback callbackTo;

		public interface Callback {
			void returnInsertedTag(Long tagID, HHTag tag);
		}

		public DBTagInsertTask(Context context, HHTag tag, Callback callbackTo){
			this.context = context;
			this.tag = tag;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Long doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			ContentValues values = tagToContentValues(tag);
			Long tagID = database.insertWithOnConflict(TABLE_NAME, "null", values, SQLiteDatabase.CONFLICT_REPLACE);

			Log.d(TAG, "Inserted new Tag with ID:" + tagID);

			database.close();

			return tagID;
		}

		@Override
		protected void onPostExecute(Long tagID){
			callbackTo.returnInsertedTag(tagID, tag);
		}
	}

	public static void deleteTag(Context context, HHTag tag, DBTagDeleteTask.Callback callbackTo){
		new DBTagDeleteTask(context, tag, callbackTo).execute();
	}

	public static class DBTagDeleteTask extends AsyncTask<Void, Void, Boolean> {

		private final Context context;
		private final HHTag tag;
		private final Callback callbackTo;

		public interface Callback {
			void returnDeletedTag(boolean success);
		}

		public DBTagDeleteTask(Context context, HHTag tag, Callback callbackTo){
			this.context = context;
			this.tag = tag;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			int deletedRows = database.delete(
				TABLE_NAME, COLUMN_POST_ID_NAME + "=? AND " +COLUMN_USER_ID_NAME + "=?",
				new String[]{String.valueOf(tag.getPostID()), String.valueOf(tag.getUserID())});

			Log.d(TAG, "Deleted "+deletedRows +" rows with ID:" + tag.getID());

			database.close();

			return (deletedRows > 0);
		}

		@Override
		protected void onPostExecute(Boolean success){
			callbackTo.returnDeletedTag(success);
		}
	}

	public static void insertTagsFromPosts(Context context, List<HHPostFullProcess> posts, DBTagInsertManyFromPostsTask.Callback callbackTo){
		new DBTagInsertManyFromPostsTask(context, posts, callbackTo).execute();
	}

	public static class DBTagInsertManyFromPostsTask extends AsyncTask<Void, Void, Boolean> {

		private final Context context;
		private final List<HHPostFullProcess> posts;
		private final Callback callbackTo;

		public interface Callback {
			void returnInsertedManyTags(List<HHPostFullProcess> postsToProcess);
		}

		public DBTagInsertManyFromPostsTask(Context context, List<HHPostFullProcess> posts, Callback callbackTo){
			this.context = context;
			this.posts = posts;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			Long tagID;
			for (int i = 0; i < posts.size(); i++) {
				HHPostFullProcess post = posts.get(i);
				for (int j = 0; j < post.getTags().size(); j++) {
					HHTagUser tag = post.getTags().get(j);
					ContentValues values = tagToContentValues(tag.getTag());
					tagID = database.insertWithOnConflict(TABLE_NAME, "null", values, SQLiteDatabase.CONFLICT_REPLACE);

					Log.d(TAG, "Inserted new Tag with ID:" + tagID);
				}
				post.setTagsProcessed(true);
			}

			database.close();

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result){
			callbackTo.returnInsertedManyTags(posts);
		}
	}

}
