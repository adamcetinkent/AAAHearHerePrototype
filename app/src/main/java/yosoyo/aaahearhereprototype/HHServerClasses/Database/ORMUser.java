package yosoyo.aaahearhereprototype.HHServerClasses.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.HashSet;
import java.util.List;

import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHCommentUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequestUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFriendshipUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHLikeUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPostFullProcess;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHTagUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUserFullProcess;

/**
 * Created by adam on 02/03/16.
 */
public class ORMUser {

	private static final String TAG = "ORMUser";

	private static final String	TABLE_NAME = 					"users";
	public static String TABLE() {return TABLE_NAME;}

	private static final String	COMMA_SEP = 					", ";

	private static final String	COLUMN_ID_NAME = 				"_id";
	private static final String	COLUMN_ID_TYPE = 				"INTEGER PRIMARY KEY";
	public static String ID() { return COLUMN_ID_NAME; }
	public static String dotID() { return TABLE_NAME + "." + COLUMN_ID_NAME; }

	private static final String	COLUMN_FIRST_NAME_NAME =		"first_name";
	private static final String	COLUMN_FIRST_NAME_TYPE =		"TEXT";
	public static String FIRST_NAME() { return COLUMN_FIRST_NAME_NAME; }
	public static String dotFIRST_NAME() { return TABLE_NAME + "." + COLUMN_FIRST_NAME_NAME; }

	private static final String	COLUMN_LAST_NAME_NAME =			"last_name";
	private static final String	COLUMN_LAST_NAME_TYPE =			"TEXT";
	public static String LAST_NAME() { return COLUMN_LAST_NAME_NAME; }
	public static String dotLAST_NAME() { return TABLE_NAME + "." + COLUMN_LAST_NAME_NAME; }

	private static final String	COLUMN_EMAIL_NAME = 			"email";
	private static final String	COLUMN_EMAIL_TYPE = 			"TEXT";
	public static String EMAIL() { return COLUMN_EMAIL_NAME; }
	public static String dotEMAIL() { return TABLE_NAME + "." + COLUMN_EMAIL_NAME; }

	private static final String	COLUMN_FB_USER_ID_NAME = 		"fb_user_id";
	private static final String	COLUMN_FB_USER_ID_TYPE = 		"TEXT";
	public static String FB_USER_ID() { return COLUMN_FB_USER_ID_NAME; }
	public static String dotFB_USER_ID() { return TABLE_NAME + "." + COLUMN_FB_USER_ID_NAME; }

	private static final String	COLUMN_PROFILE_PRIVACY_NAME =	"profile_privacy";
	private static final String	COLUMN_PROFILE_PRIVACY_TYPE =	"INTEGER";
	public static String PROFILE_PRIVACY() { return COLUMN_PROFILE_PRIVACY_NAME; }
	public static String dotPROFILE_PRIVACY() { return TABLE_NAME + "." + COLUMN_PROFILE_PRIVACY_NAME; }

	private static final String	COLUMN_SEARCH_PRIVACY_NAME = 	"search_privacy";
	private static final String	COLUMN_SEARCH_PRIVACY_TYPE = 	"INTEGER";
	public static String SEARCH_PRIVACY() { return COLUMN_SEARCH_PRIVACY_NAME; }
	public static String dotSEARCH_PRIVACY() { return TABLE_NAME + "." + COLUMN_SEARCH_PRIVACY_NAME; }

	private static final String	COLUMN_AUTO_ACCEPT_NAME = 		"auto_accept";
	private static final String	COLUMN_AUTO_ACCEPT_TYPE = 		"INTEGER";
	public static String AUTO_ACCEPT() { return COLUMN_AUTO_ACCEPT_NAME; }
	public static String dotAUTO_ACCEPT() { return TABLE_NAME + "." + COLUMN_AUTO_ACCEPT_NAME; }

	private static final String	COLUMN_BIO_NAME = 				"bio";
	private static final String	COLUMN_BIO_TYPE = 				"TEXT";
	public static String BIO() { return COLUMN_BIO_NAME; }
	public static String dotBIO() { return TABLE_NAME + "." + COLUMN_BIO_NAME; }

