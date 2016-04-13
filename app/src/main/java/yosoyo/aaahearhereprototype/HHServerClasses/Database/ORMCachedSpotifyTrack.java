package yosoyo.aaahearhereprototype.HHServerClasses.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHCachedSpotifyTrack;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPostFullProcess;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;

/**
 * Created by adam on 23/02/16.
 */
public class ORMCachedSpotifyTrack {

	private static final String TAG = 						"ORMCachedSpotifyTrack";

	private static final String TABLE_NAME = 				"spotifytracks";
	public static String TABLE() { return TABLE_NAME; }

	private static final String COMMA_SEP = 				", ";

	private static final String COLUMN_ID_NAME = 			"_id";
	private static final String COLUMN_ID_TYPE = 			"INTEGER PRIMARY KEY AUTOINCREMENT";
	public static String ID() { return COLUMN_ID_NAME; }
	public static String dotID() { return TABLE_NAME + "." + COLUMN_ID_NAME; }

	private static final String	COLUMN_TRACK_ID_NAME = 		"track_id";
	private static final String COLUMN_TRACK_ID_TYPE = 		"TEXT";
	public static String TRACK_ID() { return COLUMN_TRACK_ID_NAME; }
	public static String dotTRACK_ID() { return TABLE_NAME + "." + COLUMN_TRACK_ID_NAME; }

	private static final String	COLUMN_NAME_NAME = 			"name";
	private static final String COLUMN_NAME_TYPE = 			"TEXT";
	public static String NAME() { return COLUMN_NAME_NAME;}
	public static String dotNAME() { return TABLE_NAME + "." + COLUMN_NAME_NAME;}

	private static final String	COLUMN_ARTIST_NAME = 		"artist";
	private static final String COLUMN_ARTIST_TYPE = 		"TEXT";
	public static String ARTIST_NAME() { return COLUMN_ARTIST_NAME; }
	public static String dotARTIST_NAME() { return TABLE_NAME + "." + COLUMN_ARTIST_NAME; }

	private static final String	COLUMN_ALBUM_NAME =			"album";
	private static final String COLUMN_ALBUM_TYPE =			"TEXT";
	public static String ALBUM_NAME() { return COLUMN_ALBUM_NAME; }
	public static String dotALBUM_NAME() { return TABLE_NAME + "." + COLUMN_ALBUM_NAME; }

	private static final String	COLUMN_IMAGE_URL_NAME =		"image_url";
	private static final String COLUMN_IMAGE_URL_TYPE =		"TEXT";
	public static String IMAGE_URL() { return COLUMN_IMAGE_URL_NAME; }
	public static String dotIMAGE_URL() { return TABLE_NAME + "." + COLUMN_IMAGE_URL_NAME; }

	private static final String	COLUMN_PREVIEW_URL_NAME =	"preview_url";
	private static final String COLUMN_PREVIEW_URL_TYPE =	"TEXT";
	public static String PREVIEW_URL() { return COLUMN_PREVIEW_URL_NAME; }
	public static String dotPREVIEW_URL() { return TABLE_NAME + "." + COLUMN_PREVIEW_URL_NAME; }

	private static final String COLUMN_CACHED_AT_NAME =		"cached_at";
	private static final String COLUMN_CACHED_AT_TYPE =		"TIMESTAMP";
	private static final String COLUMN_CACHED_AT_DEFAULT =	"DEFAULT CURRENT_TIMESTAMP NOT NULL";
	public static String CACHED_AT() { return COLUMN_CACHED_AT_NAME; }
	public static String dotCACHED_AT() { return TABLE_NAME + "." + COLUMN_CACHED_AT_NAME; }

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

	public static void getCachedSpotifyTrack(Context context, String trackID, GetDBCachedSpotifyTrackTask.Callback callback){
		new GetDBCachedSpotifyTrackTask(context, trackID, callback).execute();
	}

	public static class GetDBCachedSpotifyTrackTask extends AsyncTask<Void, Void, HHCachedSpotifyTrack> {
		private final Context context;
		private final String trackID;
		private final Callback callbackTo;

		public interface Callback {
			void returnCachedSpotifyTrack(HHCachedSpotifyTrack cachedSpotifyTrack);
		}

		public GetDBCachedSpotifyTrackTask(Context context, String trackID, Callback callbackTo){
			this.context = context;
			this.trackID = trackID;
			this.callbackTo = callbackTo;
		}

		@Override
		protected HHCachedSpotifyTrack doInBackground(Void... params) {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase database = databaseHelper.getReadableDatabase();

			Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME +
												  " WHERE " + COLUMN_TRACK_ID_NAME+"=?",
											  new String[]{trackID});

			int numTracks = cursor.getCount();
			Log.d(TAG, "Loaded " + numTracks + " CachedSpotifyTracks...");

