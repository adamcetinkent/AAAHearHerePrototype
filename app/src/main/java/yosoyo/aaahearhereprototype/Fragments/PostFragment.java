package yosoyo.aaahearhereprototype.Fragments;


import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import yosoyo.aaahearhereprototype.AddressPicker;
import yosoyo.aaahearhereprototype.AddressResultReceiver;
import yosoyo.aaahearhereprototype.FetchAddressIntentService;
import yosoyo.aaahearhereprototype.HHServerClasses.HHPost;
import yosoyo.aaahearhereprototype.HHServerClasses.HHPostFull;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUser;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.CreatePostTask;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;
import yosoyo.aaahearhereprototype.HolderActivity;
import yosoyo.aaahearhereprototype.PostFragmentPostedListener;
import yosoyo.aaahearhereprototype.R;
import yosoyo.aaahearhereprototype.SearchResultsActivity;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyAlbum;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyArtist;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment
	implements CreatePostTask.CreatePostTaskCallback {

	public static final String TAG = "PostFragment";

	private PostFragmentPostedListener postFragmentPostedListener;

	private SpotifyTrack spotifyTrack;
	private SpotifyArtist spotifyArtist;
	private SpotifyAlbum spotifyAlbum;
	private Location lastLocation;

	private SearchView searchViewTrack;
	private SearchView searchViewArtist;
	private SearchView searchViewAlbum;
	private TextView txtTrack;
	private TextView txtArtist;
	private TextView txtAlbum;
	private TextView txtMessage;
	private TextView txtDateTime;
	private TextView txtLocation;
	private ImageView imgAlbumArt;
	private LinearLayout llSearch;
	private LinearLayout llText;
	private ImageView btnPlayButton;
	private ImageView btnLocationButton;

	private AddressResultReceiver mResultReceiver;
	protected boolean mAddressRequested;
	private Address address;
	String placeName = new String();
	String googlePlaceID = new String();

	public PostFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem menuItem = menu.findItem(R.id.post);
		menuItem.setVisible(false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		final View view = inflater.inflate(R.layout.fragment_post, container, false);

		TextView txtUserName = (TextView) view.findViewById(R.id.post_fragment_txtUserName);
		txtUserName.setText(Profile.getCurrentProfile().getName());

		txtDateTime = (TextView) view.findViewById(R.id.post_fragment_txtDateTime);
		txtDateTime.setText(DateFormat.getDateTimeInstance().format(new Date()));

		if (HHUser.getProfilePicture() != null){
			ImageView imgProfilePicture = (ImageView) view.findViewById(R.id.post_fragment_imgProfile);
			imgProfilePicture.setImageBitmap(HHUser.getProfilePicture());
		}

		btnLocationButton = (ImageView) view.findViewById(R.id.post_fragment_btnLocation);
		btnLocationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), AddressPicker.class);
				String stringJson = new Gson().toJson(address, Address.class);
				intent.putExtra(AddressPicker.ADDRESS_JSON, stringJson);
				startActivityForResult(intent, AddressPicker.REQUEST_CODE);
			}
		});

		if (HolderActivity.apiExists && HolderActivity.mGoogleApiClient != null){
			lastLocation = HolderActivity.getLastLocation();
			txtLocation = (TextView) view.findViewById(R.id.post_fragment_txtLocation);
			txtLocation.setText(lastLocation.getLatitude() + " " + lastLocation.getLongitude());
			placeName = lastLocation.getLatitude() + " " + lastLocation.getLongitude();
			mResultReceiver = new AddressResultReceiver(
				new Handler(),
				new AddressResultReceiver.AddressResultReceiverCallback() {
					@Override
					public void returnAddress(Address returnedAddress) {
						address = returnedAddress;
						txtLocation.setText(address.getThoroughfare().toString());
						placeName = address.getThoroughfare().toString();
						btnLocationButton.setVisibility(View.VISIBLE);
						mAddressRequested = false;
					}
				});
			if (HolderActivity.mGoogleApiClient.isConnected() && lastLocation != null) {
				startIntentService();
			}
		}

		searchViewTrack = (SearchView) view.findViewById(R.id.post_fragment_searchTrackName);
		searchViewArtist = (SearchView) view.findViewById(R.id.post_fragment_searchArtist);
		searchViewAlbum = (SearchView) view.findViewById(R.id.post_fragment_searchAlbum);
		txtMessage = (TextView) view.findViewById(R.id.post_fragment_txtMessage);
		txtTrack = (TextView) view.findViewById(R.id.post_fragment_txtTrackName);
		txtArtist = (TextView) view.findViewById(R.id.post_fragment_txtArtist);
		txtAlbum = (TextView) view.findViewById(R.id.post_fragment_txtAlbum);
		imgAlbumArt = (ImageView) view.findViewById(R.id.post_fragment_imgAlbumArt);

		llSearch = (LinearLayout) view.findViewById(R.id.post_fragment_llPostFrameSearch);
		llText = (LinearLayout) view.findViewById(R.id.post_fragment_llPostFrameText);
		llText.setVisibility(View.GONE);

		searchViewTrack.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				Log.d(TAG, "OnQueryTextSubmit");
				Intent intent = new Intent(getActivity(), SearchResultsActivity.class);
				intent.setAction(Intent.ACTION_SEARCH);
				intent.putExtra(SearchManager.QUERY, query);
				if (spotifyArtist == null && spotifyAlbum == null) {
					startActivityForResult(intent, SearchResultsActivity.REQUEST_CODE_TRACK);
				} else if (spotifyAlbum != null && spotifyArtist == null) {
					intent.putExtra(SearchResultsActivity.QUERY_ALBUM, spotifyAlbum.getName());
					startActivityForResult(intent, SearchResultsActivity.REQUEST_CODE_TRACK_ALBUM);
				} else if (spotifyArtist != null && spotifyAlbum == null) {
					intent.putExtra(SearchResultsActivity.QUERY_ARTIST, spotifyArtist.getName());
					startActivityForResult(intent, SearchResultsActivity.REQUEST_CODE_TRACK_ARTIST);
				} else {
					intent.putExtra(SearchResultsActivity.QUERY_ARTIST, spotifyArtist.getName());
					intent.putExtra(SearchResultsActivity.QUERY_ALBUM, spotifyAlbum.getName());
					startActivityForResult(intent,
										   SearchResultsActivity.REQUEST_CODE_TRACK_ARTIST_ALBUM);
				}
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				Log.d(TAG, "OnQueryTextChange");
				return false;
			}
		});

		searchViewArtist.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				Log.d(TAG, "OnQueryTextSubmit");
				Intent intent = new Intent(getActivity(), SearchResultsActivity.class);
				intent.setAction(Intent.ACTION_SEARCH);
				intent.putExtra(SearchManager.QUERY, query);
				startActivityForResult(intent, SearchResultsActivity.REQUEST_CODE_ARTIST);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				Log.d(TAG, "OnQueryTextChange");
				return false;
			}
		});

		searchViewAlbum.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				Log.d(TAG, "OnQueryTextSubmit");
				Intent intent = new Intent(getActivity(), SearchResultsActivity.class);
				intent.setAction(Intent.ACTION_SEARCH);
				intent.putExtra(SearchManager.QUERY, query);
				if (spotifyArtist != null) {
					intent.putExtra(SearchResultsActivity.QUERY_ARTIST, spotifyArtist.getName());
					startActivityForResult(intent, SearchResultsActivity.REQUEST_CODE_ALBUM_ARTIST);
				} else {
					startActivityForResult(intent, SearchResultsActivity.REQUEST_CODE_ALBUM);
				}
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				Log.d(TAG, "OnQueryTextChange");
				return false;
			}
		});

		btnPlayButton = (ImageView) view.findViewById(R.id.post_fragment_btnPlayButton);
		btnPlayButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (spotifyTrack == null || HolderActivity.mediaPlayer.isPlaying()) {
					HolderActivity.mediaPlayer.reset();
					updatePlayButton(btnPlayButton);
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

					HolderActivity.mediaPlayer
						.setOnErrorListener(new MediaPlayer.OnErrorListener() {
							@Override
							public boolean onError(MediaPlayer mp, int what, int extra) {
								HolderActivity.mediaPlayer.reset();
								updatePlayButton(btnPlayButton);
								return false;
							}
						});

					HolderActivity.mediaPlayer
						.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
							@Override
							public void onPrepared(MediaPlayer mp) {
								HolderActivity.mediaPlayer.start();
								progressDialog.dismiss();
								updatePlayButton(btnPlayButton);
							}
						});

					HolderActivity.mediaPlayer
						.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
							@Override
							public void onCompletion(MediaPlayer mp) {
								updatePlayButton(btnPlayButton);
							}
						});

					HolderActivity.mediaPlayer
						.setDataSource(spotifyTrack.getPreviewUrl());
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
		});

		updatePlayButton(btnPlayButton);

		final ImageButton postButton = (ImageButton) view.findViewById(R.id.post_fragment_btnPost);
		postButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (spotifyTrack == null){
					Toast.makeText(getActivity(), "No track selected!", Toast.LENGTH_SHORT).show();
					return;
				}
				HHPost post = new HHPost(HHUser.getCurrentUser().getID(), spotifyTrack.getID(), lastLocation.getLatitude(), lastLocation.getLongitude(), txtMessage.getText().toString(), placeName, googlePlaceID);
				new CreatePostTask(PostFragment.this, post).execute();
				postButton.setVisibility(View.INVISIBLE);
				ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.post_fragment_progressBar);
				progressBar.setVisibility(View.VISIBLE);
			}
		});

		return view;
	}

	protected void startIntentService() {
		Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
		intent.putExtra(FetchAddressIntentService.Constants.RECEIVER, mResultReceiver);
		intent.putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, lastLocation);
		getActivity().startService(intent);
	}

	private void updatePlayButton(ImageView btnPlayButton){
		if (spotifyTrack == null){
			btnPlayButton.setVisibility(View.GONE);
		} else {
			btnPlayButton.setVisibility(View.VISIBLE);
		}
		if (spotifyAlbum == null && spotifyArtist == null && spotifyTrack == null){
			imgAlbumArt.setImageBitmap(null);
			imgAlbumArt.setBackgroundColor(Color.WHITE);
		} else {
			imgAlbumArt.setBackgroundColor(Color.TRANSPARENT);
		}
		if (HolderActivity.mediaPlayer.isPlaying()) {
			btnPlayButton.setImageResource(R.drawable.pause_overlay);
		} else {
			btnPlayButton.setImageResource(R.drawable.play_overlay);
		}
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode){
		intent.putExtra(HolderActivity.REQUEST_CODE, requestCode);
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try {
			postFragmentPostedListener = (PostFragmentPostedListener) context;
		} catch (ClassCastException e){
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode){
			case (SearchResultsActivity.REQUEST_CODE_TRACK_ARTIST_ALBUM):
			case (SearchResultsActivity.REQUEST_CODE_TRACK_ALBUM):
			case (SearchResultsActivity.REQUEST_CODE_TRACK_ARTIST):
			case (SearchResultsActivity.REQUEST_CODE_TRACK): {
				if (resultCode == Activity.RESULT_OK) {
					spotifyTrack = new Gson().fromJson(data.getStringExtra(SearchResultsActivity.TRACK_JSON), SpotifyTrack.class);
					txtTrack.setText(spotifyTrack.getName());
					txtArtist.setText(spotifyTrack.getArtistName());
					txtAlbum.setText(spotifyTrack.getAlbumName());
					llSearch.setVisibility(View.GONE);
					llText.setVisibility(View.VISIBLE);

					WebHelper.getSpotifyAlbumArt(spotifyTrack.getID(),
												 spotifyTrack.getImages(0).getUrl(),
												 new WebHelper.GetSpotifyAlbumArtCallback() {
													 @Override
													 public void returnSpotifyAlbumArt(Bitmap bitmap) {
														 imgAlbumArt.setImageBitmap(bitmap);
													 }
												 });
				}
				break;
			}
			//case (SearchResultsActivity.REQUEST_CODE_ARTIST_ALBUM):
			case (SearchResultsActivity.REQUEST_CODE_ARTIST): {
				if (resultCode == Activity.RESULT_OK){
					spotifyArtist = new Gson().fromJson(data.getStringExtra(SearchResultsActivity.ARTIST_JSON), SpotifyArtist.class);
					searchViewArtist.setQueryHint(spotifyArtist.getName());
					searchViewArtist.setQuery("", false);
					searchViewArtist.clearFocus();

					if (spotifyAlbum == null || spotifyAlbum.getArtistID() != spotifyArtist.getID()) {
						searchViewArtist.setQueryHint(spotifyArtist.getName());
						searchViewArtist.setQuery("", false);
						searchViewArtist.clearFocus();
					}

					WebHelper.getSpotifyAlbumArt(spotifyArtist.getID(),
												 spotifyArtist.getImages(0).getUrl(),
												 new WebHelper.GetSpotifyAlbumArtCallback() {
													 @Override
													 public void returnSpotifyAlbumArt(Bitmap bitmap) {
														 imgAlbumArt.setImageBitmap(bitmap);
													 }
												 });
				}
				break;
			}
			case (SearchResultsActivity.REQUEST_CODE_ALBUM_ARTIST):
			case (SearchResultsActivity.REQUEST_CODE_ALBUM): {
				if (resultCode == Activity.RESULT_OK){
					spotifyAlbum = new Gson().fromJson(data.getStringExtra(SearchResultsActivity.ALBUM_JSON), SpotifyAlbum.class);
					searchViewAlbum.setQueryHint(spotifyAlbum.getName());
					searchViewAlbum.setQuery("", false);
					searchViewAlbum.clearFocus();

					if (spotifyArtist == null){
						searchViewArtist.setQueryHint(spotifyAlbum.getArtistName());
					}

					WebHelper.getSpotifyAlbumArt(spotifyAlbum.getID(),
												 spotifyAlbum.getImages(0).getUrl(),
												 new WebHelper.GetSpotifyAlbumArtCallback() {
													 @Override
													 public void returnSpotifyAlbumArt(Bitmap bitmap) {
														 imgAlbumArt.setImageBitmap(bitmap);
													 }
												 });
				}
				break;
			}
			case (AddressPicker.REQUEST_CODE): {
				if (resultCode == Activity.RESULT_OK){
					placeName = data.getStringExtra(AddressPicker.ADDRESS_STRING);
					googlePlaceID = data.getStringExtra(AddressPicker.GOOGLE_PLACE_ID);
					txtLocation.setText(placeName);
				}
			}
		}

		updatePlayButton(btnPlayButton);

	}

	@Override
	public void returnResultCreatePost(Boolean success, HHPostFull postReturned) {
		if (success) {
			Log.d(TAG, "New Post created!");
			postFragmentPostedListener.onPostFragmentPosted();
		}
	}

}
