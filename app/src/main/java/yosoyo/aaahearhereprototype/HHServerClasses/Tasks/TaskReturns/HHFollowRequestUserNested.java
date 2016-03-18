package yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import yosoyo.aaahearhereprototype.HHServerClasses.HHFollowRequest;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUser;

/**
 * Created by adam on 10/03/16.
 */
public class HHFollowRequestUserNested extends HHFollowRequest {

	private HHUser requested_user;

	public HHFollowRequestUserNested(HHFollowRequestUserNested nested) {
		super(nested);
	}

	public HHUser getUser() {
		return requested_user;
	}
}
