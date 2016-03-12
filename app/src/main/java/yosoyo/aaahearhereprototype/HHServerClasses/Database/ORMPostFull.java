package yosoyo.aaahearhereprototype.HHServerClasses.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.HHServerClasses.HHCommentUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHLikeUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHPostFull;
import yosoyo.aaahearhereprototype.HHServerClasses.HHTagUser;

/**
 * Created by adam on 02/03/16.
 */
public class ORMPostFull {

	private static final String TAG = "ORMPostFull";

	public static void getAllPosts(Context context, DBPostFullSelectAllTask.DBPostFullSelectAllTaskCallback callbackTo){
		new DBPostFullSelectAllTask(context, callbackTo).execute();
	}

	public static class DBPostFullSelectAllTask extends AsyncTask<Void, Void, List<HHPostFull> > {

		private Context context;
		private DBPostFullSelectAllTaskCallback callbackTo;

		public interface DBPostFullSelectAllTaskCallback {
			void returnPosts(List<HHPostFull> posts);
		}

		public DBPostFullSelectAllTask(Context context, DBPostFullSelectAllTaskCallback callbackTo){
			this.context = context;
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

}
