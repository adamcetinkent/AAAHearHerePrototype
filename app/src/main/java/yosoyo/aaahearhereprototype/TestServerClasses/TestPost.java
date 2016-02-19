package yosoyo.aaahearhereprototype.TestServerClasses;

/**
 * Created by adam on 18/02/16.
 */
public class TestPost {

	//long id;
	long user_id;
	String track;
	double lat;
	double lon;
	String message;
	//String updated_at;
	//String created_at;

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

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public String getMessage() {
		return message;
	}

	public long getUser_id() {
		return user_id;
	}

}
