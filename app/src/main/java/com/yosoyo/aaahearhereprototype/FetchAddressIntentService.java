package com.yosoyo.aaahearhereprototype;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by adam on 23/02/16.
 *
 * Gets the address of a location
 */
public class FetchAddressIntentService extends IntentService {

	private static final String TAG = "FetchAddressIS";
	private ResultReceiver resultReceiver;

	public FetchAddressIntentService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String errorMessage = "";

		resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);

		if (resultReceiver == null) {
			errorMessage = getString(R.string.no_location_data_provided);
			Log.e(TAG, errorMessage);
			deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
			return;
		}

		Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);

		Geocoder geocoder = new Geocoder(this, Locale.getDefault());

		List<Address> addresses = null;

		try {
			addresses = geocoder.getFromLocation(
				location.getLatitude(),
				location.getLongitude(),
				1);
		} catch (IOException e) {
			errorMessage = getString(R.string.service_not_available);
			Log.e(TAG, errorMessage, e);
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			errorMessage = getString(R.string.invalid_lat_long_used);
			Log.e(TAG, errorMessage + ". " + "Lat = " + location.getLatitude() +
				", Lon = " + location.getLongitude(), e);
		}

		if (addresses == null || addresses.size() == 0) {
			if (errorMessage.isEmpty()) {
				errorMessage = getString(R.string.no_address_found);
				Log.e(TAG, errorMessage);
			}
			deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
		} else {
			Address address = addresses.get(0);
			String addressAsJson = new Gson().toJson(address);
			Log.i(TAG, getString(R.string.address_found));
			deliverResultToReceiver(Constants.SUCCESS_RESULT, addressAsJson);
		}
	}

	private void deliverResultToReceiver(int resultCode, String addressAsJson){
		Bundle bundle = new Bundle();
		bundle.putString(Constants.RESULT_DATA_KEY, addressAsJson);
		resultReceiver.send(resultCode, bundle);
	}


	public final class Constants {
		public static final int SUCCESS_RESULT = 0;
		public static final int FAILURE_RESULT = 1;
		public static final String PACKAGE_NAME = "com.yosoyo.aaaherehereprototype";
		public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
		public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
		public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
	}
}
