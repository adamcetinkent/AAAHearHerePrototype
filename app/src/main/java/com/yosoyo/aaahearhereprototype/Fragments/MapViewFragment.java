package com.yosoyo.aaahearhereprototype.Fragments;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.gson.Gson;
import com.yosoyo.aaahearhereprototype.Activities.HolderActivity;
import com.yosoyo.aaahearhereprototype.AsyncDataManager;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHCachedSpotifyTrack;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPostFull;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHTagUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;
import com.yosoyo.aaahearhereprototype.PostMarker;
import com.yosoyo.aaahearhereprototype.R;
import com.yosoyo.aaahearhereprototype.Services.AddressResultReceiver;
import com.yosoyo.aaahearhereprototype.Services.FetchAddressIntentService;
import com.yosoyo.aaahearhereprototype.ZZZInterface.FreezeableMapView;
import com.yosoyo.aaahearhereprototype.ZZZUtility;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by adam on 27/02/16.
 *
 * MapViewFragment displays posts on a Google Maps Instance through a {@link FreezeableMapView}.
 * It is highly dependent on the Google Maps lifecycle.
 */
public class MapViewFragment
	extends Fragment
	implements GoogleMap.OnInfoWindowClickListener {

	private static final String TAG = "MapViewFragment";

	public static final String KEY_POSTS = TAG + "posts";
	private List<HHPostFull> posts = new ArrayList<>();
	private final List<PostMarker> markers = new ArrayList<>();

	private int mapType;
	public static final String KEY_MAP_TYPE = TAG + "map_type";
	public static final int GENERAL_MAP = 0;
	public static final int HOME_MAP = 1;
	public static final int USER_MAP = 2;

	public static final String KEY_USER_ID = TAG + "user_id";
	private long userID = -1;

	public static final String KEY_FETCH_DATA = TAG + "fetch_data";
	private boolean fetchData = true;

	private FreezeableMapView mMapView;
	private GoogleMap googleMap;
	private Boolean mapExists = false;
	private Location lastLocation;
	private Location middleLocation;

	private Marker currentMarker;

	private HHPostFull currentPost;
	private static final String KEY_CURRENT_POST = TAG + "current_post";

	private ImageView btnLeft;
	private ImageView btnCentre;
	private ImageView btnRight;
	private ProgressBar btnCentreProgressBar;

	private AddressResultReceiver mResultReceiver;
	private static final String ADDRESS_REQUESTED_KEY = TAG + "address-request-pending";
	private static final String LOCATION_ADDRESS_KEY = TAG + "location-address";
	private boolean mAddressRequested;
	private String mAddressOutput;
	//private ProgressBar mProgressBar;

	public static final String KEY_CAMERA_POSITION = TAG + "camera_position";
	private CameraPosition cameraPosition = null;

	public static final String KEY_LAST_LOCATION = TAG + "last_location";

	public static final String KEY_MAP_VIEW_FRAGMENT_BUNDLE = TAG + "map_view_fragment_bundle";
	public static final String KEY_WORKAROUND_BUNDLE = TAG + "workaround_bundle";

	private HHCachedSpotifyTrack currentTrack;

	//public static final String KEY_ALREADY_SHIFTED = TAG + "already_shifted";
	private boolean alreadyShifted = false;
	//public static final String KEY_SHIFTED_CAMERA_POSITION = TAG + "shifted_camera_position";
	private CameraPosition shiftedCameraPosition = null;

	public static final String KEY_INFO_WINDOW_OPEN = TAG + "info_window_open";
	private boolean needToOpenInfoWindow = false;

	//TODO incorporate pagination here too - think of how!
	public static final String KEY_EARLIEST_WEB_POST = TAG + "earliest_web_post";
	private Timestamp earliestWebPost;
	private Timestamp requestedWebPost;
	public static final String KEY_HAVE_EARLIEST_POST = TAG + "have_earliest_post";
	private boolean haveEarliestPost = false;

	private boolean alreadyFetchingPosts = false;
	private Set<Long> currentPostIDs = new HashSet<>();
	public static final String KEY_CURRENT_POST_IDS = TAG + "current_post_ids";

	public MapViewFragment() {
		//required empty public constructor
	}

	public static MapViewFragment newInstance(Bundle bundle){
		MapViewFragment mapViewFragment = new MapViewFragment();

		mapViewFragment.restoreInstanceState(bundle);

		return mapViewFragment;
	}


	public Bundle getBundle(){
		Bundle bundle = new Bundle();
		addToBundle(bundle);
		return bundle;
	}

	public void addToBundle(Bundle bundle){
		bundle.putLong(KEY_USER_ID, userID);
		bundle.putBoolean(KEY_FETCH_DATA, fetchData);
		bundle.putInt(KEY_MAP_TYPE, mapType);
		bundle.putParcelableArrayList(KEY_POSTS, (ArrayList<? extends Parcelable>) posts);
		if (googleMap != null) {
			cameraPosition = googleMap.getCameraPosition();
		}
		bundle.putParcelable(KEY_CAMERA_POSITION, cameraPosition);
		bundle.putParcelable(KEY_LAST_LOCATION, HolderActivity.getLastLocation(getActivity()));
		bundle.putParcelable(KEY_CURRENT_POST, currentPost);
		//bundle.putBoolean(KEY_ALREADY_SHIFTED, alreadyShifted);
		//bundle.putParcelable(KEY_SHIFTED_CAMERA_POSITION, shiftedCameraPosition);
		if (currentMarker != null && currentMarker.isInfoWindowShown()) {
			bundle.putBoolean(KEY_INFO_WINDOW_OPEN, true);
		} else {
			bundle.putBoolean(KEY_INFO_WINDOW_OPEN, false);
		}
	}

	public void addToBundleForSwitch(Bundle bundle){
		bundle.putLong(FeedFragment.KEY_USER_ID, userID);
		bundle.putBoolean(FeedFragment.KEY_FETCH_DATA, fetchData);
		bundle.putInt(FeedFragment.KEY_FEED_TYPE, mapType);
		bundle.putParcelableArrayList(FeedFragment.KEY_POSTS, (ArrayList<? extends Parcelable>) posts);
		bundle.putLong(FeedFragment.KEY_EARLIEST_WEB_POST, earliestWebPost.getTime());
		bundle.putBoolean(FeedFragment.KEY_HAVE_EARLIEST_POST, haveEarliestPost);
		bundle.putLongArray(FeedFragment.KEY_CURRENT_POST_IDS, ZZZUtility.getLongArray(currentPostIDs));
		/*if (googleMap != null) {
			cameraPosition = googleMap.getCameraPosition();
		}
		bundle.putParcelable(KEY_CAMERA_POSITION, cameraPosition);*/
	}

	private void restoreInstanceState(Bundle bundle){
		if (bundle == null)
			return;
		if (bundle.containsKey(KEY_WORKAROUND_BUNDLE)){
			bundle = bundle.getBundle(KEY_WORKAROUND_BUNDLE);
		}
		assert bundle != null;
		userID = bundle.getLong(KEY_USER_ID);
		fetchData = bundle.getBoolean(KEY_FETCH_DATA);
		mapType = bundle.getInt(KEY_MAP_TYPE);
		posts = bundle.getParcelableArrayList(KEY_POSTS);
		cameraPosition = bundle.getParcelable(KEY_CAMERA_POSITION);
		lastLocation = bundle.getParcelable(KEY_LAST_LOCATION);
		currentPost = bundle.getParcelable(KEY_CURRENT_POST);
		//alreadyShifted = bundle.getBoolean(KEY_ALREADY_SHIFTED);
		//shiftedCameraPosition = bundle.getParcelable(KEY_SHIFTED_CAMERA_POSITION);
		needToOpenInfoWindow = bundle.getBoolean(KEY_INFO_WINDOW_OPEN);
		earliestWebPost = new Timestamp(bundle.getLong(KEY_EARLIEST_WEB_POST));
		haveEarliestPost = bundle.getBoolean(KEY_HAVE_EARLIEST_POST);
		if (bundle.containsKey(KEY_CURRENT_POST_IDS)){
			long[] longs = bundle.getLongArray(KEY_CURRENT_POST_IDS);
			ZZZUtility.fillSetFromArray(currentPostIDs, ZZZUtility.getLongArray(longs));
		}
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
		Bundle workaroundBundle = new Bundle();
		addToBundle(workaroundBundle);
		outState.putBundle(KEY_WORKAROUND_BUNDLE, workaroundBundle);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_map, container, false);

		mMapView = (FreezeableMapView) v.findViewById(R.id.mapView);

		mMapView.onCreate(savedInstanceState);

		if (savedInstanceState != null){
			restoreInstanceState(savedInstanceState);
		}

		mMapView.onResume();

		try {
			MapsInitializer.initialize(getActivity().getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (HolderActivity.apiExists && HolderActivity.mGoogleApiClient != null) {
			HolderActivity.mGoogleApiClient.registerConnectionCallbacks(
				new GoogleApiClient.ConnectionCallbacks() {
					@Override
					public void onConnected(@Nullable Bundle bundle) {
						//apiExists = true;

						onMapReadyForLocation();

						if (mAddressRequested) {
							if (!Geocoder.isPresent()) {
								Toast.makeText(getActivity(), R.string.no_geocoder_available,
											   Toast.LENGTH_LONG).show();
								return;
							}
							startIntentService();
						}

					}

					@Override
					public void onConnectionSuspended(int i) {

					}
				});
		}

		mMapView.getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(GoogleMap googleMapBack) {
				googleMap = googleMapBack;
				mapExists = true;
				onMapReadyForLocation();
				setUpMap();
			}
		});

		btnLeft = (ImageView) v.findViewById(R.id.fragment_map_btnLeft);
		btnCentre = (ImageView) v.findViewById(R.id.fragment_map_btnCentre);
		btnRight = (ImageView) v.findViewById(R.id.fragment_map_btnRight);
		btnCentreProgressBar = (ProgressBar) v.findViewById(R.id.fragment_map_btnCentre_progress);
		updateUIWidgets();

		btnLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (googleMap == null)
					return;
				if (currentPost == null && posts.size() > 0){
					currentPost = posts.get(posts.size()-1);
				}
				if (currentPost != null && currentPost.getPost() != null) {
					int position = posts.indexOf(currentPost);
					if (--position < 0){
						position = posts.size() - 1;
					}
					currentPost = posts.get(position);
					currentMarker = markers.get(position).getMarker();
					alreadyShifted = false;
					shiftedCameraPosition = null;
					activateMarker(currentMarker);
				}
			}
		});
		btnRight.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (googleMap == null)
					return;
				if (currentPost == null && posts.size() > 0){
					currentPost = posts.get(0);
				}
				if (currentPost != null && currentPost.getPost() != null) {
					int position = posts.indexOf(currentPost);
					if (++position >= posts.size()){
						position = 0;
					}
					currentPost = posts.get(position);
					currentMarker = markers.get(position).getMarker();
					alreadyShifted = false;
					shiftedCameraPosition = null;
					activateMarker(currentMarker);
				}
			}
		});
		btnCentre.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (googleMap == null)
					return;
				if (currentMarker != null && currentPost.getPost() != null){
					activateMarker(currentMarker);
				}
			}
		});

		return v;
	}

	public void onStart() {
		//mGoogleApiClient.connect();

		/* * ADDRESS AWARENESS * */

		mResultReceiver = new AddressResultReceiver(
			new Handler(),
			new AddressResultReceiver.AddressResultReceiverCallback() {
				@Override
				public void returnAddress(Address address) {
					ArrayList<String> addressFragments = new ArrayList<>();

					for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
						addressFragments.add(address.getAddressLine(i));
					}
					Log.i(TAG, getString(R.string.address_found));
					mAddressOutput = TextUtils
						.join(System.getProperty("line.separator"), addressFragments);

					displayAddressOutput();

					mAddressRequested = false;
					//updateUIWidgets();
				}
			});
		//mProgressBar = (ProgressBar) getView().findViewById(R.id.progress_bar);
		mAddressRequested = false;
		mAddressOutput = "";

		//updateUIWidgets();

		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mMapView.onLowMemory();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode){
			case HolderActivity.LOCATION_PERMISSIONS:{
				if (grantResults.length > 0
					&& grantResults[0] == PackageManager.PERMISSION_GRANTED
					&& grantResults[1] == PackageManager.PERMISSION_GRANTED){
					setUpMap();
				}
			}
		}
	}

	private final GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
		@Override
		public boolean onMarkerClick(final Marker marker) {
			return marker.isInfoWindowShown() || activateMarker(marker);
		}
	};

	private boolean activateMarker(final Marker marker){

		if (currentMarker == null || !marker.getPosition().equals(currentMarker.getPosition())){
			alreadyShifted = false;
			shiftedCameraPosition = null;
		}

		marker.showInfoWindow();

		cameraPosition = googleMap.getCameraPosition();

		if (!alreadyShifted) {

			mMapView.setFrozen(true);
			googleMap.moveCamera(
				CameraUpdateFactory.newCameraPosition(
					new CameraPosition.Builder()
						.target(marker.getPosition())
						.zoom(cameraPosition.zoom)
						.tilt(cameraPosition.tilt)
						.bearing(cameraPosition.bearing)
						.build()));

			Point markerPoint = googleMap.getProjection().toScreenLocation(marker.getPosition());
			VisibleRegion visibleRegion = googleMap.getProjection().getVisibleRegion();
			Point farLeft = googleMap.getProjection().toScreenLocation(visibleRegion.farLeft);
			int newY = (int) ((markerPoint.y + farLeft.y) * 0.3);
			Point halfUp = new Point(markerPoint.x, newY);
			LatLng shiftedLatLng = googleMap.getProjection().fromScreenLocation(halfUp);

			googleMap.moveCamera(
				CameraUpdateFactory.newCameraPosition(
					new CameraPosition.Builder()
						.target(cameraPosition.target)
						.zoom(cameraPosition.zoom)
						.tilt(cameraPosition.tilt)
						.bearing(cameraPosition.bearing)
						.build()));
			mMapView.setFrozen(false);

			shiftedCameraPosition = new CameraPosition.Builder()
				.target(shiftedLatLng)
				.zoom(cameraPosition.zoom)
				.tilt(cameraPosition.tilt)
				.bearing(cameraPosition.bearing)
				.build();

			googleMap.animateCamera(
				CameraUpdateFactory.newCameraPosition(shiftedCameraPosition), 1000, null);

			alreadyShifted = true;

		} else if (shiftedCameraPosition != null){
			googleMap.animateCamera(
				CameraUpdateFactory.newCameraPosition(
					new CameraPosition.Builder()
						.target(shiftedCameraPosition.target)
						.zoom(cameraPosition.zoom)
						.tilt(cameraPosition.tilt)
						.bearing(cameraPosition.bearing)
						.build()), 500, null);
		}

		return true;
	}

	private void setUpMap() {
		if (ActivityCompat.checkSelfPermission(
			getActivity(),
			Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
			&& ActivityCompat.checkSelfPermission(
			getActivity(),
			Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, HolderActivity.LOCATION_PERMISSIONS);

			return;
		}
		googleMap.setMyLocationEnabled(true);

		googleMap.setOnMarkerClickListener(markerClickListener);

		googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng latLng) {

				if (!HolderActivity.apiExists)
					return;

				if (middleLocation == null)
					middleLocation = new Location(lastLocation);

				middleLocation.setLatitude(latLng.latitude);
				middleLocation.setLongitude(latLng.longitude);
				if (HolderActivity.mGoogleApiClient.isConnected() && middleLocation != null) {
					startIntentService();
				}
				mAddressRequested = true;
				//updateUIWidgets();

			}
		});

		googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

		googleMap.setOnInfoWindowClickListener(this);

		googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition cameraPosition) {
				if (currentMarker != null && needToOpenInfoWindow && !currentMarker.isInfoWindowShown()){
					activateMarker(currentMarker);
					needToOpenInfoWindow = false;
					updateUIWidgets();
				}
				if (!alreadyFetchingPosts) {
					alreadyFetchingPosts = true;
					LatLngBounds latLngBounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
					if (mapType == GENERAL_MAP) {
						AsyncDataManager.getPostsWithinBounds(
							latLngBounds,
							true,
							currentPostIDs.toArray(new Long[currentPostIDs.size()]),
							new AsyncDataManager.GetAllPostsCallback() {
								@Override
								public void returnGetPost(HHPostFull post) {
									if (ZZZUtility.addItemToList(posts, post)) {
										Collections.sort(posts);
										currentPostIDs.add(post.getPost().getID());
										addMapMarker(post, false);
										Collections.sort(markers);
										fetchData = false;
										updateUIWidgets();
									}
									alreadyFetchingPosts = false;
								}

								@Override
								public void returnPostList(List<HHPostFull> posts) {
									MapViewFragment.this.posts = ZZZUtility.mergeLists(MapViewFragment.this.posts, posts);
									Collections.sort(posts);
									for (HHPostFull post : MapViewFragment.this.posts){
										currentPostIDs.add(post.getPost().getID());
										if (posts.contains(post)){
											addMapMarker(post, false);
										}
									}
									Collections.sort(markers);
									fetchData = false;
									updateUIWidgets();
									alreadyFetchingPosts = false;
								}

								@Override
								public void warnNoEarlierPosts() {
									alreadyFetchingPosts = false;
								}
							});
					} else if (mapType == USER_MAP) {
					/*AsyncDataManager.getUserPostsWithinBounds(
						latLngBounds,
						new AsyncDataManager.GetAllPostsCallback() {
							@Override
							public void returnGetPost(HHPostFull post) {
							}

							@Override
							public void returnPostList(List<HHPostFull> posts) {
							}

							@Override
							public void warnNoEarlierPosts() {
							}
						});*/
					}
				}

				if (MapViewFragment.this.shiftedCameraPosition == null)
					return;
				if (MapViewFragment.this.shiftedCameraPosition.bearing != cameraPosition.bearing
					|| MapViewFragment.this.shiftedCameraPosition.tilt != cameraPosition.tilt){
					MapViewFragment.this.cameraPosition = cameraPosition;
					MapViewFragment.this.alreadyShifted = false;
					MapViewFragment.this.shiftedCameraPosition = null;
				}
			}
		});

		if (fetchData) {
			if (!alreadyFetchingPosts) {
				alreadyFetchingPosts = true;
				if (mapType == GENERAL_MAP) {
					AsyncDataManager.getAllPosts(
						null,
						currentPostIDs.toArray(new Long[currentPostIDs.size()]),
						new AsyncDataManager.GetAllPostsCallback() {
							@Override
							public void returnPostList(List<HHPostFull> posts) {
								MapViewFragment.this.posts = ZZZUtility.mergeLists(MapViewFragment.this.posts, posts);
								Collections.sort(posts);
								addMapMarkers();
								Collections.sort(markers);
								fetchData = false;
								updateUIWidgets();
							}

							@Override
							public void returnGetPost(HHPostFull post) {
								if (ZZZUtility.addItemToList(posts, post)) {
									Collections.sort(posts);
									currentPostIDs.add(post.getPost().getID());
									addMapMarker(post, true);
									Collections.sort(markers);
									fetchData = false;
									updateUIWidgets();
									alreadyFetchingPosts = false;
								}
							}

							@Override
							public void warnNoEarlierPosts() {
								//TODO
							}
						});
				}
			}
		} else {
			addMapMarkers();
			if (needToOpenInfoWindow && currentMarker != null){
				activateMarker(currentMarker);
				//needToOpenInfoWindow = false;
			}
			updateUIWidgets();
		}
	}

	private void onMapReadyForLocation(){

		if (!(HolderActivity.apiExists && mapExists))
			return;

		lastLocation = HolderActivity.getLastLocation(getActivity());
		if (lastLocation == null){
			Log.e(TAG, "No last location!");
			return;
		}

		LatLng myLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

		addMapMarkers();

		if (cameraPosition == null) {
			googleMap
				.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
																		 .target(myLatLng).zoom(15)
																		 .build()), 1000, null);
		} else {
			googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		}
	}

	private void addMapMarkers(){
		googleMap.clear();
		markers.clear();
		for (HHPostFull post : posts) {
			addMapMarker(post, false);
		}
		if (currentPost != null && posts != null) {
			int postsPosition = posts.indexOf(currentPost);
			if (postsPosition >= 0) {
				currentMarker = markers.get(postsPosition).getMarker();
			}
		}
	}

	private void addMapMarker(HHPostFull post, boolean newColour){
		LatLng latLng = new LatLng(post.getPost().getLat(), post.getPost().getLon());
		Marker marker = googleMap.addMarker(
			new MarkerOptions().position(latLng)
							   .title(new Gson().toJson(post))
							   .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));
		markers.add(new PostMarker(marker, post));
	}

	private void startIntentService() {
		Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
		intent.putExtra(FetchAddressIntentService.Constants.RECEIVER, mResultReceiver);
		intent.putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, middleLocation);
		getActivity().startService(intent);
	}

	@Override
	public void onInfoWindowClick(final Marker marker) {

		if (HolderActivity.mediaPlayer.isPlaying()) {
			HolderActivity.mediaPlayer.reset();
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

			HolderActivity.mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					HolderActivity.mediaPlayer.reset();
					marker.showInfoWindow();
					return false;
				}
			});

			HolderActivity.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					HolderActivity.mediaPlayer.start();
					marker.showInfoWindow();
					progressDialog.dismiss();
				}
			});

			HolderActivity.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					marker.showInfoWindow();
				}
			});

			HolderActivity.mediaPlayer.setDataSource(currentTrack.getPreviewUrl());
			HolderActivity.mediaPlayer.prepareAsync();

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

	class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

		private final View mWindow;
		private final View mContents;
		private HHCachedSpotifyTrack spotifyTrack;

		ImageView imgProfile;
		TextView txtUserName;
		TextView txtLocation;
		TextView txtDateTime;
		TextView txtTrackName;
		TextView txtArtist;
		TextView txtAlbum;
		TextView txtMessage;
		ImageView imgAlbumArt;
		ImageView btnPlayButton;
		//ToggleButton btnLikeButton;
		//ImageButton btnCommentButton;
		//ImageButton btnShareButton;
		HHPostFull post;
		//HHLike myLike;

		CustomInfoWindowAdapter(){
			mWindow = getActivity().getLayoutInflater().inflate(R.layout.custom_info_window, null);
			mContents = getActivity().getLayoutInflater().inflate(R.layout.custom_info_contents, null);
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

		private void render(final Marker marker, View view) {

			currentMarker = marker;

			post = new Gson().fromJson(marker.getTitle(), HHPostFull.class);
			currentPost = post;
			spotifyTrack = post.getTrack();
			currentTrack = spotifyTrack;

			imgAlbumArt = (ImageView) view.findViewById(R.id.list_row_timeline_imgAlbumArt);
			imgProfile = (ImageView) view.findViewById(R.id.list_row_timeline_imgProfile);

			if (!marker.isInfoWindowShown()) {
				Bitmap albumArt = WebHelper.getSpotifyAlbumArt(
					spotifyTrack,
					new WebHelper.GetSpotifyAlbumArtCallback() {
						@Override
						public void returnSpotifyAlbumArt(Bitmap bitmap) {
							imgAlbumArt.setImageBitmap(bitmap);
							//noinspection ConstantConditions
							if (marker != null && marker.isInfoWindowShown()) {
								marker.showInfoWindow();
							}
						}
					});
				imgAlbumArt.setImageBitmap(albumArt);

				Bitmap userArt = WebHelper.getFacebookProfilePicture(
					post.getUser().getFBUserID(),
					new WebHelper.GetFacebookProfilePictureCallback() {
						@Override
						public void returnFacebookProfilePicture(Bitmap bitmap) {
							imgProfile.setImageBitmap(bitmap);
							//noinspection ConstantConditions
							if (marker != null && marker.isInfoWindowShown()) {
								marker.showInfoWindow();
							}
						}
					});
				imgProfile.setImageBitmap(userArt);
			}

			txtUserName = (TextView) view.findViewById(R.id.list_row_timeline_txtUserName);
			txtLocation = (TextView) view.findViewById(R.id.list_row_timeline_txtLocation);
			txtDateTime = (TextView) view.findViewById(R.id.list_row_timeline_txtDateTime);
			txtTrackName = (TextView) view.findViewById(R.id.list_row_timeline_txtTrackName);
			txtArtist = (TextView) view.findViewById(R.id.list_row_timeline_txtArtist);
			txtAlbum = (TextView) view.findViewById(R.id.list_row_timeline_txtAlbum);
			txtMessage = (TextView) view.findViewById(R.id.list_row_timeline_txtMessage);
			btnPlayButton = (ImageView) view.findViewById(R.id.list_row_timeline_btnPlayButton);
			//btnLikeButton = (ToggleButton) view.findViewById(R.id.list_row_timeline_btnLike);
			//btnCommentButton = (ImageButton) view.findViewById(R.id.list_row_timeline_btnComment);
			//btnShareButton = (ImageButton) view.findViewById(R.id.list_row_timeline_btnShare);

			txtUserName.setText(post.getUser().getName());
			txtLocation.setText(ZZZUtility.truncatedAddress(post.getPost().getPlaceName(), 35));
			txtDateTime.setText(ZZZUtility.formatDynamicDate(post.getPost().getCreatedAt()));
			txtTrackName.setText(post.getTrack().getName());
			txtArtist.setText(post.getTrack().getArtist());
			txtAlbum.setText(post.getTrack().getAlbum());

			txtMessage.setText(post.getPost().getMessage());
			Editable message = Editable.Factory.getInstance().newEditable(txtMessage.getText());
			Pattern pattern = Pattern.compile("\\{tag_(\\d+)\\}");
			Matcher matcher = pattern.matcher(message);

			while (matcher.find()){
				int start = matcher.start();
				int end = matcher.end();

				long userID = Long.valueOf(matcher.group(1));

				HHUser tagUser = getTagUser(post.getTags(), userID);
				if (tagUser != null){
					String userName = tagUser.getName();
					message.replace(start, end, userName);
					message.setSpan(
						new HHUser.HHUserSpan(getContext(), tagUser, null),
						start,
						start + userName.length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
					);
				} else {
					message.replace(start, end, "");
				}

				matcher = pattern.matcher(message);
			}
			txtMessage.setText(message);

			updatePlayButton(btnPlayButton, spotifyTrack.getPreviewUrl());

			/*for (HHLikeUser like : post.getLikes() ){
				if (like.getUser().equals(HHUser.getCurrentUser().getUser())){
					myLike = like.getLike();
					break;
				}
			}

			if (myLike != null) {
				btnLikeButton.setChecked(true);
			} else {
				btnLikeButton.setChecked(false);
			}*/

		}
	}

	private HHUser getTagUser(List<HHTagUser> tags, long userID){
		for (HHTagUser tag : tags) {
			if (tag.getUser().getID() == userID) {
				return tag.getUser();
			}
		}
		return null;
	}

	private void updatePlayButton(ImageView btnPlayButton, String previewURL){
		if (previewURL == null) {
			btnPlayButton.setVisibility(View.GONE);
		} else {
			btnPlayButton.setVisibility(View.VISIBLE);
		}

		if (HolderActivity.mediaPlayer.isPlaying()) {
			btnPlayButton.setImageResource(R.drawable.pause_overlay);
		} else {
			btnPlayButton.setImageResource(R.drawable.play_overlay);
		}
	}

	private void showToast(String text){
		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
	}

	private void displayAddressOutput(){
		showToast(mAddressOutput);
	}

	private void updateUIWidgets(){
		if (!needToOpenInfoWindow && !fetchData && mapExists){
			btnLeft.setVisibility(View.VISIBLE);
			btnCentre.setVisibility(View.VISIBLE);
			btnRight.setVisibility(View.VISIBLE);
			btnCentreProgressBar.setVisibility(View.GONE);
		} else {
			btnLeft.setVisibility(View.INVISIBLE);
			btnCentre.setVisibility(View.INVISIBLE);
			btnRight.setVisibility(View.INVISIBLE);
			btnCentreProgressBar.setVisibility(View.VISIBLE);
		}
	}

}
