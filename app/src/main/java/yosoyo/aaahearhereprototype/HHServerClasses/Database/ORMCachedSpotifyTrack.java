package yosoyo.aaahearhereprototype.HHServerClasses.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.HHServerClasses.HHCachedSpotifyTrack;
import yosoyo.aaahearhereprototype.HHServerClasses.HHPostFullProcess;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;

/**
 * Created by adam on 23/02/16.
 */
public class ORMCachedSpotifyTrack {

	private static final String TAG = 						"ORMCachedSpotifyTrack";

	public static final String TABLE_NAME = 				"spotifytracks";

	private static final String COMMA_SEP = 				", ";

	private static final String COLUMN_ID_NAME = 			"_id";
	private static final String COLUMN_ID_TYPE = 			"INTEGER PRIMARY KEY AUTOINCREMENT";

	public static final String	COLUMN_TRACK_ID_NAME = 		"track_id";
	private static final String COLUMN_TRACK_ID_TYPE = 		"TEXT";

	public static final String	COLUMN_NAME_NAME = 			"name";
	private static final String COLUMN_NAME_TYPE = 			"TEXT";

	public static final String	COLUMN_ARTIST_NAME = 		"artist";
	private static final String COLUMN_ARTIST_TYPE = 		"TEXT";

	public static final String	COLUMN_ALBUM_NAME =			"album";
	private static final String COLUMN_ALBUM_TYPE =			"TEXT";

	public static final String	COLUMN_IMAGE_URL_NAME =		"image_url";
	private static final String COLUMN_IMAGE_URL_TYPE =		"TEXT";

	public static final String	COLUMN_PREVIEW_URL_NAME =	"preview_url";
	private static final String COLUMN_PREVIEW_URL_TYPE =	"TEXT";

	public static final String 	COLUMN_CACHED_AT_NAME =		"cached_at";
	private static final String COLUMN_CACHED_AT_TYPE =		"TIMESTAMP";
	public static final String 	COLUMN_CACHED_AT_DEFAULT =	"DEFAULT CURRENT_TIMESTAMP NOT NULL";

	public static final String SQL_CREATE_TABLE =
		"CREATE TABLE " + TABLE_NAME + " (" +
			COLUMN_ID_NAME 			+ " " 	+ COLUMN_ID_TYPE 			+ COMMA_SEP +
			COLUMN_TRACK_ID_NAME 	+ " " 	+ COLUMN_TRACK_ID_TYPE 		+ COMMA_SEP +
			COLUMN_NAME_NAME 		+ " " 	+ COLUMN_NAME_TYPE 			+ COMMA_SEP +
			COLUMN_ARTIST_NAME 		+ " " 	+ COLUMN_ARTIST_TYPE 		+ COMMA_SEP +
			COLUMN_ALBUM_NAME 		+ " " 	+ COLUMN_ALBUM_TYPE 		+ COMMA_SEP +
			COLUMN_IMAGE_URL_NAME 	+ " " 	+ COLUMN_IMAGE_URL_TYPE 	+ COMMA_SEP +
			COLUMN_PREVIEW_URL_NAME + " " 	+ COLUMN_PREVIEW_URL_TYPE 	+ COMMA_SEP	+
			COLUMN_CACHED_AT_NAME	+ " "	+ COLUMN_CACHED_AT_TYPE 	+ " " 		+ COLUMN_CACHED_AT_DEFAULT	+
			");";

	public static final String SQL_DROP_TABLE =
		"DROP TABLE IF EXISTS " + TABLE_NAME;

	public static void resetTable(Context context){
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();

		database.execSQL(SQL_DROP_TABLE);
		database.execSQL(SQL_CREATE_TABLE);
		Log.d(TAG, "Cleared HHCachedSpotifyTrack table");

		database.close();
	}

	public static void insertCachedSpotifyTrack(Context context, HHCachedSpotifyTrack cachedSpotifyTrack, InsertCachedSpotifyTrackTask.InsertCachedSpotifyTrackTaskCallback callbackTo){
		new InsertCachedSpotifyTrackTask(context, cachedSpotifyTrack, -1, callbackTo).execute();
	}

	public static void insertSpotifyTrack(Context context, SpotifyTrack spotifyTrack, InsertCachedSpotifyTrackTask.InsertCachedSpotifyTrackTaskCallback callbackTo){
		new InsertCachedSpotifyTrackTask(context, spotifyTrack, -1, callbackTo).execute();
	}

