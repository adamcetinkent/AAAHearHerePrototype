package yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import yosoyo.aaahearhereprototype.HHServerClasses.HHFollow;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUser;

/**
 * Created by adam on 10/03/16.
 */
public class HHFollowUserNested extends HHFollow {

	private HHUser followed_user;

	public HHFollowUserNested(HHFollowUserNested nested) {
		super(nested);
	}

	public HHUser getUser() {
		return followed_user;
	}
}
