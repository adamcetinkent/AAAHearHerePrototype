package yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPost;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHTag;

/**
 * Created by adam on 10/03/16.
 */
public class HHPostTagsArray extends HHPost {

	private final HHTag[] tags_attributes;

	public HHPostTagsArray(long user_id, String track, double lat, double lon, String message, String place_name, String google_place_id, HHTag[] tags){
		super(user_id, track, lat, lon, message, place_name, google_place_id);
		this.tags_attributes = tags;
	}

}
