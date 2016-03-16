package yosoyo.aaahearhereprototype.Fragments;

import android.os.Bundle;

/**
 * Created by adam on 16/03/16.
 */
public interface FragmentChangeRequestListener {
	public static final int MAP_VIEW_REQUEST = 1;
	public static final int USER_FEED_REQUEST = 2;

	void requestFragmentChange(int fragmentChange, Bundle bundle);
}