	private static final String	COLUMN_URL_NAME = 				"url";
	private static final String	COLUMN_URL_TYPE = 				"TEXT";
	public static String URL() { return COLUMN_URL_NAME; }
	public static String dotURL() { return TABLE_NAME + "." + COLUMN_URL_NAME; }

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
		COLUMN_ID_NAME 				+ " " 	+ COLUMN_ID_TYPE 				+ COMMA_SEP	+
		COLUMN_FIRST_NAME_NAME 		+ " " 	+ COLUMN_FIRST_NAME_TYPE 		+ COMMA_SEP	+
		COLUMN_LAST_NAME_NAME 		+ " " 	+ COLUMN_LAST_NAME_TYPE 		+ COMMA_SEP	+
		COLUMN_EMAIL_NAME 			+ " " 	+ COLUMN_EMAIL_TYPE 			+ COMMA_SEP	+
		COLUMN_FB_USER_ID_NAME 		+ " " 	+ COLUMN_FB_USER_ID_TYPE 		+ COMMA_SEP	+
		COLUMN_BIO_NAME 			+ " " 	+ COLUMN_BIO_TYPE 				+ COMMA_SEP	+
		COLUMN_URL_NAME 			+ " " 	+ COLUMN_URL_TYPE 				+ COMMA_SEP	+
		COLUMN_AUTO_ACCEPT_NAME 	+ " " 	+ COLUMN_AUTO_ACCEPT_TYPE 		+ COMMA_SEP	+
		COLUMN_PROFILE_PRIVACY_NAME	+ " " 	+ COLUMN_PROFILE_PRIVACY_TYPE	+ COMMA_SEP	+
		COLUMN_SEARCH_PRIVACY_NAME	+ " "	+ COLUMN_SEARCH_PRIVACY_TYPE 	+ COMMA_SEP	+
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
		Log.d(TAG, "Cleared HHUser table");

