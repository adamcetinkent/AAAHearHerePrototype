package yosoyo.aaahearhereprototype.HHServerClasses;

import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFollowUserNested;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFollowedUserNested;

/**
 * Created by adam on 10/03/16.
 */
public class HHFollow extends HHBase {

	private final long user_id;
	private final long followed_user_id;

	public HHFollow(HHFollowUserNested nested){
		super(
			nested.getID(),
			nested.getCreatedAt(),
			nested.getUpdatedAt()
		);
		this.user_id = nested.getUserID();
		this.followed_user_id = nested.getFollowedUserID();
	}

	public HHFollow(HHFollowedUserNested nested){
		super(
			nested.getID(),
			nested.getCreatedAt(),
			nested.getUpdatedAt()
		);
		this.user_id = nested.getUserID();
		this.followed_user_id = nested.getFollowedUserID();
	}

	public long getUserID() {
		return user_id;
	}

	public long getFollowedUserID() {
		return followed_user_id;
	}
}
