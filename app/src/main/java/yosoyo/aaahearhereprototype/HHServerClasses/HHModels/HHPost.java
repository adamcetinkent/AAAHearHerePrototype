package yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;

import yosoyo.aaahearhereprototype.HHServerClasses.Database.ORMPost;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHPostFullNested;

/**
 * Created by adam on 18/02/16.
 */
public class HHPost extends HHBase implements Parcelable {

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
			nested.getCreatedAt(),
			nested.getUpdatedAt()
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
			cursor.getLong(cursor.getColumnIndex(ORMPost.ID())),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMPost.CREATED_AT()))),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMPost.UPDATED_AT())))
			 );
		this.user_id = cursor.getLong(cursor.getColumnIndex(ORMPost.USER_ID()));
		this.track = cursor.getString(cursor.getColumnIndex(ORMPost.TRACK()));
		this.lat = cursor.getDouble(cursor.getColumnIndex(ORMPost.LAT()));
		this.lon = cursor.getDouble(cursor.getColumnIndex(ORMPost.LON()));
		this.message = cursor.getString(cursor.getColumnIndex(ORMPost.MESSAGE()));
		this.place_name = cursor.getString(cursor.getColumnIndex(ORMPost.PLACE_NAME()));
		this.google_place_id = cursor.getString(cursor.getColumnIndex(ORMPost.GOOGLE_PLACE_ID()));
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
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeLong(user_id);
		dest.writeString(track);
		dest.writeDouble(lat);
		dest.writeDouble(lon);
		dest.writeString(place_name);
		dest.writeString(google_place_id);
		dest.writeString(message);
	}

	public static final Parcelable.Creator<HHPost> CREATOR = new Parcelable.Creator<HHPost>(){

		@Override
		public HHPost createFromParcel(Parcel source) {
			return new HHPost(source);
		}

		@Override
		public HHPost[] newArray(int size) {
			return new HHPost[size];
		}

	};

	private HHPost(Parcel in){
		super(in);
		user_id = 			in.readLong();
		track = 			in.readString();
		lat = 				in.readDouble();
		lon = 				in.readDouble();
		place_name = 		in.readString();
		google_place_id = 	in.readString();
		message = 			in.readString();
	}
}
