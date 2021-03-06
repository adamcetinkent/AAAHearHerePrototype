package com.yosoyo.aaahearhereprototype.Fragments;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.Profile;
import com.yosoyo.aaahearhereprototype.Activities.HolderActivity;
import com.yosoyo.aaahearhereprototype.AsyncDataManager;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHComment;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHCommentUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHLike;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHLikeUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHMute;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHNotification;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPostFull;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHTagUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;
import com.yosoyo.aaahearhereprototype.R;
import com.yosoyo.aaahearhereprototype.ZZZInterface.OverscrollExpandableListView;
import com.yosoyo.aaahearhereprototype.ZZZUtility;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by adam on 26/02/2016
 *
 * FeedFragment displays a timeline of posts.
 */
public class FeedFragment extends FeedbackFragment {

	private static final String TAG = FeedFragment.class.getSimpleName();

	private int feedType;
	public static final String KEY_FEED_TYPE = TAG + "feed_type";
	public static final int GENERAL_FEED = 0;
	public static final int HOME_PROFILE_FEED = 1;
	public static final int USER_PROFILE_FEED = 2;
	public static final int SINGLE_POST_FEED = 3;

	public static final String KEY_USER_ID = TAG + "user_id";
	private long userID = -1;

	public static final String KEY_POST_ID = TAG + "post_id";
	private long postID = -1;

	public static final String KEY_FETCH_DATA = TAG + "fetch_data";
	private boolean fetchData = true;

	private ProfileFragment profileFragment;
	private Bundle profileFragmentBundle;

	private OverscrollExpandableListView lstTimeline;
	private TimelineCustomExpandableAdapter lstTimelineAdapter;
	public static final String KEY_POSTS = TAG + "posts";
	private List<HHPostFull> posts = new ArrayList<>();

	private ProgressBar footerView;
	private LinearLayout headerView;

	public static final String KEY_EARLIEST_WEB_POST = TAG + "earliest_web_post";
	private Timestamp earliestWebPost;
	//private Timestamp requestedWebPost;
	public static final String KEY_HAVE_EARLIEST_POST = TAG + "have_earliest_post";
	private boolean haveEarliestPost = false;
	public static final String KEY_LATEST_WEB_POST = TAG + "latest_web_post";
	private Timestamp latestWebPost;

	private Set<Long> currentPostIDs = new HashSet<>();
	public static final String KEY_CURRENT_POST_IDS = TAG + "current_post_ids";
	private boolean refreshing;
	//private boolean overScrollReleased = false;

	private HHNotification notification;

	public static FeedFragment newInstance(){
		return newInstance(GENERAL_FEED, -1);
	}

	public static FeedFragment newInstance(int feedType, long userID){
		FeedFragment feedFragment = new FeedFragment();

		feedFragment.feedType = feedType;
		feedFragment.userID = userID;

		return feedFragment;
	}

	public static FeedFragment newInstance(int feedType, long userID, long postID){
		FeedFragment feedFragment = new FeedFragment();

		feedFragment.feedType = feedType;
		feedFragment.userID = userID;
		feedFragment.postID = postID;

		return feedFragment;
	}

	public static FeedFragment newInstance(int feedType, final HHNotification notification){
		FeedFragment feedFragment = new FeedFragment();

		feedFragment.feedType = feedType;
		feedFragment.notification = notification;
		if (feedType == USER_PROFILE_FEED && notification != null){
			feedFragment.userID = notification.getByUserID();
		}

		return feedFragment;
	}

	public static FeedFragment newInstance(Bundle bundle){
		FeedFragment feedFragment = new FeedFragment();

		feedFragment.restoreInstanceState(bundle);

		return feedFragment;
	}

	public void addToBundle(Bundle bundle){
		bundle.putLong(KEY_USER_ID, userID);
		bundle.putBoolean(KEY_FETCH_DATA, fetchData);
		bundle.putInt(KEY_FEED_TYPE, feedType);
		bundle.putParcelableArrayList(KEY_POSTS, (ArrayList<? extends Parcelable>) posts);
	}

