package yosoyo.aaahearhereprototype.Fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import yosoyo.aaahearhereprototype.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment {


	public MapsFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_map, container, false);
	}

}
