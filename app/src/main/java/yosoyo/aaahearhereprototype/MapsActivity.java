package yosoyo.aaahearhereprototype;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;

import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;
import yosoyo.aaahearhereprototype.TestServerClasses.TestCreatePostTask;
import yosoyo.aaahearhereprototype.TestServerClasses.TestGetPostsTask;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPost;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMapClickListener,
	GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, TestCreatePostTask.TestCreatePostTaskCallback, TestGetPostsTask.TestGetPostsTaskCallback, SpotifyAPIRequestTrack.SpotifyAPIRequestTrackCallback, GoogleMap.OnInfoWindowClickListener {
	private static final String TAG = "MapsActivity";

	private GoogleMap mMap; // Might be null if Google Play services APK is not available.

	private GoogleApiClient mGoogleApiClient;
	private Location lastLocation;

	//private String trackName;
	//private String trackDesc;
	private SpotifyTrack newTrack;
	//private int numMarkers;
	private TestPost[] testPosts;
	private SpotifyTrack currentTrack;
	private MediaPlayer mediaPlayer = new MediaPlayer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
		setUpMapIfNeeded();
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			//this.trackName = extras.getString(SearchResultsActivity.TRACK_NAME);
			//this.trackDesc = extras.getString(SearchResultsActivity.TRACK_DESC);
			this.newTrack = new Gson().fromJson(extras.getString(SearchResultsActivity.TRACK_JSON),
											 SpotifyTrack.class);
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

		mMap.setMyLocationEnabled(true);

		mMap.setOnMapClickListener(this);

		mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

		mMap.setOnInfoWindowClickListener(this);

	}

	@Override
	public void onMapClick(LatLng latLng) {
		//mMap.addMarker(new MarkerOptions().position(latLng).title("Marker " + numMarkers++));
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		lastLocation = LocationServices.FusedLocationApi.getLastLocation(
			mGoogleApiClient);
		LatLng myLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
		/*mMap.addMarker(
			new MarkerOptions().position(myLatLng).title(trackName).snippet(trackDesc));*/
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new
																	 CameraPosition.Builder()
																	 .target(myLatLng).zoom(15)
																	 .build()), 1000, null);

		if (newTrack != null){
			TestPost testPost = new TestPost(1, newTrack.getID(), myLatLng.latitude, myLatLng.longitude, "OMG!");
			TestCreatePostTask testCreatePostTask = new TestCreatePostTask(this, testPost);
			testCreatePostTask.execute();
		} else {
			getAllPosts();
		}

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

	@Override
	public void processFinish(Boolean success) {
		Log.d(TAG, "Successful post!");
		getAllPosts();
	}

	private void getAllPosts(){
		TestGetPostsTask testGetPostsTask = new TestGetPostsTask(this);
		testGetPostsTask.execute();
	}

	@Override
	public void processFinish(TestPost[] testPosts) {
		this.testPosts = testPosts;
		if (testPosts != null) {
			for (int i = 0; i < testPosts.length; i++) {
				SpotifyAPIRequestTrack spotifyAPIRequestTrack = new SpotifyAPIRequestTrack(this, i);
				spotifyAPIRequestTrack.execute(testPosts[i].getTrack());
			}
		} else {
			Log.e(TAG, "No posts found!");
		}
	}

	@Override
	public void processFinish(SpotifyTrack spotifyTrack, int position) {

		LatLng latLng = new LatLng(testPosts[position].getLat(), testPosts[position].getLon());
		mMap.addMarker(
			new MarkerOptions().position(latLng).title(
				new Gson().toJson(testPosts[position])).snippet(
				new Gson().toJson(spotifyTrack)));
	}

	@Override
	public void onInfoWindowClick(Marker marker) {

		final ProgressDialog progressDialog;
		//final VideoView videoView;

		//videoView = (VideoView) findViewById(R.id.VideoView);

		progressDialog = new ProgressDialog(MapsActivity.this);
		progressDialog.setTitle("Playing from Spotify");
		progressDialog.setMessage("Buffering...");
		progressDialog.setIndeterminate(false);
		progressDialog.setCancelable(false);
		progressDialog.show();

		try {
			//MediaController mediaController = new MediaController(MapsActivity.this);
			//Uri audioStream = Uri.parse(currentTrack.getPreview_url());

			mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					mediaPlayer.reset();
					return false;
				}
			});

			mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					mediaPlayer.start();
					progressDialog.dismiss();
				}
			});

			mediaPlayer.setDataSource(currentTrack.getPreview_url());
			mediaPlayer.prepareAsync();

		} catch (IllegalArgumentException e) {
			Log.e(TAG, "Error: " + e.getMessage());
			progressDialog.dismiss();
			e.printStackTrace();
		} catch (IllegalStateException e) {
			Log.e(TAG, "Error: " + e.getMessage());
			progressDialog.dismiss();
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "Error: " + e.getMessage());
			progressDialog.dismiss();
			e.printStackTrace();
		}

	}

	class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter, DownloadImageTask.DownloadImageTaskCallback {

		private final View mWindow;
		private final View mContents;
		private SpotifyTrack spotifyTrack;

		CustomInfoWindowAdapter(){
			mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
			mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
		}

		@Override
		public View getInfoWindow(Marker marker) {
			render(marker, mWindow);
			return mWindow;
		}

		@Override
		public View getInfoContents(Marker marker) {
			render(marker, mContents);
			return mContents;
		}

		private void render(Marker marker, View view){

			TestPost testPost = new Gson().fromJson(marker.getTitle(), TestPost.class);
			spotifyTrack = new Gson().fromJson(marker.getSnippet(), SpotifyTrack.class);
			currentTrack = spotifyTrack;

			ImageView imageView = (ImageView) view.findViewById(R.id.badge);

			if (!marker.isInfoWindowShown())
				imageView.setImageBitmap(null);

			DownloadImageTask downloadImageTask = new DownloadImageTask(imageView, this, marker);
			downloadImageTask.execute(spotifyTrack.getImages(0).getUrl());

			TextView titleUI = (TextView) view.findViewById(R.id.title);
			TextView artistUI = (TextView) view.findViewById(R.id.artist);
			TextView albumUI = (TextView) view.findViewById(R.id.album);
			TextView snippetUI = (TextView) view.findViewById(R.id.snippet);

			titleUI.setText(spotifyTrack.getName());
			artistUI.setText(spotifyTrack.getArtistName());
			albumUI.setText(spotifyTrack.getAlbumName());
			snippetUI.setText("Message: " + testPost.getMessage());

			ImageButton playButton = (ImageButton) view.findViewById(R.id.play_button);

		}

		@Override
		public void processFinish(Bitmap result, int position, Marker marker) {
			Log.d(TAG, "Finished image download");
			if (marker != null && marker.isInfoWindowShown()){
				marker.showInfoWindow();
			}
		}

	}
}

