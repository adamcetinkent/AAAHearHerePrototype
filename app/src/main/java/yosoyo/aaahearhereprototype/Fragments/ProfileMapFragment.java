package yosoyo.aaahearhereprototype.Fragments;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import yosoyo.aaahearhereprototype.R;

/**
 * Created by adam on 31/03/2016.
 *
 * A dumb fragment that holds both a {@link ProfileFragment} and a {@link MapViewFragment} as children.
 */
public class ProfileMapFragment extends FeedbackFragment {

	private int profileType;
	public static final String KEY_PROFILE_TYPE = "profile_type";
	public static final int PROFILE_TYPE_CURRENT_USER = 0;
	public static final int PROFILE_TYPE_OTHER_USER = 1;

	public static final String KEY_USER_ID = "user_id";
	private long userID = -1;

	private ProfileFragment profileFragment;
	private MapViewFragment mapViewFragment;
	private Bundle profileFragmentBundle;
	private Bundle mapFragmentBundle;

	public static ProfileMapFragment newInstance(int profileType, long userID){
		ProfileMapFragment profileMapFragment = new ProfileMapFragment();

		Bundle arguments = new Bundle();
		arguments.putInt(KEY_PROFILE_TYPE, profileType);
		arguments.putLong(USER_ID, userID);
		profileMapFragment.setArguments(arguments);

		return profileMapFragment;
	}

	public static ProfileMapFragment newInstance(Bundle bundle){
		ProfileMapFragment profileMapFragment = new ProfileMapFragment();

		profileMapFragment.setProfileFragmentBundle(bundle);
		profileMapFragment.setMapFragmentBundle(bundle);

		return profileMapFragment;
	}

	public ProfileMapFragment() {
		// Required empty public constructor
	}

	private void setProfileFragmentBundle(Bundle bundle){
		this.profileFragmentBundle = bundle;
	}

	private void setMapFragmentBundle(Bundle bundle){
		this.mapFragmentBundle = bundle;
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
		if (mapViewFragment != null){
			mapFragmentBundle = mapViewFragment.getBundle();
			outState.putBundle(MapViewFragment.KEY_MAP_VIEW_FRAGMENT_BUNDLE, mapFragmentBundle);
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
			if (savedInstanceState.containsKey(MapViewFragment.KEY_MAP_VIEW_FRAGMENT_BUNDLE)){
				mapFragmentBundle = savedInstanceState.getBundle(MapViewFragment.KEY_MAP_VIEW_FRAGMENT_BUNDLE);
			}
		}

		mapViewFragment = MapViewFragment.newInstance(mapFragmentBundle);

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.fragment_profile_map_frameMap, mapViewFragment);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();

		if (profileFragmentBundle == null) {
			profileFragment = ProfileFragment.newInstance(ProfileFragment.PROFILE_TYPE_CURRENT_USER, userID);
		} else {
			profileFragment = ProfileFragment.newInstance(profileFragmentBundle);
		}

		profileFragment.setProfileMode(ProfileFragment.PROFILE_MODE_MAP);
		profileFragment.setMapFragment(mapViewFragment);

		ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.fragment_profile_map_frameProfile, profileFragment);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();

		return v;
	}

}
