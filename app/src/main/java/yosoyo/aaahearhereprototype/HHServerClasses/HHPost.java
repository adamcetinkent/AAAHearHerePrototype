package yosoyo.aaahearhereprototype.HHServerClasses;

import android.database.Cursor;

import java.sql.Timestamp;

import yosoyo.aaahearhereprototype.HHServerClasses.Database.ORMPost;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHPostFullNested;

/**
 * Created by adam on 18/02/16.
 */
public class HHPost extends HHBase {

	private long user_id;
	private String track;
	private double lat;
	private double lon;
	private String place_name;
	private String google_place_id;
	private String message;

	public String getTrack() {
		return track;
	}

	protected HHPost(){}

	public HHPost(HHPostFullNested nested){
		super(
			nested.getID(),
			nested.getUpdatedAt(),
			nested.getCreatedAt()
			 );
		this.user_id = nested.getUserID();
		this.track = nested.getTrack();
		this.lat = nested.getLat();
		this.lon = nested.getLon();
		this.message = nested.getMessage();
		this.place_name = nested.getPlaceName();
		this.google_place_id = nested.getGooglePlaceID();
	}

	protected HHPost(long user_id, String track, double lat, double lon, String message, String place_name, String google_place_id) {
		super();
		this.user_id = user_id;
		this.track = track;
		this.lat = lat;
		this.lon = lon;
		this.message = message;
		this.place_name = place_name;
		this.google_place_id = google_place_id;
	}

	public HHPost(Cursor cursor){
		super(
			cursor.getLong(cursor.getColumnIndex(ORMPost.COLUMN_ID_NAME)),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMPost.COLUMN_UPDATED_AT_NAME))),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMPost.COLUMN_CREATED_AT_NAME)))
			 );
		this.user_id = cursor.getLong(cursor.getColumnIndex(ORMPost.COLUMN_USER_ID_NAME));
		this.track = cursor.getString(cursor.getColumnIndex(ORMPost.COLUMN_TRACK_NAME));
		this.lat = cursor.getDouble(cursor.getColumnIndex(ORMPost.COLUMN_LAT_NAME));
		this.lon = cursor.getDouble(cursor.getColumnIndex(ORMPost.COLUMN_LON_NAME));
		this.message = cursor.getString(cursor.getColumnIndex(ORMPost.COLUMN_MESSAGE_NAME));
		this.place_name = cursor.getString(cursor.getColumnIndex(ORMPost.COLUMN_PLACE_NAME_NAME));
		this.google_place_id = cursor.getString(cursor.getColumnIndex(ORMPost.COLUMN_GOOGLE_PLACE_ID_NAME));
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