		database.close();
	}

	private static ContentValues userToContentValues(HHUser user){
		ContentValues contentValues = new ContentValues();
		contentValues.put(COLUMN_ID_NAME, 				user.getID());
		contentValues.put(COLUMN_FIRST_NAME_NAME, 		user.getFirstName());
		contentValues.put(COLUMN_LAST_NAME_NAME, 		user.getLastName());
		contentValues.put(COLUMN_FB_USER_ID_NAME, 		user.getFBUserID());
		contentValues.put(COLUMN_EMAIL_NAME, 			user.getEmail());
		contentValues.put(COLUMN_BIO_NAME, 				user.getBio());
		contentValues.put(COLUMN_URL_NAME, 				user.getURL());
		contentValues.put(COLUMN_PROFILE_PRIVACY_NAME,	user.getProfilePrivacy());
		contentValues.put(COLUMN_SEARCH_PRIVACY_NAME, user.getSearchPrivacy());
		contentValues.put(COLUMN_AUTO_ACCEPT_NAME, user.getAutoAccept());
		contentValues.put(COLUMN_CREATED_AT_NAME, String.valueOf(user.getCreatedAt()));
		contentValues.put(COLUMN_UPDATED_AT_NAME, String.valueOf(user.getUpdatedAt()));
		return contentValues;
	}


	public static void insertUsersFromUser(Context context, HHUserFullProcess user, DBUserInsertManyFromUserTask.Callback callbackTo){
		new DBUserInsertManyFromUserTask(context, user, callbackTo).execute();
	}

	public static class DBUserInsertManyFromUserTask extends AsyncTask<Void, Void, Long> {

		private final Context context;
		private final HHUserFullProcess user;
		private final Callback callbackTo;

		public interface Callback {
			void returnInsertedManyFromUser(long userID, HHUserFullProcess returnedUser);
		}

		public DBUserInsertManyFromUserTask(Context context, HHUserFullProcess user, Callback callbackTo){
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

			HashSet<HHUser> userSet = new HashSet<>();

			for (HHFriendshipUser friendship : user.getFriendships()){
				userSet.add(friendship.getUser());
			}
			for (HHFollowUser follow : user.getFollowOuts()){
				userSet.add(follow.getUser());
			}
			for (HHFollowUser follow : user.getFollowIns()){
				userSet.add(follow.getUser());
			}
			for (HHFollowRequestUser followRequest : user.getFollowOutRequests()){
				userSet.add(followRequest.getUser());
			}
			for (HHFollowRequestUser followRequest : user.getFollowInRequests()){
				userSet.add(followRequest.getUser());
			}

			long subUserID;
			for (HHUser user : userSet){
				values = userToContentValues(user);
				subUserID = database.insertWithOnConflict(TABLE_NAME, "null", values, SQLiteDatabase.CONFLICT_REPLACE);
				Log.d(TAG, "Inserted new HHUser with ID:" + subUserID);
			}

			user.setUserProcessed(true);

			database.close();

			return userID;
		}

		@Override
		protected void onPostExecute(Long userID){
			callbackTo.returnInsertedManyFromUser(userID, user);
		}
	}

	public static void insertUsersFromPosts(Context context, List<HHPostFullProcess> posts, DBUserInsertManyFromPostsTask.Callback callbackTo){
		new DBUserInsertManyFromPostsTask(context, posts, callbackTo).execute();
	}

	public static class DBUserInsertManyFromPostsTask extends AsyncTask<Void, Void, Boolean> {

		private final Context context;
		private final List<HHPostFullProcess> posts;
		private final Callback callbackTo;

		public interface Callback {
			void returnInsertedManyUsers(List<HHPostFullProcess> postsToProcess);
		}

		public DBUserInsertManyFromPostsTask(Context context, List<HHPostFullProcess> posts, Callback callbackTo){
			this.context = context;
			this.posts = posts;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			long userID;
			ContentValues values;
			for (int i = 0; i < posts.size(); i++) {
				HHPostFullProcess post = posts.get(i);

				HashSet<HHUser> userSet = new HashSet<>();
				userSet.add(post.getUser());

				for (HHCommentUser comment : post.getComments()){
					userSet.add(comment.getUser());
				}
				for (HHLikeUser like : post.getLikes()){
					userSet.add(like.getUser());
				}
				for (HHTagUser tag : post.getTags()){
					userSet.add(tag.getUser());
				}

				for (HHUser user : userSet){
					values = userToContentValues(user);
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

	public static void getUserPrivacy(Context context, final long userID, DBUserPrivacyTask.Callback callbackTo){
		new DBUserPrivacyTask(context, userID, callbackTo).execute();
	}

	static class DBUserPrivacyTask extends AsyncTask<Void, Void, Boolean> {

		private final Context context;
		private final long userID;
		private final Callback callbackTo;

		public interface Callback {
			void returnUserPrivacy(final boolean userPrivacy);
		}

		public DBUserPrivacyTask(Context context, final long userID, Callback callbackTo){
			this.context = context;
			this.userID = userID;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getReadableDatabase();

			Cursor cursor = database.rawQuery(
				"SELECT ? FROM " + TABLE_NAME +
					" WHERE " + COLUMN_ID_NAME + "=?"
				, new String[]{
					COLUMN_PROFILE_PRIVACY_NAME,
					String.valueOf(userID)
				});

			int privacyLevel = cursor.getInt(cursor.getColumnIndex(COLUMN_PROFILE_PRIVACY_NAME));

			cursor.close();
			database.close();

			if (privacyLevel == 0) {
				return true;
			} else if (privacyLevel == 1){
				if (HHUser.userIsFollowed(userID) || HHUser.userIsFriend(userID))
					return true;
			} else if (privacyLevel == 2){
				if (HHUser.userIsFollowed(userID))
					return true;
			}

			return false;
		}

		@Override
		protected void onPostExecute(Boolean userPrivacy){
			callbackTo.returnUserPrivacy(userPrivacy);
		}
	}

}
