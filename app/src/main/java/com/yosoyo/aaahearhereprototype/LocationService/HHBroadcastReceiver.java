package com.yosoyo.aaahearhereprototype.LocationService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by adam on 15/03/16.
 *
 * A receiver for new location results from the {@link LocationListenerService}
 */
public class HHBroadcastReceiver extends BroadcastReceiver {

	public static final String DOUBLE_LATITUDE = "latitude";
	public static final String DOUBLE_LONGITUDE = "longitude";
	private final HHBroadCastReceiverCallback callback;

	public interface HHBroadCastReceiverCallback{
		void returnNewLocation(double lat, double lng);
	}

	public HHBroadcastReceiver(HHBroadCastReceiverCallback callback) {
		this.callback = callback;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		double lat = intent.getDoubleExtra(DOUBLE_LATITUDE, 0);
		double lng = intent.getDoubleExtra(DOUBLE_LONGITUDE, 0);

		callback.returnNewLocation(lat, lng);

	}

}
