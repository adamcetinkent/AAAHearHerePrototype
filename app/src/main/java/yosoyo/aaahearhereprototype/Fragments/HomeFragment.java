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
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.facebook.Profile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import yosoyo.aaahearhereprototype.AsyncDataManager;
import yosoyo.aaahearhereprototype.HolderActivity;
import yosoyo.aaahearhereprototype.R;
import yosoyo.aaahearhereprototype.TestServerClasses.CachedSpotifyTrack;
import yosoyo.aaahearhereprototype.TestServerClasses.Tasks.WebHelper;
import yosoyo.aaahearhereprototype.TestServerClasses.TestComment;
import yosoyo.aaahearhereprototype.TestServerClasses.TestCommentUser;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPost;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPostFull;
import yosoyo.aaahearhereprototype.TestServerClasses.TestUser;
import yosoyo.aaahearhereprototype.ZZZUtility;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

	private static final String TAG = HomeFragment.class.getSimpleName();
	private ExpandableListView lstTimeline;
	private TimelineCustomExpandableAdapter lstTimelineAdapter;
	private List<TestPostFull> posts = new ArrayList<>();

	public HomeFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_home, container, false);

		lstTimeline = (ExpandableListView) view.findViewById(R.id.lstTimeline);
		lstTimelineAdapter = new TimelineCustomExpandableAdapter(getActivity(), posts);
		lstTimeline.setAdapter(lstTimelineAdapter);

		getAllData();

		// Inflate the layout for this fragment
		return view;
	}

	private void getAllData(){
		AsyncDataManager.getAllPosts(new AsyncDataManager.GetAllPostsCallback() {
			@Override
			public void returnAllCachedPosts(List<TestPostFull> cachedPosts) {
				Log.d(TAG, "Cached posts returned");
				posts = ZZZUtility.mergeLists(posts, cachedPosts);
				notifyAdapter();
			}

			@Override
			public void returnWebPost(TestPostFull webPost) {
				Log.d(TAG, "Web post returned!");
				posts = ZZZUtility.mergeLists(posts, webPost);
				notifyAdapter();
			}
		});
	}

	private void notifyAdapter(){
		Collections.sort(posts);
		lstTimelineAdapter.notifyDataSetChanged();
		for(int i=0; i < lstTimelineAdapter.getGroupCount(); i++) {
			lstTimeline.expandGroup(i);
			lstTimelineAdapter.getChildrenCount(i);
		}
	}

	@Override
	public void onStart(){
		super.onStart();

		//View view = getView();

	}

	private class TimelineCustomExpandableAdapter extends BaseExpandableListAdapter{

		private Activity context;
		private List<TestPostFull> posts;
		int addingComment = -1;

		public TimelineCustomExpandableAdapter(Activity context, List<TestPostFull> posts){
			super();
			this.context = context;
			this.posts = posts;
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		@Override
		public int getGroupCount() {
			return posts.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			int n = posts.get(groupPosition).getComments().size();
			return (groupPosition == addingComment ? n + 1 : n);
		}

		@Override
		public Object getGroup(int groupPosition) {
			return posts.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return posts.get(groupPosition).getComments().get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView = inflater.inflate(R.layout.list_row_timeline, null, true);

			TestPost testPost = posts.get(groupPosition).getPost();
			TestUser testUser = posts.get(groupPosition).getUser();
			final CachedSpotifyTrack cachedSpotifyTrack = posts.get(groupPosition).getTrack();

			// get Album Art
			final ImageView imgAlbumArt = (ImageView) rowView.findViewById(R.id.list_row_timeline_imgAlbumArt);
			if (testPost != null) {
				WebHelper.getSpotifyAlbumArt(cachedSpotifyTrack,
											 new WebHelper.GetSpotifyAlbumArtCallback() {
												 @Override
												 public void returnSpotifyAlbumArt(Bitmap bitmap) {
													 imgAlbumArt.setImageBitmap(bitmap);
												 }
											 });
			}

			// get User Image
			final ImageView imgProfile = (ImageView) rowView.findViewById(R.id.list_row_timeline_imgProfile);
			if (testUser != null) {
				WebHelper.getFacebookProfilePicture(testUser.getFBUserID(),
													new WebHelper.GetFacebookProfilePictureCallback() {
														@Override
														public void returnFacebookProfilePicture(Bitmap bitmap) {
															imgProfile.setImageBitmap(bitmap);
														}
													});
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

			final ImageView btnPlayButton = (ImageView) rowView.findViewById(R.id.list_row_timeline_btnPlayButton);
			btnPlayButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (HolderActivity.mediaPlayer.isPlaying()) {
						HolderActivity.mediaPlayer.reset();
						updatePlayButton(btnPlayButton);
						return;
					}

					final ProgressDialog progressDialog;

					progressDialog = new ProgressDialog(context);
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
							.setDataSource(cachedSpotifyTrack.getPreviewUrl());
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

			final ToggleButton btnLikeButton = (ToggleButton) rowView.findViewById(R.id.list_row_timeline_btnLike);
			final ImageButton btnCommentButton = (ImageButton) rowView.findViewById(R.id.list_row_timeline_btnComment);
			//final LinearLayout llAddComment = (LinearLayout) rowView.findViewById(R.id.list_row_timeline_llAddComment);
			//final EditText txtAddComment = (EditText) rowView.findViewById(R.id.list_row_timeline_txtAddComment);
			//llAddComment.setVisibility((addingComment == groupPosition) ? View.VISIBLE : View.GONE);
			/*if (addingComment == groupPosition){
				txtAddComment.requestFocus();
				InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(
					Context.INPUT_METHOD_SERVICE);
				inputMethodManager.showSoftInput(txtAddComment,
												 InputMethodManager.SHOW_IMPLICIT);
			}*/
			btnCommentButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						InputMethodManager inputMethodManager;
						if (addingComment != groupPosition) {
							//llAddComment.setVisibility(View.VISIBLE);
							addingComment = groupPosition;
							//txtAddComment.requestFocus();
							/*inputMethodManager = (InputMethodManager) context.getSystemService(
								Context.INPUT_METHOD_SERVICE);
							inputMethodManager.showSoftInput(txtAddComment,
															 InputMethodManager.SHOW_IMPLICIT);*/
						} else {
							//llAddComment.setVisibility(View.GONE);
							//txtAddComment.clearFocus();
							addingComment = -1;
							/*inputMethodManager = (InputMethodManager) context.getSystemService(
								Context.INPUT_METHOD_SERVICE);
							inputMethodManager
								.hideSoftInputFromWindow(txtAddComment.getWindowToken(), 0);*/
						}
						notifyDataSetChanged();
					}
				});

			return rowView;
		}

		@Override
		public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

			LayoutInflater inflater = context.getLayoutInflater();

			if (childPosition >= posts.get(groupPosition).getComments().size()){
				final View rowView = inflater.inflate(R.layout.list_row_comment_add, null, true);

				final ImageView imgProfile = (ImageView) rowView.findViewById(R.id.list_row_comment_add_imgProfile);
				WebHelper.getFacebookProfilePicture(Profile.getCurrentProfile().getId(),
													new WebHelper.GetFacebookProfilePictureCallback() {
														@Override
														public void returnFacebookProfilePicture(Bitmap bitmap) {
															imgProfile.setImageBitmap(bitmap);
														}
													});

				final EditText txtAddComment = (EditText) rowView.findViewById(R.id.list_row_comment_add_txtAddComment);

				/*if (addingComment == groupPosition){
					//txtAddComment.requestFocus();
					InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(
						Context.INPUT_METHOD_SERVICE);
					inputMethodManager.showSoftInput(txtAddComment,
													 InputMethodManager.SHOW_IMPLICIT);
				}*/

				final ImageButton btnAddComment = (ImageButton) rowView.findViewById(R.id.list_row_comment_add_btnAddComment);
				btnAddComment.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String message = txtAddComment.getText().toString();
						if (message.isEmpty())
							return;
						final long post_id = posts.get(groupPosition).getPost().getID();
						TestComment comment = new TestComment(post_id, HolderActivity.testUser.getID(), message);
						AsyncDataManager.postComment(comment, new AsyncDataManager.PostCommentCallback() {
							@Override
							public void returnPostedComment(TestComment returnedComment) {
								Log.d(TAG, "Posted new comment!");
								AsyncDataManager.getWebPost(post_id,
															new AsyncDataManager.GetWebPostCallback() {
																@Override
																public void returnWebPost(TestPostFull webPost) {
																	posts = ZZZUtility
																		.mergeLists(posts, webPost);
																	notifyAdapter();
																}
															});
								addingComment = -1;
							}
						});
						btnAddComment.setVisibility(View.INVISIBLE);
						ProgressBar progressBar = (ProgressBar) rowView.findViewById(R.id.list_row_comment_add_progressBar);
						progressBar.setVisibility(View.VISIBLE);
					}
				});

				return rowView;
			}

			View rowView = inflater.inflate(R.layout.list_row_comment, null, true);

			TestCommentUser comment = posts.get(groupPosition).getComments().get(childPosition);

			TextView txtUserName = (TextView) rowView.findViewById(R.id.list_row_comment_txtUserName);
			txtUserName.setText(comment.getUser().getFirstName() + " " + comment.getUser().getLastName());

			TextView txtComment = (TextView) rowView.findViewById(R.id.list_row_comment_txtComment);
			txtComment.setText(comment.getComment().getMessage());

			// get User Image
			final ImageView imgProfile = (ImageView) rowView.findViewById(R.id.list_row_comment_imgProfile);
			if (comment.getUser() != null) {
				WebHelper.getFacebookProfilePicture(comment.getUser().getFBUserID(),
													new WebHelper.GetFacebookProfilePictureCallback() {
														@Override
														public void returnFacebookProfilePicture(Bitmap bitmap) {
															imgProfile.setImageBitmap(bitmap);
														}
													});
			}

			return rowView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		private void updatePlayButton(ImageView btnPlayButton){
			if (HolderActivity.mediaPlayer.isPlaying()) {
				btnPlayButton.setImageResource(R.drawable.pause_overlay);
			} else {
				btnPlayButton.setImageResource(R.drawable.play_overlay);
			}
		}
	}

}