	private static ContentValues cachedSpotifyTrackToContentValues(HHCachedSpotifyTrack track){
		ContentValues contentValues = new ContentValues();
		contentValues.put(COLUMN_TRACK_ID_NAME, 	track.getTrackID());
		contentValues.put(COLUMN_NAME_NAME, 		track.getName());
		contentValues.put(COLUMN_ARTIST_NAME, 		track.getArtist());
		contentValues.put(COLUMN_ALBUM_NAME, 		track.getAlbum());
		contentValues.put(COLUMN_IMAGE_URL_NAME, 	track.getImageUrl());
		contentValues.put(COLUMN_PREVIEW_URL_NAME, track.getPreviewUrl());
		return contentValues;
	}

	public static void getCachedSpotifyTracks(Context context, GetDBCachedSpotifyTracksTask.GetDBCachedSpotifyTracksCallback callbackTo){
		new GetDBCachedSpotifyTracksTask(context, callbackTo).execute();
	}

	public static class GetDBCachedSpotifyTracksTask extends AsyncTask<Void, Void, List<HHCachedSpotifyTrack>> {
		private Context context;
		private GetDBCachedSpotifyTracksCallback callbackTo;

		public interface GetDBCachedSpotifyTracksCallback {
			void returnCachedSpotifyTracks(List<HHCachedSpotifyTrack> cachedSpotifyTracks);
		}

		public GetDBCachedSpotifyTracksTask(Context context, GetDBCachedSpotifyTracksCallback callbackTo){
			this.context = context;
			this.callbackTo = callbackTo;
		}

		@Override
		protected List<HHCachedSpotifyTrack> doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getReadableDatabase();

			Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);

			int numTracks = cursor.getCount();
			Log.d(TAG, "Loaded " + numTracks + " CachedSpotifyTracks...");
			List<HHCachedSpotifyTrack> cachedSpotifyTracks = new ArrayList<>(numTracks);

			if (numTracks > 0){
				cursor.moveToFirst();
				while (!cursor.isAfterLast()){
					HHCachedSpotifyTrack track = new HHCachedSpotifyTrack(cursor);
					cachedSpotifyTracks.add(track);
					cursor.moveToNext();
				}
				Log.d(TAG, "CachedSpotifyTracks loaded successfully");
			}

			cursor.close();
			database.close();

