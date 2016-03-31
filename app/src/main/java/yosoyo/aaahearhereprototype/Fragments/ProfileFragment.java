package yosoyo.aaahearhereprototype.Fragments;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import android.widget.ToggleButton;

import yosoyo.aaahearhereprototype.AsyncDataManager;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequest;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequestUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUserFull;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;
import yosoyo.aaahearhereprototype.R;
import yosoyo.aaahearhereprototype.ZZZUtility;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends FeedbackFragment {

	private static final String TAG = ProfileFragment.class.getSimpleName();

	private int profileType;
	public static final String PROFILE_TYPE = "profile_type";
	public static final int PROFILE_TYPE_CURRENT_USER = 0;
	public static final int PROFILE_TYPE_OTHER_USER = 1;

	private int profileMode;
	public static final String PROFILE_MODE = "profile_mode";
	public static final int PROFILE_MODE_FEED = 0;
	public static final int PROFILE_MODE_MAP = 1;

	private long userID = -1;
	private HHUserFull user;

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
	private ToggleButton btnShowHide;
	private boolean readyToHide = false;
	private boolean alreadyHidden = false;

	private boolean friendIsFollowed;
	private boolean friendFollowsMe;
	private boolean friendIsRequested;
	private boolean friendRequestedMe;

	public static ProfileFragment newInstance(int profileType, long userID){
		ProfileFragment profileFragment = new ProfileFragment();

		Bundle arguments = new Bundle();
		arguments.putInt(PROFILE_TYPE, profileType);
		arguments.putLong(USER_ID, userID);
		profileFragment.setArguments(arguments);

		return profileFragment;
	}

	public ProfileFragment() {
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
		profileType = arguments.getInt(PROFILE_TYPE);

		if (profileType == PROFILE_TYPE_OTHER_USER){
			userID = arguments.getLong(USER_ID);
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

		view.getViewTreeObserver().addOnGlobalLayoutListener(
			new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					readyToHide = true;
					checkBtnShowHide();
				}
			});

		switch (profileType){
			case PROFILE_TYPE_CURRENT_USER:{
				user = HHUser.getCurrentUser();
				createView(view);
				AsyncDataManager.updateCurrentUser(
					new AsyncDataManager.UpdateCurrentUserCallback() {
						@Override
						public void returnUpdateCurrentUser(boolean success) {
							user = HHUser.getCurrentUser();
							readyToHide = true;
							createView(view);
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
								readyToHide = true;
								createView(view);
							}
						}

						@Override
						public void returnGetWebUser(HHUserFull returnedUser) {
							user = returnedUser;
							readyToHide = true;
							createView(view);
						}
					}
				);
				break;
			}
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
		btnShowHide = (ToggleButton) view.findViewById(R.id.fragment_profile_btnShowHide);
	}

	private View.OnClickListener onClickAcceptRequestListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			btnAccept.setVisibility(View.GONE);
			btnDelete.setColorFilter(ZZZUtility.greyOut);
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

	private View.OnClickListener onClickDeleteRequestListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			btnDelete.setVisibility(View.GONE);
			btnAccept.setColorFilter(ZZZUtility.greyOut);
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

	private View.OnClickListener onClickFollowListener = new View.OnClickListener() {
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
							btnFollow.setColorFilter(ZZZUtility.greyOut);
							btnFollow.setEnabled(false);
						} else {
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

	private View.OnClickListener onClickUnfollowListener = new View.OnClickListener() {
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

	private View.OnClickListener onClickFollowsInListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			requestFollowerList(user.getUser(), FollowersListFragment.FOLLOWER_TYPE_IN);
		}
	};

	private View.OnClickListener onClickFollowsOutListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			requestFollowerList(user.getUser(), FollowersListFragment.FOLLOWER_TYPE_OUT);
		}
	};

	private void createView(final View view){

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

		if (privacy == true)
			llPrivacy.setVisibility(View.VISIBLE);

		llFollowsInCount.setOnClickListener(onClickFollowsInListener);
		llFollowsOutCount.setOnClickListener(onClickFollowsOutListener);

		updatePostCount(true);
		updateFollowersInCount(true);
		updateFollowersOutCount(true);

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
			}
		});

		checkBtnShowHide();

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
			btnFollow.setColorFilter(ZZZUtility.greyOut);
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

	private void updatePostCount(final boolean webOnly){
		AsyncDataManager.getUserPostCount(
			userID,
			webOnly,
			new AsyncDataManager.GetUserPostCountCallback() {
				@Override
				public void returnCachedUserPostCount(int postCount) {
					txtPostsCount.setText(String.valueOf(postCount));
					txtPostsCount.setVisibility(View.VISIBLE);
					txtPostsCountProgressBar.setVisibility(View.GONE);
				}

				@Override
				public void returnWebUserPostCount(int postCount) {
					txtPostsCount.setText(String.valueOf(postCount));
					txtPostsCount.setVisibility(View.VISIBLE);
					txtPostsCountProgressBar.setVisibility(View.GONE);
				}
			});
	}

	private void updateFollowersInCount(final boolean webOnly){
		AsyncDataManager.getUserFollowersInCount(
			userID,
			webOnly,
			new AsyncDataManager.GetUserFollowersInCountCallback() {
				@Override
				public void returnCachedUserFollowersInCount(int followersInCount) {
					txtFollowsInCount.setText(String.valueOf(followersInCount));
					txtFollowsInCount.setVisibility(View.VISIBLE);
					txtFollowsInCountProgressBar.setVisibility(View.GONE);
				}

				@Override
				public void returnWebUserFollowersInCount(int followersInCount) {
					txtFollowsInCount.setText(String.valueOf(followersInCount));
					txtFollowsInCount.setVisibility(View.VISIBLE);
					txtFollowsInCountProgressBar.setVisibility(View.GONE);
				}
			});
	}

	private void updateFollowersOutCount(final boolean webOnly){
		AsyncDataManager.getUserFollowersOutCount(
			userID,
			webOnly,
			new AsyncDataManager.GetUserFollowersOutCountCallback() {
				@Override
				public void returnCachedUserFollowersOutCount(int followersOutCount) {
					txtFollowsOutCount.setText(String.valueOf(followersOutCount));
					txtFollowsOutCount.setVisibility(View.VISIBLE);
					txtFollowsOutCountProgressBar.setVisibility(View.GONE);
				}

				@Override
				public void returnWebUserFollowersOutCount(int followersOutCount) {
					txtFollowsOutCount.setText(String.valueOf(followersOutCount));
					txtFollowsOutCount.setVisibility(View.VISIBLE);
					txtFollowsOutCountProgressBar.setVisibility(View.GONE);
				}
			});
	}

	void setPrivate(){
		if (llPrivacy != null)
			llPrivacy.setVisibility(View.VISIBLE);
		privacy = true;
	}

	void setProfileMode(int mode){
		profileMode = mode;
	}

	void setBtnShowHideVisibility(){
		if (profileMode != PROFILE_MODE_MAP)
			btnShowHide.setVisibility(View.INVISIBLE);
		else
			btnShowHide.setVisibility(View.VISIBLE);
	}

	void checkBtnShowHide(){
		if (profileMode == PROFILE_MODE_MAP && readyToHide && !alreadyHidden && btnShowHide.isChecked()){
			btnShowHide.setChecked(false);
			alreadyHidden = true;
		}
	}

}
