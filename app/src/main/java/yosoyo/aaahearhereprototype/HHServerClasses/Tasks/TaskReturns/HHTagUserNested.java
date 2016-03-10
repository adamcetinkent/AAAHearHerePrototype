package yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import yosoyo.aaahearhereprototype.HHServerClasses.HHTag;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUser;

/**
 * Created by adam on 02/03/16.
 */
public class HHTagUserNested extends HHTag {

	HHUser user;

	protected HHTagUserNested(HHTagUserNested nested) {
		super(nested);
	}


	public HHUser getUser(){
		return user;
	}

}
