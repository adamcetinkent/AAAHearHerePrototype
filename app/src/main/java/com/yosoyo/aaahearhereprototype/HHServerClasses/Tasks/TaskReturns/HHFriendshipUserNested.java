package com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import android.annotation.SuppressLint;

import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFriendship;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;

/**
 * Created by adam on 10/03/16.
 *
 * A {@link HHFriendship} with a nested {@link HHUser} of its friend user.
 *
 * Only used when parsed from JSON.
 */
@SuppressWarnings("unused")
@SuppressLint("ParcelCreator")
public class HHFriendshipUserNested extends HHFriendship {

	private HHUser friend_user;

	public HHFriendshipUserNested(HHFriendshipUserNested nested) {
		super(nested);
	}

	public HHUser getUser() {
		return friend_user;
	}
}
