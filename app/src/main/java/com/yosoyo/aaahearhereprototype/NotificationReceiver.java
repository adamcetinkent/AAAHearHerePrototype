package com.yosoyo.aaahearhereprototype;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class NotificationReceiver extends Activity {
	private static final String TAG = "NotificationReceiver";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification_receiver);

		Intent intent = getIntent();
		switch (intent.getAction()){
			case Intent.ACTION_SEND:{

				Log.d(TAG, "NEW INTENT: "+intent.toString());
				String sharedTrackResult = intent.getStringExtra(Intent.EXTRA_TEXT);
				String PREFIX = "https://open.spotify.com/track/";
				String trackID = sharedTrackResult.substring(PREFIX.length());
				Log.d(TAG, "SHARED TRACK: "+trackID);
				Toast.makeText(this, "SHARED TRACK: "+trackID, Toast.LENGTH_LONG).show();

				//TODO : send HolderActivity to post page

				break;
			}
		}

	}

}
