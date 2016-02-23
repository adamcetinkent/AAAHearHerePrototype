package yosoyo.aaahearhereprototype.TestServerClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.DatabaseHelper;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;

/**
 * Created by adam on 23/02/16.
 */
public class ORMCachedSpotifyTrack {

	private static final String TAG = "ORMCachedSpotifyTrack";

	private static final String TABLE_NAME = "spotifytrack";

	private static final String COMMA_SEP = ", ";

	private static final String COLUMN_ID_TYPE = "INTEGER PRIMARY KEY AUTOINCREMENT";
	private static final String COLUMN_ID_NAME = "_id";

	private static final String COLUMN_TRACK_ID_TYPE = "TEXT";
	public static final String COLUMN_TRACK_ID_NAME = "track_id";

	private static final String COLUMN_NAME_TYPE = "TEXT";
	public static final String COLUMN_NAME_NAME = "name";

	private static final String COLUMN_ARTIST_TYPE = "TEXT";
	public static final String COLUMN_ARTIST_NAME = "artist";

	private static final String COLUMN_ALBUM_TYPE = "TEXT";
	public static final String COLUMN_ALBUM_NAME = "album";

	private static final String COLUMN_IMAGE_URL_TYPE = "TEXT";
	public static final String COLUMN_IMAGE_URL_NAME = "image_url";

	private static final String COLUMN_PREVIEW_URL_TYPE = "TEXT";
	public static final String COLUMN_PREVIEW_URL_NAME = "preview_url";

	public static final String SQL_CREATE_TABLE =
		"CREATE TABLE " + TABLE_NAME + " (" +
			COLUMN_ID_NAME + " " + COLUMN_ID_TYPE + COMMA_SEP +
			COLUMN_TRACK_ID_NAME + " " + COLUMN_TRACK_ID_TYPE + COMMA_SEP +
			COLUMN_NAME_NAME + " " + COLUMN_NAME_TYPE + COMMA_SEP +
			COLUMN_ARTIST_NAME + " " + COLUMN_ARTIST_TYPE + COMMA_SEP +
			COLUMN_ALBUM_NAME + " " + COLUMN_ALBUM_TYPE + COMMA_SEP +
			COLUMN_IMAGE_URL_NAME + " " + COLUMN_IMAGE_URL_TYPE + COMMA_SEP +
			COLUMN_PREVIEW_URL_NAME + " " + COLUMN_PREVIEW_URL_TYPE +
			");";

	public static final String SQL_DROP_TABLE =
		"DROP TABLE IF EXISTS " + TABLE_NAME;

	public static void resetTable(Context context){
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();

		database.execSQL(SQL_DROP_TABLE);
		database.execSQL(SQL_CREATE_TABLE);
		Log.d(TAG, "Cleared CachedSpotifyTrack table");

		database.close();
	}

	public static long insertSpotifyTrack(Context context, SpotifyTrack spotifyTrack){
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();

		ContentValues values = spotifyTrackToContentValuse(spotifyTrack);
		long postID = database.insert(TABLE_NAME, "null", values);
		Log.d(TAG, "Inserted new CachedSpotifyTrack with ID:" + postID);

		database.close();

		return postID;
	}

	private static ContentValues spotifyTrackToContentValuse(SpotifyTrack track){
		ContentValues contentValues = new ContentValues();
		contentValues.put(COLUMN_TRACK_ID_NAME, track.getID());
		contentValues.put(COLUMN_NAME_NAME, track.getName());
		contentValues.put(COLUMN_ARTIST_NAME, track.getArtistName());
		contentValues.put(COLUMN_ALBUM_NAME, track.getAlbumName());
		contentValues.put(COLUMN_IMAGE_URL_NAME, track.getImages(0).getUrl());
		contentValues.put(COLUMN_PREVIEW_URL_NAME, track.getPreview_url());
		return contentValues;
	}

	public static List<CachedSpotifyTrack> getCachedSpotiyTracks(Context context){
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		SQLiteDatabase database = databaseHelper.getReadableDatabase();

		Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);

		int numTracks = cursor.getCount();
		Log.d(TAG, "Loaded " + numTracks + " CachedSpotifyTracks...");
		List<CachedSpotifyTrack> cachedSpotifyTracks = new ArrayList<>(numTracks);

		if (numTracks > 0){
			cursor.moveToFirst();
			while (!cursor.isAfterLast()){
				CachedSpotifyTrack track = new CachedSpotifyTrack(cursor);
				cachedSpotifyTracks.add(track);
				cursor.moveToNext();
			}
			Log.d(TAG, "CachedSpotifyTracks loaded successfully");
		}

		cursor.close();
		database.close();

		return cachedSpotifyTracks;
	}

}
