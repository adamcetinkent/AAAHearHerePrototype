package yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import android.annotation.SuppressLint;

import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollow;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;

/**
 * Created by adam on 10/03/16.
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
