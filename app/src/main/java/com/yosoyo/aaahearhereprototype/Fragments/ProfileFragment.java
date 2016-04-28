package com.yosoyo.aaahearhereprototype.Fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yosoyo.aaahearhereprototype.AsyncDataManager;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequest;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequestUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUserFull;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;
import com.yosoyo.aaahearhereprototype.R;
import com.yosoyo.aaahearhereprototype.ZZZInterface.AutoShowHideButton;
import com.yosoyo.aaahearhereprototype.ZZZUtility;

/**
 * Created by adam on 26/02/2016.
 *
 * ProfileFragment displays user information.
 */
public class ProfileFragment extends FeedbackFragment {

	private static final String TAG = ProfileFragment.class.getSimpleName();

	private int profileType;
	public static final String KEY_PROFILE_TYPE = TAG + "profile_type";
	public static final int PROFILE_TYPE_CURRENT_USER = 0;
	public static final int PROFILE_TYPE_OTHER_USER = 1;

	private int profileMode;
	public static final String KEY_PROFILE_MODE = TAG + "profile_mode";
	public static final int PROFILE_MODE_FEED = 0;
	public static final int PROFILE_MODE_MAP = 1;

	private boolean fetchData = true;
	public static final String KEY_FETCH_DATA = TAG + "fetch_data";

	public static final String KEY_USER_ID = TAG + "user_id";
	private long userID = -1;

	public static final String KEY_USER = TAG + "user";
	private HHUserFull user;

	private FeedFragment feedFragment;
	private MapViewFragment mapFragment;

	private LinearLayout llProfile;
	private LinearLayout llProfileMode;

	private ImageView imgProfile;

	private TextView txtUserName;
	private TextView txtBio;
	private TextView txtURL;
	private ImageView imgFollowStatus;

	private LinearLayout llPostsCount;
	private TextView txtPostsCount;
	private ProgressBar txtPostsCountProgressBar;

	private LinearLayout llFollowsOutCount;
	private TextView txtFollowsOutCount;
	private ProgressBar txtFollowsOutCountProgressBar;

	private LinearLayout llFollowsInCount;
	private TextView txtFollowsInCount;
	private ProgressBar txtFollowsInCountProgressBar;

	private LinearLayout llRequestedResponse;
	private ImageView btnAccept;
	private ProgressBar btnAcceptProgressBar;
	private ImageView btnDelete;
	private ProgressBar btnDeleteProgressBar;

	private FrameLayout flFollow;
	private ImageView btnFollow;
	private ProgressBar btnFollowProgressBar;
	private ImageView btnUnfollow;
	private ProgressBar btnUnfollowProgressBar;

	private LinearLayout llPrivacy;
	private boolean privacy = false;

	private ImageView btnFeed;
	private ImageView btnMap;
	private AutoShowHideButton btnShowHide;
	private boolean readyToHide = false;
	private boolean alreadyHidden = false;
	private boolean btnHideManualOverride = false;
	private boolean btnHideManualState = false;
	public static final String KEY_BUTTON_HIDE_MANUAL_OVERRIDE = TAG + "button_hide_manual_override";
	public static final String KEY_BUTTON_HIDE_MANUAL_STATE = TAG + "button_hide_manual_state";

	private boolean friendIsFollowed;
	private boolean friendFollowsMe;
	private boolean friendIsRequested;
	private boolean friendRequestedMe;

	public static final String KEY_POST_COUNT = TAG + "post_count";
	private int postCount;
	public static final String KEY_FOLLOWERS_IN_COUNT = TAG + "followers_in_count";
	private int followersInCount;
	public static final String KEY_FOLLOWERS_OUT_COUNT = TAG + "followers_out_count";
	private int followersOutCount;

	public static final String KEY_PROFILE_FRAGMENT_BUNDLE = TAG + "profile_fragment_bundle";

	public static ProfileFragment newInstance(int profileType, long userID){
		ProfileFragment profileFragment = new ProfileFragment();

		Bundle arguments = new Bundle();
		arguments.putInt(KEY_PROFILE_TYPE, profileType);
		arguments.putLong(USER_ID, userID);
		profileFragment.setArguments(arguments);

		return profileFragment;
	}

	public static ProfileFragment newInstance(Bundle bundle){
		ProfileFragment profileFragment = new ProfileFragment();

		profileFragment.restoreInstanceState(bundle);

		return profileFragment;
	}

