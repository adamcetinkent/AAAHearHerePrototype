package yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import yosoyo.aaahearhereprototype.HHServerClasses.HHPost;

/**
 * Created by adam on 10/03/16.
 */
public class HHPostTagsArray extends HHPost {

	long[] tagIDs;

	public HHPostTagsArray(long user_id, String track, double lat, double lon, String message, String place_name, String google_place_id, long[] tagIDs){
		super(user_id, track, lat, lon, message, place_name, google_place_id);
		this.tagIDs = tagIDs;
	}

}
