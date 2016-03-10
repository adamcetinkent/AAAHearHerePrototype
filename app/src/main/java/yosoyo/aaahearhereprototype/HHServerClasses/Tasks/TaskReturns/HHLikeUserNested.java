package yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import yosoyo.aaahearhereprototype.HHServerClasses.HHLike;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUser;

/**
 * Created by adam on 02/03/16.
 */
public class HHLikeUserNested extends HHLike {

	HHUser user;

	protected HHLikeUserNested(HHLikeUserNested nested) {
		super(nested);
	}

	public HHUser getUser(){
		return user;
	}

}
