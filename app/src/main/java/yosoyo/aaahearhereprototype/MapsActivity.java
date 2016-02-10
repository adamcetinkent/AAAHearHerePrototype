package yosoyo.aaahearhereprototype;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMapClickListener,
	GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	private GoogleMap mMap; // Might be null if Google Play services APK is not available.

	private GoogleApiClient mGoogleApiClient;
	private Location lastLocation;

	private String artistName;
	private int numMarkers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
		setUpMapIfNeeded();
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			this.artistName = extras.getString("artistName");
		}

		// Create an instance of GoogleAPIClient.
		if (mGoogleApiClient == null) {
			mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	/**
	 * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
	 * installed) and the map has not already been instantiated.. This will ensure that we only ever
	 * call {@link #setUpMap()} once when {@link #mMap} is not null.
	 * <p/>
	 * If it isn't installed {@link SupportMapFragment} (and
	 * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
	 * install/update the Google Play services APK on their device.
	 * <p/>
	 * A user can return to this FragmentActivity after following the prompt and correctly
	 * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
	 * have been completely destroyed during this process (it is likely that it would only be
	 * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
	 * method in {@link #onResume()} to guarantee that it will be called.
	 */
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
				.getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
			}
		}
	}

	/**
	 * This is where we can add markers or lines, add listeners or move the camera. In this case, we
	 * just add a marker near Africa.
	 * <p/>
	 * This should only be called once and when we are sure that {@link #mMap} is not null.
	 */
	private void setUpMap() {
		//mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

		mMap.setMyLocationEnabled(true);

		mMap.setOnMapClickListener(this);

		/*Location myLocation = mMap.getMyLocation();
		LatLng myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
		mMap.addMarker(new MarkerOptions().position(myLatLng).title(artistName));*/
	}

	@Override
	public void onMapClick(LatLng latLng) {
		mMap.addMarker(new MarkerOptions().position(latLng).title("Marker " + numMarkers++));
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		lastLocation = LocationServices.FusedLocationApi.getLastLocation(
			mGoogleApiClient);
		LatLng myLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
		mMap.addMarker(new MarkerOptions().position(myLatLng).title(artistName));
	}

	protected void onStart() {
		mGoogleApiClient.connect();
		super.onStart();
	}

	protected void onStop() {
		mGoogleApiClient.disconnect();
		super.onStop();
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}
}