	public ProfileFragment() {
		// Required empty public constructor
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		addToBundle(outState);

	}

	public Bundle getBundle(){
		Bundle bundle = new Bundle();
		addToBundle(bundle);
		return bundle;
	}

	private void addToBundle(Bundle bundle){
		bundle.putLong(			KEY_USER_ID, 					userID);
		bundle.putInt(			KEY_PROFILE_TYPE, 				profileType);
		bundle.putInt(			KEY_PROFILE_MODE,				profileMode);
		bundle.putParcelable(	KEY_USER, 						user);
		bundle.putBoolean(		KEY_FETCH_DATA, 				fetchData);
		bundle.putInt(			KEY_POST_COUNT, 				postCount);
		bundle.putInt(			KEY_FOLLOWERS_IN_COUNT,			followersInCount);
		bundle.putInt(			KEY_FOLLOWERS_OUT_COUNT,		followersOutCount);
		bundle.putBoolean(		KEY_BUTTON_HIDE_MANUAL_OVERRIDE,btnHideManualOverride);
		bundle.putBoolean(		KEY_BUTTON_HIDE_MANUAL_STATE, 	btnHideManualState);

	}

	private void restoreInstanceState(Bundle bundle){

		userID = 				bundle.getLong(KEY_USER_ID);
		profileType =			bundle.getInt(			KEY_PROFILE_TYPE);
		profileMode =			bundle.getInt(KEY_PROFILE_MODE);
		user =					bundle.getParcelable(KEY_USER);
		fetchData =				bundle.getBoolean(KEY_FETCH_DATA);
		postCount =				bundle.getInt(			KEY_POST_COUNT);
		followersInCount =		bundle.getInt(			KEY_FOLLOWERS_IN_COUNT);
		followersOutCount =		bundle.getInt(KEY_FOLLOWERS_OUT_COUNT);
		btnHideManualOverride =	bundle.getBoolean(		KEY_BUTTON_HIDE_MANUAL_OVERRIDE);
		btnHideManualState =	bundle.getBoolean(		KEY_BUTTON_HIDE_MANUAL_STATE);

		//Log.d(TAG, "restoredState");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {

			setHasOptionsMenu(true);

			Bundle arguments = getArguments();
			if (arguments != null) {
				handleArguments(arguments);
			}
		} else {

			restoreInstanceState(savedInstanceState);

		}
	}

	private void handleArguments(Bundle arguments){
		profileType = arguments.getInt(KEY_PROFILE_TYPE);

		if (profileType == PROFILE_TYPE_OTHER_USER){
			userID = arguments.getLong(USER_ID);
			//noinspection ConstantConditions
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		} else {
			userID = HHUser.getCurrentUserID();
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
		// Inflate the layout for this fragment
		final View view = inflater.inflate(R.layout.fragment_profile, container, false);

		getViewComponents(view);
		setBtnShowHideVisibility();
		txtUserName.setVisibility(View.INVISIBLE);
		txtBio.setVisibility(View.INVISIBLE);
		txtURL.setVisibility(View.INVISIBLE);
		btnShowHide.setManualOverride(btnHideManualOverride);

		view.getViewTreeObserver().addOnGlobalLayoutListener(
			new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					//readyToHide = true;
					if (!btnShowHide.getManualOverride())
						checkBtnShowHide();
				}
			});

		if (fetchData){

			switch (profileType){
				case PROFILE_TYPE_CURRENT_USER:{
					user = HHUser.getCurrentUser();
					createView(view, true);
					AsyncDataManager.updateCurrentUser(
						new AsyncDataManager.UpdateCurrentUserCallback() {
							@Override
							public void returnUpdateCurrentUser(boolean success) {
								user = HHUser.getCurrentUser();
								fetchData = false;
								createView(view, true);
							}
						});
					break;
				}
				case PROFILE_TYPE_OTHER_USER:{
					AsyncDataManager.getUser(
						userID,
						false,
						new AsyncDataManager.GetUserCallback() {
							@Override
							public void returnGetCachedUser(HHUserFull returnedUser) {
								user = returnedUser;
								if (user != null) {
									fetchData = false;
									createView(view, true);
								}
							}

							@Override
							public void returnGetWebUser(HHUserFull returnedUser) {
								user = returnedUser;
								fetchData = false;
								createView(view, true);
							}
						}
					);
					break;
				}
			}
		} else {
			createView(view, false);
		}

		return view;
	}

	private void getViewComponents(View view){
		llProfile = (LinearLayout) view.findViewById(R.id.fragment_profile_llProfile);
		llProfileMode = (LinearLayout) view.findViewById(R.id.fragment_profile_llProfileMode);

		imgProfile = (ImageView) view.findViewById(R.id.fragment_profile_imgProfile);

		txtUserName = (TextView) view.findViewById(R.id.fragment_profile_txtUserName);
		txtBio = (TextView) view.findViewById(R.id.fragment_profile_txtBio);
		txtURL = (TextView) view.findViewById(R.id.fragment_profile_txtURL);
		imgFollowStatus = (ImageView) view.findViewById(R.id.fragment_profile_imgFollowStatus);

		llPostsCount = (LinearLayout) view.findViewById(R.id.fragment_profile_llPostsCount);
		txtPostsCount = (TextView) view.findViewById(R.id.fragment_profile_txtPostsCount);
		txtPostsCountProgressBar = (ProgressBar) view.findViewById(R.id.fragment_profile_txtPostsCount_progress);

		llFollowsOutCount = (LinearLayout) view.findViewById(R.id.fragment_profile_llFollowsOutCount);
		txtFollowsOutCount = (TextView)	view.findViewById(R.id.fragment_profile_txtFollowsOutCount);
		txtFollowsOutCountProgressBar = (ProgressBar) view.findViewById(R.id.fragment_profile_txtFollowsOutCount_progress);

		llFollowsInCount = (LinearLayout) view.findViewById(R.id.fragment_profile_llFollowsInCount);
		txtFollowsInCount = (TextView) view.findViewById(R.id.fragment_profile_txtFollowsInCount);
		txtFollowsInCountProgressBar = (ProgressBar) view.findViewById(R.id.fragment_profile_txtFollowsInCount_progress);

		llRequestedResponse = (LinearLayout) view.findViewById(R.id.fragment_profile_llRequestResponse);
		btnAccept = (ImageView) view.findViewById(R.id.fragment_profile_btnAccept);
		btnAcceptProgressBar = (ProgressBar) view.findViewById(R.id.fragment_profile_btnAccept_progress);
		btnDelete = (ImageView) view.findViewById(R.id.fragment_profile_btnDelete);
		btnDeleteProgressBar = (ProgressBar) view.findViewById(R.id.fragment_profile_btnDelete_progress);

		flFollow = (FrameLayout) view.findViewById(R.id.fragment_profile_flFollow);
		btnFollow = (ImageView) view.findViewById(R.id.fragment_profile_btnFollow);
		btnFollowProgressBar = (ProgressBar) view.findViewById(R.id.fragment_profile_btnFollow_progress);
		btnUnfollow = (ImageView) view.findViewById(R.id.fragment_profile_btnUnfollow);
		btnUnfollowProgressBar = (ProgressBar) view.findViewById(R.id.fragment_profile_btnUnfollow_progress);

		llPrivacy = (LinearLayout) view.findViewById(R.id.fragment_profile_llPrivacy);

		btnFeed = (ImageView) view.findViewById(R.id.fragment_profile_btnFeed);
		btnMap = (ImageView) view.findViewById(R.id.fragment_profile_btnMap);
		btnShowHide = (AutoShowHideButton) view.findViewById(R.id.fragment_profile_btnShowHide);
	}

	private final View.OnClickListener onClickAcceptRequestListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			btnAccept.setVisibility(View.GONE);
			btnDelete.setColorFilter(ZZZUtility.screen(ContextCompat.getColor(getActivity(), R.color.adam_theme_darkest)));
			btnDelete.setEnabled(false);
			btnAcceptProgressBar.setVisibility(View.VISIBLE);

			HHFollowRequestUser acceptFollowRequest = null;
			for (HHFollowRequestUser followRequest : HHUser.getCurrentUser().getFollowInRequests()){
				if (followRequest.getUser().equals(user.getUser())){
					acceptFollowRequest = followRequest;
					break;
				}
			}

			if (acceptFollowRequest == null) {
				btnAccept.setVisibility(View.VISIBLE);
				btnAcceptProgressBar.setVisibility(View.GONE);
				btnDelete.clearColorFilter();
				btnDelete.setEnabled(true);
				return;
			}

			AsyncDataManager.acceptFollowRequest(
				acceptFollowRequest,
				new AsyncDataManager.AcceptFollowRequestCallback() {
					@Override
					public void returnAcceptFollowRequest(boolean success, HHFollowRequestUser followRequest) {
						if (success) {

							hideFollowsOutCount();

							AsyncDataManager.updateCurrentUser(
								new AsyncDataManager.UpdateCurrentUserCallback() {
									@Override
									public void returnUpdateCurrentUser(boolean success) {
										if (success) {
											getActivity().invalidateOptionsMenu();
											updateFollowersOutCount(true);
											updateFollowStatus();
										}
									}
								}
							);

							llRequestedResponse.setVisibility(View.GONE);

						} else {
							btnAccept.setVisibility(View.VISIBLE);
							btnDelete.clearColorFilter();
							btnDelete.setEnabled(true);
						}
						btnAcceptProgressBar.setVisibility(View.GONE);
					}
				});
		}
	};

	private final View.OnClickListener onClickDeleteRequestListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			btnDelete.setVisibility(View.GONE);
			btnAccept.setColorFilter(ZZZUtility.screen(ContextCompat.getColor(getActivity(), R.color.adam_theme_darkest)));
			btnAccept.setEnabled(false);
			btnDeleteProgressBar.setVisibility(View.VISIBLE);

			HHFollowRequestUser deleteFollowRequest = null;
			for (HHFollowRequestUser followRequest : HHUser.getCurrentUser().getFollowInRequests()){
				if (followRequest.getUser().equals(user.getUser())){
					deleteFollowRequest = followRequest;
					break;
				}
			}

			if (deleteFollowRequest == null) {
				btnDelete.setVisibility(View.VISIBLE);
				btnDeleteProgressBar.setVisibility(View.GONE);
				btnAccept.clearColorFilter();
				btnAccept.setEnabled(true);
				return;
			}

			AsyncDataManager.deleteFollowRequest(
				deleteFollowRequest,
				new AsyncDataManager.DeleteFollowRequestCallback() {
					@Override
					public void returnDeleteFollowRequest(boolean success, HHFollowRequestUser followRequest) {
						if (success) {

							AsyncDataManager.updateCurrentUser(
								new AsyncDataManager.UpdateCurrentUserCallback() {
									@Override
									public void returnUpdateCurrentUser(boolean success) {
										if (success) {
											getActivity().invalidateOptionsMenu();
										}
									}
								}
							);

							llRequestedResponse.setVisibility(View.GONE);

						} else {
							btnDelete.setVisibility(View.VISIBLE);
							btnAccept.clearColorFilter();
							btnAccept.setEnabled(true);
						}
						btnDeleteProgressBar.setVisibility(View.GONE);
					}
				});
		}
	};

	private final View.OnClickListener onClickFollowListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			btnFollow.setVisibility(View.GONE);
			btnFollowProgressBar.setVisibility(View.VISIBLE);
			AsyncDataManager.postFollowRequest(
				new HHFollowRequest(HHUser.getCurrentUserID(), userID),
				new AsyncDataManager.PostFollowRequestCallback() {
					@Override
					public void returnPostFollowRequest(boolean success, HHFollowRequestUser returnedFollowRequest) {
						if (success) {
							AsyncDataManager.updateCurrentUser(
								new AsyncDataManager.UpdateCurrentUserCallback() {
									@Override
									public void returnUpdateCurrentUser(boolean success) {
										if (success) {
											getActivity().invalidateOptionsMenu();
											if (profileType == PROFILE_TYPE_CURRENT_USER) {
												user = HHUser.getCurrentUser();
											}
										}
									}
								}
							);
							btnFollow.setColorFilter(ZZZUtility.screen(ContextCompat.getColor(getActivity(), R.color.adam_theme_darkest)));
							btnFollow.setEnabled(false);
						}
						btnFollow.setVisibility(View.VISIBLE);
						btnFollowProgressBar.setVisibility(View.GONE);
					}

					@Override
					public void returnPostFollowRequestAccepted(boolean success, HHFollowUser returnedFollowUser) {

						hideFollowsInCount();

						if (success) {
							AsyncDataManager.updateCurrentUser(
								new AsyncDataManager.UpdateCurrentUserCallback() {
									@Override
									public void returnUpdateCurrentUser(boolean success) {
										if (success) {
											getActivity().invalidateOptionsMenu();
											if (profileType == PROFILE_TYPE_CURRENT_USER){
												user = HHUser.getCurrentUser();
											}
											updateFollowersInCount(true);
											updateFollowStatus();
										}
									}
								}
							);
							btnUnfollow.setVisibility(View.VISIBLE);
						} else {
							txtFollowsInCount.setVisibility(View.VISIBLE);
							btnFollow.setVisibility(View.VISIBLE);
						}
						btnFollowProgressBar.setVisibility(View.GONE);
					}
				});
		}
	};

	private final View.OnClickListener onClickUnfollowListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			btnUnfollow.setVisibility(View.GONE);
			btnUnfollowProgressBar.setVisibility(View.VISIBLE);
			HHFollowUser deleteFollow = null;
			for (HHFollowUser follow : HHUser.getCurrentUser().getFollowOuts()){
				if (follow.getUser().equals(user.getUser())){
					deleteFollow = follow;
					break;
				}
			}

			if (deleteFollow == null) {
				btnUnfollow.setVisibility(View.VISIBLE);
				btnUnfollowProgressBar.setVisibility(View.GONE);
				return;
			}

			AsyncDataManager.deleteFollow(
				deleteFollow,
				new AsyncDataManager.DeleteFollowCallback() {
					@Override
					public void returnDeleteFollow(boolean success, HHFollowUser deletedFollow) {

						hideFollowsInCount();

						if (success) {
							AsyncDataManager.updateCurrentUser(
								new AsyncDataManager.UpdateCurrentUserCallback() {
									@Override
									public void returnUpdateCurrentUser(boolean success) {
										if (success) {
											getActivity().invalidateOptionsMenu();
											if (profileType == PROFILE_TYPE_CURRENT_USER) {
												user = HHUser.getCurrentUser();
											}
											updateFollowersInCount(true);
											updateFollowStatus();
										}
									}
								}
							);
							btnFollow.setVisibility(View.VISIBLE);
						} else {
							txtFollowsInCount.setVisibility(View.VISIBLE);
							btnUnfollow.setVisibility(View.VISIBLE);
						}
						btnUnfollowProgressBar.setVisibility(View.GONE);
					}
				});
		}
	};

	private final View.OnClickListener onClickFollowsInListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			requestFollowerList(user.getUser(), FollowersListFragment.FOLLOWER_TYPE_IN);
		}
	};

	private final View.OnClickListener onClickFollowsOutListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			requestFollowerList(user.getUser(), FollowersListFragment.FOLLOWER_TYPE_OUT);
		}
	};

	private void createView(final View view, boolean fullUpdate){

		WebHelper.getFacebookProfilePicture(
			user.getUser().getFBUserID(),
			new WebHelper.GetFacebookProfilePictureCallback() {
				@Override
				public void returnFacebookProfilePicture(Bitmap bitmap) {
					imgProfile.setImageBitmap(bitmap);
				}
			});

		txtUserName.setVisibility(View.VISIBLE);
		txtUserName.setText(user.getUser().getName());

		if (user.getUser().getBio().isEmpty())
			txtBio.setVisibility(View.GONE);
		else {
			txtBio.setText(user.getUser().getBio());
			txtBio.setVisibility(View.VISIBLE);
		}

		if (user.getUser().getURL().isEmpty())
			txtURL.setVisibility(View.GONE);
		else {
			txtURL.setText(user.getUser().getURL());
			txtURL.setVisibility(View.VISIBLE);
		}

		if (privacy)
			llPrivacy.setVisibility(View.VISIBLE);

		llFollowsInCount.setOnClickListener(onClickFollowsInListener);
		llFollowsOutCount.setOnClickListener(onClickFollowsOutListener);

		if (fullUpdate) {
			updatePostCount(true);
			updateFollowersInCount(true);
			updateFollowersOutCount(true);
		} else {
			updatePostCount(postCount);
			updateFollowersInCount(followersInCount);
			updateFollowersOutCount(followersOutCount);
		}

		if (profileType == PROFILE_TYPE_CURRENT_USER){
			flFollow.setVisibility(View.GONE);
			llRequestedResponse.setVisibility(View.GONE);
			imgFollowStatus.setVisibility(View.GONE);
		} else {

			flFollow.setVisibility(View.VISIBLE);
			imgFollowStatus.setVisibility(View.VISIBLE);

			updateFollowStatus();

			updateFollowButtons();

			txtFollowsInCount.setText(String.valueOf(user.getFollowIns().size()));
			txtFollowsOutCount.setText(String.valueOf(user.getFollowOuts().size()));

			btnAccept.setOnClickListener(onClickAcceptRequestListener);
			btnDelete.setOnClickListener(onClickDeleteRequestListener);
			btnFollow.setOnClickListener(onClickFollowListener);
			btnUnfollow.setOnClickListener(onClickUnfollowListener);

		}

		btnShowHide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
				if (!btnShowHide.getListening())
					return;

				if (!btnShowHide.getAutoListening() && !btnShowHide.getManualOverride()) {
					btnShowHide.setManualOverride(true);
					btnHideManualOverride = true;
				}

				if (isChecked) {
					Log.d(TAG, "TRUE");
					final int translateY = -llProfile.getHeight();
					llProfile.setVisibility(View.VISIBLE);
					Animation show = new Animation() {

						@Override
						protected void applyTransformation(float interpolatedTime, Transformation t) {
							view.setTranslationY(translateY * (1-interpolatedTime));
							view.requestLayout();
							llProfileMode.requestLayout();
						}

						@Override
						public boolean willChangeBounds() {
							return true;
						}
					};

					show.setDuration(200);
					view.startAnimation(show);

				} else {
					Log.d(TAG, "FALSE");
					final int translateY = -llProfile.getHeight();

					Animation hide = new Animation() {

						@Override
						protected void applyTransformation(float interpolatedTime, Transformation t) {
							view.setTranslationY(translateY * interpolatedTime);
							view.requestLayout();
							llProfileMode.requestLayout();
							if (interpolatedTime == 1){
								llProfile.setVisibility(View.INVISIBLE);
							}
						}

						@Override
						public boolean willChangeBounds() {
							return true;
						}
					};

					hide.setDuration(200);
					view.startAnimation(hide);
				}

				btnHideManualState = btnShowHide.isChecked();
			}
		});

		checkBtnShowHide();

		btnFeed.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (profileMode == PROFILE_MODE_FEED) {
					Log.d(TAG, "Feed -> Feed: NOTHING");
				} else {
					Log.d(TAG, "Feed -> Map: SWITCH");
					Bundle bundle = getBundle();
					mapFragment.addToBundleForSwitch(bundle);
					requestProfileModeSwitch(PROFILE_MODE_FEED, userID, bundle);
				}
			}
		});

		btnMap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (profileMode == PROFILE_MODE_MAP){
					Log.d(TAG, "Map -> Map: NOTHING");
				} else {
					Log.d(TAG, "Map -> Feed: SWITCH");
					Bundle bundle = getBundle();
					feedFragment.addToBundleForSwitch(bundle);
					requestProfileModeSwitch(PROFILE_MODE_MAP, userID, bundle);
				}
			}
		});

		readyToHide = true;

	}

	private void updateFollowStatus(){
		friendIsFollowed = HHUser.userIsFollowed(user.getUser());
		friendFollowsMe = HHUser.userFollowsMe(user.getUser());
		friendIsRequested = HHUser.userIsRequested(user.getUser());
		friendRequestedMe = HHUser.userRequestedMe(user.getUser());

		if (friendIsFollowed && friendFollowsMe) {
			imgFollowStatus.setImageResource(R.drawable.follow_in_out);
		} else if (friendIsFollowed) {
			imgFollowStatus.setImageResource(R.drawable.follow_out);
		} else if (friendFollowsMe) {
			imgFollowStatus.setImageResource(R.drawable.follow_in);
		} else {
			imgFollowStatus.setImageResource(R.drawable.follow_none);
		}

	}

	private void updateFollowButtons(){
		if (friendIsFollowed) {
			btnFollow.setVisibility(View.GONE);
			btnUnfollow.setVisibility(View.VISIBLE);
		} else {
			btnFollow.setVisibility(View.VISIBLE);
			btnFollow.setEnabled(true);
			btnFollow.clearColorFilter();
			btnUnfollow.setVisibility(View.GONE);
		}

		if (friendIsRequested) {
			btnFollow.setColorFilter(ZZZUtility.screen(ContextCompat.getColor(getActivity(), R.color.adam_theme_darkest)));
			btnFollow.setEnabled(false);
		}

		if (friendRequestedMe) {
			llRequestedResponse.setVisibility(View.VISIBLE);
		} else {
			llRequestedResponse.setVisibility(View.GONE);
		}
	}

	private void hidePostCount(){
		txtPostsCount.setVisibility(View.INVISIBLE);
		txtPostsCountProgressBar.setVisibility(View.VISIBLE);
	}

	private void hideFollowsOutCount(){
		txtFollowsOutCount.setVisibility(View.INVISIBLE);
		txtFollowsOutCountProgressBar.setVisibility(View.VISIBLE);
	}

	private void hideFollowsInCount(){
		txtFollowsInCount.setVisibility(View.INVISIBLE);
		txtFollowsInCountProgressBar.setVisibility(View.VISIBLE);
	}

	private void updatePostCount(final boolean webOnly) {
		AsyncDataManager.getUserPostCount(
			userID,
			webOnly,
			new AsyncDataManager.GetUserPostCountCallback() {
				@Override
				public void returnCachedUserPostCount(int postCount) {
					updatePostCount(postCount);
				}

				@Override
				public void returnWebUserPostCount(int postCount) {
					updatePostCount(postCount);
				}
			});
	}

	private void updatePostCount(final int postCount){
		ProfileFragment.this.postCount = postCount;
		txtPostsCount.setText(String.valueOf(postCount));
		txtPostsCount.setVisibility(View.VISIBLE);
		txtPostsCountProgressBar.setVisibility(View.GONE);
	}

	private void updateFollowersInCount(final boolean webOnly) {
		AsyncDataManager.getUserFollowersInCount(
			userID,
			webOnly,
			new AsyncDataManager.GetUserFollowersInCountCallback() {
				@Override
				public void returnCachedUserFollowersInCount(int followersInCount) {
					updateFollowersInCount(followersInCount);
				}

				@Override
				public void returnWebUserFollowersInCount(int followersInCount) {
					updateFollowersInCount(followersInCount);
				}
			});
	}

	private void updateFollowersInCount(final int followersInCount){
		ProfileFragment.this.followersInCount = followersInCount;
		txtFollowsInCount.setText(String.valueOf(followersInCount));
		txtFollowsInCount.setVisibility(View.VISIBLE);
		txtFollowsInCountProgressBar.setVisibility(View.GONE);
	}

	private void updateFollowersOutCount(final boolean webOnly) {
		AsyncDataManager.getUserFollowersOutCount(
			userID,
			webOnly,
			new AsyncDataManager.GetUserFollowersOutCountCallback() {
				@Override
				public void returnCachedUserFollowersOutCount(int followersOutCount) {
					updateFollowersOutCount(followersOutCount);
				}

				@Override
				public void returnWebUserFollowersOutCount(int followersOutCount) {
					updateFollowersOutCount(followersOutCount);
				}
			});
	}

	private void updateFollowersOutCount(final int followersOutCount){
		ProfileFragment.this.followersOutCount = followersOutCount;
		txtFollowsOutCount.setText(String.valueOf(followersOutCount));
		txtFollowsOutCount.setVisibility(View.VISIBLE);
		txtFollowsOutCountProgressBar.setVisibility(View.GONE);
	}

	void setPrivate(){
		if (llPrivacy != null)
			llPrivacy.setVisibility(View.VISIBLE);
		privacy = true;
	}

	public void setProfileMode(int mode){
		profileMode = mode;
	}

	public void setFeedFragment(final FeedFragment feedFragment){
		this.feedFragment = feedFragment;
	}


	public void setMapFragment(final MapViewFragment mapFragment){
		this.mapFragment = mapFragment;
	}

	private void setBtnShowHideVisibility(){
		if (profileMode != PROFILE_MODE_MAP)
			btnShowHide.setVisibility(View.INVISIBLE);
		else
			btnShowHide.setVisibility(View.VISIBLE);
	}

	private void checkBtnShowHide(){
		if (profileMode == PROFILE_MODE_MAP && btnShowHide.getManualOverride()){
			alreadyHidden = true;
			if (btnShowHide.isChecked() && !btnHideManualState){
				btnShowHide.setAutoListening(true);
				btnShowHide.setChecked(false);
				btnShowHide.setAutoListening(false);
			} else {
				btnShowHide.setAutoListening(true);
				btnShowHide.setChecked(true);
				btnShowHide.setAutoListening(false);
			}
			return;
		}

		if (profileMode == PROFILE_MODE_MAP && readyToHide && !alreadyHidden && btnShowHide.isChecked()){
			btnShowHide.setAutoListening(true);
			btnShowHide.setChecked(false);
			btnShowHide.setAutoListening(false);
			alreadyHidden = true;
		}
	}

	public void resetReadyToHide(){
		readyToHide = true;
		alreadyHidden = false;
	}

}
