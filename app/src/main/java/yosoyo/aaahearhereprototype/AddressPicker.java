package yosoyo.aaahearhereprototype;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class AddressPicker extends Activity {

	public static final String TAG = "AddressPicker";

	public static final int REQUEST_CODE = 14070703;
	public static final String ADDRESS_JSON = "addressJson";
	public static final String ADDRESS_STRING = "addressString";
	public static final String GOOGLE_PLACE_ID = "googlePlaceID";

	private Address address;

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

	private PlaceArrayAdapter placeArrayAdapter;
	private final List<SimpleGooglePlace> places = new ArrayList<>();
	private final String[] addressOutput = new String[4];
	private String googlePlaceID = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_address_picker);
		this.setFinishOnTouchOutside(true);

		Intent intent = getIntent();
		String addressJson = intent.getStringExtra(ADDRESS_JSON);
		address = new Gson().fromJson(addressJson, Address.class);

		addressOutput[1] = address.getThoroughfare();

		txtLocation = (AutoCompleteTextView) findViewById(R.id.address_picker_txtLocation);
		txtStreet = (EditText) findViewById(R.id.address_picker_txtStreet);
		txtLocality = (EditText) findViewById(R.id.address_picker_txtLocality);
		txtCountry = (EditText) findViewById(R.id.address_picker_txtCountry);
		txtOutput = (TextView) findViewById(R.id.address_picker_txtOutput);

		btnLocation = (ToggleButton) findViewById(R.id.address_picker_btnLocation);
		btnStreet = (ToggleButton) findViewById(R.id.address_picker_btnStreet);
		btnLocality = (ToggleButton) findViewById(R.id.address_picker_btnLocality);
		btnCountry = (ToggleButton) findViewById(R.id.address_picker_btnCountry);
		btnContinue = (ImageButton) findViewById(R.id.address_picker_btnContinue);

		txtLocation.setText("");
		txtStreet.setText(address.getThoroughfare());
		txtLocality.setText(address.getLocality());
		txtCountry.setText(address.getCountryName());

		txtLocation.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				addressOutput[0] = s.toString();
				btnLocation.setChecked(true);
				updateOutput();
			}
		});

		placeArrayAdapter = new PlaceArrayAdapter(this, places);

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

		btnContinue.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String outputString = updateOutput();
				if (outputString.isEmpty())
					return;
				Intent resultIntent = new Intent();
				resultIntent.putExtra(ADDRESS_STRING, outputString);
				resultIntent.putExtra(GOOGLE_PLACE_ID, googlePlaceID);

				setResult(Activity.RESULT_OK, resultIntent);
				finish();
			}
		});

		if (ActivityCompat.checkSelfPermission(this,
											   Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(
			HolderActivity.mGoogleApiClient, null);
		result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
			@Override
			public void onResult(@NonNull PlaceLikelihoodBuffer placeLikelihoods) {
				places.clear();
				for (PlaceLikelihood placeLikelihood : placeLikelihoods){
					places.add(new SimpleGooglePlace(placeLikelihood.getPlace()));
				}
				placeLikelihoods.release();
				placeArrayAdapter = new PlaceArrayAdapter(AddressPicker.this, places);
				txtLocation.setAdapter(placeArrayAdapter);
				txtLocation.showDropDown();
			}
		});

		updateOutput();

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
			}
		}
		String outputString = sb.toString();
		txtOutput.setText(outputString);
		return outputString;
	}

	private static class PlaceArrayAdapter extends ArrayAdapter<SimpleGooglePlace> {

		private final Activity context;
		private final List<SimpleGooglePlace> places;

		public PlaceArrayAdapter(Activity context, List<SimpleGooglePlace> places) {
			super(context, R.layout.list_row_track, places);
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
