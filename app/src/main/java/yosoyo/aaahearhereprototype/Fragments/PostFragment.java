package yosoyo.aaahearhereprototype.Fragments;


import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.gson.Gson;

import java.io.IOException;

import yosoyo.aaahearhereprototype.HolderActivity;
import yosoyo.aaahearhereprototype.PostFragmentPostedListener;
import yosoyo.aaahearhereprototype.R;
import yosoyo.aaahearhereprototype.SearchResultsActivity;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyAlbum;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyArtist;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;
import yosoyo.aaahearhereprototype.TestServerClasses.Tasks.TestCreatePostTask;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPost;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPostFull;
import yosoyo.aaahearhereprototype.ZZZUtility;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment
	implements TestCreatePostTask.TestCreatePostTaskCallback {

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
	private ImageView imgAlbumArt;
	private LinearLayout llSearch;
	private LinearLayout llText;
	private ImageButton btnPlayButton;

	private boolean postAdded = false;
	private boolean trackAdded = false;

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

		View view = inflater.inflate(R.layout.fragment_post, container, false);

		TextView txtUserName = (TextView) view.findViewById(R.id.post_fragment_txtUserName);
		txtUserName.setText(Profile.getCurrentProfile().getName());

		if (HolderActivity.profilePicture != null){
			ImageView imgProfilePicture = (ImageView) view.findViewById(R.id.post_fragment_imgProfile);
			imgProfilePicture.setImageBitmap(HolderActivity.profilePicture);
		}

		if (HolderActivity.apiExists && HolderActivity.mGoogleApiClient != null){
			lastLocation = HolderActivity.getLastLocation();
			TextView txtLocation = (TextView) view.findViewById(R.id.post_fragment_txtLocation);
			txtLocation.setText(lastLocation.getLatitude() + " " + lastLocation.getLongitude());
		}

		searchViewTrack = (SearchView) view.findViewById(R.id.post_fragment_searchTrackName);
		searchViewArtist = (SearchView) view.findViewById(R.id.post_fragment_searchArtist);
		searchViewAlbum = (SearchView) view.findViewById(R.id.post_fragment_searchAlbum);
		txtTrack = (TextView) view.findViewById(R.id.post_fragment_txtTrackName);
		txtArtist = (TextView) view.findViewById(R.id.post_fragment_txtArtist);
		txtAlbum = (TextView) view.findViewById(R.id.post_fragment_txtAlbum);
		txtMessage = (TextView) view.findViewById(R.id.post_fragment_txtMessage);
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
				/*if (spotifyAlbum != null) {
					intent.putExtra(SearchResultsActivity.QUERY_ALBUM, spotifyAlbum.getName());
					startActivityForResult(intent, SearchResultsActivity.REQUEST_CODE_ARTIST_ALBUM);
				} else {
					startActivityForResult(intent, SearchResultsActivity.REQUEST_CODE_ARTIST);
				}*/
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

		btnPlayButton = (ImageButton) view.findViewById(R.id.post_fragment_btnPlayButton);
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

		Button postButton = (Button) view.findViewById(R.id.post_fragment_btnPost);
		postButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (spotifyTrack == null){
					Toast.makeText(getActivity(), "No track selected!", Toast.LENGTH_SHORT).show();
					return;
				}
				TestPost testPost = new TestPost(HolderActivity.testUser.getID(), spotifyTrack.getID(), lastLocation.getLatitude(), lastLocation.getLongitude(), txtMessage.getText().toString());
				new TestCreatePostTask(PostFragment.this, testPost).execute();
			}
		});

		return view;
	}

	private void updatePlayButton(ImageButton btnPlayButton){
		if (spotifyTrack == null){
			btnPlayButton.setVisibility(View.GONE);
		} else {
			btnPlayButton.setVisibility(View.VISIBLE);
		}
		if (HolderActivity.mediaPlayer.isPlaying()) {
			btnPlayButton.setImageResource(R.drawable.ic_media_pause);
		} else {
			btnPlayButton.setImageResource(R.drawable.ic_media_play);
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

					byte[] bytes = data.getByteArrayExtra(SearchResultsActivity.BMP_JSON);
					imgAlbumArt.setImageBitmap(ZZZUtility.convertByteArrayToBitmap(bytes));
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

					byte[] bytes = data.getByteArrayExtra(SearchResultsActivity.BMP_JSON);
					imgAlbumArt.setImageBitmap(ZZZUtility.convertByteArrayToBitmap(bytes));
					break;
				}
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

					byte[] bytes = data.getByteArrayExtra(SearchResultsActivity.BMP_JSON);
					imgAlbumArt.setImageBitmap(ZZZUtility.convertByteArrayToBitmap(bytes));
					break;
				}
			}
		}

		updatePlayButton(btnPlayButton);

	}

	@Override
	public void returnResultCreatePost(Boolean success, TestPostFull testPostUser) {
		if (success) {
			Log.d(TAG, "New Post created!");
			//TestPostUserTrack testPostUserTrack = new TestPostUserTrack(testPostUser, new CachedSpotifyTrack(spotifyTrack));
			//HolderActivity.dataHolder.addTestPostUserTrack(testPostUserTrack, this, this);
			postFragmentPostedListener.onPostFragmentPosted();
		}
	}

	/*@Override
	public void returnInsertCachedSpotifyTrackCallback(long id, CachedSpotifyTrack cachedSpotifyTrack) {
		trackAdded = true;
		successfulPost();
	}

	@Override
	public void returnInsertPost(long id, TestPostUser testPostUser) {
		postAdded = true;
		successfulPost();
	}

	private void successfulPost(){
		if (postAdded && trackAdded) {
			Toast.makeText(getActivity(), "Posted to Hear Here!", Toast.LENGTH_LONG).show();
			postFragmentPostedListener.onFragmentSuicide(getTag());
		}
	}*/

}
