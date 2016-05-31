package com.yosoyo.aaahearhereprototype.Services.LocationService;

import android.Manifest;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.yosoyo.aaahearhereprototype.AsyncDataManager;
import com.yosoyo.aaahearhereprototype.HHNotificationsManager;
import com.yosoyo.aaahearhereprototype.R;

public class LocationListenerService extends Service implements HHLocationListener.HHLocationListenerCallback {

	private static final String TAG = "LocationListenerService";
	private LocationManager locationManager;
	private static final int LOCATION_INTERVAL = 1000;
	private static final float LOCATION_DISTANCE = 10f;
	public static final String LOCATION_UPDATE = "locationUpdate";
	private static final int NOTIFICATION_ID = 17441503;
	private long userID;
	private String authToken;
	public static final String USER_ID = TAG+"userID";
	public static final String AUTH_TOKEN = TAG+"authToken";

	private HHNotificationsManager notificationsManager;

	@Override
	public void returnNewLocation(final Location location) {
		final Intent intent = new Intent();
		intent.setAction(LOCATION_UPDATE);
		intent.putExtra(HHLocationBroadcastReceiver.DOUBLE_LATITUDE, location.getLatitude());
		intent.putExtra(HHLocationBroadcastReceiver.DOUBLE_LONGITUDE, location.getLongitude());
		sendBroadcast(intent);

		Log.d(TAG, "returnNewLocation:: userID: " + userID);

		AsyncDataManager.getPostsAtLocation(
			this,
			location,
			userID,
			authToken,
			notificationsManager.getPostsAtLocationCallback
		);

		AsyncDataManager.getNotifications(
			authToken,
			notificationsManager.getNotificationsCallback
		);

	}

	private final HHLocationListener[] locationListeners = new HHLocationListener[]{
		new HHLocationListener(LocationManager.GPS_PROVIDER, this),
		new HHLocationListener(LocationManager.NETWORK_PROVIDER, this),
		new HHLocationListener(LocationManager.PASSIVE_PROVIDER, this)
	};

	@Override
	public IBinder onBind(Intent intent) {
		/*// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");*/
		Log.d(TAG, "onBind");
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		if (intent != null) {
			if (intent.hasExtra(USER_ID)) {
				userID = intent.getLongExtra(USER_ID, -1);
			}
			if (intent.hasExtra(AUTH_TOKEN)){
				authToken = intent.getStringExtra(AUTH_TOKEN);
			}
		}
		Log.d(TAG, "userID: "+userID);
		super.onStartCommand(intent, flags, startId);
		return START_REDELIVER_INTENT;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		initialiseLocationManager();
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED
			&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {
			return;
		}
		try {
			locationManager
				.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL,
										LOCATION_DISTANCE, locationListeners[1]);
		} catch (SecurityException e){
			e.printStackTrace();
		} catch (IllegalArgumentException e){
			e.printStackTrace();
		}
		try {
			locationManager
				.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, LOCATION_INTERVAL,
										LOCATION_DISTANCE, locationListeners[2]);
		} catch (SecurityException e){
			e.printStackTrace();
		} catch (IllegalArgumentException e){
			e.printStackTrace();
		}

		notificationsManager = new HHNotificationsManager(
			getApplicationContext(),
			(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE),
			authToken,
			getString(R.string.app_name));
	}

	private void initialiseLocationManager(){
		Log.d(TAG, "initialiseLocationManager");
		if (locationManager == null){
			locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		}
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED
			&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {
			return;
		}
		if (locationManager != null){
			for (HHLocationListener locationListener : locationListeners) {
				try {
					locationManager.removeUpdates(locationListener);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
