package yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import android.annotation.SuppressLint;

import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHComment;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;

/**
 * Created by adam on 02/03/16.
 *
 * A {@link HHComment} with a nested {@link HHUser}.
 *
 * Only used when parsed from JSON.
 */
@SuppressWarnings("unused")
@SuppressLint("ParcelCreator")
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
