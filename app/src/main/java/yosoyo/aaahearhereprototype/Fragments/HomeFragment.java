package yosoyo.aaahearhereprototype.Fragments;


import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import yosoyo.aaahearhereprototype.AsyncDataManager;
import yosoyo.aaahearhereprototype.DownloadImageTask;
import yosoyo.aaahearhereprototype.HolderActivity;
import yosoyo.aaahearhereprototype.R;
import yosoyo.aaahearhereprototype.TestServerClasses.CachedSpotifyTrack;
import yosoyo.aaahearhereprototype.TestServerClasses.TestCommentUser;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPost;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPostFull;
import yosoyo.aaahearhereprototype.TestServerClasses.TestUser;
import yosoyo.aaahearhereprototype.ZZZUtility;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment /*implements
	ORMTestPostUser.GetDBTestPostsTask.GetDBTestPostUsersCallback,
	ORMCachedSpotifyTrack.GetDBCachedSpotifyTracksTask.GetDBCachedSpotifyTracksCallback*/ {

	private static final String TAG = HomeFragment.class.getSimpleName();
	private ListView lstTimeline;
	private TimelineCustomListAdapter lstTimelineAdapter;
	private List<TestPostFull> posts = new ArrayList<>();

	//private static MediaPlayer mediaPlayer = new MediaPlayer();

	public HomeFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_home, container, false);

		lstTimeline = (ListView) view.findViewById(R.id.lstTimeline);
		lstTimelineAdapter = new TimelineCustomListAdapter(getActivity(), posts);
		lstTimeline.setAdapter(lstTimelineAdapter);

		AsyncDataManager.getAllPosts(new AsyncDataManager.GetAllPostsCallback() {
			@Override
			public void returnAllCachedPosts(List<TestPostFull> cachedPosts) {
				Log.d(TAG, "Cached posts returned");
				posts = ZZZUtility.mergeLists(posts, cachedPosts);
				Collections.sort(posts);
				lstTimelineAdapter.notifyDataSetChanged();
			}

			@Override
			public void returnWebPost(TestPostFull webPost) {
				Log.d(TAG, "Web post returned!");
				if(ZZZUtility.mergeLists(posts, webPost)) {
					Collections.sort(posts);
					lstTimelineAdapter.notifyDataSetChanged();
				}
			}
		});

		// Inflate the layout for this fragment
		return view;
	}

	@Override
	public void onStart(){
		super.onStart();

		//View view = getView();

	}

	/*@Override
	public void returnTestPostUsers(List<TestPostUser> testPostUsers) {
		ZZZDataHolder.testPostUsers = testPostUsers;
		ORMCachedSpotifyTrack.getCachedSpotifyTracks(getActivity(), this);
	}

	@Override
	public void returnCachedSpotifyTracks(List<CachedSpotifyTrack> cachedSpotifyTracks) {
		ZZZDataHolder.cachedSpotifyTracks = cachedSpotifyTracks;
		//HolderActivity.dataHolder.createTestPostUserTracks();
		lstTimeline.setAdapter(
			new TimelineCustomListAdapter(getActivity(), ZZZDataHolder.testPostUserTracks));
		Log.d(TAG, "Adapter set!");
	}*/

	private static class TimelineCustomListAdapter extends ArrayAdapter implements DownloadImageTask.DownloadImageTaskCallback {
		private static final String TAG = TimelineCustomListAdapter.class.getSimpleName();

		private final Activity context;
		//private List<TestPostUserTrack> testPostUserTracks;
		private List<TestPostFull> posts;
		private Bitmap[] userBitmaps;
		private Bitmap[] artistBitmaps;

		public TimelineCustomListAdapter(Activity context, List<TestPostFull> posts) {
			super(context, R.layout.list_row_timeline, posts);

			this.context = context;
			this.posts = posts;
			Collections.sort(posts, new Comparator<TestPostFull>() {
				@Override
				public int compare(TestPostFull lhs, TestPostFull rhs) {
					return rhs.getPost().getCreatedAt().compareTo(lhs.getPost().getCreatedAt());
				}
			});
			artistBitmaps = new Bitmap[posts.size()];
			userBitmaps = new Bitmap[posts.size()];

		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();

			Bitmap[] newUserBitmaps = new Bitmap[posts.size()];
			System.arraycopy(userBitmaps, 0, newUserBitmaps, 0, userBitmaps.length);
			userBitmaps = newUserBitmaps;

			Bitmap[] newArtistBitmaps = new Bitmap[posts.size()];
			System.arraycopy(artistBitmaps, 0, newArtistBitmaps, 0, artistBitmaps.length);
			artistBitmaps = newArtistBitmaps;
		}

		public View getView(int position, View view, ViewGroup parent){
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView = inflater.inflate(R.layout.list_row_timeline, null, true);

			TestPost testPost = posts.get(position).getPost();
			TestUser testUser = posts.get(position).getUser();
			final CachedSpotifyTrack cachedSpotifyTrack = posts.get(position).getTrack();
			List<TestCommentUser> comments = posts.get(position).getComments();

			// get Album Art
			ImageView imgAlbumArt = (ImageView) rowView.findViewById(R.id.list_row_timeline_imgAlbumArt);
			if (posts.get(position) != null) {
				if (artistBitmaps[position] == null) { // need to download image
					new DownloadImageTask(imgAlbumArt, this, position)
						.execute(cachedSpotifyTrack.getImageUrl());
				} else {
					imgAlbumArt.setImageBitmap(artistBitmaps[position]); // get from storage
				}
			}

			// get User Image
			ImageView imgProfile = (ImageView) rowView.findViewById(R.id.list_row_timeline_imgProfile);
			if (posts.get(position) != null) {
				if (userBitmaps[position] == null) { // need to download image
					new DownloadImageTask(imgProfile, this, position)
						.execute(DownloadImageTask.FACEBOOK_PROFILE_PHOTO +
									 testUser.getFBUserID() +
									 DownloadImageTask.FACEBOOK_PROFILE_PHOTO_NORMAL);
				} else {
					imgProfile.setImageBitmap(userBitmaps[position]); // get from storage
				}
			}

			TextView txtUserName = (TextView) rowView.findViewById(R.id.list_row_timeline_txtUserName);
			txtUserName.setText(testUser.getFirstName() + " " + testUser.getLastName());

			TextView txtLocation = (TextView) rowView.findViewById(R.id.list_row_timeline_txtLocation);
			txtLocation.setText(testPost.getLat() + " " + testPost.getLon());

			TextView txtDateTime = (TextView) rowView.findViewById(R.id.list_row_timeline_txtDateTime);
			txtDateTime.setText(String.valueOf(testPost.getCreatedAt()));

			TextView txtTrackName = (TextView) rowView.findViewById(R.id.list_row_timeline_txtTrackName);
			txtTrackName.setText(cachedSpotifyTrack.getName());

			TextView txtArtist = (TextView) rowView.findViewById(R.id.list_row_timeline_txtArtist);
			txtArtist.setText(cachedSpotifyTrack.getArtist());

			TextView txtAlbum = (TextView) rowView.findViewById(R.id.list_row_timeline_txtAlbum);
			txtAlbum.setText(cachedSpotifyTrack.getAlbum());

			TextView txtMessage = (TextView) rowView.findViewById(R.id.list_row_timeline_txtMessage);
			txtMessage.setText(testPost.getMessage());

			final ImageButton btnPlayButton = (ImageButton) rowView.findViewById(R.id.list_row_timeline_btnPlayButton);
			btnPlayButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (HolderActivity.mediaPlayer.isPlaying()) {
						HolderActivity.mediaPlayer.reset();
						updatePlayButton(btnPlayButton);
						return;
					}

					final ProgressDialog progressDialog;

					progressDialog = new ProgressDialog(getContext());
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
								updatePlayButton(btnPlayButton);
								return false;
							}
						});

						HolderActivity.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
							@Override
							public void onPrepared(MediaPlayer mp) {
								HolderActivity.mediaPlayer.start();
								progressDialog.dismiss();
								updatePlayButton(btnPlayButton);
							}
						});

						HolderActivity.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
							@Override
							public void onCompletion(MediaPlayer mp) {
								updatePlayButton(btnPlayButton);
							}
						});

						HolderActivity.mediaPlayer.setDataSource(cachedSpotifyTrack.getPreviewUrl());
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

			return rowView;
		}

		private void updatePlayButton(ImageButton btnPlayButton){
			if (HolderActivity.mediaPlayer.isPlaying()) {
				btnPlayButton.setImageResource(R.drawable.ic_media_pause);
			} else {
				btnPlayButton.setImageResource(R.drawable.ic_media_play);
			}
		}

		@Override
		public void returnDownloadedImage(Bitmap result, int position, Marker marker) {
			//artistBitmaps[position] = result; // store downloaded bitmap
		}

	}

}
