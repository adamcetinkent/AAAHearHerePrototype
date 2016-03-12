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
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
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

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import yosoyo.aaahearhereprototype.AddressPicker;
import yosoyo.aaahearhereprototype.AddressResultReceiver;
import yosoyo.aaahearhereprototype.AsyncDataManager;
import yosoyo.aaahearhereprototype.FetchAddressIntentService;
import yosoyo.aaahearhereprototype.HHServerClasses.HHFriendshipUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHPostFullProcess;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUserFull;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHPostTagsArray;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;
import yosoyo.aaahearhereprototype.HolderActivity;
import yosoyo.aaahearhereprototype.PostFragmentPostedListener;
import yosoyo.aaahearhereprototype.R;
import yosoyo.aaahearhereprototype.SearchResultsActivity;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyAlbum;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyArtist;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;
import yosoyo.aaahearhereprototype.ZZZInterface.TaggableEditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment
	/*implements CreatePostTask.CreatePostTaskCallback*/ {

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
	private TaggableEditText txtMessage;
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
				if (spotifyTrack == null) {
					Toast.makeText(getActivity(), "No track selected!", Toast.LENGTH_SHORT).show();
					return;
				}
				HHPostTagsArray post = new HHPostTagsArray(
					HHUser.getCurrentUser().getUser().getID(),
					spotifyTrack.getID(),
					lastLocation.getLatitude(),
					lastLocation.getLongitude(),
					txtMessage.getText().toString(),
					placeName,
					googlePlaceID,
					new long[]{1, 2, 3}
				);
				AsyncDataManager.postPost(post, new AsyncDataManager.PostPostCallback() {
					@Override
					public void returnPostedPost(boolean success, HHPostFullProcess returnedPost) {
						postFragmentPostedListener.onPostFragmentPosted();
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

				if (count == 0){ // deletion
					if (txtMessage.isTagging() && start > 0 && s.charAt(start-1) != '@'){
						txtMessage.setListenerBlock(true);
						txtMessage.setText(
							s.subSequence(0, s.subSequence(0, start).toString().lastIndexOf('@'))
							 .toString() + txtMessage.getSuffix());
						txtMessage.setSelection(s.subSequence(0, start).toString().lastIndexOf('@'));
						txtMessage.setListenerBlock(false);
					}
					txtMessage.setIsTagging(false);
					txtMessage.showSuggestions(false);
					return;
				}

				if (!txtMessage.isTagging()) {
					if (s.charAt(start) == '@'){
						txtMessage.setIsTagging(true);
						txtMessage.showSuggestions(true);
						txtMessage.setPrefix(s.subSequence(0, start));
						txtMessage.setSuffix(s.subSequence(start+1, s.length()));
					}
				} else {

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
					txtMessage.showSuggestions(false);
					txtMessage.setIsTagging(false);
				}
			}
		});
		final List<HHUser> userList = new ArrayList<>();
		HHUserFull currentUser = HHUser.getCurrentUser();
		for (HHFriendshipUser friendship: currentUser.getFriendships()){
			userList.add(friendship.getUser());
		}
		final TagArrayAdapter tagArrayAdapter = new TagArrayAdapter(getActivity(), userList);
		txtMessage.setAdapter(tagArrayAdapter);
		txtMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				/*String str = txtMessage.getPrefix()
					+ ((HHUser) tagArrayAdapter.getItem(position)).getName()
					+ txtMessage.getSuffix();
				txtMessage.setText(str);*/
				//int tagNo = txtMessage.addTag((HHUser) tagArrayAdapter.getItem(position), true, txtMessage.getPrefix().length());
				//String str = txtMessage.getPrefix() + "$tag" + String.format("%1$02d", tagNo) + "$" + txtMessage.getSuffix();

				HHUser user = ((HHUser) tagArrayAdapter.getItem(position));
				/*String strLink = "<a href=" + user.getID() + ">" + ((HHUser) tagArrayAdapter.getItem(position)).getName() + "</a>";
				String strAll = txtMessage.getPrefix() + strLink + txtMessage.getSuffix();*/
				HHUserSpan userSpan = new HHUserSpan(user);

				int selectionEnd = setTaggableText(txtMessage, userSpan, user);
				txtMessage.setSelection(selectionEnd);

				txtMessage.setIsTagging(false);
				txtMessage.showSuggestions(false);
			}
		});

		return view;
	}

	private int setTaggableText(TaggableEditText text, HHUserSpan userSpan, HHUser user){
		CharSequence prefix = txtMessage.getPrefix();
		CharSequence spanStr = userSpan.toString();
		SpannableStringBuilder ssb = new SpannableStringBuilder(prefix);
		ssb.append(spanStr);
		ssb.append(txtMessage.getSuffix());
		ssb.setSpan(userSpan, prefix.length(), prefix.length() + spanStr.length(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		HHUserSpan[] spans = ssb.getSpans(0, ssb.length(), HHUserSpan.class);
		int selectionEnd = 0;
		for (HHUserSpan span : spans){
			//makeLinkClickable(ssb, span);
			if (span.user.equals(user)){
				selectionEnd = ssb.getSpanEnd(span);
			}
		}
		text.setText(ssb);
		return selectionEnd;
	}

	/*private void makeLinkClickable(SpannableStringBuilder ssb, final URLSpan span){
		int start = ssb.getSpanStart(span);
		int end = ssb.getSpanEnd(span);
		int flags = ssb.getSpanFlags(span);
		ClickableSpan clickable = new ClickableSpan(span.) {
			@Override
			public void onClick(View view) {
				Toast.makeText(getActivity(), "Span Clicked!", Toast.LENGTH_SHORT).show();
			}
		};
		ssb.setSpan(clickable, start, end, flags);
		ssb.removeSpan(span);
	}*/

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

					WebHelper.getSpotifyAlbumArt(
						spotifyTrack.getID(),
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

					WebHelper.getSpotifyAlbumArt(
						spotifyArtist.getID(),
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

					WebHelper.getSpotifyAlbumArt(
						spotifyAlbum.getID(),
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

	private class TagArrayAdapter extends BaseAdapter implements Filterable {

		private Activity context;
		private List<HHUser> users;
		private List<HHUser> filteredUsers;
		private int maxLength = 5;

		public TagArrayAdapter(Activity context, List<HHUser> users) {
			this.context = context;
			this.users = users;
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView = inflater.inflate(R.layout.list_row_place, null, true);

			TextView txtPlace = (TextView) rowView.findViewById(R.id.list_row_place_txtPlace);
			txtPlace.setText(filteredUsers.get(position).getName());

			return rowView;
		}

		Filter filter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();

				if (!txtMessage.isTagging() || users == null)
					return null;

				constraint = constraint.subSequence(txtMessage.getTagStart()+1, constraint.length() - txtMessage.getTagSuffixLength());

				Spanned spanned = (Spanned) txtMessage.getText();
				HHUserSpan[] spans = spanned.getSpans(0, spanned.length(), HHUserSpan.class);
				ArrayList<HHUser> alreadyTagged = new ArrayList<>();
				for (HHUserSpan span : spans){
					alreadyTagged.add(span.user);
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
				if (txtMessage.isTagging() && users != null) {
					filteredUsers = (ArrayList<HHUser>) results.values;
					notifyDataSetChanged();
				}
			}

			@Override
			public CharSequence convertResultToString(Object resultValue) {
				if (resultValue instanceof HHUser){
					return ((HHUser) resultValue).toCharSequence();
				}
				return super.convertResultToString(resultValue);
			}
		};

		@Override
		public Filter getFilter() {
			return filter;
		}

	}

	public static class HHUserSpan extends ClickableSpan {
		private final HHUser user;

		public HHUserSpan(HHUser user){
			super();
			this.user = user;
		}

		@Override
		public void updateDrawState(TextPaint ds){
			ds.setUnderlineText(true);
			ds.setColor(Color.BLUE);
		}

		@Override
		public void onClick(View view){
		}

		@Override
		public String toString(){
			return user.getName();
		}

	}

}
