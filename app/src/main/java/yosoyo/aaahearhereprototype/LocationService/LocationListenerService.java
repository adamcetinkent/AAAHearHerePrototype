package yosoyo.aaahearhereprototype.LocationService;

import android.Manifest;
import android.app.Notification;
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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import yosoyo.aaahearhereprototype.AsyncDataManager;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHCachedSpotifyTrack;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPostFull;
import yosoyo.aaahearhereprototype.R;

public class LocationListenerService extends Service implements HHLocationListener.HHLocationListenerCallback {

	private static final String TAG = "LocationListenerService";
	private LocationManager locationManager;
	private static final int LOCATION_INTERVAL = 1000;
	private static final float LOCATION_DISTANCE = 10f;
	public static final String LOCATION_UPDATE = "locationUpdate";
	private static int NOTIFICATION_ID = 17441503;
	private static long userID;
	public static final String USER_ID = "userID";

	@Override
	public void returnNewLocation(final Location location) {
		Intent intent = new Intent();
		intent.setAction(LOCATION_UPDATE);
		intent.putExtra(HHBroadcastReceiver.DOUBLE_LATITUDE, location.getLatitude());
		intent.putExtra(HHBroadcastReceiver.DOUBLE_LONGITUDE, location.getLongitude());
		sendBroadcast(intent);

		Log.d(TAG, "returnNewLocation:: userID: " + userID);

		AsyncDataManager.getPostsAtLocation(
			this,
			location,
			userID,
			new AsyncDataManager.GetPostsAtLocationCallback() {
				@Override
				public void returnPostsAtLocation(List<HHPostFull> returnedPosts) {
					Log.d(TAG, (returnedPosts == null) ? "null" : returnedPosts.toString());
					if (returnedPosts != null && returnedPosts.size() > 0) {

						Collections.sort(returnedPosts, new Comparator<HHPostFull>() {
							@Override
							public int compare(HHPostFull lhs, HHPostFull rhs) {
								double lhsR2 = Math
									.pow(lhs.getPost().getLat() - location.getLatitude(), 2)
									+ Math.pow(lhs.getPost().getLon() - location.getLongitude(), 2);
								double rhsR2 = Math
									.pow(rhs.getPost().getLat() - location.getLatitude(), 2)
									+ Math.pow(rhs.getPost().getLon() - location.getLongitude(), 2);
								double result = lhsR2 - rhsR2;

								if (result > 0)
									return 1;
								if (result < 0)
									return -1;

								return 0;
							}
						});

						final HHPostFull post = returnedPosts.get(0);

						AsyncDataManager.getSpotifyTrack(
							getApplicationContext(),
							post.getPost().getTrack(),
							new AsyncDataManager.GetSpotifyTrackCallback() {
								@Override
								public void returnSpotifyTrack(HHCachedSpotifyTrack cachedSpotifyTrack) {
									String notificationText = post.getUser().getName()
										+ " posted " + cachedSpotifyTrack.getName()
										+ " at " + post.getPost().getPlaceName();

									Notification notification = new Notification.Builder(
										getApplicationContext())
										.setSmallIcon(R.mipmap.ic_launcher)
										.setContentTitle(getString(R.string.app_name))
										.setContentText(notificationText)
										.setAutoCancel(true)
										.setPriority(Notification.PRIORITY_DEFAULT)
										.setDefaults(Notification.DEFAULT_VIBRATE)
										.build();

									NotificationManager notificationManager = (NotificationManager) getSystemService(
										Context.NOTIFICATION_SERVICE);
									notificationManager.notify(NOTIFICATION_ID, notification);
								}
							}
						);
					}
				}
			});

	}

	HHLocationListener[] locationListeners = new HHLocationListener[]{
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
			for (int i = 0; i < locationListeners.length; i++){
				try {
					locationManager.removeUpdates(locationListeners[i]);
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}
}
