package yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHComment;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;

/**
 * Created by adam on 02/03/16.
 */
public class HHCommentUserNested extends HHComment {

	private final HHUser user;

	protected HHCommentUserNested(long post_id, long user_id, String message, HHUser user){
		super(post_id, user_id, message);
		this.user = user;
	}

	public HHUser getUser(){
		return user;
	}

}
