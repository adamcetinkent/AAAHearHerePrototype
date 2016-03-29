package yosoyo.aaahearhereprototype.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;

/**
 * Created by adam on 18/03/16.
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

	protected void requestUserProfile(HHUser user){
		Bundle bundle = new Bundle();
		bundle.putLong(USER_ID, user.getID());
		fragmentChangeRequestListener.requestFragmentChange(
			FragmentChangeRequestListener.USER_PROFILE_REQUEST, bundle);
	}


	protected void requestFollowerList(HHUser user, int followerType){
		Bundle bundle = new Bundle();
		bundle.putLong(USER_ID, user.getID());
		bundle.putInt(FollowersListFragment.FOLLOWER_TYPE, followerType);
		fragmentChangeRequestListener.requestFragmentChange(
			FragmentChangeRequestListener.FOLLOWERS_LIST_REQUEST, bundle);
	}

}
