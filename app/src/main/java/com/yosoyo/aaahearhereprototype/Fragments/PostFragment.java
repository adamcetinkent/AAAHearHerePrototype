package com.yosoyo.aaahearhereprototype.Fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.gson.Gson;
import com.yosoyo.aaahearhereprototype.Activities.AddressPicker;
import com.yosoyo.aaahearhereprototype.Activities.HolderActivity;
import com.yosoyo.aaahearhereprototype.Activities.SpotifySearchResultsActivity;
import com.yosoyo.aaahearhereprototype.AsyncDataManager;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHCachedSpotifyTrack;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPostFullProcess;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHTag;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUserFull;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHPostTagsArray;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;
import com.yosoyo.aaahearhereprototype.R;
import com.yosoyo.aaahearhereprototype.Services.AddressResultReceiver;
import com.yosoyo.aaahearhereprototype.Services.FetchAddressIntentService;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyAlbum;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyArtist;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;
import com.yosoyo.aaahearhereprototype.ZZZInterface.TaggableEditText;
import com.yosoyo.aaahearhereprototype.ZZZUtility;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by adam on 26/02/2016.
 *
 * PostFragment is used to create new posts. It provides a platform to search the Spotify database.
 */
public class PostFragment extends FeedbackFragment {

	private static final String TAG = "PostFragment";

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
	private TaggableEditText txtMessage;
	private TextView txtDateTime;
	private TextView txtLocation;
	private ImageView imgAlbumArt;
	private LinearLayout llSearch;
	private LinearLayout llText;
	private ImageView btnPlayButton;
	private ImageView btnLocationButton;

	private AddressResultReceiver mResultReceiver;
	private boolean mAddressRequested;
	private Address address;
	private String placeName = "";
	private String googlePlaceID = "";

	private static final String KEY_TRACK_ID = TAG + "track_id";
	private String trackID;

	public static PostFragment newInstance(String trackID){
		PostFragment postFragment = new PostFragment();

		Bundle bundle = new Bundle();
		bundle.putString(KEY_TRACK_ID, trackID);
		postFragment.setArguments(bundle);

		return postFragment;
	}

