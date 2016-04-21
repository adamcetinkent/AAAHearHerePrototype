package yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import android.annotation.SuppressLint;

import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequest;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;

/**
 * Created by adam on 10/03/16.
 */
@SuppressWarnings("unused")
@SuppressLint("ParcelCreator")
public class HHFollowedRequestUserNested extends HHFollowRequest {

	private HHUser user;

	public HHFollowedRequestUserNested(HHFollowedRequestUserNested nested) {
		super(nested);
	}

	public HHUser getUser() {
		return user;
	}
}
