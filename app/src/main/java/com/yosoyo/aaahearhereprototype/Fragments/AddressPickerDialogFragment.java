package com.yosoyo.aaahearhereprototype.Fragments;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.yosoyo.aaahearhereprototype.Activities.HolderActivity;
import com.yosoyo.aaahearhereprototype.GoogleClasses.SimpleGooglePlace;
import com.yosoyo.aaahearhereprototype.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adam on 01/06/16.
 */
public class AddressPickerDialogFragment extends DialogFragment {

	private static final String TAG = AddressPickerDialogFragment.class.getSimpleName();

	private Address address;
	private Callback callback;

	private AutoCompleteTextView txtLocation;
	private EditText txtStreet;
	private EditText txtLocality;
	private EditText txtCountry;
	private TextView txtOutput;
	private ToggleButton btnLocation;
	private ToggleButton btnStreet;
	private ToggleButton btnLocality;
	private ToggleButton btnCountry;
	private ImageButton btnContinue;
	private ProgressBar txtLocationProgressBar;

	private PlaceArrayAdapter placeArrayAdapter;
	private final List<SimpleGooglePlace> places = new ArrayList<>();
	private final String[] addressOutput = new String[4];
	private String[] addressInput = new String[4];
	private boolean[] addressInputToggles = new boolean[4];
	private String googlePlaceID = "";

	private boolean hasBeenDismissed = false;

	interface Callback {
		void setPlaceName(String placeName, String[] addressOutput, boolean[] addressToggles);
		void setGooglePlaceID(String googlePlaceID);
	}

	public static AddressPickerDialogFragment newInstance(Address address,
														  String[] addressInput,
														  boolean[] addressInputToggles,
														  Callback callback){
		AddressPickerDialogFragment addressPickerDialogFragment = new AddressPickerDialogFragment();
		addressPickerDialogFragment.address = address;
		addressPickerDialogFragment.addressInput = addressInput;
		addressPickerDialogFragment.addressInputToggles = addressInputToggles;
		addressPickerDialogFragment.callback = callback;
		return addressPickerDialogFragment;
	}

	public AddressPickerDialogFragment() {
		// Required empty public constructor
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.dialog_fragment_address_picker, container, false);

		addressOutput[1] = address.getThoroughfare();

		txtLocation = (AutoCompleteTextView) view.findViewById(R.id.fragment_address_picker_txtLocation);
		txtStreet = (EditText) view.findViewById(R.id.fragment_address_picker_txtStreet);
		txtLocality = (EditText) view.findViewById(R.id.fragment_address_picker_txtLocality);
		txtCountry = (EditText) view.findViewById(R.id.fragment_address_picker_txtCountry);
		txtOutput = (TextView) view.findViewById(R.id.fragment_address_picker_txtOutput);

		btnLocation = (ToggleButton) view.findViewById(R.id.fragment_address_picker_btnLocation);
		btnStreet = (ToggleButton) view.findViewById(R.id.fragment_address_picker_btnStreet);
		btnLocality = (ToggleButton) view.findViewById(R.id.fragment_address_picker_btnLocality);
		btnCountry = (ToggleButton) view.findViewById(R.id.fragment_address_picker_btnCountry);
		btnContinue = (ImageButton) view.findViewById(R.id.fragment_address_picker_btnContinue);

		txtLocation.setText(addressInput[0]);
		txtStreet.setText(address.getThoroughfare());
		txtLocality.setText(address.getLocality());
		txtCountry.setText(address.getCountryName());

		txtLocation.addTextChangedListener(new TextWatcher() {
			boolean delete = false;

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (googlePlaceID != null && !googlePlaceID.isEmpty()){
					delete = true;
					googlePlaceID = "";
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

				if (delete){
					s.clear();
					addressOutput[0] = "";
					btnLocation.setChecked(false);
					delete = false;
				} else {
					addressOutput[0] = s.toString();
					btnLocation.setChecked(true);
				}
				updateOutput();
			}
		});

		placeArrayAdapter = new PlaceArrayAdapter(getActivity(), places);

