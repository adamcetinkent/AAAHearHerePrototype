package com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import android.annotation.SuppressLint;

import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollow;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;

/**
 * Created by adam on 10/03/16.
 *
 * A {@link HHFollow} with a nested {@link HHUser} of its associated user.
 *
 * Only used when parsed from JSON.
 */
@SuppressWarnings("unused")
@SuppressLint("ParcelCreator")
public class HHFollowedUserNested extends HHFollow {

	private HHUser user;

	public HHFollowedUserNested(HHFollowedUserNested nested) {
		super(nested);
	}

	public HHUser getUser() {
		return user;
	}
}
