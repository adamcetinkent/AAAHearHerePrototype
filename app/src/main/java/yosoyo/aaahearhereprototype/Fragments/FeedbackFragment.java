package yosoyo.aaahearhereprototype.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;

/**
 * Created by adam on 18/03/16.
 *
 * A fragment that can communicate with its parent activity to transfer data between fragments.
 */
public abstract class FeedbackFragment extends Fragment {

	private FragmentChangeRequestListener fragmentChangeRequestListener;
	public static final String USER_ID = "user_id";

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try {
			fragmentChangeRequestListener = (FragmentChangeRequestListener) context;
		} catch (ClassCastException e){
			e.printStackTrace();
		}
	}

	/**
	 * Switch to a fragment displaying the selected user
	 *
	 * @param user : the {@link HHUser} to display
	 */
	void requestUserProfile(HHUser user){
		Bundle bundle = new Bundle();
		bundle.putLong(USER_ID, user.getID());
		fragmentChangeRequestListener.requestFragmentChange(
			FragmentChangeRequestListener.USER_PROFILE_REQUEST, bundle);
	}

	/**
	 * Switch to fragment displaying the selected user's followers
	 *
	 * @param user			: the {@link HHUser} whose followers to display
	 * @param followerType	: static constants in {@link FollowersListFragment}
	 */
	void requestFollowerList(HHUser user, int followerType){
		Bundle bundle = new Bundle();
		bundle.putLong(USER_ID, user.getID());
		bundle.putInt(FollowersListFragment.FOLLOWER_TYPE, followerType);
		fragmentChangeRequestListener.requestFragmentChange(
			FragmentChangeRequestListener.FOLLOWERS_LIST_REQUEST, bundle);
	}

	/**
	 * Switch between profile view modes
	 *
	 * @param profileType	: static constants in {@link ProfileFragment}
	 * @param userID		: ID of user whose profile is being switched
	 * @param bundle		: bundled profile data to pass between modes
	 */
	void requestProfileModeSwitch(int profileType, long userID, Bundle bundle){
		fragmentChangeRequestListener.requestProfileModeSwitch(profileType, userID, bundle);
	}

}
