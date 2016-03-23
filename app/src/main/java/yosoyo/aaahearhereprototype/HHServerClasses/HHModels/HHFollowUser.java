package yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;

import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFollowUserNested;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFollowedUserNested;

/**
 * Created by adam on 10/03/16.
 */
public class HHFollowUser {

	private final HHFollow follow;
	private final HHUser user;

	public HHFollowUser(HHFollowUserNested nested){
		this.follow = new HHFollow(nested);
		this.user = nested.getUser();
	}

	public HHFollowUser(HHFollowedUserNested nested){
		this.follow = new HHFollow(nested);
		this.user = nested.getUser();
	}

	public HHFollowUser(Cursor cursor){
		this.follow = new HHFollow(cursor);
		this.user = new HHUser(cursor);
	}

	public HHFollow getFollow() {
		return follow;
	}

	public HHUser getUser() {
		return user;
	}
}
