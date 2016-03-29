package yosoyo.aaahearhereprototype.Fragments;

import android.os.Bundle;

/**
 * Created by adam on 16/03/16.
 */
public interface FragmentChangeRequestListener {
	public static final int MAP_VIEW_REQUEST = 1;
	public static final int USER_PROFILE_REQUEST = 2;
	public static final int FOLLOWERS_LIST_REQUEST = 3;

	void requestFragmentChange(int fragmentChange, Bundle bundle);
}
