package com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import android.annotation.SuppressLint;

import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHTag;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;

/**
 * Created by adam on 02/03/16.
 *
 * A {@link HHTag} with a nested {@link HHUser} of its associated user.
 *
 * Only used when parsed from JSON.
 */
@SuppressWarnings("unused")
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