	public PostFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		Bundle arguments = getArguments();
		if (arguments != null){
			if (arguments.containsKey(KEY_TRACK_ID)){
				trackID = arguments.getString(KEY_TRACK_ID);
			}
		}

	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem menuItem = menu.findItem(R.id.action_post);
		menuItem.setVisible(false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		final View view = inflater.inflate(R.layout.fragment_post, container, false);

		final TextView txtUserName = (TextView) view.findViewById(R.id.post_fragment_txtUserName);
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

		txtLocation = (TextView) view.findViewById(R.id.post_fragment_txtLocation);
		getLocation();

		searchViewTrack = (SearchView) view.findViewById(R.id.post_fragment_searchTrackName);
		searchViewArtist = (SearchView) view.findViewById(R.id.post_fragment_searchArtist);
		searchViewAlbum = (SearchView) view.findViewById(R.id.post_fragment_searchAlbum);
		txtMessage = (TaggableEditText) view.findViewById(R.id.post_fragment_txtMessage);
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
				//Log.d(TAG, "OnQueryTextSubmit");
				Intent intent = new Intent(getActivity(), SpotifySearchResultsActivity.class);
				intent.setAction(Intent.ACTION_SEARCH);
				intent.putExtra(SearchManager.QUERY, query);
				if (spotifyArtist == null && spotifyAlbum == null) {
					startActivityForResult(intent, SpotifySearchResultsActivity.REQUEST_CODE_TRACK);
				} else if (spotifyAlbum != null && spotifyArtist == null) {
					intent.putExtra(SpotifySearchResultsActivity.QUERY_ALBUM, spotifyAlbum.getName());
					startActivityForResult(intent, SpotifySearchResultsActivity.REQUEST_CODE_TRACK_ALBUM);
				} else if (spotifyAlbum == null) {
					intent.putExtra(SpotifySearchResultsActivity.QUERY_ARTIST, spotifyArtist.getName());
					startActivityForResult(intent, SpotifySearchResultsActivity.REQUEST_CODE_TRACK_ARTIST);
				} else {
					intent.putExtra(SpotifySearchResultsActivity.QUERY_ARTIST, spotifyArtist.getName());
					intent.putExtra(SpotifySearchResultsActivity.QUERY_ALBUM, spotifyAlbum.getName());
					startActivityForResult(intent,
										   SpotifySearchResultsActivity.REQUEST_CODE_TRACK_ARTIST_ALBUM);
				}
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				//Log.d(TAG, "OnQueryTextChange");
				return false;
			}
		});

		searchViewArtist.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				//Log.d(TAG, "OnQueryTextSubmit");
				Intent intent = new Intent(getActivity(), SpotifySearchResultsActivity.class);
				intent.setAction(Intent.ACTION_SEARCH);
				intent.putExtra(SearchManager.QUERY, query);
				startActivityForResult(intent, SpotifySearchResultsActivity.REQUEST_CODE_ARTIST);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				//Log.d(TAG, "OnQueryTextChange");
				return false;
			}
		});

		searchViewAlbum.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				//Log.d(TAG, "OnQueryTextSubmit");
				Intent intent = new Intent(getActivity(), SpotifySearchResultsActivity.class);
				intent.setAction(Intent.ACTION_SEARCH);
				intent.putExtra(SearchManager.QUERY, query);
				if (spotifyArtist != null) {
					intent.putExtra(SpotifySearchResultsActivity.QUERY_ARTIST, spotifyArtist.getName());
					startActivityForResult(intent, SpotifySearchResultsActivity.REQUEST_CODE_ALBUM_ARTIST);
				} else {
					startActivityForResult(intent, SpotifySearchResultsActivity.REQUEST_CODE_ALBUM);
				}
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				//Log.d(TAG, "OnQueryTextChange");
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

					HolderActivity.mediaPlayer.setDataSource(spotifyTrack.getPreviewURL());
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
				} catch (NullPointerException e) {
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
				if (spotifyTrack == null) {
					Toast.makeText(getActivity(), "No track selected!", Toast.LENGTH_SHORT).show();
					return;
				}
				if (lastLocation == null){
					Toast.makeText(getActivity(), "No location found!", Toast.LENGTH_SHORT).show();
					getLocation();
					return;
				}

				Editable message = txtMessage.getText();
				HHUser.HHUserSpan[] spans = message.getSpans(0, message.length(), HHUser.HHUserSpan.class);
				HHTag[] tags = new HHTag[spans.length];
				for (int i = 0; i < spans.length; i++){
					HHUser.HHUserSpan span = spans[i];
					message.replace(
						message.getSpanStart(span),
						message.getSpanEnd(span),
						"{tag_"+String.format(Locale.ENGLISH, "%1$d", span.getUser().getID())+"}"
					);
					tags[i] = new HHTag(0, span.getUser().getID());
				}

				HHPostTagsArray post = new HHPostTagsArray(
					HHUser.getCurrentUserID(),
					spotifyTrack.getID(),
					lastLocation.getLatitude(),
					lastLocation.getLongitude(),
					message.toString(),
					placeName,
					googlePlaceID,
					tags
				);
				AsyncDataManager.postPost(post, new AsyncDataManager.PostPostCallback() {
					@Override
					public void returnPostPost(boolean success, HHPostFullProcess returnedPost) {
						requestMapView();
					}
				});
				postButton.setVisibility(View.INVISIBLE);
				ProgressBar progressBar = (ProgressBar) view
					.findViewById(R.id.post_fragment_progressBar);
				progressBar.setVisibility(View.VISIBLE);
			}
		});

		txtMessage.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				if (txtMessage.isListenerBlocked())
					return;

				if (count == 0) { // deletion
					if (txtMessage.isTagging() && start > 0 && s.charAt(start - 1) != '@') {
						txtMessage.setListenerBlock(true);
						String newString = s.subSequence(
							0,
							s.subSequence(0, start).toString().lastIndexOf('@')
						).toString()
							+ txtMessage.getSuffix();
						txtMessage.setText(newString);
						txtMessage
							.setSelection(s.subSequence(0, start).toString().lastIndexOf('@'));
						txtMessage.setListenerBlock(false);
					} else {
						Editable text = txtMessage.getText();
						HHUser.HHUserSpan[] spans = text.getSpans(0, text.length(), HHUser.HHUserSpan.class);
						if (spans.length > 0) {
							for (HHUser.HHUserSpan span : spans) {
								int spanStart = text.getSpanStart(span);
								int spanEnd = text.getSpanEnd(span);
								if (start > spanStart && start <= spanEnd) {
									text.replace(
										spanStart,
										spanEnd,
										""
									);
									text.removeSpan(span);
								}
							}
						}
					}
					txtMessage.setIsTagging(false);
					return;
				}

				if (!txtMessage.isTagging()) {
					if (s.charAt(start) == '@') {
						txtMessage.setIsTagging(true);
						txtMessage.setPrefix(s.subSequence(0, start));
						txtMessage.setSuffix(s.subSequence(start + 1, s.length()));
					}
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		txtMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					txtMessage.setIsTagging(false);
				}
			}
		});

		Set<HHUser> userSet = new HashSet<>();
		HHUserFull currentUser = HHUser.getCurrentUser();
		for (HHFollowUser followUser: currentUser.getFollowOuts()){
			userSet.add(followUser.getUser());
		}
		for (HHFollowUser followUser: currentUser.getFollowIns()){
			userSet.add(followUser.getUser());
		}
		final List<HHUser> userList = new ArrayList<>();
		userList.addAll(userSet);
		final TagArrayAdapter tagArrayAdapter = new TagArrayAdapter(getActivity(), userList, txtMessage);
		txtMessage.setAdapter(tagArrayAdapter);
		txtMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				HHUser user = ((HHUser) tagArrayAdapter.getItem(position));
				HHUser.HHUserSpan userSpan = new HHUser.HHUserSpan(getActivity(), user, null);

				int selectionEnd = setTaggableText(txtMessage, userSpan, user);
				txtMessage.setSelection(selectionEnd);

				txtMessage.setIsTagging(false);
			}
		});

		if (trackID != null && !trackID.isEmpty()){
			searchViewTrack.setEnabled(false);
			searchViewArtist.setEnabled(false);
			searchViewAlbum.setEnabled(false);
			AsyncDataManager.getSpotifyTrack(
				trackID,
				new AsyncDataManager.GetSpotifyTrackCallback() {
					@Override
					public void returnSpotifyTrack(SpotifyTrack spotifyTrack) {

						if (spotifyTrack == null) {
							Toast.makeText(getActivity(), "NOT A TRACK!", Toast.LENGTH_LONG).show();

							AsyncDataManager.getSpotifyAlbum(
								trackID,
								new AsyncDataManager.GetSpotifyAlbumCallback(){
									@Override
									public void returnSpotifyAlbum(SpotifyAlbum spotifyAlbum) {

										if (spotifyAlbum == null) {

											Toast.makeText(getActivity(),
														   "NOT AN ALBUM!",
														   Toast.LENGTH_LONG).show();

											AsyncDataManager.getSpotifyArtist(
												trackID,
												new AsyncDataManager.GetSpotifyArtistCallback() {
													@Override
													public void returnSpotifyArtist(SpotifyArtist spotifyArtist) {
														if (spotifyArtist == null){
															Toast.makeText(getActivity(),
																		   "NOT AN ARTIST!",
																		   Toast.LENGTH_LONG).show();
														} else {
															Toast.makeText(getActivity(),
																		   "ARTIST FOUND: " + spotifyArtist.getName(),
																		   Toast.LENGTH_LONG).show();
															PostFragment.this.spotifyArtist = spotifyArtist;
															searchViewArtist.setQueryHint(spotifyArtist.getName());
															searchViewArtist.setQuery("", false);
															searchViewArtist.clearFocus();

															WebHelper.getSpotifyAlbumArt(
																spotifyArtist.getID(),
																spotifyArtist.getImageURL(),
																new WebHelper.GetSpotifyAlbumArtCallback() {
																	@Override
																	public void returnSpotifyAlbumArt(Bitmap bitmap) {
																		imgAlbumArt.setImageBitmap(bitmap);
																	}
																});

															searchViewTrack.setEnabled(true);
															searchViewArtist.setEnabled(true);
															searchViewAlbum.setEnabled(true);
														}
													}
												}
											);


										} else {
											Toast.makeText(getActivity(),
														   "ALBUM FOUND: " + spotifyAlbum.getName(),
														   Toast.LENGTH_LONG).show();
											PostFragment.this.spotifyAlbum = spotifyAlbum;
											searchViewAlbum.setQueryHint(spotifyAlbum.getName());
											searchViewAlbum.setQuery("", false);
											searchViewAlbum.clearFocus();

											if (spotifyArtist == null) {
												searchViewArtist
													.setQueryHint(spotifyAlbum.getArtistName());
											}

											WebHelper.getSpotifyAlbumArt(
												spotifyAlbum.getID(),
												spotifyAlbum.getImages(0).getUrl(),
												new WebHelper.GetSpotifyAlbumArtCallback() {
													@Override
													public void returnSpotifyAlbumArt(Bitmap bitmap) {
														imgAlbumArt.setImageBitmap(bitmap);
													}
												});

											searchViewTrack.setEnabled(true);
											searchViewArtist.setEnabled(true);
											searchViewAlbum.setEnabled(true);
										}
									}
								}
							);

						} else {

							PostFragment.this.spotifyTrack = spotifyTrack;
							txtTrack.setText(spotifyTrack.getName());
							txtArtist.setText(spotifyTrack.getArtistName());
							txtAlbum.setText(spotifyTrack.getAlbumName());
							llSearch.setVisibility(View.GONE);
							llText.setVisibility(View.VISIBLE);

							WebHelper.getSpotifyAlbumArt(
								spotifyTrack.getID(),
								spotifyTrack.getImageURL(),
								new WebHelper.GetSpotifyAlbumArtCallback() {
									@Override
									public void returnSpotifyAlbumArt(Bitmap bitmap) {
										imgAlbumArt.setImageBitmap(bitmap);
									}
								});

							updatePlayButton(btnPlayButton);

						}

						getLocation();

					}
					@Override
					public void returnCachedSpotifyTrack(HHCachedSpotifyTrack cachedSpotifyTrack) {}
				});
		}

		return view;
	}

	private int setTaggableText(TaggableEditText text, HHUser.HHUserSpan userSpan, HHUser user){
		CharSequence prefix = txtMessage.getPrefix();
		CharSequence spanStr = userSpan.toString();
		SpannableStringBuilder ssb = new SpannableStringBuilder(prefix);
		ssb.append(spanStr);
		ssb.append(txtMessage.getSuffix());
		ssb.setSpan(userSpan, prefix.length(), prefix.length() + spanStr.length(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		HHUser.HHUserSpan[] spans = ssb.getSpans(0, ssb.length(), HHUser.HHUserSpan.class);
		int selectionEnd = 0;
		for (HHUser.HHUserSpan span : spans){
			if (span.getUser().equals(user)){
				selectionEnd = ssb.getSpanEnd(span);
			}
		}
		text.setText(ssb);
		return selectionEnd;
	}

	private void getLocation(){
		if (HolderActivity.apiExists && HolderActivity.mGoogleApiClient != null){
			lastLocation = HolderActivity.getLastLocation(getActivity());
			placeName = ZZZUtility.getLatLng(lastLocation);
			txtLocation.setText(placeName);
			mResultReceiver = new AddressResultReceiver(
				new Handler(),
				new AddressResultReceiver.AddressResultReceiverCallback() {
					@Override
					public void returnAddress(Address returnedAddress) {
						if (returnedAddress != null) {
							address = returnedAddress;
							txtLocation.setText(address.getThoroughfare());
							placeName = address.getThoroughfare();
							btnLocationButton.setVisibility(View.VISIBLE);
							mAddressRequested = false;
						} else {
							Toast.makeText(getActivity(), "NO ADDRESS FOUND", Toast.LENGTH_LONG).show();
						}
					}
				});
			if (HolderActivity.mGoogleApiClient.isConnected() && lastLocation != null) {
				startIntentService();
			}
		}
	}

	private void startIntentService() {
		Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
		intent.putExtra(FetchAddressIntentService.Constants.RECEIVER, mResultReceiver);
		intent.putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, lastLocation);
		getActivity().startService(intent);
	}

	private void updatePlayButton(ImageView btnPlayButton){
		if (spotifyTrack == null || spotifyTrack.getPreviewURL() == null){
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
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);

		switch (resultCode){
			case (SpotifySearchResultsActivity.REQUEST_CODE_TRACK): {
				spotifyTrack = new Gson().fromJson(data.getStringExtra(
					SpotifySearchResultsActivity.TRACK_JSON), SpotifyTrack.class);
				txtTrack.setText(spotifyTrack.getName());
				txtArtist.setText(spotifyTrack.getArtistName());
				txtAlbum.setText(spotifyTrack.getAlbumName());
				llSearch.setVisibility(View.GONE);
				llText.setVisibility(View.VISIBLE);

				WebHelper.getSpotifyAlbumArt(
					spotifyTrack.getID(),
					spotifyTrack.getImageURL(),
					new WebHelper.GetSpotifyAlbumArtCallback() {
						@Override
						public void returnSpotifyAlbumArt(Bitmap bitmap) {
							imgAlbumArt.setImageBitmap(bitmap);
						}
					});
				break;
			}
			case (SpotifySearchResultsActivity.REQUEST_CODE_ARTIST): {
				spotifyArtist = new Gson().fromJson(data.getStringExtra(
					SpotifySearchResultsActivity.ARTIST_JSON), SpotifyArtist.class);
				searchViewArtist.setQueryHint(spotifyArtist.getName());
				searchViewArtist.setQuery("", false);
				searchViewArtist.clearFocus();

				if (spotifyAlbum == null || spotifyAlbum.getArtistID().equals(spotifyArtist.getID())) {
					searchViewArtist.setQueryHint(spotifyArtist.getName());
					searchViewArtist.setQuery("", false);
					searchViewArtist.clearFocus();
				}

				WebHelper.getSpotifyAlbumArt(
					spotifyArtist.getID(),
					spotifyArtist.getImageURL(),
					new WebHelper.GetSpotifyAlbumArtCallback() {
						@Override
						public void returnSpotifyAlbumArt(Bitmap bitmap) {
							imgAlbumArt.setImageBitmap(bitmap);
						}
					});
				break;
			}
			//case (SpotifySearchResultsActivity.REQUEST_CODE_ALBUM_ARTIST):
			case (SpotifySearchResultsActivity.REQUEST_CODE_ALBUM): {
				spotifyAlbum = new Gson().fromJson(data.getStringExtra(
					SpotifySearchResultsActivity.ALBUM_JSON), SpotifyAlbum.class);
				searchViewAlbum.setQueryHint(spotifyAlbum.getName());
				searchViewAlbum.setQuery("", false);
				searchViewAlbum.clearFocus();

				if (spotifyArtist == null){
					//searchViewArtist.setQueryHint(spotifyAlbum.getArtistName());
					AsyncDataManager.getSpotifyAlbum(
						spotifyAlbum.getID(),
						new AsyncDataManager.GetSpotifyAlbumCallback() {
							@Override
							public void returnSpotifyAlbum(SpotifyAlbum spotifyAlbum) {
								if (spotifyAlbum != null) {
									PostFragment.this.spotifyAlbum = spotifyAlbum;
									searchViewArtist.setQueryHint(spotifyAlbum.getArtistName());
								}
							}
						});
				}

				WebHelper.getSpotifyAlbumArt(
					spotifyAlbum.getID(),
					spotifyAlbum.getImages(0).getUrl(),
					new WebHelper.GetSpotifyAlbumArtCallback() {
						@Override
						public void returnSpotifyAlbumArt(Bitmap bitmap) {
							imgAlbumArt.setImageBitmap(bitmap);
						}
					});
				break;
			}
			case (AddressPicker.REQUEST_CODE): {
				placeName = data.getStringExtra(AddressPicker.ADDRESS_STRING);
				googlePlaceID = data.getStringExtra(AddressPicker.GOOGLE_PLACE_ID);
				txtLocation.setText(placeName);
			}
		}

		updatePlayButton(btnPlayButton);

	}

	private static class TagArrayAdapter extends BaseAdapter implements Filterable {

		private final Activity context;
		private final List<HHUser> users;
		private List<HHUser> filteredUsers;
		private final TaggableEditText txtTags;
		private final int maxLength = 5;

		public TagArrayAdapter(Activity context, List<HHUser> users, TaggableEditText txtTags) {
			this.context = context;
			this.users = users;
			this.txtTags = txtTags;
		}

		@Override
		public int getCount() {
			if (filteredUsers == null)
				return 0;
			return filteredUsers.size();
		}

		@Override
		public Object getItem(int position) {
			if (filteredUsers == null)
				return null;
			return filteredUsers.get(position);
		}

		@Override
		public long getItemId(int position) {
			if (filteredUsers == null)
				return -1;
			return filteredUsers.get(position).getID();
		}

		private static class ViewHolder{
			int position;
			HHUser user;
			TextView txtUser;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;

			if (convertView == null) {
				LayoutInflater inflater = context.getLayoutInflater();
				convertView = inflater.inflate(R.layout.list_row_tag, parent, false);

				viewHolder = new ViewHolder();

				viewHolder.position = position;
				viewHolder.user = filteredUsers.get(position);

				viewHolder.txtUser = (TextView) convertView.findViewById(R.id.list_row_tag_txtUser);

				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();
				viewHolder.position = position;
				viewHolder.user = filteredUsers.get(position);
			}

			viewHolder.txtUser.setText(viewHolder.user.getName());

			return convertView;
		}

		final Filter filter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();

				if (!txtTags.isTagging() || users == null)
					return null;

				constraint = constraint.subSequence(txtTags.getTagStart()+1, constraint.length() - txtTags.getTagSuffixLength());

				Spanned spanned = txtTags.getText();
				HHUser.HHUserSpan[] spans = spanned.getSpans(0, spanned.length(), HHUser.HHUserSpan.class);
				ArrayList<HHUser> alreadyTagged = new ArrayList<>();
				for (HHUser.HHUserSpan span : spans){
					alreadyTagged.add(span.getUser());
				}

				ArrayList<HHUser> filtered = new ArrayList<>();

				for (int i = 0; i < users.size(); i++) {
					HHUser testUser = users.get(i);
					if (alreadyTagged(testUser, alreadyTagged))
						continue;
					if (constraint.toString().toLowerCase().equals(constraint.toString())) {
						if (testUser.getName().toLowerCase().contains(constraint)) {
							filtered.add(testUser);
							if (filtered.size() > maxLength)
								break;
						}
					} else {
						if (testUser.getName().contains(constraint)) {
							filtered.add(testUser);
							if (filtered.size() > maxLength)
								break;
						}
					}
				}

				Collections.sort(filtered, new Comparator<HHUser>() {
					@Override
					public int compare(HHUser lhs, HHUser rhs) {
						return (lhs.getLastName()+lhs.getFirstName()).compareTo(rhs.getLastName()+rhs.getFirstName());
					}
				});
				filterResults.values = filtered;
				filterResults.count = filtered.size();

				return filterResults;
			}

			private boolean alreadyTagged(HHUser testUser, List<HHUser> alreadyTagged){
				for (HHUser taggedUser : alreadyTagged){
					if (testUser.equals(taggedUser))
						return true;
				}
				return false;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				if (txtTags.isTagging() && users != null) {
					//noinspection unchecked
					filteredUsers = (ArrayList<HHUser>) results.values;
					notifyDataSetChanged();
				}
			}

			@Override
			public CharSequence convertResultToString(Object resultValue) {
				if (resultValue instanceof HHUser){
					return ((HHUser) resultValue).toCharSequence(context);
				}
				return super.convertResultToString(resultValue);
			}
		};

		@Override
		public Filter getFilter() {
			return filter;
		}

	}

}
