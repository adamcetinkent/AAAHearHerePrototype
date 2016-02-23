package yosoyo.aaahearhereprototype.TestServerClasses;

import android.database.Cursor;

/**
 * Created by adam on 18/02/16.
 */
public class TestPost {

	long id;
	long user_id;
	String track;
	double lat;
	double lon;
	String message;
	String updated_at;
	String created_at;

	public String getTrack() {
		return track;
	}

	public TestPost(long user_id, String track, double lat, double lon, String message) {
		this.user_id = user_id;
		this.track = track;
		this.lat = lat;
		this.lon = lon;
		this.message = message;
	}

	public TestPost(Cursor cursor){
		this.id = cursor.getLong(cursor.getColumnIndex(ORMTestPost.COLUMN_ID_NAME));
		this.user_id = cursor.getLong(cursor.getColumnIndex(ORMTestPost.COLUMN_USER_ID_NAME));
		this.track = cursor.getString(cursor.getColumnIndex(ORMTestPost.COLUMN_TRACK_NAME));
		this.lat = cursor.getDouble(cursor.getColumnIndex(ORMTestPost.COLUMN_LAT_NAME));
		this.lon = cursor.getDouble(cursor.getColumnIndex(ORMTestPost.COLUMN_LON_NAME));
		this.message = cursor.getString(cursor.getColumnIndex(ORMTestPost.COLUMN_MESSAGE_NAME));
		this.updated_at = cursor.getString(cursor.getColumnIndex(ORMTestPost.COLUMN_UPDATED_AT_NAME));
		this.created_at = cursor.getString(cursor.getColumnIndex(ORMTestPost.COLUMN_CREATED_AT_NAME));
	}

	public long getId() {
		return id;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public String getMessage() {
		return message;
	}

	public long getUserID() {
		return user_id;
	}

	public String getCreatedAt() {
		return created_at;
	}
}
