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
public class ProfileMapFragment extends FeedbackFragment {

	private int profileType;
	public static final String KEY_PROFILE_TYPE = "profile_type";
	public static final int PROFILE_TYPE_CURRENT_USER = 0;
	public static final int PROFILE_TYPE_OTHER_USER = 1;

	public static final String KEY_USER_ID = "user_id";
	private long userID = -1;

	private ProfileFragment profileFragment;
	private Bundle profileFragmentBundle;

	public static ProfileMapFragment newInstance(int profileType, long userID){
		ProfileMapFragment profileMapFragment = new ProfileMapFragment();

		Bundle arguments = new Bundle();
		arguments.putInt(KEY_PROFILE_TYPE, profileType);
		arguments.putLong(USER_ID, userID);
		profileMapFragment.setArguments(arguments);

		return profileMapFragment;
	}

	public ProfileMapFragment() {
		// Required empty public constructor
	}

	public void setProfileFragmentBundle(Bundle bundle){
		this.profileFragmentBundle = bundle;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(KEY_USER_ID, userID);
		outState.putInt(KEY_PROFILE_TYPE, profileType);
		if (profileFragment != null){
			profileFragmentBundle = profileFragment.getBundle();
			outState.putBundle(ProfileFragment.KEY_PROFILE_FRAGMENT_BUNDLE, profileFragmentBundle);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_profile_map, container, false);

		if (savedInstanceState != null){
			userID = savedInstanceState.getLong(KEY_USER_ID);
			profileType = savedInstanceState.getInt(KEY_PROFILE_TYPE);
			if (savedInstanceState.containsKey(ProfileFragment.KEY_PROFILE_FRAGMENT_BUNDLE)){
				profileFragmentBundle = savedInstanceState.getBundle(ProfileFragment.KEY_PROFILE_FRAGMENT_BUNDLE);
			}
		}

		if (profileFragmentBundle == null) {
			profileFragment = ProfileFragment.newInstance(ProfileFragment.PROFILE_TYPE_CURRENT_USER, userID);
		} else {
			profileFragment = ProfileFragment.newInstance(profileFragmentBundle);
		}

		profileFragment.setProfileMode(ProfileFragment.PROFILE_MODE_MAP);

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