			HHCachedSpotifyTrack track = null;
			if (numTracks > 0){
				cursor.moveToFirst();
				track = new HHCachedSpotifyTrack(cursor);
				Log.d(TAG, "CachedSpotifyTrack loaded successfully");
			}

			cursor.close();
			database.close();

			return track;
		}

		@Override
		protected void onPostExecute(HHCachedSpotifyTrack cachedSpotifyTrack){
			callbackTo.returnCachedSpotifyTrack(cachedSpotifyTrack);
		}
	}

	public static void getCachedSpotifyTracks(Context context, GetDBCachedSpotifyTracksTask.Callback callbackTo){
		new GetDBCachedSpotifyTracksTask(context, callbackTo).execute();
	}

	public static class GetDBCachedSpotifyTracksTask extends AsyncTask<Void, Void, List<HHCachedSpotifyTrack>> {
		private final Context context;
		private final Callback callbackTo;

		public interface Callback {
			void returnCachedSpotifyTracks(List<HHCachedSpotifyTrack> cachedSpotifyTracks);
		}

		public GetDBCachedSpotifyTracksTask(Context context, Callback callbackTo){
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

	public static void insertSpotifyTrack(Context context, SpotifyTrack spotifyTrack, InsertCachedSpotifyTrackTask.Callback callback){
		new InsertCachedSpotifyTrackTask(context, spotifyTrack, callback);
	}

	public static class InsertCachedSpotifyTrackTask extends AsyncTask<Void, Void, Long> {

		private final Context context;
		private final HHCachedSpotifyTrack cachedSpotifyTrack;
		private final Callback callbackTo;

		public interface Callback {
			void returnInsertCachedSpotifyTrack(Long trackID, HHCachedSpotifyTrack cachedSpotifyTrack);
		}

		public InsertCachedSpotifyTrackTask(Context context, HHCachedSpotifyTrack cachedSpotifyTrack, Callback callbackTo){
			this.context = context;
			this.cachedSpotifyTrack = cachedSpotifyTrack;
			this.callbackTo = callbackTo;
		}

		public InsertCachedSpotifyTrackTask(Context context, SpotifyTrack spotifyTrack, Callback callbackTo){
			this.context = context;
			this.cachedSpotifyTrack = new HHCachedSpotifyTrack(spotifyTrack);
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
			callbackTo.returnInsertCachedSpotifyTrack(trackID, cachedSpotifyTrack);
		}
	}

	public static void getTracksFromPosts(Context context, List<HHPostFullProcess> posts, DBCachedSpotifyTrackSelectManyFromPostsTask.Callback callbackTo){
		new DBCachedSpotifyTrackSelectManyFromPostsTask(context, posts, callbackTo).execute();
	}

	public static class DBCachedSpotifyTrackSelectManyFromPostsTask extends AsyncTask<Void, Void, Boolean> {

		private final Context context;
		private final List<HHPostFullProcess> posts;
		private final Callback callbackTo;

		public interface Callback {
			void returnSelectedManyCachedSpotifyTracks(List<HHPostFullProcess> postToProcess);
		}

		public DBCachedSpotifyTrackSelectManyFromPostsTask(Context context, List<HHPostFullProcess> posts, Callback callbackTo){
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

	public static void insertTrackFromPosts(Context context, HHPostFullProcess postProcess, DBCachedSpotifyTrackInsertFromPostTask.Callback callback) {
		new DBCachedSpotifyTrackInsertFromPostTask(context, postProcess, callback).execute();
	}

	public static class DBCachedSpotifyTrackInsertFromPostTask extends AsyncTask<Void, Void, Boolean> {

		private final Context context;
		private final HHPostFullProcess post;
		private final Callback callbackTo;

		public interface Callback {
			void returnInsertedManyCachedSpotifyTracks(HHPostFullProcess postToProcess);
		}

		public DBCachedSpotifyTrackInsertFromPostTask(Context context, HHPostFullProcess post, Callback callbackTo){
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

	public static void insertTracksFromPosts(Context context, List<HHPostFullProcess> posts, DBCachedSpotifyTrackInsertManyFromPostsTask.Callback callbackTo){
		new DBCachedSpotifyTrackInsertManyFromPostsTask(context, posts, callbackTo).execute();
	}

	public static class DBCachedSpotifyTrackInsertManyFromPostsTask extends AsyncTask<Void, Void, Boolean> {

		private final Context context;
		private final List<HHPostFullProcess> posts;
		private final Callback callbackTo;

		public interface Callback {
			void returnInsertedManyCachedSpotifyTracks(List<HHPostFullProcess> postsToProcess);
		}

		public DBCachedSpotifyTrackInsertManyFromPostsTask(Context context, List<HHPostFullProcess> posts, Callback callbackTo){
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
