package yosoyo.aaahearhereprototype.HHServerClasses.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.HHServerClasses.HHCommentUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHLikeUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHPostFull;
import yosoyo.aaahearhereprototype.HHServerClasses.HHTagUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUser;

/**
 * Created by adam on 02/03/16.
 */
class ORMPostFull {

	private static final String TAG = "ORMPostFull";

	public static void getAllPosts(Context context, DBPostFullSelectAllTask.Callback callbackTo){
		new DBPostFullSelectAllTask(context, callbackTo).execute();
	}

	public static class DBPostFullSelectAllTask extends AsyncTask<Void, Void, List<HHPostFull> > {

		private final Context context;
		private final Callback callbackTo;

		public interface Callback {
			void returnPosts(List<HHPostFull> posts);
		}

		public DBPostFullSelectAllTask(Context context, Callback callbackTo){
			this.context = context;
			this.callbackTo = callbackTo;
		}

		@Override
		protected List<HHPostFull> doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getReadableDatabase();

			Cursor cursorFollows = database.rawQuery(
				"SELECT " + ORMFollow.COLUMN_FOLLOWED_USER_ID_NAME + " FROM " + ORMFollow.TABLE_NAME
					+ " WHERE " + ORMFollow.COLUMN_USER_ID_NAME + "=?"
				, new String[]{String.valueOf(HHUser.getCurrentUserID())});

			StringBuilder followIDs = new StringBuilder(String.valueOf(HHUser.getCurrentUserID())).append(",");
			int numFollows = cursorFollows.getCount();
			if (numFollows > 0){
				cursorFollows.moveToFirst();
				while (!cursorFollows.isAfterLast()){
					long id = cursorFollows.getLong(cursorFollows.getColumnIndex(ORMFollow.COLUMN_FOLLOWED_USER_ID_NAME));
					followIDs.append(id).append(",");
					cursorFollows.moveToNext();
				}
			} else {
				return new ArrayList<>();
			}
			String followsInArray = "(" + followIDs.substring(0, followIDs.length()-1) + ")";

			Cursor cursorPost = database.rawQuery(
				"SELECT * FROM " + ORMCachedSpotifyTrack.TABLE_NAME
					+ " INNER JOIN ("
					+ ORMPost.TABLE_NAME + " LEFT JOIN " + ORMUser.TABLE_NAME
					+ " ON " + ORMPost.TABLE_NAME + "." + ORMPost.COLUMN_USER_ID_NAME + " = " + ORMUser.TABLE_NAME + "." + ORMUser.COLUMN_ID_NAME
					+ ") ON " + ORMCachedSpotifyTrack.TABLE_NAME + "." + ORMCachedSpotifyTrack.COLUMN_TRACK_ID_NAME + " = " + ORMPost.TABLE_NAME + "." + ORMPost.COLUMN_TRACK_NAME
					+ " WHERE " + ORMPost.TABLE_NAME + "." + ORMPost.COLUMN_USER_ID_NAME + " IN " + followsInArray
				, null);

			int numPosts = cursorPost.getCount();
			Log.d(TAG, "Loaded " + numPosts + " Posts...");
			List<HHPostFull> posts = new ArrayList<>(numPosts);

