package yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;

import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFriendshipUserNested;

/**
 * Created by adam on 10/03/16.
 */
public class HHFriendshipUser {

	private final HHFriendship friendship;
	private final HHUser user;

	public HHFriendshipUser(HHFriendshipUserNested nested){
		this.friendship = new HHFriendship(nested);
		this.user = nested.getUser();
	}

	public HHFriendshipUser(Cursor cursor){
		this.friendship = new HHFriendship(cursor);
		this.user = new HHUser(cursor);
	}

	public HHFriendship getFriendship() {
		return friendship;
	}

	public HHUser getUser() {
		return user;
	}

}
