package yosoyo.aaahearhereprototype.TestServerClasses.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.TestServerClasses.TestCommentUser;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPostFull;

/**
 * Created by adam on 02/03/16.
 */
public class ORMTestPostFull {

	private static final String TAG = "ORMTestPostFull";

	public static void getAllPosts(Context context, DBTestPostFullSelectAllTask.DBTestPostFullSelectAllTaskCallback callbackTo){
		new DBTestPostFullSelectAllTask(context, callbackTo).execute();
	}

	public static class DBTestPostFullSelectAllTask extends AsyncTask<Void, Void, List<TestPostFull> > {

		private Context context;
		private DBTestPostFullSelectAllTaskCallback callbackTo;

		public interface DBTestPostFullSelectAllTaskCallback {
			void returnTestPosts(List<TestPostFull> testPosts);
		}

		public DBTestPostFullSelectAllTask(Context context, DBTestPostFullSelectAllTaskCallback callbackTo){
			this.context = context;
			this.callbackTo = callbackTo;
		}

		@Override
		protected List<TestPostFull> doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getReadableDatabase();

			Cursor cursorPost = database.rawQuery(
				"SELECT * FROM " + ORMCachedSpotifyTrack.TABLE_NAME
					+ " INNER JOIN ("
						+ ORMTestPost.TABLE_NAME + " LEFT JOIN " + ORMTestUser.TABLE_NAME
						+ " ON " + ORMTestPost.TABLE_NAME + "." + ORMTestPost.COLUMN_USER_ID_NAME + " = " + ORMTestUser.TABLE_NAME + "." + ORMTestUser.COLUMN_ID_NAME
					+ ") ON " + ORMCachedSpotifyTrack.TABLE_NAME + "." + ORMCachedSpotifyTrack.COLUMN_TRACK_ID_NAME + " = " + ORMTestPost.TABLE_NAME + "." + ORMTestPost.COLUMN_TRACK_NAME
				, null);

			int numTracks = cursorPost.getCount();
			Log.d(TAG, "Loaded " + numTracks + " TestPosts...");
			List<TestPostFull> testPosts = new ArrayList<>(numTracks);

			if (numTracks > 0){
				cursorPost.moveToFirst();
				while (!cursorPost.isAfterLast()){
					TestPostFull testPost = new TestPostFull(cursorPost);
					testPosts.add(testPost);

					Cursor cursorComment = database.rawQuery(
						"SELECT * FROM " + ORMTestComment.TABLE_NAME
							+ " LEFT JOIN " + ORMTestUser.TABLE_NAME
							+ " ON " + ORMTestComment.TABLE_NAME + "." + ORMTestComment.COLUMN_USER_ID_NAME + " = " + ORMTestUser.TABLE_NAME + "." + ORMTestUser.COLUMN_ID_NAME
							+ " WHERE " + ORMTestComment.TABLE_NAME + "." + ORMTestComment.COLUMN_POST_ID_NAME + " = ?"
						, new String[]{String.valueOf(testPost.getPost().getID())});

					int numComments = cursorComment.getCount();
					Log.d(TAG, "Loaded " + numComments + " TestComments...");
					List<TestCommentUser> testComments = new ArrayList<>(numComments);
					if (numComments > 0){
						cursorComment.moveToFirst();
						while (!cursorComment.isAfterLast()){
							TestCommentUser testCommentUser = new TestCommentUser(cursorComment);
							testComments.add(testCommentUser);
							cursorComment.moveToNext();
						}
					}
					cursorComment.close();
					testPost.setComments(testComments);

					cursorPost.moveToNext();
				}
				Log.d(TAG, "TestPostUsers loaded successfully");
			}

			cursorPost.close();
			database.close();

			return testPosts;
		}

		@Override
		protected void onPostExecute(List<TestPostFull> testPosts){
			callbackTo.returnTestPosts(testPosts);
		}
	}

}
