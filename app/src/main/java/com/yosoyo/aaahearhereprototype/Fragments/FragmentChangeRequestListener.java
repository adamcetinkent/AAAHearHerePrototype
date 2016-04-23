package com.yosoyo.aaahearhereprototype.Fragments;

import android.os.Bundle;

/**
 * Created by adam on 16/03/16.
 *
 * Listener for {@link FeedbackFragment}s to communicate with their parent activity
 */
public interface FragmentChangeRequestListener {
	int MAP_VIEW_REQUEST = 1;
	int USER_PROFILE_REQUEST = 2;
	int FOLLOWERS_LIST_REQUEST = 3;

	void onLoginSuccess();
	void requestFragmentChange(int fragmentChange, Bundle bundle);
	void requestProfileModeSwitch(int profileMode, long userID, Bundle bundle);
}