			if (numPosts > 0){
				cursorPost.moveToFirst();
				while (!cursorPost.isAfterLast()){
					HHPostFull post = new HHPostFull(cursorPost);
					posts.add(post);

					{
						Cursor cursorComment = database.rawQuery(
							"SELECT * FROM " + ORMComment.TABLE_NAME
								+ " LEFT JOIN " + ORMUser.TABLE_NAME
								+ " ON " + ORMComment.TABLE_NAME + "." + ORMComment.COLUMN_USER_ID_NAME + " = " + ORMUser.TABLE_NAME + "." + ORMUser.COLUMN_ID_NAME
								+ " WHERE " + ORMComment.TABLE_NAME + "." + ORMComment.COLUMN_POST_ID_NAME + " = ?"
							, new String[]{String.valueOf(post.getPost().getID())});

						int numComments = cursorComment.getCount();
						Log.d(TAG, "Loaded " + numComments + " Comments...");
						List<HHCommentUser> comments = new ArrayList<>(numComments);
						if (numComments > 0) {
							cursorComment.moveToFirst();
							while (!cursorComment.isAfterLast()) {
								HHCommentUser commentUser = new HHCommentUser(
									cursorComment);
								comments.add(commentUser);
								cursorComment.moveToNext();
							}
						}
						cursorComment.close();
						post.setComments(comments);
					}

					{
						Cursor cursorLike = database.rawQuery(
							"SELECT * FROM " + ORMLike.TABLE_NAME
								+ " LEFT JOIN " + ORMUser.TABLE_NAME
								+ " ON " + ORMLike.TABLE_NAME + "." + ORMLike.COLUMN_USER_ID_NAME + " = " + ORMUser.TABLE_NAME + "." + ORMUser.COLUMN_ID_NAME
								+ " WHERE " + ORMLike.TABLE_NAME + "." + ORMLike.COLUMN_POST_ID_NAME + " = ?"
							, new String[]{String.valueOf(post.getPost().getID())});

						int numLikes = cursorLike.getCount();
						Log.d(TAG, "Loaded " + numLikes + " Likes...");
						List<HHLikeUser> likes = new ArrayList<>(numLikes);
						if (numLikes > 0) {
							cursorLike.moveToFirst();
							while (!cursorLike.isAfterLast()) {
								HHLikeUser likeUser = new HHLikeUser(cursorLike);
								likes.add(likeUser);
								cursorLike.moveToNext();
							}
						}
						cursorLike.close();
						post.setLikes(likes);
					}

					{
						Cursor cursorTag = database.rawQuery(
							"SELECT * FROM " + ORMTag.TABLE_NAME
								+ " LEFT JOIN " + ORMUser.TABLE_NAME
								+ " ON " + ORMTag.TABLE_NAME + "." + ORMTag.COLUMN_USER_ID_NAME + " = " + ORMUser.TABLE_NAME + "." + ORMUser.COLUMN_ID_NAME
								+ " WHERE " + ORMTag.TABLE_NAME + "." + ORMTag.COLUMN_POST_ID_NAME + " = ?"
							, new String[]{String.valueOf(post.getPost().getID())});

						int numTags = cursorTag.getCount();
						Log.d(TAG, "Loaded " + numTags + " Tags...");
						List<HHTagUser> tags = new ArrayList<>();
						if (numTags > 0) {
							cursorTag.moveToFirst();
							while (!cursorTag.isAfterLast()) {
								HHTagUser tagUser = new HHTagUser(cursorTag);
								tags.add(tagUser);
								cursorTag.moveToNext();
							}
						}
						cursorTag.close();
						post.setTags(tags);
					}

					cursorPost.moveToNext();
				}
				Log.d(TAG, "PostUsers loaded successfully");
			}

			cursorPost.close();
			database.close();

			return posts;
		}

