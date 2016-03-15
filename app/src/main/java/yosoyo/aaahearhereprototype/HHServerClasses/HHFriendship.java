package yosoyo.aaahearhereprototype.HHServerClasses;

import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFriendshipUserNested;

/**
 * Created by adam on 10/03/16.
 */
public class HHFriendship extends HHBase {

	private final long user_id;
	private final long friend_user_id;

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
