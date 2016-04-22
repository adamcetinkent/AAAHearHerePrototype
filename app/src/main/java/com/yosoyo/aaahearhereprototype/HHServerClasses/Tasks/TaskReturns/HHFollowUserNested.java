package com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import android.annotation.SuppressLint;

import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollow;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;

/**
 * Created by adam on 10/03/16.
 *
 * A {@link HHFollow} with a nested {@link HHUser} of its associated followed user.
 *
 * Only used when parsed from JSON.
 */
@SuppressWarnings("unused")
@SuppressLint("ParcelCreator")
public class HHFollowUserNested extends HHFollow {

	private HHUser followed_user;

	public HHFollowUserNested(HHFollowUserNested nested) {
		super(nested);
	}

	public HHUser getUser() {
		return followed_user;
	}
}
