package yosoyo.aaahearhereprototype.HHServerClasses.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequestUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFriendshipUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUserFull;

/**
 * Created by adam on 02/03/16.
 */
class ORMUserFull {

	private static final String TAG = "ORMUserFull";

	public static void getUser(final Context context, final long userID, DBGetUserTask.Callback callback){
		new DBGetUserTask(context, userID, callback).execute();
	}

	public static class DBGetUserTask extends AsyncTask<Void, Void, HHUserFull> {
		private final Context context;
		private final long userID;
		private final Callback callbackTo;

		public interface Callback {
			void returnGetUser(HHUserFull user);
		}

		public DBGetUserTask(Context context, long userID, Callback callbackTo){
			this.context = context;
			this.userID = userID;
			this.callbackTo = callbackTo;
		}

		@Override
		protected HHUserFull doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getReadableDatabase();

			Cursor cursorUser = database.rawQuery("SELECT * FROM " + ORMUser.TABLE_NAME +
													  " WHERE " + ORMUser.COLUMN_ID_NAME + "=?",
												  new String[]{String.valueOf(userID)});

			int numUsers = cursorUser.getCount();
			Log.d(TAG, "Loaded " + numUsers + " Users...");

			HHUserFull user = null;
			if (numUsers > 0){
				cursorUser.moveToFirst();
				user = new HHUserFull(cursorUser, ORMUser.COLUMN_ID_NAME);
				Log.d(TAG, "User loaded successfully");

				{
					Cursor cursorFriends = database.rawQuery(
						"SELECT * FROM " + ORMFriendship.TABLE_NAME
							+ " LEFT JOIN " + ORMUser.TABLE_NAME
							+ " ON " + ORMFriendship.TABLE_NAME + "." + ORMFriendship.COLUMN_USER_ID_NAME + " = " + ORMUser.TABLE_NAME + "." + ORMUser.COLUMN_ID_NAME
							+ " WHERE " + ORMFriendship.TABLE_NAME + "." + ORMFriendship.COLUMN_USER_ID_NAME + " = ?"
						, new String[]{String.valueOf(userID)});

					int numFriends = cursorFriends.getCount();
					Log.d(TAG, "Loaded " + numFriends + " Friendships...");
					List<HHFriendshipUser> friendships = new ArrayList<>(numFriends);
					if (numFriends > 0) {
						cursorFriends.moveToFirst();
						while (!cursorFriends.isAfterLast()) {
							HHFriendshipUser friendshipUser = new HHFriendshipUser(cursorFriends, ORMFriendship.COLUMN_USER_ID_NAME);
							friendships.add(friendshipUser);
							cursorFriends.moveToNext();
						}
					}
					cursorFriends.close();
					user.setFriendships(friendships);
				}

				{
					Cursor cursorFollows = database.rawQuery(
						"SELECT * FROM " + ORMFollow.TABLE_NAME
							+ " LEFT JOIN " + ORMUser.TABLE_NAME
							+ " ON " + ORMFollow.TABLE_NAME + "." + ORMFollow.COLUMN_USER_ID_NAME + " = " + ORMUser.TABLE_NAME + "." + ORMUser.COLUMN_ID_NAME
							+ " WHERE " + ORMFollow.TABLE_NAME + "." + ORMFollow.COLUMN_USER_ID_NAME + " = ?"
						, new String[]{String.valueOf(userID)});

					int numFollows = cursorFollows.getCount();
					Log.d(TAG, "Loaded " + numFollows + " Follows...");
					List<HHFollowUser> follows = new ArrayList<>(numFollows);
					if (numFollows > 0) {
						cursorFollows.moveToFirst();
						while (!cursorFollows.isAfterLast()) {
							HHFollowUser followUser = new HHFollowUser(cursorFollows, ORMFollow.COLUMN_USER_ID_NAME);
							follows.add(followUser);
							cursorFollows.moveToNext();
						}
					}
					cursorFollows.close();
					user.setFollowOuts(follows);
				}

				{
					Cursor cursorFollows = database.rawQuery(
						"SELECT * FROM " + ORMFollow.TABLE_NAME
							+ " LEFT JOIN " + ORMUser.TABLE_NAME
							+ " ON " + ORMFollow.TABLE_NAME + "." + ORMFollow.COLUMN_FOLLOWED_USER_ID_NAME + " = " + ORMUser.TABLE_NAME + "." + ORMUser.COLUMN_ID_NAME
							+ " WHERE " + ORMFollow.TABLE_NAME + "." + ORMFollow.COLUMN_FOLLOWED_USER_ID_NAME + " = ?"
						, new String[]{String.valueOf(userID)});

					int numFollows = cursorFollows.getCount();
					Log.d(TAG, "Loaded " + numFollows + " Follows...");
					List<HHFollowUser> follows = new ArrayList<>(numFollows);
					if (numFollows > 0) {
						cursorFollows.moveToFirst();
						while (!cursorFollows.isAfterLast()) {
							HHFollowUser followUser = new HHFollowUser(cursorFollows, ORMFollow.COLUMN_FOLLOWED_USER_ID_NAME);
							follows.add(followUser);
							cursorFollows.moveToNext();
						}
					}
					cursorFollows.close();
					user.setFollowIns(follows);
				}

				{
					Cursor cursorFollowRequests = database.rawQuery(
						"SELECT * FROM " + ORMFollowRequest.TABLE_NAME
							+ " LEFT JOIN " + ORMUser.TABLE_NAME
							+ " ON " + ORMFollowRequest.TABLE_NAME + "." + ORMFollowRequest.COLUMN_USER_ID_NAME + " = " + ORMUser.TABLE_NAME + "." + ORMUser.COLUMN_ID_NAME
							+ " WHERE " + ORMFollowRequest.TABLE_NAME + "." + ORMFollowRequest.COLUMN_USER_ID_NAME + " = ?"
						, new String[]{String.valueOf(userID)});

					int numFollowRequests = cursorFollowRequests.getCount();
					Log.d(TAG, "Loaded " + numFollowRequests + " FollowRequests...");
					List<HHFollowRequestUser> followRequests = new ArrayList<>(numFollowRequests);
					if (numFollowRequests > 0) {
						cursorFollowRequests.moveToFirst();
						while (!cursorFollowRequests.isAfterLast()) {
							HHFollowRequestUser followRequestUser = new HHFollowRequestUser(cursorFollowRequests, ORMFollowRequest.COLUMN_USER_ID_NAME);
							followRequests.add(followRequestUser);
							cursorFollowRequests.moveToNext();
						}
					}
					cursorFollowRequests.close();
					user.setFollowOutRequests(followRequests);
				}

				{
					Cursor cursorFollowRequests = database.rawQuery(
						"SELECT * FROM " + ORMFollowRequest.TABLE_NAME
							+ " LEFT JOIN " + ORMUser.TABLE_NAME
							+ " ON " + ORMFollowRequest.TABLE_NAME + "." + ORMFollowRequest.COLUMN_REQUESTED_USER_ID_NAME + " = " + ORMUser.TABLE_NAME + "." + ORMUser.COLUMN_ID_NAME
							+ " WHERE " + ORMFollowRequest.TABLE_NAME + "." + ORMFollowRequest.COLUMN_REQUESTED_USER_ID_NAME + " = ?"
						, new String[]{String.valueOf(userID)});

					int numFollowRequests = cursorFollowRequests.getCount();
					Log.d(TAG, "Loaded " + numFollowRequests + " FollowRequests...");
					List<HHFollowRequestUser> followRequests = new ArrayList<>(numFollowRequests);
					if (numFollowRequests > 0) {
						cursorFollowRequests.moveToFirst();
						while (!cursorFollowRequests.isAfterLast()) {
							HHFollowRequestUser followRequestUser = new HHFollowRequestUser(cursorFollowRequests, ORMFollowRequest.COLUMN_REQUESTED_USER_ID_NAME);
							followRequests.add(followRequestUser);
							cursorFollowRequests.moveToNext();
						}
					}
					cursorFollowRequests.close();
					user.setFollowInRequests(followRequests);
				}

			}

			cursorUser.close();
			database.close();

			return user;
		}

		@Override
		protected void onPostExecute(HHUserFull user){
			callbackTo.returnGetUser(user);
		}
	}

}
