package yosoyo.aaahearhereprototype.HHServerClasses;

import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFriendshipUserNested;

/**
 * Created by adam on 10/03/16.
 */
public class HHFriendshipUser {

	HHFriendship friendship;
	HHUser user;

	public HHFriendshipUser(HHFriendshipUserNested nested){
		this.friendship = new HHFriendship(nested);
		this.user = nested.getUser();
	}

	public HHFriendship getFriendship() {
		return friendship;
	}

	public HHUser getUser() {
		return user;
	}
}
