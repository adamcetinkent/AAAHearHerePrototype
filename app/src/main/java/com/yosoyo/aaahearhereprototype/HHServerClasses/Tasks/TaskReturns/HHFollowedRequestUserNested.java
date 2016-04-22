package com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import android.annotation.SuppressLint;

import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequest;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;

/**
 * Created by adam on 10/03/16.
 *
 * A {@link HHFollowRequest} with a nested {@link HHUser}.
 *
 * Only used when parsed from JSON.
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
