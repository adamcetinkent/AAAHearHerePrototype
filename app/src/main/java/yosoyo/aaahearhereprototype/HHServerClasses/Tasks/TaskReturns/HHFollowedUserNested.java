package yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import yosoyo.aaahearhereprototype.HHServerClasses.HHFollow;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUser;

/**
 * Created by adam on 10/03/16.
 */
public class HHFollowedUserNested extends HHFollow {

	private HHUser user;

	public HHFollowedUserNested(HHFollowedUserNested nested) {
		super(nested);
	}

	public HHUser getUser() {
		return user;
	}
}