		txtLocation.setAdapter(placeArrayAdapter);
		txtLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				googlePlaceID = places.get(position).getID();
			}
		});

		btnLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btnLocation.isChecked()) {
					addressOutput[0] = txtLocation.getText().toString();
					txtLocation.setEnabled(true);
				} else {
					addressOutput[0] = "";
					txtLocation.setEnabled(false);
				}
				updateOutput();
			}
		});

		btnStreet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btnStreet.isChecked()) {
					addressOutput[1] = address.getThoroughfare();
				} else {
					addressOutput[1] = "";
				}
				updateOutput();
			}
		});

		btnLocality.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btnLocality.isChecked()) {
					addressOutput[2] = address.getLocality();
				} else {
					addressOutput[2] = "";
				}
				updateOutput();
			}
		});

		btnCountry.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btnCountry.isChecked()) {
					addressOutput[3] = address.getCountryName();
				} else {
					addressOutput[3] = "";
				}
				updateOutput();
			}
		});

		if (addressInputToggles != null){
			btnLocation.setChecked(addressInputToggles[0]);
			btnStreet.setChecked(addressInputToggles[1]);
			btnLocality.setChecked(addressInputToggles[2]);
			btnCountry.setChecked(addressInputToggles[3]);
			for (int i = 0; i < 4; i++)
				if (addressInputToggles[i]) addressOutput[i] = addressInput[i];
		}

		btnContinue.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String outputString = updateOutput();
				if (outputString.isEmpty())
					return;

				callback.setPlaceName(outputString, addressOutput, addressInputToggles);
				callback.setGooglePlaceID(googlePlaceID);
				dismiss();
			}
		});

		if (ActivityCompat.checkSelfPermission(
			getActivity(),
			Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
			&& ActivityCompat.checkSelfPermission(
			getActivity(),
			Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, HolderActivity.LOCATION_PERMISSIONS);

			return view;
		}

		txtLocationProgressBar = (ProgressBar) view.findViewById(R.id.fragment_address_picker_txtLocationProgressBar);
		txtLocation.setEnabled(false);
		final PendingResult<PlaceLikelihoodBuffer> result =
			Places.PlaceDetectionApi.getCurrentPlace(HolderActivity.mGoogleApiClient, null);
		result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
			@Override
			public void onResult(@NonNull PlaceLikelihoodBuffer placeLikelihoods) {
				if (hasBeenDismissed)
					return;

				places.clear();
				for (PlaceLikelihood placeLikelihood : placeLikelihoods){
					places.add(new SimpleGooglePlace(placeLikelihood.getPlace()));
				}
				placeLikelihoods.release();
				placeArrayAdapter = new PlaceArrayAdapter(getActivity(), places);
				txtLocation.setEnabled(true);
				txtLocationProgressBar.setVisibility(View.INVISIBLE);
				txtLocation.setAdapter(placeArrayAdapter);
				txtLocation.showDropDown();
			}
		});

		updateOutput();

		return view;
	}

	private String updateOutput(){
		boolean started = false;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 4; i++){
			if (!(addressOutput[i] == null || addressOutput[i].isEmpty())) {
				if (started)
					sb.append(", ");
				sb.append(addressOutput[i]);
				started = true;
				if (addressInputToggles == null)
					addressInputToggles = new boolean[4];
				addressInputToggles[i] = true;
			}
		}
		String outputString = sb.toString();
		txtOutput.setText(outputString);
		return outputString;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		hasBeenDismissed = true;
	}

	private static class PlaceArrayAdapter extends ArrayAdapter<SimpleGooglePlace> {

		private final Activity context;
		private final List<SimpleGooglePlace> places;

		public PlaceArrayAdapter(Activity context, List<SimpleGooglePlace> places) {
			super(context, R.layout.list_row_spotify_search_results, places);
			this.context = context;
			this.places = places;
		}

		private static class ViewHolder {
			int position;
			SimpleGooglePlace place;
			TextView txtPlace;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;

			if (convertView == null) {
				LayoutInflater inflater = context.getLayoutInflater();
				convertView = inflater.inflate(R.layout.list_row_place, parent, false);

				viewHolder = new ViewHolder();

				viewHolder.position = position;
				viewHolder.place = places.get(position);

				viewHolder.txtPlace = (TextView) convertView.findViewById(R.id.list_row_place_txtPlace);

				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();
				viewHolder.position = position;
				viewHolder.place = places.get(position);
			}

			viewHolder.txtPlace.setText(viewHolder.place.getName());

			return convertView;
		}
	}

}
