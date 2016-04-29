package com.yosoyo.aaahearhereprototype.Services.LocationService;

import android.content.BroadcastReceiver;
import android.os.Bundle;

/**
 * Created by adam on 28/04/16.
 */
public abstract class HHServiceBroadcastReceiver extends BroadcastReceiver {

	final Callback callback;

	interface Callback {
		void respond(Bundle bundle);
	}

	public HHServiceBroadcastReceiver(Callback callback) {
		this.callback = callback;
	}
}
