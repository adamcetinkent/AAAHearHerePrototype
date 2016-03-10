package yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import yosoyo.aaahearhereprototype.HHServerClasses.HHComment;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUser;

/**
 * Created by adam on 02/03/16.
 */
public class HHCommentUserNested extends HHComment {

	HHUser user;

	protected HHCommentUserNested(long post_id, long user_id, String message, HHUser user){
		super(post_id, user_id, message);
		this.user = user;
	}

	public HHUser getUser(){
		return user;
	}

}
