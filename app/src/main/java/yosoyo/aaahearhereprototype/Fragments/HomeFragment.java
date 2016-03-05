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
import android.widget.CompoundButton;
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
import yosoyo.aaahearhereprototype.TestServerClasses.TestLike;
import yosoyo.aaahearhereprototype.TestServerClasses.TestLikeUser;
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
				posts = ZZZUtility.updateList(posts, webPost);
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
			if (posts.get(groupPosition).getLikes().size() > 0) n++;
			if (groupPosition == addingComment) n++;
			return n;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return posts.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			int n = childPosition;
			if (posts.get(groupPosition).getLikes().size() > 0) n++;
			return posts.get(groupPosition).getComments().get(n);
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

			final TestPost testPost = posts.get(groupPosition).getPost();
			final TestUser testUser = posts.get(groupPosition).getUser();
			final CachedSpotifyTrack cachedSpotifyTrack = posts.get(groupPosition).getTrack();

			// get Album Art
			final ImageView imgAlbumArt = (ImageView) rowView.findViewById(R.id.list_row_timeline_imgAlbumArt);
			if (testPost != null) {
				WebHelper.getSpotifyAlbumArt(
					cachedSpotifyTrack,
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
				WebHelper.getFacebookProfilePicture(
					testUser.getFBUserID(),
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
			TestLikeUser myLikeUser = null;
			for (TestLikeUser like : posts.get(groupPosition).getLikes() ){
				if (like.getUser().equals(HolderActivity.testUser)){
					btnLikeButton.setChecked(true);
					myLikeUser = like;
					break;
				}
			}
			final TestLike myLike;
			if (myLikeUser != null)
				 myLike = myLikeUser.getLike();
			else
				myLike = null;

			btnLikeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					btnLikeButton.setEnabled(false);
					if (isChecked){
						TestLike like = new TestLike(testPost.getID(), HolderActivity.testUser.getID());
						AsyncDataManager.postLike(like, new AsyncDataManager.PostLikeCallback() {
							@Override
							public void returnPostedLike(TestLike returnedLike) {
								Log.d(TAG, "Posted new comment!");
								AsyncDataManager.getWebPost(
									testPost.getID(),
									new AsyncDataManager.GetWebPostCallback() {
										@Override
										public void returnWebPost(TestPostFull webPost) {
											posts = ZZZUtility.updateList(posts, webPost);
											notifyAdapter();
											btnLikeButton.setEnabled(true);
										}
									});
							}
						});
					} else {
						if (myLike == null)
							return;

						AsyncDataManager.deleteLike(
							myLike,
							new AsyncDataManager.DeleteLikeCallback() {
								@Override
								public void returnDeletedLike(boolean success) {
									Log.d(TAG, "Deleted like!");
									AsyncDataManager.getWebPost(
										testPost.getID(),
										new AsyncDataManager.GetWebPostCallback() {
											@Override
											public void returnWebPost(TestPostFull webPost) {
												posts = ZZZUtility.updateList(posts, webPost);
												notifyAdapter();
												btnLikeButton.setEnabled(true);
											}
										});
								}
							});

					}
				}
			});

			final ImageButton btnCommentButton = (ImageButton) rowView.findViewById(R.id.list_row_timeline_btnComment);
			btnCommentButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						InputMethodManager inputMethodManager;
						if (addingComment != groupPosition) {
							addingComment = groupPosition;
						} else {
							addingComment = -1;
						}
						notifyDataSetChanged();
					}
				});

			return rowView;
		}

		@Override
		public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

			LayoutInflater inflater = context.getLayoutInflater();

			if (childPosition == 0 && posts.get(groupPosition).getLikes().size() > 0){
				// HEADER FOR LIKES
				final View rowView = inflater.inflate(R.layout.list_row_comment_like, null, true);

				TextView txtLikers = (TextView) rowView.findViewById(R.id.list_row_comment_like_txtLikers);
				List<TestLikeUser> likes = posts.get(groupPosition).getLikes();

				int youLike = -1;
				StringBuilder sb;
				if (likes.get(0).getUser().equals(HolderActivity.testUser)) {
					youLike = 0;
					sb = new StringBuilder();
				} else {
					sb = new StringBuilder(likes.get(0).getUser().getName());
				}
				for (int i = 1; i < likes.size(); i++){
					if (likes.get(i).getUser().equals(HolderActivity.testUser)){
						youLike = i;
					} else {
						sb.append(", " + likes.get(i).getUser().getName());
					}
				}
				txtLikers.setText((youLike >= 0 ? "You" + ((youLike > 0) ? ", " : "") : "") + sb.toString());

				TextView txtNumber = (TextView) rowView.findViewById(R.id.list_row_comment_like_txtNumber);
				txtNumber.setText(String.valueOf(likes.size()));

				return rowView;
			}

			if ((posts.get(groupPosition).getLikes().size() == 0 && childPosition >= posts.get(groupPosition).getComments().size())
				|| (posts.get(groupPosition).getLikes().size() > 0 && childPosition >= posts.get(groupPosition).getComments().size() + 1)){
				// FOOTER FOR ADDING COMMENT
				final View rowView = inflater.inflate(R.layout.list_row_comment_add, null, true);

				final ImageView imgProfile = (ImageView) rowView.findViewById(R.id.list_row_comment_add_imgProfile);
				WebHelper.getFacebookProfilePicture(
					Profile.getCurrentProfile().getId(),
					new WebHelper.GetFacebookProfilePictureCallback() {
						@Override
						public void returnFacebookProfilePicture(Bitmap bitmap) {
							imgProfile.setImageBitmap(bitmap);
						}
					});

				final EditText txtAddComment = (EditText) rowView.findViewById(R.id.list_row_comment_add_txtAddComment);

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
								AsyncDataManager.getWebPost(
									post_id,
									new AsyncDataManager.GetWebPostCallback() {
										@Override
										public void returnWebPost(TestPostFull webPost) {
											posts = ZZZUtility.updateList(posts, webPost);
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

			if (posts.get(groupPosition).getLikes().size() > 0)
				childPosition--;

			TestCommentUser comment = posts.get(groupPosition).getComments().get(childPosition);

			TextView txtUserName = (TextView) rowView.findViewById(R.id.list_row_comment_txtUserName);
			txtUserName.setText(comment.getUser().getFirstName() + " " + comment.getUser().getLastName());

			TextView txtComment = (TextView) rowView.findViewById(R.id.list_row_comment_txtComment);
			txtComment.setText(comment.getComment().getMessage());

			// get User Image
			final ImageView imgProfile = (ImageView) rowView.findViewById(R.id.list_row_comment_imgProfile);
			if (comment.getUser() != null) {
				WebHelper.getFacebookProfilePicture(
					comment.getUser().getFBUserID(),
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
