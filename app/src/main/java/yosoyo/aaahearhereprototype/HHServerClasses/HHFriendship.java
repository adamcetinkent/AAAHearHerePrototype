package yosoyo.aaahearhereprototype.HHServerClasses;

import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFriendshipUserNested;

/**
 * Created by adam on 10/03/16.
 */
public class HHFriendship extends HHBase {

	long user_id;
	long friend_user_id;

	public HHFriendship(HHFriendshipUserNested nested){
		super(
			nested.getID(),
			nested.getCreatedAt(),
			nested.getUpdatedAt()
			 );
		this.user_id = nested.getUserID();
		this.friend_user_id = nested.getFriendUserID();
	}

	public long getUserID() {
		return user_id;
	}

	public long getFriendUserID() {
		return friend_user_id;
	}
}
