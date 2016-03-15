package yosoyo.aaahearhereprototype.HHServerClasses;

import android.database.Cursor;

import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHCommentUserNested;

/**
 * Created by adam on 02/03/16.
 */
public class HHCommentUser {

	private final HHComment comment;
	private final HHUser user;

	public HHCommentUser(HHCommentUserNested nested){
		this.comment = new HHComment(nested);
		this.user = nested.getUser();
	}

	public HHCommentUser(Cursor cursor){
		this.comment = new HHComment(cursor);
		this.user = new HHUser(cursor);
	}

	public HHComment getComment() {
		return comment;
	}

	public HHUser getUser() {
		return user;
	}
}
