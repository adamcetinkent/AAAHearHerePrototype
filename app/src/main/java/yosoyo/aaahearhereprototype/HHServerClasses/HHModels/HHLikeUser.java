package yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;

import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHLikeUserNested;

/**
 * Created by adam on 02/03/16.
 */
public class HHLikeUser {

	private final HHLike like;
	private final HHUser user;

	public HHLikeUser(HHLikeUserNested nested){
		this.like = new HHLike(nested);
		this.user = nested.getUser();
	}

	public HHLikeUser(Cursor cursor, String userIDColumnIndex){
		this.like = new HHLike(cursor);
		this.user = new HHUser(cursor, userIDColumnIndex);
	}

	public HHLike getLike() {
		return like;
	}

	public HHUser getUser() {
		return user;
	}
}
