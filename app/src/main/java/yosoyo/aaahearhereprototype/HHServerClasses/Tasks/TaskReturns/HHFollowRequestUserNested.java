package yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import android.annotation.SuppressLint;

import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequest;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;

/**
 * Created by adam on 10/03/16.
 */
@SuppressLint("ParcelCreator")
public class HHFollowRequestUserNested extends HHFollowRequest {

	private HHUser requested_user;

	public HHFollowRequestUserNested(HHFollowRequestUserNested nested) {
		super(nested);
	}

	public HHUser getUser() {
		return requested_user;
	}
}
