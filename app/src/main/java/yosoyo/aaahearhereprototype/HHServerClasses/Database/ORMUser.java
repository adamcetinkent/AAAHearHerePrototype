package yosoyo.aaahearhereprototype.HHServerClasses.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import yosoyo.aaahearhereprototype.HHServerClasses.HHCommentUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHFriendshipUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHLikeUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHPostFullProcess;
import yosoyo.aaahearhereprototype.HHServerClasses.HHTagUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUserFullProcess;

/**
 * Created by adam on 02/03/16.
 */
public class ORMUser {

	private static final String TAG = "ORMUser";

	public static final String	TABLE_NAME = 				"users";

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
		Log.d(TAG, "Cleared HHUser table");

		database.close();
	}

	private static ContentValues userToContentValues(HHUser user){
		ContentValues contentValues = new ContentValues();
		contentValues.put(COLUMN_ID_NAME, 			user.getID());
		contentValues.put(COLUMN_FIRST_NAME_NAME, 	user.getFirstName());
		contentValues.put(COLUMN_LAST_NAME_NAME, 	user.getLastName());
		contentValues.put(COLUMN_EMAIL_NAME, user.getEmail());
		contentValues.put(COLUMN_FB_USER_ID_NAME, user.getFBUserID());
		contentValues.put(COLUMN_CREATED_AT_NAME, String.valueOf(user.getCreatedAt()));
		contentValues.put(COLUMN_UPDATED_AT_NAME, String.valueOf(user.getUpdatedAt()));
		return contentValues;
	}

	public static void insertCurrentUser(Context context, HHUserFullProcess user, DBUserInsertCurrentTask.DBUserInsertCurrentTaskCallback callbackTo){
		new DBUserInsertCurrentTask(context, user, callbackTo).execute();
	}

	public static class DBUserInsertCurrentTask extends AsyncTask<Void, Void, Long> {

		private Context context;
		private HHUserFullProcess user;
		private DBUserInsertCurrentTaskCallback callbackTo;

		public interface DBUserInsertCurrentTaskCallback {
			void returnInsertedUser(long userID, HHUserFullProcess returnedUser);
		}

		public DBUserInsertCurrentTask(Context context, HHUserFullProcess user, DBUserInsertCurrentTaskCallback callbackTo){
			this.context = context;
			this.user = user;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Long doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			ContentValues values = userToContentValues(user.getUser());
			long userID = database.insertWithOnConflict(TABLE_NAME, "null", values,
														SQLiteDatabase.CONFLICT_REPLACE);
			Log.d(TAG, "Inserted new HHUser with ID:" + userID);

			List<HHFriendshipUser> friendships = user.getFriendships();
			for (int j = 0; j < friendships.size(); j++){
				HHFriendshipUser friendship = friendships.get(j);
				values = userToContentValues(friendship.getUser());
				userID = database.insertWithOnConflict(TABLE_NAME, "null", values, SQLiteDatabase.CONFLICT_REPLACE);
				Log.d(TAG, "Inserted new HHUser with ID:" + userID);
			}

			database.close();

			user.setUserProcessed(true);

			return userID;
		}

		@Override
		protected void onPostExecute(Long userID){
			callbackTo.returnInsertedUser(userID, user);
		}
	}

	public static void insertUsersFromPosts(Context context, List<HHPostFullProcess> posts, DBUserInsertManyFromPostsTask.DBUserInsertManyFromPostsTaskCallback callbackTo){
		new DBUserInsertManyFromPostsTask(context, posts, callbackTo).execute();
	}

	public static class DBUserInsertManyFromPostsTask extends AsyncTask<Void, Void, Boolean> {

		private Context context;
		private List<HHPostFullProcess> posts;
		private DBUserInsertManyFromPostsTaskCallback callbackTo;

		public interface DBUserInsertManyFromPostsTaskCallback {
			void returnInsertedManyUsers(List<HHPostFullProcess> postsToProcess);
		}

		public DBUserInsertManyFromPostsTask(Context context, List<HHPostFullProcess> posts, DBUserInsertManyFromPostsTaskCallback callbackTo){
			this.context = context;
			this.posts = posts;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			long userID;
			for (int i = 0; i < posts.size(); i++) {
				HHPostFullProcess post = posts.get(i);
				ContentValues values = userToContentValues(post.getUser());
				userID = database.insertWithOnConflict(TABLE_NAME, "null", values,
													   SQLiteDatabase.CONFLICT_REPLACE);
				Log.d(TAG, "Inserted new HHUser with ID:" + userID);

				List<HHCommentUser> comments = post.getComments();
				for (int j = 0; j < comments.size(); j++){
					HHCommentUser comment = comments.get(j);
					values = userToContentValues(comment.getUser());
					userID = database.insertWithOnConflict(TABLE_NAME, "null", values, SQLiteDatabase.CONFLICT_REPLACE);
					Log.d(TAG, "Inserted new HHUser with ID:" + userID);
				}

				List<HHLikeUser> likes = post.getLikes();
				for (int j = 0; j < likes.size(); j++){
					HHLikeUser likeUser = likes.get(j);
					values = userToContentValues(likeUser.getUser());
					userID = database.insertWithOnConflict(TABLE_NAME, "null", values, SQLiteDatabase.CONFLICT_REPLACE);
					Log.d(TAG, "Inserted new HHUser with ID:" + userID);
				}

				List<HHTagUser> tags = post.getTags();
				for (int j = 0; j < tags.size(); j++){
					HHTagUser tagUser = tags.get(j);
					values = userToContentValues(tagUser.getUser());
					userID = database.insertWithOnConflict(TABLE_NAME, "null", values, SQLiteDatabase.CONFLICT_REPLACE);
					Log.d(TAG, "Inserted new HHUser with ID:" + userID);
				}

				post.setUsersProcessed(true);
			}

			database.close();

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result){
			callbackTo.returnInsertedManyUsers(posts);
		}
	}

}
