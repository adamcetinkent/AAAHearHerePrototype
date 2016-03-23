package yosoyo.aaahearhereprototype.Fragments;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import yosoyo.aaahearhereprototype.AsyncDataManager;
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
	public static final int HOME_PROFILE = 0;
	public static final int USER_PROFILE = 1;

	private long userID = -1;
	private HHUserFull user;

	public static ProfileFragment newInstance(){
		return newInstance(HOME_PROFILE, -1);
	}

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

		if (profileType == USER_PROFILE){
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

		switch (profileType){
			case HOME_PROFILE:{
				user = HHUser.getCurrentUser();
				createView(view);
				AsyncDataManager.updateCurrentUser(
					new AsyncDataManager.UpdateCurrentUserCallback() {
						@Override
						public void returnUpdateCurrentUser(boolean success) {
							user = HHUser.getCurrentUser();
							createView(view);
						}
					});
				break;
			}
			case USER_PROFILE:{
				AsyncDataManager.getUser(
					userID,
					new AsyncDataManager.GetUserCallback() {
						@Override
						public void returnGetCachedUser(HHUserFull returnedUser) {
							user = returnedUser;
							if (user != null)
								createView(view);
						}

						@Override
						public void returnGetWebUser(HHUserFull returnedUser) {
							user = returnedUser;
							createView(view);
						}
					}
				);
				break;
			}
		}

		/*FeedFragment testFragment = FeedFragment.newInstance(FeedFragment.USER_FEED, userID);
		commitFragmentTransaction(testFragment, false);*/

		return view;
	}

	private void createView(View view){

		final ImageView imgProfile = (ImageView) view.findViewById(R.id.fragment_profile_imgProfile);

		final TextView txtUserName = (TextView) view.findViewById(R.id.fragment_profile_txtUserName);
		final TextView txtBio = (TextView) view.findViewById(R.id.fragment_profile_txtBio);
		final ImageView imgFollowStatus = (ImageView) view.findViewById(R.id.fragment_profile_imgFollowStatus);
		final TextView txtPostsCount = (TextView) view.findViewById(R.id.fragment_profile_txtPostsCount);
		final ProgressBar txtPostsCountProgressBar = (ProgressBar) view.findViewById(R.id.fragment_profile_txtPostsCount_progress);
		final TextView txtFollowsOutCount = (TextView)	view.findViewById(R.id.fragment_profile_txtFollowsOutCount);
		final ProgressBar txtFollowsOutCountProgressBar = (ProgressBar) view.findViewById(R.id.fragment_profile_txtFollowsOutCount_progress);
		final TextView txtFollowsInCount = (TextView) view.findViewById(R.id.fragment_profile_txtFollowsInCount);
		final ProgressBar txtFollowsInCountProgressBar = (ProgressBar) view.findViewById(R.id.fragment_profile_txtFollowsInCount_progress);

		final LinearLayout llRequestedResponse = (LinearLayout) view.findViewById(R.id.fragment_profile_llRequestResponse);
		final ImageView btnAccept = (ImageView) view.findViewById(R.id.fragment_profile_btnAccept);
		final ProgressBar btnAcceptProgressBar = (ProgressBar) view.findViewById(R.id.fragment_profile_btnAccept_progress);
		final ImageView btnDelete = (ImageView) view.findViewById(R.id.fragment_profile_btnDelete);
		final ProgressBar btnDeleteProgressBar = (ProgressBar) view.findViewById(R.id.fragment_profile_btnDelete_progress);

		final FrameLayout flFollow = (FrameLayout) view.findViewById(R.id.fragment_profile_flFollow);
		final ImageView btnFollow = (ImageView) view.findViewById(R.id.fragment_profile_btnFollow);
		final ProgressBar btnFollowProgressBar = (ProgressBar) view.findViewById(R.id.fragment_profile_btnFollow_progress);
		final ImageView btnUnfollow = (ImageView) view.findViewById(R.id.fragment_profile_btnUnfollow);
		final ProgressBar btnUnfollowProgressBar = (ProgressBar) view.findViewById(R.id.fragment_profile_btnUnfollow_progress);

		final ImageView btnFeed = (ImageView) view.findViewById(R.id.fragment_profile_btnFeed);
		final ImageView btnMap = (ImageView) view.findViewById(R.id.fragment_profile_btnMap);

		WebHelper.getFacebookProfilePicture(
			user.getUser().getFBUserID(),
			new WebHelper.GetFacebookProfilePictureCallback() {
				@Override
				public void returnFacebookProfilePicture(Bitmap bitmap) {
					imgProfile.setImageBitmap(bitmap);
				}
			});

		txtUserName.setText(user.getUser().getName());
		txtBio.setText(user.getUser().getBio());

		txtFollowsInCount.setText(String.valueOf(user.getFollowIns().size()));
		txtFollowsInCount.setVisibility(View.VISIBLE);
		txtFollowsInCountProgressBar.setVisibility(View.GONE);
		txtFollowsOutCount.setText(String.valueOf(user.getFollowOuts().size()));
		txtFollowsOutCount.setVisibility(View.VISIBLE);
		txtFollowsOutCountProgressBar.setVisibility(View.GONE);
		AsyncDataManager.getUserPostCount(userID, new AsyncDataManager.GetUserPostCountCallback() {
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

		if (profileType == HOME_PROFILE){
			flFollow.setVisibility(View.GONE);
			llRequestedResponse.setVisibility(View.GONE);
			imgFollowStatus.setVisibility(View.GONE);
		} else {

			flFollow.setVisibility(View.VISIBLE);
			imgFollowStatus.setVisibility(View.VISIBLE);

			boolean friendIsFollowed = HHUser.friendIsFollowed(HHUser.getCurrentUser(), user.getUser());
			boolean friendFollowsMe = HHUser.friendFollowsMe(HHUser.getCurrentUser(), user.getUser());
			boolean friendIsRequested = HHUser.friendIsRequested(HHUser.getCurrentUser(), user.getUser());
			boolean friendRequestedMe = HHUser.friendRequestedMe(HHUser.getCurrentUser(), user.getUser());

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

	}

}
