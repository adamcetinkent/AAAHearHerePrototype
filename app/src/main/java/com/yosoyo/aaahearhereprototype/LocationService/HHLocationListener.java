package com.yosoyo.aaahearhereprototype.LocationService;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by adam on 15/03/16.
 *
 * TODO: REMEMBER WHAT THIS DOES?!
 */
class HHLocationListener implements android.location.LocationListener {
	public static final String TAG = "HHLocationListener";

	private final Location lastLocation;
	private final HHLocationListenerCallback callback;

	public interface HHLocationListenerCallback {
		void returnNewLocation(Location location);
	}

	public HHLocationListener(String provider, HHLocationListenerCallback callback) {
		Log.d(TAG, "HHLocationListener: " + provider);
		lastLocation = new Location(provider);
		this.callback = callback;
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG, "onLocationChanged: " + location);
		lastLocation.set(location);
		callback.returnNewLocation(location);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d(TAG, "onStatusChanged: " + provider + " " + status);
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d(TAG, "onProviderEnabled: " + provider);
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d(TAG, "onProviderDisabled: " + provider);
	}
}