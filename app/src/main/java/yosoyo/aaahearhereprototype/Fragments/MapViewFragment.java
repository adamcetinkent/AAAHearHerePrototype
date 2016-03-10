package yosoyo.aaahearhereprototype.Fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.AddressResultReceiver;
import yosoyo.aaahearhereprototype.AsyncDataManager;
import yosoyo.aaahearhereprototype.FetchAddressIntentService;
import yosoyo.aaahearhereprototype.HHServerClasses.HHCachedSpotifyTrack;
import yosoyo.aaahearhereprototype.HHServerClasses.HHPostFull;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;
import yosoyo.aaahearhereprototype.HolderActivity;
import yosoyo.aaahearhereprototype.R;
import yosoyo.aaahearhereprototype.ZZZUtility;

/**
 * Created by adam on 27/02/16.
 */
public class MapViewFragment
	extends Fragment
	implements GoogleMap.OnInfoWindowClickListener
	{

	private static final String TAG = "MapViewFragment";

	private List<HHPostFull> posts = new ArrayList<>();

	private MapView mMapView;
	private GoogleMap googleMap;
	private Boolean mapExists = false;
	private Location lastLocation;
	private Location middleLocation;
	private Marker currentMarker;

	private AddressResultReceiver mResultReceiver;
	private static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
	private static final String LOCATION_ADDRESS_KEY = "location-address";
	protected boolean mAddressRequested;
	protected String mAddressOutput;
	private ProgressBar mProgressBar;

	private HHCachedSpotifyTrack currentTrack;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_map, container, false);

		mMapView = (MapView) v.findViewById(R.id.mapView);

		mMapView.onCreate(savedInstanceState);

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
					mAddressOutput = TextUtils.join(System.getProperty("line.separator"), addressFragments);

					displayAddressOutput();

					mAddressRequested = false;
					updateUIWidgets();
				}
			});
		mProgressBar = (ProgressBar) getView().findViewById(R.id.progress_bar);
		mAddressRequested = false;
		mAddressOutput = "";

		updateUIWidgets();

		super.onStart();
	}

	public void onStop() {
		//mGoogleApiClient.disconnect();
		super.onStop();
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

	private void setUpMap(){
		googleMap.setMyLocationEnabled(true);

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
				updateUIWidgets();

			}
		});

		googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

		googleMap.setOnInfoWindowClickListener(this);

		AsyncDataManager.getAllPosts(
			new AsyncDataManager.GetAllPostsCallback() {
				@Override
				public void returnAllCachedPosts(List<HHPostFull> cachedPosts) {
					posts = ZZZUtility.mergeLists(posts, cachedPosts);
					addMapMarkers();
				}

				@Override
				public void returnWebPost(HHPostFull webPost) {
					if (ZZZUtility.addItemToList(posts, webPost))
						addMapMarker(webPost, true);
				}
			});
	}

	private void onMapReadyForLocation(){

		if (!(HolderActivity.apiExists && mapExists))
			return;

		lastLocation = HolderActivity.getLastLocation();
		LatLng myLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

		addMapMarkers();

		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
																		  .target(myLatLng).zoom(15)
																		  .build()), 1000, null);
	}

	private void addMapMarkers(){
		googleMap.clear();
		for (HHPostFull post : posts) {
			addMapMarker(post, false);
		}
	}

	private void addMapMarker(HHPostFull post, boolean newColour){
		LatLng latLng = new LatLng(post.getPost().getLat(), post.getPost().getLon());
		googleMap.addMarker(
			new MarkerOptions().position(latLng)
							   .title(new Gson().toJson(post))
							   .icon(BitmapDescriptorFactory
										 .fromResource(
											 newColour ? R.drawable.music_marker_new_small : R.drawable.music_marker_small))
						   );
	}

	protected void startIntentService() {
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

		class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter/*, DownloadImageTask.DownloadImageTaskCallback*/ {

		private final View mWindow;
		private final View mContents;
		private HHCachedSpotifyTrack spotifyTrack;

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

		private void render(Marker marker, View view) {

			currentMarker = marker;

			HHPostFull post = new Gson().fromJson(marker.getTitle(), HHPostFull.class);
			spotifyTrack = post.getTrack();
			currentTrack = spotifyTrack;

			final ImageView imgAlbumArt = (ImageView) view.findViewById(R.id.imgAlbumArt);
			final ImageView imgUserArt = (ImageView) view.findViewById(R.id.imgUserArt);

			if (!marker.isInfoWindowShown()) {
				imgAlbumArt.setImageBitmap(null);
				imgUserArt.setImageBitmap(null);

				WebHelper.getSpotifyAlbumArt(
					spotifyTrack,
					new WebHelper.GetSpotifyAlbumArtCallback() {
						@Override
						public void returnSpotifyAlbumArt(Bitmap bitmap) {
							imgAlbumArt.setImageBitmap(bitmap);
						}
					});

				WebHelper.getFacebookProfilePicture(
					post.getUser().getFBUserID(),
					new WebHelper.GetFacebookProfilePictureCallback() {
						@Override
						public void returnFacebookProfilePicture(Bitmap bitmap) {
							imgUserArt.setImageBitmap(bitmap);
						}
					});
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
			snippetUI.setText("Message: " + post.getPost().getMessage());
			dateUI.setText("Posted: " + ZZZUtility.formatDynamicDate(
				post.getPost().getCreatedAt()));
			userUI.setText(post.getUser().getName());

			final ImageButton playButton = (ImageButton) view.findViewById(R.id.play_button);
			if (HolderActivity.mediaPlayer.isPlaying()) {
				playButton.setImageResource(R.drawable.ic_media_pause);
			} else {
				playButton.setImageResource(R.drawable.ic_media_play);
			}

		}
	}

	private void showToast(String text){
		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
	}

	private void displayAddressOutput(){
		showToast(mAddressOutput);
	}

	private void updateUIWidgets(){
		if (mAddressRequested){
			mProgressBar.setVisibility(ProgressBar.VISIBLE);
		} else {
			mProgressBar.setVisibility(ProgressBar.GONE);
		}
	}
}