	public void addToBundleForSwitch(Bundle bundle){
		bundle.putLong(MapViewFragment.KEY_USER_ID, userID);
		bundle.putBoolean(MapViewFragment.KEY_FETCH_DATA, fetchData);
		bundle.putInt(MapViewFragment.KEY_MAP_TYPE, feedType);
		bundle.putParcelableArrayList(MapViewFragment.KEY_POSTS, (ArrayList<? extends Parcelable>) posts);
		bundle.putLong(MapViewFragment.KEY_EARLIEST_WEB_POST, earliestWebPost.getTime());
		bundle.putBoolean(MapViewFragment.KEY_HAVE_EARLIEST_POST, haveEarliestPost);
		bundle.putLongArray(MapViewFragment.KEY_CURRENT_POST_IDS, ZZZUtility.getLongArray(currentPostIDs));
		bundle.putLong(MapViewFragment.KEY_LATEST_WEB_POST, latestWebPost.getTime());
	}

	public FeedFragment() {
		// Required empty public constructor
	}

	private void restoreInstanceState(Bundle bundle){
		userID = bundle.getLong(KEY_USER_ID);
		fetchData = bundle.getBoolean(KEY_FETCH_DATA);
		feedType = bundle.getInt(KEY_FEED_TYPE);
		posts = bundle.getParcelableArrayList(KEY_POSTS);
		earliestWebPost = new Timestamp(bundle.getLong(KEY_EARLIEST_WEB_POST));
		latestWebPost = new Timestamp(bundle.getLong(KEY_LATEST_WEB_POST));
		haveEarliestPost = bundle.getBoolean(KEY_HAVE_EARLIEST_POST);
		if (bundle.containsKey(KEY_POST_ID)) postID = bundle.getLong(KEY_POST_ID);
		if (bundle.containsKey(ProfileFragment.KEY_PROFILE_FRAGMENT_BUNDLE))
			profileFragmentBundle = bundle.getBundle(ProfileFragment.KEY_PROFILE_FRAGMENT_BUNDLE);
		if (bundle.containsKey(KEY_CURRENT_POST_IDS)){
			long[] longs = bundle.getLongArray(KEY_CURRENT_POST_IDS);
			ZZZUtility.fillSetFromArray(currentPostIDs, ZZZUtility.getLongArray(longs));
		}
	}

	public void setProfileFragmentBundle(Bundle bundle){
		this.profileFragmentBundle = bundle;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);

