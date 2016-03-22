package yosoyo.aaahearhereprototype.Fragments;


import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.facebook.Profile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import yosoyo.aaahearhereprototype.AsyncDataManager;
import yosoyo.aaahearhereprototype.HHServerClasses.HHComment;
import yosoyo.aaahearhereprototype.HHServerClasses.HHCommentUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHLike;
import yosoyo.aaahearhereprototype.HHServerClasses.HHLikeUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHPostFull;
import yosoyo.aaahearhereprototype.HHServerClasses.HHTagUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUser;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;
import yosoyo.aaahearhereprototype.HolderActivity;
import yosoyo.aaahearhereprototype.R;
import yosoyo.aaahearhereprototype.ZZZUtility;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends FeedbackFragment {

	private static final String TAG = FeedFragment.class.getSimpleName();

	private int feedType;
	public static final String FEED_TYPE = "feed_type";
	public static final int GENERAL_FEED = 0;
	public static final int USER_FEED = 1;

	private long userID = -1;

	private ExpandableListView lstTimeline;
	private TimelineCustomExpandableAdapter lstTimelineAdapter;
	private List<HHPostFull> posts = new ArrayList<>();

	public static FeedFragment newInstance(){
		return newInstance(GENERAL_FEED, -1);
	}

	public static FeedFragment newInstance(int feedType, long userID){
		FeedFragment feedFragment = new FeedFragment();

		Bundle arguments = new Bundle();
		arguments.putInt(FEED_TYPE, feedType);
		arguments.putLong(USER_ID, userID);
		feedFragment.setArguments(arguments);

		return feedFragment;
	}

	public FeedFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);

		Bundle arguments = getArguments();
		if (arguments != null){
			handleArguments(arguments);
		}

	}

	private void handleArguments(Bundle arguments){
		feedType = arguments.getInt(FEED_TYPE);

		if (feedType == USER_FEED){
			userID = arguments.getLong(USER_ID);
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case android.R.id.home: {
				getActivity().onBackPressed();
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_home, container, false);

		lstTimeline = (ExpandableListView) view.findViewById(R.id.lstTimeline);
		lstTimelineAdapter = new TimelineCustomExpandableAdapter(
			getActivity(),
			posts,
			new TimelineCustomExpandableAdapter.AdapterCallback() {
				@Override
				public void onDataChange() {
					notifyAdapter();
				}

				@Override
				public void onUserClick(HHUser user) {
					if (user.getID() != userID)
						requestUserFeed(user);
				}
			},
			new HHUser.HHUserSpan.HHUserSpanClickCallback() {
				@Override
				public void onClickSpan(HHUser user) {
					if (user.getID() != userID)
						requestUserFeed(user);
				}
			});
		lstTimeline.setAdapter(lstTimelineAdapter);

		switch (feedType){
			case GENERAL_FEED: {
				getAllData();
				break;
			}
			case USER_FEED:{
				getUserData();
				break;
			}
		}

		return view;
	}

	private AsyncDataManager.GetAllPostsCallback getAllPostsCallback = new AsyncDataManager.GetAllPostsCallback() {
		@Override
		public void returnGetAllCachedPosts(List<HHPostFull> cachedPosts) {
			Log.d(TAG, "Cached posts returned");
			posts = ZZZUtility.mergeLists(posts, cachedPosts);
			notifyAdapter();
		}

		@Override
		public void returnGetWebPost(HHPostFull webPost) {
			Log.d(TAG, "Web post returned!");
			posts = ZZZUtility.updateList(posts, webPost);
			notifyAdapter();
		}
	};

	private void getAllData(){
		AsyncDataManager.getAllPosts(getAllPostsCallback);
	}

	private void getUserData(){
		AsyncDataManager.getUserPosts(userID, getAllPostsCallback);
	}

	private void notifyAdapter(){
		Collections.sort(posts);
		lstTimelineAdapter.notifyDataSetChanged();
		for(int i=0; i < lstTimelineAdapter.getGroupCount(); i++) {
			lstTimeline.expandGroup(i);
			lstTimelineAdapter.getChildrenCount(i);
		}
	}

	public static class TimelineCustomExpandableAdapter extends BaseExpandableListAdapter{

		private class OnClickUserListener implements View.OnClickListener {

			private HHUser user;
			private final FeedFragment.TimelineCustomExpandableAdapter.AdapterCallback adapterCallback;

			public OnClickUserListener(HHUser user, AdapterCallback adapterCallback){
				this.user = user;
				this.adapterCallback = adapterCallback;
			}

			public void setUser(HHUser user) {
				this.user = user;
			}

			@Override
			public void onClick(View v) {
				adapterCallback.onUserClick(user);
			}

		}

		public interface AdapterCallback {
			void onDataChange();
			void onUserClick(HHUser user);
		}

		private final Activity context;
		private final AdapterCallback callback;
		private final HHUser.HHUserSpan.HHUserSpanClickCallback userSpanClickCallback;

		private List<HHPostFull> posts;
		int addingComment = -1;

		public TimelineCustomExpandableAdapter(Activity context, List<HHPostFull> posts, AdapterCallback callback, HHUser.HHUserSpan.HHUserSpanClickCallback userSpanClickCallback){
			super();
			this.context = context;
			this.posts = posts;
			this.callback = callback;
			this.userSpanClickCallback = userSpanClickCallback;
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
			LinearLayout llTags;
			TextView txtTags;
			int groupPosition;
			boolean addingComment;
			HHPostFull post;
			HHLike myLike;
			CompoundButton.OnCheckedChangeListener likeCheckListener;
			View.OnClickListener commentClickListener;
			OnClickUserListener onClickUserListener;
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

				for (HHLikeUser like : posts.get(groupPosition).getLikes() ){
					if (like.getUser().equals(HHUser.getCurrentUser().getUser())){
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
				viewHolder.llTags = (LinearLayout) convertView.findViewById(R.id.list_row_timeline_llTagFrame);
				viewHolder.txtTags = (TextView) convertView.findViewById(R.id.list_row_timeline_txtTags);

				viewHolder.likeCheckListener = new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						viewHolder.btnLikeButton.setEnabled(false);
						if (isChecked) {
							HHLike like = new HHLike(viewHolder.post.getPost().getID(),
													 HHUser.getCurrentUserID());
							AsyncDataManager
								.postLike(like, new AsyncDataManager.PostLikeCallback() {
									@Override
									public void returnPostLike(HHLike returnedLike) {
										Log.d(TAG, "Posted new comment!");
										AsyncDataManager.getWebPost(
											viewHolder.post.getPost().getID(),
											new AsyncDataManager.GetWebPostCallback() {
												@Override
												public void returnGetWebPost(HHPostFull webPost) {
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
									public void returnDeleteLike(boolean success) {
										Log.d(TAG, "Deleted like!");
										AsyncDataManager.getWebPost(
											viewHolder.post.getPost().getID(),
											new AsyncDataManager.GetWebPostCallback() {
												@Override
												public void returnGetWebPost(HHPostFull webPost) {
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
							updatePlayButton(viewHolder.btnPlayButton, viewHolder.post.getTrack().getPreviewUrl());
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
										updatePlayButton(viewHolder.btnPlayButton, viewHolder.post.getTrack().getPreviewUrl());
										return false;
									}
								});

							HolderActivity.mediaPlayer
								.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
									@Override
									public void onPrepared(MediaPlayer mp) {
										HolderActivity.mediaPlayer.start();
										progressDialog.dismiss();
										updatePlayButton(viewHolder.btnPlayButton, viewHolder.post.getTrack().getPreviewUrl());
									}
								});

							HolderActivity.mediaPlayer
								.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
									@Override
									public void onCompletion(MediaPlayer mp) {
										updatePlayButton(viewHolder.btnPlayButton, viewHolder.post.getTrack().getPreviewUrl());
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

				viewHolder.onClickUserListener = new OnClickUserListener(viewHolder.post.getUser(), callback);
				viewHolder.imgProfile.setOnClickListener(viewHolder.onClickUserListener);
				viewHolder.txtUserName.setOnClickListener(viewHolder.onClickUserListener);

				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolderGroupItem) convertView.getTag();
				viewHolder.groupPosition = groupPosition;
				viewHolder.post = posts.get(groupPosition);
				viewHolder.onClickUserListener.setUser(viewHolder.post.getUser());
				viewHolder.myLike = null;
				for (HHLikeUser like : posts.get(groupPosition).getLikes() ){
					if (like.getUser().equals(HHUser.getCurrentUser().getUser())){
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
			viewHolder.txtLocation.setText(ZZZUtility.truncatedAddress(
				viewHolder.post.getPost().getPlaceName(), 35));
			viewHolder.txtDateTime.setText(ZZZUtility.formatDynamicDate(
				viewHolder.post.getPost().getCreatedAt()));
			viewHolder.txtTrackName.setText(viewHolder.post.getTrack().getName());
			viewHolder.txtArtist.setText(viewHolder.post.getTrack().getArtist());
			viewHolder.txtAlbum.setText(viewHolder.post.getTrack().getAlbum());

			viewHolder.txtMessage.setText(viewHolder.post.getPost().getMessage());
			Editable message = Editable.Factory.getInstance().newEditable(viewHolder.txtMessage.getText());
			Pattern pattern = Pattern.compile("\\{tag_\\d\\d\\}");
			Matcher matcher = pattern.matcher(message);
			int inlineTagged = 0;
			while (matcher.find()){
				int start = matcher.start();
				int end = matcher.end();

				if (inlineTagged >= viewHolder.post.getTags().size())
					break;

				HHUser tagUser = viewHolder.post.getTags().get(inlineTagged).getUser();
				String userName = tagUser.getName();
				message.replace(start, end, userName);
				message.setSpan(
					new HHUser.HHUserSpan(tagUser, userSpanClickCallback),
					start,
					start + userName.length(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
				);
				matcher = pattern.matcher(message);
				inlineTagged++;
			}
			viewHolder.txtMessage.setText(message);
			viewHolder.txtMessage.setMovementMethod(LinkMovementMethod.getInstance());

			updatePlayButton(viewHolder.btnPlayButton, viewHolder.post.getTrack().getPreviewUrl());

			viewHolder.btnLikeButton.setOnCheckedChangeListener(null);
			if (viewHolder.myLike != null) {
				viewHolder.btnLikeButton.setChecked(true);
			} else {
				viewHolder.btnLikeButton.setChecked(false);
			}
			viewHolder.btnLikeButton.setOnCheckedChangeListener(viewHolder.likeCheckListener);

			if (viewHolder.post.getTags().size() > inlineTagged){

				viewHolder.llTags.setVisibility(View.VISIBLE);

				List<HHTagUser> tags = viewHolder.post.getTags();
				int youTag = -1;
				StringBuilder sb;
				if (tags.get(inlineTagged).getUser().equals(HHUser.getCurrentUser().getUser())) {
					youTag = inlineTagged;
					sb = new StringBuilder();
				} else {
					sb = new StringBuilder(tags.get(inlineTagged).getUser().getName());
				}
				for (int i = inlineTagged + 1; i < tags.size(); i++){
					if (tags.get(i).getUser().equals(HHUser.getCurrentUser().getUser())){
						youTag = i;
					} else {
						sb.append(", ").append(tags.get(i).getUser().getName());
					}
				}
				viewHolder.txtTags.setText((youTag >= 0 ? "You" + ((youTag > 0) ? ", " : "") : "")
											   + sb.toString());

			} else {
				viewHolder.llTags.setVisibility(View.GONE);
			}

			return convertView;
		}

		static class ViewHolderChildItem{
			ImageView imgProfile;
			TextView txtUserName;
			TextView txtComment;
			int groupPosition;
			int childPosition;
			HHCommentUser comment;
			OnClickUserListener onClickUserListener;
		}

		@Override
		public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

			if (childPosition == 0 && posts.get(groupPosition).getLikes().size() > 0){
				// HEADER FOR LIKES
				return getChildViewLikes(groupPosition, parent);
			}

			if ((posts.get(groupPosition).getLikes().size() == 0 && childPosition >= posts.get(groupPosition).getComments().size())
				|| (posts.get(groupPosition).getLikes().size() > 0 && childPosition >= posts.get(groupPosition).getComments().size() + 1)){
				// FOOTER FOR ADDING COMMENT
				return getChildViewComment(groupPosition, parent);
			}

			LayoutInflater inflater = context.getLayoutInflater();

			if (posts.get(groupPosition).getLikes().size() > 0) {
				childPosition--;
			}

			ViewHolderChildItem viewHolder;
			if (convertView != null)
				viewHolder = (ViewHolderChildItem) convertView.getTag();
			else
				viewHolder = null;

			if (viewHolder == null){
				convertView = inflater.inflate(R.layout.list_row_comment, parent, false);

				viewHolder = new ViewHolderChildItem();
				viewHolder.groupPosition = groupPosition;
				viewHolder.childPosition = childPosition;

				viewHolder.comment = posts.get(groupPosition).getComments().get(childPosition);

				viewHolder.txtUserName = (TextView) convertView.findViewById(R.id.list_row_comment_txtUserName);
				viewHolder.txtComment = (TextView) convertView.findViewById(R.id.list_row_comment_txtComment);
				viewHolder.imgProfile = (ImageView) convertView.findViewById(R.id.list_row_comment_imgProfile);

				viewHolder.onClickUserListener = new OnClickUserListener(viewHolder.comment.getUser(), callback);
				viewHolder.imgProfile.setOnClickListener(viewHolder.onClickUserListener);
				viewHolder.txtUserName.setOnClickListener(viewHolder.onClickUserListener);

				convertView.setTag(viewHolder);

			} else {
				viewHolder.groupPosition = groupPosition;
				viewHolder.childPosition = childPosition;
				viewHolder.comment = posts.get(groupPosition).getComments().get(childPosition);
				viewHolder.onClickUserListener.setUser(viewHolder.comment.getUser());
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

		private View getChildViewLikes(final int groupPosition, ViewGroup parent){
			LayoutInflater inflater = context.getLayoutInflater();
			final View rowView = inflater.inflate(R.layout.list_row_comment_like, parent, false);

			TextView txtLikers = (TextView) rowView.findViewById(R.id.list_row_comment_like_txtLikers);
			List<HHLikeUser> likes = posts.get(groupPosition).getLikes();

			int youLike = -1;
			SpannableStringBuilder sb;
			if (likes.get(0).getUser().equals(HHUser.getCurrentUser().getUser())) {
				youLike = 0;
				sb = new SpannableStringBuilder("You");
				sb.setSpan(
					new HHUser.HHUserSpan(HHUser.getCurrentUser().getUser(), userSpanClickCallback),
					0,
					3,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
				);
			} else {
				sb = new SpannableStringBuilder(likes.get(0).getUser().getName());
				sb.setSpan(
					new HHUser.HHUserSpan(likes.get(0).getUser(), userSpanClickCallback),
					0,
					sb.length(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			for (int i = 1; i < likes.size(); i++){
				HHUser user = likes.get(i).getUser();
				if (user.equals(HHUser.getCurrentUser().getUser())){
					youLike = i;
				} else {
					sb.append(", ").append(user.getName());
					sb.setSpan(
						new HHUser.HHUserSpan(user, userSpanClickCallback),
						sb.length()-user.getName().length(),
						sb.length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
			if (youLike > 0){
				sb.insert(0, "You, ");
				sb.setSpan(
					new HHUser.HHUserSpan(HHUser.getCurrentUser().getUser(), userSpanClickCallback),
					0,
					3,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
				);
			}
			txtLikers.setText(sb);
			txtLikers.setMovementMethod(LinkMovementMethod.getInstance());

			TextView txtNumber = (TextView) rowView.findViewById(R.id.list_row_comment_like_txtNumber);
			txtNumber.setText(String.valueOf(likes.size()));

			return rowView;
		}

		private View getChildViewComment(final int groupPosition, ViewGroup parent){
			LayoutInflater inflater = context.getLayoutInflater();
			final View rowView = inflater.inflate(R.layout.list_row_comment_add, parent, false);

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
					HHComment comment = new HHComment(post_id,
													  HHUser.getCurrentUserID(),
													  message);
					AsyncDataManager
						.postComment(comment, new AsyncDataManager.PostCommentCallback() {
							@Override
							public void returnPostComment(HHComment returnedComment) {
								Log.d(TAG, "Posted new comment!");
								AsyncDataManager.getWebPost(
									post_id,
									new AsyncDataManager.GetWebPostCallback() {
										@Override
										public void returnGetWebPost(HHPostFull webPost) {
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

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
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
	}

}