			return cachedSpotifyTracks;
		}

		@Override
		protected void onPostExecute(List<HHCachedSpotifyTrack> cachedSpotifyTracks){
			callbackTo.returnCachedSpotifyTracks(cachedSpotifyTracks);
		}
	}

	public static class InsertCachedSpotifyTrackTask extends AsyncTask<Void, Void, Long> {

		private Context context;
		private HHCachedSpotifyTrack cachedSpotifyTrack;
		private int position;
		private InsertCachedSpotifyTrackTaskCallback callbackTo;

		public interface InsertCachedSpotifyTrackTaskCallback {
			void returnInsertCachedSpotifyTrack(Long trackID, int position, HHCachedSpotifyTrack cachedSpotifyTrack);
		}

		public InsertCachedSpotifyTrackTask(Context context, HHCachedSpotifyTrack cachedSpotifyTrack, int position, InsertCachedSpotifyTrackTaskCallback callbackTo){
			this.context = context;
			this.cachedSpotifyTrack = cachedSpotifyTrack;
			this.position = position;
			this.callbackTo = callbackTo;
		}

		public InsertCachedSpotifyTrackTask(Context context, SpotifyTrack spotifyTrack, int position, InsertCachedSpotifyTrackTaskCallback callbackTo){
			this.context = context;
			this.cachedSpotifyTrack = new HHCachedSpotifyTrack(spotifyTrack);
			this.position = position;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Long doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			ContentValues values = cachedSpotifyTrackToContentValues(cachedSpotifyTrack);
			long trackID = database.insert(TABLE_NAME, "null", values);
			Log.d(TAG, "Inserted new HHCachedSpotifyTrack with ID:" + trackID);

			database.close();

			return trackID;
		}

		@Override
		protected void onPostExecute(Long trackID){
			callbackTo.returnInsertCachedSpotifyTrack(trackID, position, cachedSpotifyTrack);
		}
	}

	public static void getTracksFromPosts(Context context, List<HHPostFullProcess> posts, DBCachedSpotifyTrackSelectManyFromPostsTask.DBCachedSpotifyTrackSelectManyFromPostsTaskCallback callbackTo){
		new DBCachedSpotifyTrackSelectManyFromPostsTask(context, posts, callbackTo).execute();
	}

	public static class DBCachedSpotifyTrackSelectManyFromPostsTask extends AsyncTask<Void, Void, Boolean> {

		private Context context;
		private List<HHPostFullProcess> posts;
		private DBCachedSpotifyTrackSelectManyFromPostsTaskCallback callbackTo;

		public interface DBCachedSpotifyTrackSelectManyFromPostsTaskCallback {
			void returnSelectedManyCachedSpotifyTracks(List<HHPostFullProcess> postToProcess);
		}

		public DBCachedSpotifyTrackSelectManyFromPostsTask(Context context, List<HHPostFullProcess> posts, DBCachedSpotifyTrackSelectManyFromPostsTaskCallback callbackTo){
			this.context = context;
			this.posts = posts;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getReadableDatabase();

			for (int i = 0; i < posts.size(); i++) {
				HHPostFullProcess post = posts.get(i);

				Cursor cursor = database.query(TABLE_NAME, null,
											   COLUMN_TRACK_ID_NAME + " = '" + post.getPost()
																					  .getTrack() + "'",
											   null, null, null, null);

				if (cursor.getCount() > 0) {
					cursor.moveToFirst();
					HHCachedSpotifyTrack track = new HHCachedSpotifyTrack(cursor);
					post.setTrack(track);
					post.setTrackProcessed(true);
					Log.d(TAG, "Found HHCachedSpotifyTrack:" + track.getTrackID());
				}
				cursor.close();

			}

			database.close();

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result){
			callbackTo.returnSelectedManyCachedSpotifyTracks(posts);
		}
	}

	public static void insertTrackFromPosts(Context context, HHPostFullProcess postProcess, DBCachedSpotifyTrackInsertFromPostTask.DBCachedSpotifyTrackInsertFromPostTaskCallback callback) {
		new DBCachedSpotifyTrackInsertFromPostTask(context, postProcess, callback).execute();
	}

	public static class DBCachedSpotifyTrackInsertFromPostTask extends AsyncTask<Void, Void, Boolean> {

		private Context context;
		private HHPostFullProcess post;
		private DBCachedSpotifyTrackInsertFromPostTaskCallback callbackTo;

		public interface DBCachedSpotifyTrackInsertFromPostTaskCallback {
			void returnInsertedManyCachedSpotifyTracks(HHPostFullProcess postToProcess);
		}

		public DBCachedSpotifyTrackInsertFromPostTask(Context context, HHPostFullProcess post, DBCachedSpotifyTrackInsertFromPostTaskCallback callbackTo){
			this.context = context;
			this.post = post;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			ContentValues values = cachedSpotifyTrackToContentValues(post.getTrack());
			Long trackID = database.insert(TABLE_NAME, "null", values);

			post.setTrackProcessed(true);

			Log.d(TAG, "Inserted new HHCachedSpotifyTrack with ID:" + trackID);

			database.close();

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result){
			callbackTo.returnInsertedManyCachedSpotifyTracks(post);
		}
	}

	public static void insertTracksFromPosts(Context context, List<HHPostFullProcess> posts, DBCachedSpotifyTrackInsertManyFromPostsTask.DBCachedSpotifyTrackInsertManyFromPostsTaskCallback callbackTo){
		new DBCachedSpotifyTrackInsertManyFromPostsTask(context, posts, callbackTo).execute();
	}

	public static class DBCachedSpotifyTrackInsertManyFromPostsTask extends AsyncTask<Void, Void, Boolean> {

		private Context context;
		private List<HHPostFullProcess> posts;
		private DBCachedSpotifyTrackInsertManyFromPostsTaskCallback callbackTo;

		public interface DBCachedSpotifyTrackInsertManyFromPostsTaskCallback {
			void returnInsertedManyCachedSpotifyTracks(List<HHPostFullProcess> postsToProcess);
		}

		public DBCachedSpotifyTrackInsertManyFromPostsTask(Context context, List<HHPostFullProcess> posts, DBCachedSpotifyTrackInsertManyFromPostsTaskCallback callbackTo){
			this.context = context;
			this.posts = posts;
			this.callbackTo = callbackTo;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getWritableDatabase();

			//List<Long> postIDs = new ArrayList<>(posts.size());
			Long trackID;
			for (int i = 0; i < posts.size(); i++) {
				HHPostFullProcess post = posts.get(i);
				ContentValues values = cachedSpotifyTrackToContentValues(post.getTrack());
				trackID = database.insert(TABLE_NAME, "null", values);

				post.setTrackProcessed(true);

				Log.d(TAG, "Inserted new HHCachedSpotifyTrack with ID:" + trackID);
			}

			database.close();

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result){
			callbackTo.returnInsertedManyCachedSpotifyTracks(posts);
		}
	}

}
