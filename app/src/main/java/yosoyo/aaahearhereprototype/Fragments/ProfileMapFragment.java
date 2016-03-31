package yosoyo.aaahearhereprototype.Fragments;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import yosoyo.aaahearhereprototype.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileMapFragment extends Fragment {

	private long userID = -1;

	private ProfileFragment profileFragment;

	public ProfileMapFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_profile_map, container, false);

		//if (userID == HHUser.getCurrentUserID()) {
		profileFragment = ProfileFragment.newInstance(ProfileFragment.PROFILE_TYPE_CURRENT_USER, userID);
		profileFragment.setProfileMode(ProfileFragment.PROFILE_MODE_MAP);
		/*} else {
			profileFragment = ProfileFragment.newInstance(ProfileFragment.PROFILE_TYPE_OTHER_USER, userID);
		}*/

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.fragment_profile_map_frameProfile, profileFragment);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();

		MapViewFragment mapViewFragment = new MapViewFragment();
		ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.fragment_profile_map_frameMap, mapViewFragment);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();

		return v;
	}

}
