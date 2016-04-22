package yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import android.annotation.SuppressLint;

import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHLike;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;

/**
 * Created by adam on 02/03/16.
 *
 * A {@link HHLike} with a nested {@link HHUser} of its associated user.
 *
 * Only used when parsed from JSON.
 */
@SuppressWarnings("unused")
@SuppressLint("ParcelCreator")
public class HHLikeUserNested extends HHLike {

	private HHUser user;

	protected HHLikeUserNested(HHLikeUserNested nested) {
		super(nested);
	}

	public HHUser getUser(){
		return user;
	}

}
