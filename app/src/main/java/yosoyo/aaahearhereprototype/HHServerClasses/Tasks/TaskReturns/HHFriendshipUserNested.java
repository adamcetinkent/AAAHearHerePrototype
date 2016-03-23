package yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFriendship;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;

/**
 * Created by adam on 10/03/16.
 */
public class HHFriendshipUserNested extends HHFriendship {

	private HHUser friend_user;

	public HHFriendshipUserNested(HHFriendshipUserNested nested) {
		super(nested);
	}

	public HHUser getUser() {
		return friend_user;
	}
}
