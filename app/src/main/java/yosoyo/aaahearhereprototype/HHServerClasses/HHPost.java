package yosoyo.aaahearhereprototype.HHServerClasses;

import android.database.Cursor;

import java.sql.Timestamp;

import yosoyo.aaahearhereprototype.HHServerClasses.Database.ORMPost;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHPostFullNested;

/**
 * Created by adam on 18/02/16.
 */
public class HHPost {

	long id;
	long user_id;
	String track;
	double lat;
	double lon;
	String place_name;
	String google_place_id;
	String message;
	Timestamp updated_at;
	Timestamp created_at;

	public String getTrack() {
		return track;
	}

	public HHPost(){}

	public HHPost(HHPostFullNested nested){
		this.id = nested.getID();
		this.user_id = nested.getUserID();
		this.track = nested.getTrack();
		this.lat = nested.getLat();
		this.lon = nested.getLon();
		this.message = nested.getMessage();
		this.place_name = nested.getPlaceName();
		this.google_place_id = nested.getGooglePlaceID();
		this.updated_at = nested.getUpdatedAt();
		this.created_at = nested.getCreatedAt();
	}

	public HHPost(long user_id, String track, double lat, double lon, String message, String place_name, String google_place_id) {
		this.user_id = user_id;
		this.track = track;
		this.lat = lat;
		this.lon = lon;
		this.message = message;
		this.place_name = place_name;
		this.google_place_id = google_place_id;
	}

	public HHPost(Cursor cursor){
		this.id = cursor.getLong(cursor.getColumnIndex(ORMPost.COLUMN_ID_NAME));
		this.user_id = cursor.getLong(cursor.getColumnIndex(ORMPost.COLUMN_USER_ID_NAME));
		this.track = cursor.getString(cursor.getColumnIndex(ORMPost.COLUMN_TRACK_NAME));
		this.lat = cursor.getDouble(cursor.getColumnIndex(ORMPost.COLUMN_LAT_NAME));
		this.lon = cursor.getDouble(cursor.getColumnIndex(ORMPost.COLUMN_LON_NAME));
		this.message = cursor.getString(cursor.getColumnIndex(ORMPost.COLUMN_MESSAGE_NAME));
		this.place_name = cursor.getString(
			cursor.getColumnIndex(ORMPost.COLUMN_PLACE_NAME_NAME));
		this.google_place_id = cursor.getString(cursor.getColumnIndex(ORMPost.COLUMN_GOOGLE_PLACE_ID_NAME));
		this.updated_at = Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMPost.COLUMN_UPDATED_AT_NAME)));
		this.created_at = Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMPost.COLUMN_CREATED_AT_NAME)));
	}

	public long getID() {
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

	public String getPlaceName(){
		return place_name;
	}

	public String getGooglePlaceID(){
		return google_place_id;
	}

	public Timestamp getCreatedAt() {
		return created_at;
	}

	public Timestamp getUpdatedAt() {
		return updated_at;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		HHPost post = (HHPost) o;

		return id == post.id;

	}

	@Override
	public int hashCode() {
		return (int) (id ^ (id >>> 32));
	}

}