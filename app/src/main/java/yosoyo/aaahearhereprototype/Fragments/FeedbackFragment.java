package yosoyo.aaahearhereprototype.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import yosoyo.aaahearhereprototype.HHServerClasses.HHUser;

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

	protected void requestUserFeed(HHUser user){
		Bundle bundle = new Bundle();
		bundle.putLong(USER_ID, user.getID());
		fragmentChangeRequestListener.requestFragmentChange(
			FragmentChangeRequestListener.USER_FEED_REQUEST, bundle);
	}

}
