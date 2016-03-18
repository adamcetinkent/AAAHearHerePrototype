package yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import yosoyo.aaahearhereprototype.HHServerClasses.HHFollowRequest;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUser;

/**
 * Created by adam on 10/03/16.
 */
public class HHFollowedRequestUserNested extends HHFollowRequest {

	private HHUser user;

	public HHFollowedRequestUserNested(HHFollowedRequestUserNested nested) {
		super(nested);
	}

	public HHUser getUser() {
		return user;
	}
}