		if (savedInstanceState != null){
			restoreInstanceState(savedInstanceState);
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
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(KEY_FEED_TYPE, feedType);
		outState.putBoolean(KEY_FETCH_DATA, fetchData);
		outState.putLong(KEY_USER_ID, userID);
		outState.putLong(KEY_POST_ID, postID);
		outState.putParcelableArrayList(KEY_POSTS, (ArrayList<? extends Parcelable>) posts);
		outState.putBoolean(KEY_HAVE_EARLIEST_POST, haveEarliestPost);
		outState.putLong(KEY_EARLIEST_WEB_POST, earliestWebPost.getTime());
		outState.putLong(KEY_LATEST_WEB_POST, latestWebPost.getTime());

		if (profileFragmentBundle != null){
			outState.putBundle(ProfileFragment.KEY_PROFILE_FRAGMENT_BUNDLE, profileFragmentBundle);
		} else if (profileFragment != null) {
			profileFragmentBundle = profileFragment.getBundle();
			outState.putBundle(ProfileFragment.KEY_PROFILE_FRAGMENT_BUNDLE, profileFragmentBundle);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_feed, container, false);

		lstTimeline = (OverscrollExpandableListView) view.findViewById(R.id.fragment_feed_lstTimeline);

		if (feedType == HOME_PROFILE_FEED || feedType == USER_PROFILE_FEED) {
			View headerView = inflater.inflate(R.layout.fragment_frame, null, false);

			if (profileFragmentBundle != null){
				profileFragment = ProfileFragment.newInstance(profileFragmentBundle);
			} else {

				if (savedInstanceState == null && profileFragment == null) {
					if (userID == HHUser.getCurrentUserID()) {
						profileFragment = ProfileFragment
							.newInstance(ProfileFragment.PROFILE_TYPE_CURRENT_USER, userID);
					} else if (userID >= 0) {
						profileFragment = ProfileFragment
							.newInstance(ProfileFragment.PROFILE_TYPE_OTHER_USER, userID);
					} else if (notification != null){

					}
				} else if (profileFragment != null) {
					profileFragmentBundle = profileFragment.getBundle();
					profileFragment = ProfileFragment.newInstance(profileFragmentBundle);
				} else {
					if (savedInstanceState.containsKey(ProfileFragment.KEY_PROFILE_FRAGMENT_BUNDLE)) {
						profileFragment = ProfileFragment.newInstance(savedInstanceState.getBundle(ProfileFragment.KEY_PROFILE_FRAGMENT_BUNDLE));
					}
				}
			}

			if (profileFragment != null) {
				profileFragment.setProfileMode(ProfileFragment.PROFILE_MODE_FEED);
				profileFragment.setFeedFragment(this);
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.fragment_frame_frame, profileFragment);
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.commit();
			}

			lstTimeline.addHeaderView(headerView);
		}

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
						requestUserProfile(user);
				}

				@Override
				public void onLastItemReached(){
					if (feedType != SINGLE_POST_FEED)
						getData();
				}
			},
			new HHUser.HHUserSpan.HHUserSpanClickCallback() {
				@Override
				public void onClickSpan(HHUser user) {
					if (user.getID() != userID)
						requestUserProfile(user);
				}
			});
		lstTimeline.setAdapter(lstTimelineAdapter);

		if (savedInstanceState != null){
			if (savedInstanceState.containsKey(KEY_HAVE_EARLIEST_POST))
				lstTimelineAdapter.setHaveEarliestPost(haveEarliestPost);
			if (savedInstanceState.containsKey(KEY_EARLIEST_WEB_POST))
				lstTimelineAdapter.setEarliestWebPost(earliestWebPost);
			//if (savedInstanceState.containsKey(KEY_LATEST_WEB_POST))
			//	lstTimelineAdapter.setLatestWebPost(latestWebPost);
		}
		lstTimeline.setOnOverScrollListener(
			new OverscrollExpandableListView.onOverScrollListener() {
				@Override
				public void onOverScroll() {
					//Toast.makeText(getActivity(), "REFRESH", Toast.LENGTH_LONG).show();
					getNewData();
					AsyncDataManager.getNotifications(HolderActivity.notificationsManager.getNotificationsCallback);
				}

				@Override
				public void onRelease() {
					//overScrollReleased = true;
					if (!refreshing)
						setListRefreshProgressBar(false);
				}
			});

		if (fetchData) {
			getData();
		} else {
			notifyAdapter();
		}

		return view;
	}

	private void setListProgressBar(boolean active){
		int footersCount = lstTimeline.getFooterViewsCount();
		if (active && footersCount <= 0){
			footerView = new ProgressBar(getActivity());
			footerView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.adam_theme_black));
			lstTimeline.addFooterView(footerView);
		} else if (footersCount > 0) {
			lstTimeline.removeFooterView(footerView);
		}
	}

	private void setListRefreshProgressBar(boolean active){
		int headersCount = lstTimeline.getHeaderViewsCount();
		if (active && (headersCount <= 0 || feedType == USER_PROFILE_FEED && headersCount <= 1)){
			headerView = new LinearLayout(getActivity());
			headerView.setGravity(Gravity.CENTER);
			headerView.addView(new ProgressBar(getActivity()));
			headerView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.adam_theme_black));
			lstTimeline.addHeaderView(headerView);
		} else if (headerView != null && headersCount > 0 || (feedType == USER_PROFILE_FEED && headersCount > 1)) {
			final int height = headerView.getHeight();
			Animation shrink = new Animation() {

				@Override
				protected void applyTransformation(float interpolatedTime, Transformation t) {
					if (interpolatedTime > 0.95){
						// NOT USING A LISTENER TO AVOID WEIRD FLASHING BEHAVIOUR
						lstTimeline.removeHeaderView(headerView);
					} else {
						headerView.setScaleX(1-interpolatedTime);
						headerView.setScaleY(1-interpolatedTime);
						ViewGroup.LayoutParams layoutParams = headerView.getLayoutParams();
						layoutParams.height = (int) (height * (1.0f-interpolatedTime));
						headerView.setLayoutParams(layoutParams);
					}
				}

				@Override
				public boolean willChangeBounds() {
					return true;
				}
			};

			shrink.setDuration(500);

			headerView.startAnimation(shrink);
		}
	}

	private void getData(){
		setListProgressBar(true);
		switch (feedType) {
			case GENERAL_FEED: {
				getAllData();
				break;
			}
			case HOME_PROFILE_FEED:
			case USER_PROFILE_FEED: {
				getUserData();
				break;
			}
			case SINGLE_POST_FEED:{
				getSinglePostData();
				break;
			}
		}
	}

	private void getNewData(){
		refreshing = true;
		setListRefreshProgressBar(true);
		switch (feedType) {
			case GENERAL_FEED: {
				getAllNewData();
				break;
			}
			/*case HOME_PROFILE_FEED: //TODO ??
			case USER_PROFILE_FEED: {
				getUserNewData();
				break;
			}*/
			case SINGLE_POST_FEED:{
				getSinglePostData();
				break;
			}
		}
	}

	private final AsyncDataManager.GetAllPostsCallback getAllPostsCallback = new AsyncDataManager.GetAllPostsCallback() {
		@Override
		public void returnPostList(List<HHPostFull> posts) {
			Log.d(TAG, "Cached posts returned");
			FeedFragment.this.posts = ZZZUtility.mergeLists(FeedFragment.this.posts, posts);
			notifyAdapter();
			fetchData = false;	//TODO: MAKE THIS SMARTER FOR WEB REQUESTS ACROSS ROTATIONS ETC
		}

		@Override
		public void returnGetPost(final HHPostFull post) {
			Log.d(TAG, "Web post returned!");

			if (post == null)
				return;

			posts = ZZZUtility.updateList(posts, post);
			notifyAdapter();
			fetchData = false; //TODO: MAKE THIS SMARTER FOR WEB REQUESTS ACROSS ROTATIONS ETC
			if (earliestWebPost == null || feedType == SINGLE_POST_FEED || post.getPost().getCreatedAt().before(earliestWebPost)){
				earliestWebPost = post.getPost().getCreatedAt();
				lstTimelineAdapter.setEarliestWebPost(earliestWebPost);
				setListProgressBar(false);
				currentPostIDs.add(post.getPost().getID());
			}
			if (latestWebPost == null || feedType == SINGLE_POST_FEED || post.getPost().getCreatedAt().after(latestWebPost)){
				latestWebPost = post.getPost().getCreatedAt();
				lstTimelineAdapter.setLatestWebPost(latestWebPost);
				//setListRefreshProgressBar(false);
				currentPostIDs.add(post.getPost().getID());
			}
			if (refreshing){
				setListRefreshProgressBar(false);
			}
			if (feedType == SINGLE_POST_FEED && feedType == USER_PROFILE_FEED && notification != null){
				AsyncDataManager.readNotification(
					notification,
					new AsyncDataManager.ReadNotificationCallback(){
						@Override
						public void returnReadNotification(HHNotification readNotification){
							if (feedType == SINGLE_POST_FEED) {
								postID = post.getPost().getID();
							} else if (feedType == USER_PROFILE_FEED){
								//userID;
							}
							notification = null;
						}
					}
				);

			}
		}

		@Override
		public void warnNoEarlierPosts() {
			haveEarliestPost = true;
			lstTimelineAdapter.setHaveEarliestPost(true);
			setListProgressBar(false);
		}

		@Override
		public void warnNoLaterPosts() {
			Toast.makeText(getActivity(), "NO NEW POSTS", Toast.LENGTH_SHORT).show();
			refreshing = false;
			setListRefreshProgressBar(false);
		}
	};

	private void getAllData(){
		if (earliestWebPost == null)
			earliestWebPost = new Timestamp(System.currentTimeMillis());
		AsyncDataManager.getAllPosts(earliestWebPost,
									 currentPostIDs.toArray(new Long[currentPostIDs.size()]),
									 getAllPostsCallback);
	}

	private void getAllNewData(){
		if (latestWebPost == null)
			latestWebPost = new Timestamp(System.currentTimeMillis());
		AsyncDataManager.getAllPostsSince(latestWebPost,
										  currentPostIDs.toArray(new Long[currentPostIDs.size()]),
										  getAllPostsCallback);
	}

	private void getUserData(){
		if (earliestWebPost == null)
			earliestWebPost = new Timestamp(System.currentTimeMillis());
		AsyncDataManager.getUserPrivacy(
			userID,
			true,
			new AsyncDataManager.GetUserPrivacyCallback() {
				@Override
				public void returnCachedUserPrivacy(boolean userPrivacy) {
				}

				@Override
				public void returnWebUserPrivacy(boolean userPrivacy) {
					AsyncDataManager.getUserPosts(userID,
												  earliestWebPost,
												  currentPostIDs.toArray(new Long[currentPostIDs.size()]),
												  getAllPostsCallback);
					if (!userPrivacy) {
						setPrivateProfile();
					}
				}
			});
	}

	private void getSinglePostData(){
		if (earliestWebPost == null)
			earliestWebPost = new Timestamp(System.currentTimeMillis());
		if (postID >= 0) {
			AsyncDataManager.getPost(postID, getAllPostsCallback);
		} else if (notification != null){
			AsyncDataManager.getPost(notification.getPostID(), getAllPostsCallback);
		}
	}

	private void setPrivateProfile(){
		profileFragment.setPrivate();
	}

	private void notifyAdapter(){
		//noinspection unchecked
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
			void onLastItemReached();
		}

		private final Activity context;
		private final AdapterCallback callback;
		private final HHUser.HHUserSpan.HHUserSpanClickCallback userSpanClickCallback;

		private List<HHPostFull> posts;
		private Timestamp earliestWebPost;
		private Timestamp latestWebPost;
		//private Timestamp requestedWebPost;
		private int addingComment = -1;
		private boolean haveEarliestPost = false;
		//private boolean refreshing = false;

		public TimelineCustomExpandableAdapter(Activity context, List<HHPostFull> posts, AdapterCallback callback, HHUser.HHUserSpan.HHUserSpanClickCallback userSpanClickCallback){
			super();
			this.context = context;
			this.posts = posts;
			this.callback = callback;
			this.userSpanClickCallback = userSpanClickCallback;
		}

		public void setEarliestWebPost(Timestamp earliestWebPost){
			this.earliestWebPost = earliestWebPost;
		}

		public void setLatestWebPost(Timestamp latestWebPost){
			this.latestWebPost = latestWebPost;
		}

		/*public void setRequestedWebPost(Timestamp requestedWebPost){
			this.requestedWebPost = requestedWebPost;
		}*/

		public void setHaveEarliestPost(boolean haveEarliestPost){
			this.haveEarliestPost = haveEarliestPost;
		}

		@Override
		public int getGroupCount() {
			return posts.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			if (posts == null)
				return 0;
			int n = 0;
			HHPostFull post = posts.get(groupPosition);
			if (post != null) {
				List<HHCommentUser> comments = post.getComments();
				if (comments != null)
					n += comments.size();
				if (post.getLikes().size() > 0) n++;
			}
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
			ImageButton btnMenuButton;
			int groupPosition;
			boolean addingComment;
			HHPostFull post;
			HHLike myLike;
			CompoundButton.OnCheckedChangeListener likeCheckListener;
			View.OnClickListener commentClickListener;
			OnClickUserListener onClickUserListener;
			ImageView btnSpotifyButton;
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

				if (viewHolder.post != null) {
					for (HHLikeUser like : posts.get(groupPosition).getLikes()) {
						if (like.getUser().equals(HHUser.getCurrentUser().getUser())) {
							viewHolder.myLike = like.getLike();
							break;
						}
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
				viewHolder.btnSpotifyButton = (ImageView) convertView.findViewById(R.id.list_row_timeline_btnSpotify);
				viewHolder.btnMenuButton = (ImageButton) convertView.findViewById(R.id.list_row_timeline_btnMore);

				{
					// TODO: WORKING OUT POPUP MENU!

					viewHolder.btnMenuButton.setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								final ListPopupWindow listPopupWindow = new ListPopupWindow(context);
								final String[] listMute = {"Mute Post"};
								final String[] listUnmute = {"Unmute Post"};

								final ListAdapter adapter = new ArrayAdapter<String>(
									context,
									android.R.layout.simple_list_item_1,
									viewHolder.post.getMute() == null ? listMute : listUnmute
								);

								listPopupWindow.setAnchorView(viewHolder.btnMenuButton);
								listPopupWindow.setAdapter(adapter);
								listPopupWindow.setWidth(400);

								listPopupWindow.setOnItemClickListener(
									new AdapterView.OnItemClickListener() {
										@Override
										public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
											if (position == 0) {
												if (viewHolder.post.getMute() == null){
													AsyncDataManager.postMutePost(
														HHUser.getAuthorisationToken(),
														viewHolder.post.getPost().getID(),
														new AsyncDataManager.PostMutePostCallback() {
															@Override
															public void returnPostMutePost(boolean success, HHMute returnedMute) {
																if (success){
																	viewHolder.post.setMute(returnedMute);
																	final ListAdapter newAdapter = new ArrayAdapter<String>(
																		context,
																		android.R.layout.simple_list_item_1,
																		listUnmute
																	);
																	listPopupWindow.setAdapter(newAdapter);
																}
															}
														}
													);
												} else {
													AsyncDataManager.deleteMutePost(
														HHUser.getAuthorisationToken(),
														viewHolder.post.getPost().getID(),
														new AsyncDataManager.DeleteMutePostCallback() {
															@Override
															public void returnDeleteMutePost(boolean success, HHMute deletedMute) {
																if (success){
																	viewHolder.post.setMute(null);
																	final ListAdapter newAdapter = new ArrayAdapter<String>(
																		context,
																		android.R.layout.simple_list_item_1,
																		listMute
																	);
																	listPopupWindow.setAdapter(newAdapter);
																}
															}
														}
													);
												}
											}
										}
									});

								listPopupWindow.show();

							}
						}
					);
				}

				viewHolder.likeCheckListener = new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						viewHolder.btnLikeButton.setEnabled(false);
						if (isChecked) {
							HHLike like = new HHLike(viewHolder.post.getPost().getID(),
													 HHUser.getCurrentUserID());
							AsyncDataManager.postLike(
								HHUser.getAuthorisationToken(),
								like,
								new AsyncDataManager.PostLikeCallback() {
									@Override
									public void returnPostLike(HHLike returnedLike) {
										Log.d(TAG, "Posted new comment!");
										AsyncDataManager.getWebPost(
											viewHolder.post.getPost().getID(),
											new AsyncDataManager.GetPostCallback() {
												@Override
												public void returnGetPost(HHPostFull post) {
													posts = ZZZUtility.updateList(posts, post);
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
								HHUser.getAuthorisationToken(),
								viewHolder.myLike,
								new AsyncDataManager.DeleteLikeCallback() {
									@Override
									public void returnDeleteLike(boolean success) {
										Log.d(TAG, "Deleted like!");
										AsyncDataManager.getWebPost(
											viewHolder.post.getPost().getID(),
											new AsyncDataManager.GetPostCallback() {
												@Override
												public void returnGetPost(HHPostFull post) {
													posts = ZZZUtility.updateList(posts, post);
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
							try {
								//noinspection ConstantConditions
								inputMethodManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
							} catch (NullPointerException e){
								Log.e(TAG, e.getMessage());
								e.printStackTrace();
							}
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

				viewHolder.btnSpotifyButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("spotify:track:"+viewHolder.post.getTrack().getTrackID()));
						try {
							context.startActivity(intent);
						} catch (ActivityNotFoundException activityNotFoundException){
							try {
								context.getPackageManager().getPackageInfo("com.spotify.music", PackageManager.GET_ACTIVITIES);
								Toast.makeText(context, "SPOTIFY NOT FOUND", Toast.LENGTH_LONG).show();
							} catch (PackageManager.NameNotFoundException nameNotFoundException) {
								nameNotFoundException.printStackTrace();
								try {
									context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.spotify.music")));
								} catch (ActivityNotFoundException playStoreNotFoundException){
									context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.spotify.music")));
								}
							}
							activityNotFoundException.printStackTrace();
						}
					}
				});

				if (viewHolder.post != null) {
					viewHolder.onClickUserListener = new OnClickUserListener(viewHolder.post.getUser(), callback);
					viewHolder.imgProfile.setOnClickListener(viewHolder.onClickUserListener);
					viewHolder.txtUserName.setOnClickListener(viewHolder.onClickUserListener);
				}

				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolderGroupItem) convertView.getTag();
				viewHolder.groupPosition = groupPosition;
				viewHolder.post = posts.get(groupPosition);
				viewHolder.myLike = null;
				if (viewHolder.post != null) {
					if (viewHolder.onClickUserListener != null) {
						viewHolder.onClickUserListener.setUser(viewHolder.post.getUser());
					} else {
						viewHolder.onClickUserListener = new OnClickUserListener(viewHolder.post.getUser(), callback);
					}
					for (HHLikeUser like : posts.get(groupPosition).getLikes() ){
						if (like.getUser().equals(HHUser.getCurrentUser().getUser())){
							viewHolder.myLike = like.getLike();
							break;
						}
					}
				}
			}

			if (viewHolder.post == null)
				return convertView;

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
			viewHolder.txtLocation.setText(ZZZUtility.truncatedAddress(viewHolder.post.getPost().getPlaceName(), 35));
			viewHolder.txtDateTime.setText(ZZZUtility.formatDynamicDate(viewHolder.post.getPost().getCreatedAt()));
			viewHolder.txtTrackName.setText(viewHolder.post.getTrack().getName());
			viewHolder.txtArtist.setText(viewHolder.post.getTrack().getArtist());
			viewHolder.txtAlbum.setText(viewHolder.post.getTrack().getAlbum());

			viewHolder.txtMessage.setText(viewHolder.post.getPost().getMessage());
			Editable message = Editable.Factory.getInstance().newEditable(
				viewHolder.txtMessage.getText());
			Pattern pattern = Pattern.compile("\\{tag_(\\d+)\\}");
			Matcher matcher = pattern.matcher(message);

			while (matcher.find()){
				int start = matcher.start();
				int end = matcher.end();

				long userID = Long.valueOf(matcher.group(1));

				HHUser tagUser = getTagUser(viewHolder.post.getTags(), userID);
				if (tagUser != null){
					String userName = String.format(" %s ", tagUser.getName());
					message.replace(start, end, userName);
					message.setSpan(
						new HHUser.HHUserSpan(context, tagUser, userSpanClickCallback),
						start,
						start + userName.length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
					);
				} else {
					message.replace(start, end, "");
				}

				matcher = pattern.matcher(message);
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

			if (!haveEarliestPost
				//&& (requestedWebPost == null || !requestedWebPost.equals(earliestWebPost))
				&& (viewHolder.post.getPost().getCreatedAt().equals(earliestWebPost)
					|| groupPosition == getGroupCount() - 1)
				){
				Log.d(TAG, "LOADING NEW POSTS!");
				callback.onLastItemReached();
			}

			return convertView;
		}

		private HHUser getTagUser(List<HHTagUser> tags, long userID){
			for (HHTagUser tag : tags) {
				if (tag.getUser().getID() == userID) {
					return tag.getUser();
				}
			}
			return null;
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
				sb = new SpannableStringBuilder(" You ");
				sb.setSpan(
					new HHUser.HHUserSpan(context, HHUser.getCurrentUser().getUser(), userSpanClickCallback),
					0,
					5,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
				);
			} else {
				sb = new SpannableStringBuilder(" ").append(likes.get(0).getUser().getName()).append(" ");
				sb.setSpan(
					new HHUser.HHUserSpan(context, likes.get(0).getUser(), userSpanClickCallback),
					0,
					sb.length(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			for (int i = 1; i < likes.size(); i++){
				HHUser user = likes.get(i).getUser();
				if (user.equals(HHUser.getCurrentUser().getUser())){
					youLike = i;
				} else {
					sb.append(",  ").append(user.getName()).append(" ");
					sb.setSpan(
						new HHUser.HHUserSpan(context, user, userSpanClickCallback),
						sb.length()-user.getName().length()-2,
						sb.length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
			if (youLike > 0){
				sb.insert(0, " You , ");
				sb.setSpan(
					new HHUser.HHUserSpan(context, HHUser.getCurrentUser().getUser(), userSpanClickCallback),
					0,
					5,
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
					AsyncDataManager.postComment(
						HHUser.getAuthorisationToken(),
						comment,
						new AsyncDataManager.PostCommentCallback() {
							@Override
							public void returnPostComment(HHComment returnedComment) {
								Log.d(TAG, "Posted new comment!");
								AsyncDataManager.getWebPost(
									post_id,
									new AsyncDataManager.GetPostCallback() {
										@Override
										public void returnGetPost(HHPostFull post) {
											posts = ZZZUtility.updateList(posts, post);
											callback.onDataChange();
										}
									});
								addingComment = -1;
								InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(
									Context.INPUT_METHOD_SERVICE);
								try {
									//noinspection ConstantConditions
									inputMethodManager.hideSoftInputFromWindow(
										context.getCurrentFocus().getWindowToken(), 0);
								} catch (NullPointerException e){
									Log.e(TAG, e.getMessage());
									e.printStackTrace();
								}
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
