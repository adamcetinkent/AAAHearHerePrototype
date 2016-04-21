package yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import android.annotation.SuppressLint;

import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHTag;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;

/**
 * Created by adam on 02/03/16.
 */
@SuppressLint("ParcelCreator")
public class HHTagUserNested extends HHTag {

	private HHUser user;

	protected HHTagUserNested(HHTagUserNested nested) {
		super(nested);
	}


	public HHUser getUser(){
		return user;
	}

}
