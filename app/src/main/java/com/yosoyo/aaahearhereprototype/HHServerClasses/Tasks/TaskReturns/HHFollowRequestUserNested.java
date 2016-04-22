package com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import android.annotation.SuppressLint;

import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequest;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;

/**
 * Created by adam on 10/03/16.
 *
 * A {@link HHFollowRequest} with a nested {@link HHUser} of its associated requested user.
 *
 * Only used when parsed from JSON.
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