		@Override
		protected void onPostExecute(List<HHPostFull> posts){
			callbackTo.returnPosts(posts);
		}
	}

	public static void getUserPosts(Context context, long userID, DBPostFullSelectUserTask.Callback callbackTo){
		new DBPostFullSelectUserTask(context, userID, callbackTo).execute();
	}

	public static class DBPostFullSelectUserTask extends AsyncTask<Void, Void, List<HHPostFull> > {

		private final Context context;
		private final long userID;
		private final Callback callbackTo;

		public interface Callback {
			void returnPosts(List<HHPostFull> posts);
		}

		public DBPostFullSelectUserTask(Context context, long userID, Callback callbackTo){
			this.context = context;
			this.userID = userID;
			this.callbackTo = callbackTo;
		}

		@Override
		protected List<HHPostFull> doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getReadableDatabase();

			Cursor cursorPost = database.rawQuery(
				"SELECT * FROM " + ORMCachedSpotifyTrack.TABLE_NAME
					+ " INNER JOIN ("
					+ ORMPost.TABLE_NAME + " LEFT JOIN " + ORMUser.TABLE_NAME
					+ " ON " + ORMPost.TABLE_NAME + "." + ORMPost.COLUMN_USER_ID_NAME + " = " + ORMUser.TABLE_NAME + "." + ORMUser.COLUMN_ID_NAME
					+ ") ON " + ORMCachedSpotifyTrack.TABLE_NAME + "." + ORMCachedSpotifyTrack.COLUMN_TRACK_ID_NAME + " = " + ORMPost.TABLE_NAME + "." + ORMPost.COLUMN_TRACK_NAME
					+ " WHERE " + ORMPost.COLUMN_USER_ID_NAME + "=?"
				, new String[]{String.valueOf(userID)});

			int numPosts = cursorPost.getCount();
			Log.d(TAG, "Loaded " + numPosts + " Posts...");
			List<HHPostFull> posts = new ArrayList<>(numPosts);

			if (numPosts > 0){
				cursorPost.moveToFirst();
				while (!cursorPost.isAfterLast()){
					HHPostFull post = new HHPostFull(cursorPost);
					posts.add(post);

					{
						Cursor cursorComment = database.rawQuery(
							"SELECT * FROM " + ORMComment.TABLE_NAME
								+ " LEFT JOIN " + ORMUser.TABLE_NAME
								+ " ON " + ORMComment.TABLE_NAME + "." + ORMComment.COLUMN_USER_ID_NAME + " = " + ORMUser.TABLE_NAME + "." + ORMUser.COLUMN_ID_NAME
								+ " WHERE " + ORMComment.TABLE_NAME + "." + ORMComment.COLUMN_POST_ID_NAME + " = ?"
							, new String[]{String.valueOf(post.getPost().getID())});

						int numComments = cursorComment.getCount();
						Log.d(TAG, "Loaded " + numComments + " Comments...");
						List<HHCommentUser> comments = new ArrayList<>(numComments);
						if (numComments > 0) {
							cursorComment.moveToFirst();
							while (!cursorComment.isAfterLast()) {
								HHCommentUser commentUser = new HHCommentUser(
									cursorComment);
								comments.add(commentUser);
								cursorComment.moveToNext();
							}
						}
						cursorComment.close();
						post.setComments(comments);
					}

					{
						Cursor cursorLike = database.rawQuery(
							"SELECT * FROM " + ORMLike.TABLE_NAME
								+ " LEFT JOIN " + ORMUser.TABLE_NAME
								+ " ON " + ORMLike.TABLE_NAME + "." + ORMLike.COLUMN_USER_ID_NAME + " = " + ORMUser.TABLE_NAME + "." + ORMUser.COLUMN_ID_NAME
								+ " WHERE " + ORMLike.TABLE_NAME + "." + ORMLike.COLUMN_POST_ID_NAME + " = ?"
							, new String[]{String.valueOf(post.getPost().getID())});

						int numLikes = cursorLike.getCount();
						Log.d(TAG, "Loaded " + numLikes + " Likes...");
						List<HHLikeUser> likes = new ArrayList<>(numLikes);
						if (numLikes > 0) {
							cursorLike.moveToFirst();
							while (!cursorLike.isAfterLast()) {
								HHLikeUser likeUser = new HHLikeUser(cursorLike);
								likes.add(likeUser);
								cursorLike.moveToNext();
							}
						}
						cursorLike.close();
						post.setLikes(likes);
					}

					{
						Cursor cursorTag = database.rawQuery(
							"SELECT * FROM " + ORMTag.TABLE_NAME
								+ " LEFT JOIN " + ORMUser.TABLE_NAME
								+ " ON " + ORMTag.TABLE_NAME + "." + ORMTag.COLUMN_USER_ID_NAME + " = " + ORMUser.TABLE_NAME + "." + ORMUser.COLUMN_ID_NAME
								+ " WHERE " + ORMTag.TABLE_NAME + "." + ORMTag.COLUMN_POST_ID_NAME + " = ?"
							, new String[]{String.valueOf(post.getPost().getID())});

						int numTags = cursorTag.getCount();
						Log.d(TAG, "Loaded " + numTags + " Tags...");
						List<HHTagUser> tags = new ArrayList<>();
						if (numTags > 0) {
							cursorTag.moveToFirst();
							while (!cursorTag.isAfterLast()) {
								HHTagUser tagUser = new HHTagUser(cursorTag);
								tags.add(tagUser);
								cursorTag.moveToNext();
							}
						}
						cursorTag.close();
						post.setTags(tags);
					}

					cursorPost.moveToNext();
				}
				Log.d(TAG, "PostUsers loaded successfully");
			}

			cursorPost.close();
			database.close();

			return posts;
		}

		@Override
		protected void onPostExecute(List<HHPostFull> posts){
			callbackTo.returnPosts(posts);
		}
	}

	public static void getPostsAtLocation(Context context, Location location, DBPostSelectAtLocationTask.Callback callbackTo){
		new DBPostSelectAtLocationTask(context, location, callbackTo).execute();
	}

	public static class DBPostSelectAtLocationTask extends AsyncTask<Void, Void, List<HHPostFull> > {

		private final Context context;
		private final Location location;
		private final Callback callbackTo;
		private static final double RANGE = 0.001;

		public interface Callback {
			void returnPosts(List<HHPostFull> posts);
		}

		public DBPostSelectAtLocationTask(Context context, Location location, Callback callbackTo){
			this.context = context;
			this.location = location;
			this.callbackTo = callbackTo;
		}

		@Override
		protected List<HHPostFull> doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);

			if (databaseHelper == null || context == null)
				return null;

			SQLiteDatabase database = databaseHelper.getReadableDatabase();

			Cursor cursor = database.rawQuery(
				"SELECT * FROM " + ORMCachedSpotifyTrack.TABLE_NAME
					+ " INNER JOIN ("
					+ ORMPost.TABLE_NAME + " LEFT JOIN " + ORMUser.TABLE_NAME
					+ " ON " + ORMPost.TABLE_NAME + "." + ORMPost.COLUMN_USER_ID_NAME + " = " + ORMUser.TABLE_NAME + "." + ORMUser.COLUMN_ID_NAME
					+ ") ON " + ORMCachedSpotifyTrack.TABLE_NAME + "." + ORMCachedSpotifyTrack.COLUMN_TRACK_ID_NAME + " = " + ORMPost.TABLE_NAME + "." + ORMPost.COLUMN_TRACK_NAME
					+ " WHERE " + ORMPost.COLUMN_LAT_NAME + ">? AND " + ORMPost.COLUMN_LAT_NAME + "<?"
					+ " AND " + ORMPost.COLUMN_LON_NAME + ">? AND " + ORMPost.COLUMN_LON_NAME + "<?",
				new String[]{
					String.valueOf(location.getLatitude() - RANGE),
					String.valueOf(location.getLatitude() + RANGE),
					String.valueOf(location.getLongitude() - RANGE),
					String.valueOf(location.getLongitude() + RANGE)
				});

			int numPosts = cursor.getCount();
			Log.d(TAG, "Loaded " + numPosts + " Posts...");
			List<HHPostFull> posts = new ArrayList<>(numPosts);

			if (numPosts > 0){
				cursor.moveToFirst();
				while (!cursor.isAfterLast()){
					HHPostFull post = new HHPostFull(cursor);
					posts.add(post);
					cursor.moveToNext();
				}
				Log.d(TAG, "Posts loaded successfully");
			}

			cursor.close();
			database.close();

			return posts;
		}

		@Override
		protected void onPostExecute(List<HHPostFull> posts){
			callbackTo.returnPosts(posts);
		}
	}

}
