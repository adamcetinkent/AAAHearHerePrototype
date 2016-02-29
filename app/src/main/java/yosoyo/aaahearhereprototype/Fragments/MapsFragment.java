package yosoyo.aaahearhereprototype.Fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import yosoyo.aaahearhereprototype.DatabaseHelper;
import yosoyo.aaahearhereprototype.DownloadImageTask;
import yosoyo.aaahearhereprototype.FetchAddressIntentService;
import yosoyo.aaahearhereprototype.R;
import yosoyo.aaahearhereprototype.SpotifyAPIRequestTrack;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;
import yosoyo.aaahearhereprototype.TestServerClasses.CachedSpotifyTrack;
import yosoyo.aaahearhereprototype.TestServerClasses.ORMCachedSpotifyTrack;
import yosoyo.aaahearhereprototype.TestServerClasses.ORMTestPostUser;
import yosoyo.aaahearhereprototype.TestServerClasses.TestCreatePostTask;
import yosoyo.aaahearhereprototype.TestServerClasses.TestGetPostUsersTask;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPost;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPostUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment
	extends Fragment
	implements GoogleMap.OnMapLongClickListener,
	GoogleApiClient.ConnectionCallbacks,
	GoogleApiClient.OnConnectionFailedListener,
	TestCreatePostTask.TestCreatePostTaskCallback,
	TestGetPostUsersTask.TestGetPostsTaskCallback,
	SpotifyAPIRequestTrack.SpotifyAPIRequestTrackCallback,
	GoogleMap.OnInfoWindowClickListener,
	ORMCachedSpotifyTrack.GetDBCachedSpotifyTracksTask.GetDBCachedSpotifyTracksCallback,
	ORMTestPostUser.GetDBTestPostsTask.GetDBTestPostUsersCallback,
	ORMTestPostUser.InsertDBTestPostUserTask.InsertDBTestPostUserCallback{

	private static final String TAG = "MapsFragment";

	private Activity activityContext;
	private GoogleMap mMap; // Might be null if Google Play services APK is not available.
	private SupportMapFragment supportMapFragment;

	private GoogleApiClient mGoogleApiClient;
	private Location lastLocation;
	private Location middleLocation;

	private SpotifyTrack newTrack;
	private TestPostUser[] testPostUsers;
	private List<TestPostUser> localTestPostUsers;
	private boolean cachedPostsAvailable = false;
	private boolean waitingForCachedPostsAvailable = false;
	private List<CachedSpotifyTrack> localSpotifyTracks;
	private CachedSpotifyTrack currentTrack;
	private MediaPlayer mediaPlayer = new MediaPlayer();

	private static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
	private static final String LOCATION_ADDRESS_KEY = "location-address";
	protected boolean mAddressRequested;
	protected String mAddressOutput;
	private AddressResultReceiver mResultReceiver;
	private ProgressBar mProgressBar;
	private Marker currentMarker;

	public MapsFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		// Create an instance of GoogleAPIClient.
		if (mGoogleApiClient == null) {
			mGoogleApiClient = new GoogleApiClient.Builder(activityContext)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();
		}

		updateValuesFromBundle(savedInstanceState);

		return inflater.inflate(R.layout.fragment_map, container, false);
	}

	/*@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);

		android.app.FragmentManager fm = getChildFragmentManager();

		supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
		if (supportMapFragment == null){
			supportMapFragment = SupportMapFragment.newInstance();
			fm.beginTransaction().replace(R.id.map, supportMapFragment).commit();
		}
	}*/

	@Override
	public void onAttach(Context context){
		activityContext = (Activity) context;
		super.onAttach(context);
	}

	@Override
	public void onStart() {
		mGoogleApiClient.connect();
		super.onStart();

		View view = getView();

		setUpMapIfNeeded();
		//Bundle extras = getIntent().getExtras();
		/*if (extras != null) {

			this.newTrack = new Gson().fromJson(extras.getString(SearchResultsActivity.TRACK_JSON),
												SpotifyTrack.class);
		}*/

		ORMTestPostUser.getTestPosts(getActivity(), this);
		ORMCachedSpotifyTrack.getCachedSpotifyTracks(getActivity(), this);

		ImageButton resetButton = (ImageButton) view.findViewById(R.id.reset_button);
		resetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mMap.clear();
				localTestPostUsers.clear();
				localSpotifyTracks.clear();
				ORMTestPostUser.getTestPosts(getActivity(), MapsFragment.this);
				ORMCachedSpotifyTrack.getCachedSpotifyTracks(getActivity(), MapsFragment.this);
			}
		});

		ImageButton clearCacheButton = (ImageButton) view.findViewById(R.id.clear_cache_button);
		clearCacheButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DatabaseHelper.reset(getActivity());
				mMap.clear();
				localTestPostUsers.clear();
				localSpotifyTracks.clear();
				getAllPosts();
			}
		});

		/* * ADDRESS AWARENESS * */

		mResultReceiver = new AddressResultReceiver(new Handler());
		mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
		mAddressRequested = false;
		mAddressOutput = "";

		updateUIWidgets();

	}

	@Override
	public void onResume() {
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
			//mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			/*mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map))
				.getMap();*/

			/*FragmentManager fm = ((FragmentActivity) getActivity()).getSupportFragmentManager();
			SupportMapFragment fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_fragment);
			mMap = fragment.getMap();*/

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

		mMap.setOnMapLongClickListener(this);

		mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

		mMap.setOnInfoWindowClickListener(this);

	}

	@Override
	public void onMapLongClick(LatLng latLng) {

		if (middleLocation == null)
			middleLocation = new Location(lastLocation);

		middleLocation.setLatitude(latLng.latitude);
		middleLocation.setLongitude(latLng.longitude);
		if (mGoogleApiClient.isConnected() && middleLocation != null){
			startIntentService();
		}
		mAddressRequested = true;
		updateUIWidgets();

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		lastLocation = LocationServices.FusedLocationApi.getLastLocation(
			mGoogleApiClient);
		LatLng myLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new
																	 CameraPosition.Builder()
																	 .target(myLatLng).zoom(15)
																	 .build()), 1000, null);
		if (cachedPostsAvailable)
			getCachedPosts();
		else
			waitingForCachedPostsAvailable = true;

		if (newTrack != null){
			TestPost testPost = new TestPost(1, newTrack.getID(), myLatLng.latitude, myLatLng.longitude, "OMG!");
			TestCreatePostTask testCreatePostTask = new TestCreatePostTask(this, testPost);
			testCreatePostTask.execute();
		} else {
			getAllPosts();
		}

		if (mAddressRequested){
			if (!Geocoder.isPresent()) {
				Toast.makeText(getActivity(), R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
				return;
			}
			startIntentService();
		}

	}

	/*public void onStart() {
		mGoogleApiClient.connect();
		super.onStart();
	}*/

	public void onStop() {
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
	public void returnResultCreatePost(Boolean success, TestPost testPost) {
		Log.d(TAG, "Successful post!");
		getAllPosts();
	}

	public void getCachedPosts(){
		for (TestPostUser testPostUser : localTestPostUsers){
			addPostMarker(testPostUser);
		}
	}

	private void addPostMarker(TestPostUser testPostUser){
		LatLng latLng = new LatLng(testPostUser.getTestPost().getLat(), testPostUser.getTestPost().getLon());
		for (CachedSpotifyTrack cachedSpotifyTrack : localSpotifyTracks) {
			if (cachedSpotifyTrack.getTrackID().equals(testPostUser.getTestPost().getTrack())){
				mMap.addMarker(
					new MarkerOptions().position(latLng)
									   .title(new Gson().toJson(testPostUser))
									   .snippet(new Gson().toJson(cachedSpotifyTrack))
									   .icon(BitmapDescriptorFactory
												 .fromResource(R.drawable.music_marker_small))
							  );
				return;
			}
		}
	}

	private void getAllPosts(){
		TestGetPostUsersTask testGetPostUsersTask = new TestGetPostUsersTask(this);
		testGetPostUsersTask.execute();
	}

	@Override
	public void returnTestPostUsers(TestPostUser[] testPostUsers) {
		this.testPostUsers = testPostUsers;
		if (this.testPostUsers != null) {
			for (int i = 0; i < this.testPostUsers.length; i++) {
				if (addTestPost(this.testPostUsers[i])) {
					ORMTestPostUser.insertPost(getActivity(), this.testPostUsers[i], i, this);
				}
			}
		} else {
			Log.e(TAG, "No posts found!");
		}
	}

	public boolean addTestPost(TestPostUser testPostUser){
		for (TestPostUser postUser : localTestPostUsers){
			if (postUser.getTestPost().getId() == testPostUser.getTestPost().getId())
				return false;
		}
		return true;
	}

	@Override
	public void returnSpotifyTrack(SpotifyTrack spotifyTrack, int position, TestPostUser testPostUser) {
		CachedSpotifyTrack cachedSpotifyTrack = new CachedSpotifyTrack(spotifyTrack);
		for (CachedSpotifyTrack track : localSpotifyTracks){
			if (track.getTrackID() == cachedSpotifyTrack.getTrackID())
				return;
		}
		ORMCachedSpotifyTrack.insertSpotifyTrack(getActivity(), spotifyTrack);
		localSpotifyTracks.add(cachedSpotifyTrack);
		LatLng latLng = new LatLng(testPostUsers[position].getTestPost().getLat(), testPostUsers[position].getTestPost().getLon());
		mMap.addMarker(
			new MarkerOptions().position(latLng)
							   .title(new Gson().toJson(testPostUsers[position]))
							   .snippet(new Gson().toJson(cachedSpotifyTrack))
							   .icon(BitmapDescriptorFactory.fromResource(R.drawable.music_marker_small))
					  );
	}

	@Override
	public void onInfoWindowClick(final Marker marker) {

		if (mediaPlayer.isPlaying()) {
			mediaPlayer.reset();
			marker.showInfoWindow();
			return;
		}

		final ProgressDialog progressDialog;

		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setTitle("Playing from Spotify");
		progressDialog.setMessage("Buffering...");
		progressDialog.setIndeterminate(false);
		progressDialog.setCancelable(false);
		progressDialog.show();

		try {

			mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					mediaPlayer.reset();
					marker.showInfoWindow();
					return false;
				}
			});

			mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					mediaPlayer.start();
					marker.showInfoWindow();
					progressDialog.dismiss();
				}
			});

			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					marker.showInfoWindow();
				}
			});

			mediaPlayer.setDataSource(currentTrack.getPreviewUrl());
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

	@Override
	public void returnCachedSpotifyTracks(List<CachedSpotifyTrack> cachedSpotifyTracks) {
		localSpotifyTracks = cachedSpotifyTracks;
		getCachedPosts();
	}

	@Override
	public void returnTestPostUsers(List<TestPostUser> testPostUsers) {
		localTestPostUsers = testPostUsers;
		if (waitingForCachedPostsAvailable)
			getCachedPosts();
	}

	@Override
	public void returnInsertedPostUserID(Long postID, int position, TestPostUser testPostUser) {
		SpotifyAPIRequestTrack spotifyAPIRequestTrack = new SpotifyAPIRequestTrack(this, position);
		spotifyAPIRequestTrack.execute(testPostUsers[position].getTestPost().getTrack());
		localTestPostUsers.add(testPostUsers[position]);
	}

	class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter, DownloadImageTask.DownloadImageTaskCallback {

		private final View mWindow;
		private final View mContents;
		private CachedSpotifyTrack spotifyTrack;

		CustomInfoWindowAdapter(){
			mWindow = activityContext.getLayoutInflater().inflate(R.layout.custom_info_window, null);
			mContents = activityContext.getLayoutInflater().inflate(R.layout.custom_info_contents, null);
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

		private void render(Marker marker, View view) {

			currentMarker = marker;

			TestPostUser testPostUser = new Gson().fromJson(marker.getTitle(), TestPostUser.class);
			spotifyTrack = new Gson().fromJson(marker.getSnippet(), CachedSpotifyTrack.class);
			currentTrack = spotifyTrack;

			ImageView imgAlbumArt = (ImageView) view.findViewById(R.id.imgAlbumArt);
			ImageView imgUserArt = (ImageView) view.findViewById(R.id.imgUserArt);

			if (!marker.isInfoWindowShown()) {
				imgAlbumArt.setImageBitmap(null);
				imgUserArt.setImageBitmap(null);

				DownloadImageTask downloadImageTaskAlbumArt = new DownloadImageTask(imgAlbumArt, this, marker);
				downloadImageTaskAlbumArt.execute(spotifyTrack.getImageUrl());

				DownloadImageTask downloadImageTaskUserArt = new DownloadImageTask(imgUserArt, this, marker);
				downloadImageTaskUserArt.execute(DownloadImageTask.FACEBOOK_PROFILE_PHOTO + testPostUser.getTestUser().getFBUserID() + DownloadImageTask.FACEBOOK_PROFILE_PHOTO_SMALL);
			}

			TextView titleUI = (TextView) view.findViewById(R.id.title);
			TextView artistUI = (TextView) view.findViewById(R.id.artist);
			TextView albumUI = (TextView) view.findViewById(R.id.album);
			TextView snippetUI = (TextView) view.findViewById(R.id.snippet);
			TextView dateUI = (TextView) view.findViewById(R.id.date_time);
			TextView userUI = (TextView) view.findViewById(R.id.txtUserID);

			titleUI.setText(spotifyTrack.getName());
			artistUI.setText(spotifyTrack.getArtist());
			albumUI.setText(spotifyTrack.getAlbum());
			snippetUI.setText("Message: " + testPostUser.getTestPost().getMessage());
			dateUI.setText("Posted: " + testPostUser.getTestPost().getCreatedAt());
			userUI.setText(testPostUser.getTestUser().getFirstName() + " " + testPostUser.getTestUser().getLastName());

			final ImageButton playButton = (ImageButton) view.findViewById(R.id.play_button);
			if (mediaPlayer.isPlaying()) {
				playButton.setImageResource(R.drawable.ic_media_pause);
			} else {
				playButton.setImageResource(R.drawable.ic_media_play);
			}

		}

		@Override
		public void returnDownloadedImage(Bitmap result, int position, Marker marker) {
			Log.d(TAG, "Finished image download");
			if (marker != null && marker.isInfoWindowShown()){
				marker.showInfoWindow();
			}
		}

	}

	protected void startIntentService() {
		Intent intent = new Intent(activityContext, FetchAddressIntentService.class);
		intent.putExtra(FetchAddressIntentService.Constants.RECEIVER, mResultReceiver);
		intent.putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, middleLocation);
		activityContext.startService(intent);
	}

	private void updateUIWidgets(){
		if (mAddressRequested){
			mProgressBar.setVisibility(ProgressBar.VISIBLE);
		} else {
			mProgressBar.setVisibility(ProgressBar.GONE);
		}
	}

	private void updateValuesFromBundle(Bundle savedInstanceState) {
		if (savedInstanceState != null){
			if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)){
				mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
			}

			if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)){
				mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
				displayAddressOutput();
			}
		}
	}

	private void showToast(String text){
		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
	}

	private void displayAddressOutput(){
		showToast(mAddressOutput);
	}

	@SuppressLint("ParcelCreator")
	private class AddressResultReceiver extends ResultReceiver {
		public AddressResultReceiver(Handler handler){
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData){
			mAddressOutput = resultData.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY);
			displayAddressOutput();

			//if (resultCode == FetchAddressIntentService.Constants.SUCCESS_RESULT){
			//	showToast(getString(R.string.address_found));
			//}

			mAddressRequested = false;
			updateUIWidgets();
		}
	}
}
