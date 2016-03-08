package yosoyo.aaahearhereprototype.Fragments;


import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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
import yosoyo.aaahearhereprototype.TestServerClasses.Tasks.WebHelper;
import yosoyo.aaahearhereprototype.TestServerClasses.TestComment;
import yosoyo.aaahearhereprototype.TestServerClasses.TestCommentUser;
import yosoyo.aaahearhereprototype.TestServerClasses.TestLike;
import yosoyo.aaahearhereprototype.TestServerClasses.TestLikeUser;
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
		lstTimelineAdapter = new TimelineCustomExpandableAdapter(
			getActivity(), posts,
			new TimelineCustomExpandableAdapter.AdapterCallback() {
				@Override
				public void onDataChange() {
					notifyAdapter();
				}
			});
		lstTimeline.setAdapter(lstTimelineAdapter);

		getAllData();

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

	private static class TimelineCustomExpandableAdapter extends BaseExpandableListAdapter{

		interface AdapterCallback {
			void onDataChange();
		}

		private Activity context;
		private List<TestPostFull> posts;
		private AdapterCallback callback;
		int addingComment = -1;
		int addingCommentFocus = -1;
		private Handler handler = new Handler();

		public TimelineCustomExpandableAdapter(Activity context, List<TestPostFull> posts, AdapterCallback callback){
			super();
			this.context = context;
			this.posts = posts;
			this.callback = callback;
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

		static class ViewHolderGroupItem{
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
			ToggleButton btnLikeButton;
			ImageButton btnCommentButton;
			ImageButton btnShareButton;
			int groupPosition;
			boolean addingComment;
			//boolean addingCommentFocus;
			TestPostFull post;
			TestLike myLike;
			CompoundButton.OnCheckedChangeListener likeCheckListener;
			View.OnClickListener commentClickListener;
		}

		@Override
		public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			final ViewHolderGroupItem viewHolder;

			if (convertView == null){
				LayoutInflater inflater = context.getLayoutInflater();
				convertView = inflater.inflate(R.layout.list_row_timeline, parent, false);

				viewHolder = new ViewHolderGroupItem();

				viewHolder.groupPosition = groupPosition;

				viewHolder.post = posts.get(groupPosition);

				for (TestLikeUser like : posts.get(groupPosition).getLikes() ){
					if (like.getUser().equals(TestUser.getCurrentUser())){
						viewHolder.myLike = like.getLike();
						break;
					}
				}

				viewHolder.imgAlbumArt = (ImageView) convertView.findViewById(R.id.list_row_timeline_imgAlbumArt);
				viewHolder.imgProfile = (ImageView) convertView.findViewById(R.id.list_row_timeline_imgProfile);
				viewHolder.txtUserName = (TextView) convertView.findViewById(R.id.list_row_timeline_txtUserName);
				viewHolder.txtLocation = (TextView) convertView.findViewById(R.id.list_row_timeline_txtLocation);
				viewHolder.txtDateTime = (TextView) convertView.findViewById(R.id.list_row_timeline_txtDateTime);
				viewHolder.txtTrackName = (TextView) convertView.findViewById(R.id.list_row_timeline_txtTrackName);
				viewHolder.txtArtist = (TextView) convertView.findViewById(R.id.list_row_timeline_txtArtist);
				viewHolder.txtAlbum = (TextView) convertView.findViewById(R.id.list_row_timeline_txtAlbum);
				viewHolder.txtMessage = (TextView) convertView.findViewById(R.id.list_row_timeline_txtMessage);
				viewHolder.btnPlayButton = (ImageView) convertView.findViewById(R.id.list_row_timeline_btnPlayButton);
				viewHolder.btnLikeButton = (ToggleButton) convertView.findViewById(R.id.list_row_timeline_btnLike);
				viewHolder.btnCommentButton = (ImageButton) convertView.findViewById(R.id.list_row_timeline_btnComment);
				viewHolder.btnShareButton = (ImageButton) convertView.findViewById(R.id.list_row_timeline_btnShare);

				viewHolder.likeCheckListener = new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						viewHolder.btnLikeButton.setEnabled(false);
						if (isChecked) {
							TestLike like = new TestLike(viewHolder.post.getPost().getID(),
														 TestUser.getCurrentUser().getID());
							AsyncDataManager
								.postLike(like, new AsyncDataManager.PostLikeCallback() {
									@Override
									public void returnPostedLike(TestLike returnedLike) {
										Log.d(TAG, "Posted new comment!");
										AsyncDataManager.getWebPost(
											viewHolder.post.getPost().getID(),
											new AsyncDataManager.GetWebPostCallback() {
												@Override
												public void returnWebPost(TestPostFull webPost) {
													posts = ZZZUtility.updateList(posts, webPost);
													callback.onDataChange();
													viewHolder.btnLikeButton.setEnabled(true);
												}
											});
									}
								});
						} else {
							if (viewHolder.myLike == null) {
								viewHolder.btnLikeButton.setEnabled(true);
								return;
							}

							AsyncDataManager.deleteLike(
								viewHolder.myLike,
								new AsyncDataManager.DeleteLikeCallback() {
									@Override
									public void returnDeletedLike(boolean success) {
										Log.d(TAG, "Deleted like!");
										AsyncDataManager.getWebPost(
											viewHolder.post.getPost().getID(),
											new AsyncDataManager.GetWebPostCallback() {
												@Override
												public void returnWebPost(TestPostFull webPost) {
													posts = ZZZUtility.updateList(posts, webPost);
													callback.onDataChange();
													viewHolder.btnLikeButton.setEnabled(true);
												}
											});
									}
								});

						}
					}
				};

				viewHolder.btnLikeButton.setOnCheckedChangeListener(viewHolder.likeCheckListener);

				viewHolder.commentClickListener = new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (viewHolder.addingComment) {
							viewHolder.addingComment = false;
							addingComment = -1;
							InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(
								Context.INPUT_METHOD_SERVICE);
							inputMethodManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
						} else {
							viewHolder.addingComment = true;
							addingComment = viewHolder.groupPosition;
							InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(
								Context.INPUT_METHOD_SERVICE);
							inputMethodManager.showSoftInput(context.getCurrentFocus(),InputMethodManager.SHOW_IMPLICIT);
						}
						notifyDataSetChanged();
					}
				};
				viewHolder.btnCommentButton.setOnClickListener(viewHolder.commentClickListener);

				viewHolder.btnPlayButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (HolderActivity.mediaPlayer.isPlaying()) {
							HolderActivity.mediaPlayer.reset();
							updatePlayButton(viewHolder.btnPlayButton);
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
										updatePlayButton(viewHolder.btnPlayButton);
										return false;
									}
								});

							HolderActivity.mediaPlayer
								.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
									@Override
									public void onPrepared(MediaPlayer mp) {
										HolderActivity.mediaPlayer.start();
										progressDialog.dismiss();
										updatePlayButton(viewHolder.btnPlayButton);
									}
								});

							HolderActivity.mediaPlayer
								.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
									@Override
									public void onCompletion(MediaPlayer mp) {
										updatePlayButton(viewHolder.btnPlayButton);
									}
								});

							HolderActivity.mediaPlayer
								.setDataSource(viewHolder.post.getTrack().getPreviewUrl());
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

				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolderGroupItem) convertView.getTag();
				viewHolder.groupPosition = groupPosition;
				viewHolder.post = posts.get(groupPosition);
				viewHolder.myLike = null;
				for (TestLikeUser like : posts.get(groupPosition).getLikes() ){
					if (like.getUser().equals(TestUser.getCurrentUser())){
						viewHolder.myLike = like.getLike();
						break;
					}
				}
			}

			// get Album Art
			WebHelper.getSpotifyAlbumArt(
				viewHolder.post.getTrack(),
				 new WebHelper.GetSpotifyAlbumArtCallback() {
					 @Override
					 public void returnSpotifyAlbumArt(Bitmap bitmap) {
						 viewHolder.imgAlbumArt.setImageBitmap(bitmap);
					 }
				 });

			// get User Image
			WebHelper.getFacebookProfilePicture(
				viewHolder.post.getUser().getFBUserID(),
				new WebHelper.GetFacebookProfilePictureCallback() {
					@Override
					public void returnFacebookProfilePicture(Bitmap bitmap) {
						viewHolder.imgProfile.setImageBitmap(bitmap);
					}
				});

			viewHolder.txtUserName.setText(viewHolder.post.getUser().getName());
			viewHolder.txtLocation.setText(viewHolder.post.getPost().getPlaceName());
			viewHolder.txtDateTime.setText(ZZZUtility.formatDynamicDate(viewHolder.post.getPost().getCreatedAt()));
			viewHolder.txtTrackName.setText(viewHolder.post.getTrack().getName());
			viewHolder.txtArtist.setText(viewHolder.post.getTrack().getArtist());
			viewHolder.txtAlbum.setText(viewHolder.post.getTrack().getAlbum());
			viewHolder.txtMessage.setText(viewHolder.post.getPost().getMessage());

			updatePlayButton(viewHolder.btnPlayButton);

			viewHolder.btnLikeButton.setOnCheckedChangeListener(null);
			if (viewHolder.myLike != null) {
				viewHolder.btnLikeButton.setChecked(true);
			} else {
				viewHolder.btnLikeButton.setChecked(false);
			}
			viewHolder.btnLikeButton.setOnCheckedChangeListener(viewHolder.likeCheckListener);

			return convertView;
		}

		static class ViewHolderChildItem{
			ImageView imgProfile;
			TextView txtUserName;
			TextView txtComment;
			int groupPosition;
			int childPosition;
			TestCommentUser comment;
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
				if (likes.get(0).getUser().equals(TestUser.getCurrentUser())) {
					youLike = 0;
					sb = new StringBuilder();
				} else {
					sb = new StringBuilder(likes.get(0).getUser().getName());
				}
				for (int i = 1; i < likes.size(); i++){
					if (likes.get(i).getUser().equals(TestUser.getCurrentUser())){
						youLike = i;
					} else {
						sb.append(", " + likes.get(i).getUser().getName());
					}
				}
				txtLikers.setText(
					(youLike >= 0 ? "You" + ((youLike > 0) ? ", " : "") : "") + sb.toString());

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
				if (addingComment == groupPosition){
					txtAddComment.requestFocus();
				}

				final ImageButton btnAddComment = (ImageButton) rowView.findViewById(R.id.list_row_comment_add_btnAddComment);
				btnAddComment.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String message = txtAddComment.getText().toString();
						if (message.isEmpty())
							return;
						final long post_id = posts.get(groupPosition).getPost().getID();
						TestComment comment = new TestComment(post_id,
															  TestUser.getCurrentUser().getID(),
															  message);
						AsyncDataManager
							.postComment(comment, new AsyncDataManager.PostCommentCallback() {
								@Override
								public void returnPostedComment(TestComment returnedComment) {
									Log.d(TAG, "Posted new comment!");
									AsyncDataManager.getWebPost(
										post_id,
										new AsyncDataManager.GetWebPostCallback() {
											@Override
											public void returnWebPost(TestPostFull webPost) {
												posts = ZZZUtility.updateList(posts, webPost);
												callback.onDataChange();
											}
										});
									addingComment = -1;
									InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(
										Context.INPUT_METHOD_SERVICE);
									inputMethodManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
								}
							});
						btnAddComment.setVisibility(View.INVISIBLE);
						ProgressBar progressBar = (ProgressBar) rowView
							.findViewById(R.id.list_row_comment_add_progressBar);
						progressBar.setVisibility(View.VISIBLE);
					}
				});

				return rowView;
			}

			if (posts.get(groupPosition).getLikes().size() > 0) {
				childPosition--;
			}

			ViewHolderChildItem viewHolder;
			if (convertView != null)
				viewHolder = (ViewHolderChildItem) convertView.getTag();
			else
				viewHolder = null;

			if (viewHolder == null){
				convertView = inflater.inflate(R.layout.list_row_comment, null, true);

				viewHolder = new ViewHolderChildItem();
				viewHolder.groupPosition = groupPosition;
				viewHolder.childPosition = childPosition;

				viewHolder.comment = posts.get(groupPosition).getComments().get(childPosition);

				viewHolder.txtUserName = (TextView) convertView.findViewById(R.id.list_row_comment_txtUserName);
				viewHolder.txtComment = (TextView) convertView.findViewById(R.id.list_row_comment_txtComment);
				viewHolder.imgProfile = (ImageView) convertView.findViewById(R.id.list_row_comment_imgProfile);

				convertView.setTag(viewHolder);

			} else {
				viewHolder.groupPosition = groupPosition;
				viewHolder.childPosition = childPosition;
				viewHolder.comment = posts.get(groupPosition).getComments().get(childPosition);
			}

			viewHolder.txtUserName.setText(viewHolder.comment.getUser().getName());

			viewHolder.txtComment.setText(viewHolder.comment.getComment().getMessage());

			final ViewHolderChildItem viewHolderChildItem = viewHolder;
			// get User Image
			WebHelper.getFacebookProfilePicture(
				viewHolder.comment.getUser().getFBUserID(),
				new WebHelper.GetFacebookProfilePictureCallback() {
					@Override
					public void returnFacebookProfilePicture(Bitmap bitmap) {
						viewHolderChildItem.imgProfile.setImageBitmap(bitmap);
					}
				});

			return convertView;
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
