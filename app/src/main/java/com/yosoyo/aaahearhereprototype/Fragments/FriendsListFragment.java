package com.yosoyo.aaahearhereprototype.Fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yosoyo.aaahearhereprototype.AsyncDataManager;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequest;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequestUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFriendshipUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUserFull;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;
import com.yosoyo.aaahearhereprototype.R;
import com.yosoyo.aaahearhereprototype.ZZZUtility;

import java.util.Collections;
import java.util.Comparator;

/**
 * Created by adam on 18/03/2016.
 *
 * FriendsListFragment displays a list of a user's friends
 */
public class FriendsListFragment extends FeedbackFragment {

	private HHUserFull currentUser;

	private RecyclerView lstFriends;
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager layoutManager;

	public FriendsListFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		currentUser = HHUser.getCurrentUser();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_friends_list, container, false);

		lstFriends = (RecyclerView) view.findViewById(R.id.fragment_friends_list_lstFriends);

		layoutManager = new LinearLayoutManager(getActivity());
		lstFriends.setLayoutManager(layoutManager);

		final FriendsListAdapter.AdapterCallback adapterCallback = new FriendsListAdapter.AdapterCallback() {
			@Override
			public void madeFollowRequest(HHFollowRequestUser followRequest, int position) {
				currentUser.getFollowOutRequests().add(followRequest);
				adapter.notifyItemChanged(position);

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
			}

			@Override
			public void madeFollowRequestAccepted(HHFollowUser follow, int position) {
				currentUser.getFollowOuts().add(follow);
				adapter.notifyItemChanged(position);

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
			}

			@Override
			public void deleteFollow(HHFollowUser deletedFollow, int position) {
				currentUser.getFollowOuts().remove(deletedFollow);
				adapter.notifyItemChanged(position);

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
			}

			@Override
			public void onUserClick(HHUser user) {
				if (user != null)
					requestUserProfile(user);
			}
		};

		adapter = new FriendsListAdapter(
			currentUser,
			adapterCallback);
		lstFriends.setAdapter(adapter);

		return view;
	}

	static class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder> {
		private static HHUserFull user;
		private final AdapterCallback adapterCallback;

		interface AdapterCallback {
			void madeFollowRequest(HHFollowRequestUser followRequest, int position);
			void madeFollowRequestAccepted(HHFollowUser follow, int position);
			void deleteFollow(HHFollowUser deletedFollow, int position);
			void onUserClick(HHUser user);
		}

		private abstract static class OnClickFollowRequestListener implements View.OnClickListener {

			public HHFriendshipUser friendship;

			public void setFriendship(HHFriendshipUser friendship) {
				this.friendship = friendship;
			}

		}

		static class ViewHolder extends RecyclerView.ViewHolder{

			private class OnClickUserListener implements View.OnClickListener {

				private HHUser user;
				private final AdapterCallback adapterCallback;

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

			public final TextView txtUserName;
			public final ImageView imgProfile;
			public final ImageView imgFollowStatus;
			public final ImageView btnFollow;
			public final ImageView btnUnfollow;
			public final ProgressBar btnFollowProgressBar;
			public final ProgressBar btnUnfollowProgressBar;
			public final OnClickFollowRequestListener btnFollowOnClickListener;
			public final OnClickFollowRequestListener btnUnfollowOnClickListener;
			public final OnClickUserListener onClickUserListener;
			public int position;
			//private final AdapterCallback adapterCallback;

			public ViewHolder(View view, final AdapterCallback adapterCallback){
				super(view);
				//this.adapterCallback = adapterCallback;
				txtUserName = (TextView) view.findViewById(R.id.rv_row_friendship_txtUserName);
				imgProfile = (ImageView) view.findViewById(R.id.rv_row_friendship_imgProfile);
				onClickUserListener = new OnClickUserListener(null, adapterCallback);
				txtUserName.setOnClickListener(onClickUserListener);
				imgProfile.setOnClickListener(onClickUserListener);

				imgFollowStatus = (ImageView) view.findViewById(R.id.rv_row_friendship_imgFollowStatus);
				btnFollow = (ImageView) view.findViewById(R.id.rv_row_friendship_btnFollow);
				btnUnfollow = (ImageView) view.findViewById(R.id.rv_row_friendship_btnUnfollow);
				btnFollowProgressBar = (ProgressBar) view.findViewById(R.id.rv_row_friendship_btnFollow_progress);
				btnUnfollowProgressBar = (ProgressBar) view.findViewById(R.id.rv_row_friendship_btnUnfollow_progress);
				btnFollowOnClickListener = new OnClickFollowRequestListener(){
					@Override
					public void onClick(View v) {
						btnFollow.setVisibility(View.GONE);
						btnFollowProgressBar.setVisibility(View.VISIBLE);
						AsyncDataManager.postFollowRequest(
							new HHFollowRequest(user.getUser().getID(), this.friendship.getUser().getID()),
							new AsyncDataManager.PostFollowRequestCallback() {
								@Override
								public void returnPostFollowRequest(boolean success, HHFollowRequestUser returnedFollowRequest) {
									if (success) {
										adapterCallback.madeFollowRequest(returnedFollowRequest, position);
									} else {
										btnFollow.setVisibility(View.VISIBLE);
									}
									btnFollowProgressBar.setVisibility(View.GONE);
								}

								@Override
								public void returnPostFollowRequestAccepted(boolean success,HHFollowUser returnedFollowUser) {
									if (success){
										adapterCallback.madeFollowRequestAccepted(returnedFollowUser, position);
										btnUnfollow.setVisibility(View.VISIBLE);
									} else {
										btnFollow.setVisibility(View.VISIBLE);
									}
									btnFollowProgressBar.setVisibility(View.GONE);
								}
							});
					}
				};
				btnFollow.setOnClickListener(btnFollowOnClickListener);

				btnUnfollowOnClickListener = new OnClickFollowRequestListener(){
					@Override
					public void onClick(View v) {
						btnUnfollow.setVisibility(View.GONE);
						btnUnfollowProgressBar.setVisibility(View.VISIBLE);
						HHFollowUser deleteFollow = null;
						for (HHFollowUser follow : user.getFollowOuts()){
							if (follow.getUser().equals(this.friendship.getUser())){
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
									if (success) {
										adapterCallback.deleteFollow(deletedFollow, position);
										btnFollow.setVisibility(View.VISIBLE);
									} else {
										btnUnfollow.setVisibility(View.VISIBLE);
									}
									btnUnfollowProgressBar.setVisibility(View.GONE);
								}
							});
					}
				};
				btnUnfollow.setOnClickListener(btnUnfollowOnClickListener);
			}
		}

		public FriendsListAdapter(final HHUserFull user, AdapterCallback adapterCallback){
			FriendsListAdapter.user = user;
			this.adapterCallback = adapterCallback;

			Collections.sort(FriendsListAdapter.user.getFriendships(), new Comparator<HHFriendshipUser>() {
				@Override
				public int compare(HHFriendshipUser lhs, HHFriendshipUser rhs) {
					int lhsScore =
						(HHUser.userRequestedMe(user, lhs.getUser()) ? 1 : 0)
						+ (HHUser.userIsRequested(user, lhs.getUser()) ? 2 : 0)
						+ (HHUser.userFollowsMe(user, lhs.getUser()) ? 4 : 0)
						+ (HHUser.userIsFollowed(user, lhs.getUser()) ? 8 : 0);
					int rhsScore =
						(HHUser.userRequestedMe(user, rhs.getUser()) ? 1 : 0)
							+ (HHUser.userIsRequested(user, rhs.getUser()) ? 2 : 0)
							+ (HHUser.userFollowsMe(user, rhs.getUser()) ? 4 : 0)
							+ (HHUser.userIsFollowed(user, rhs.getUser()) ? 8 : 0);

					if (lhsScore != rhsScore)
						return rhsScore - lhsScore;

					return lhs.getUser().getLastName().compareTo(rhs.getUser().getLastName());
				}
			});
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext())
									  .inflate(R.layout.rv_row_friend, parent, false);
			ViewHolder viewHolder = new ViewHolder(view, adapterCallback);

			return viewHolder;
		}

		@Override
		public void onBindViewHolder(final ViewHolder holder, int position) {
			HHFriendshipUser friendship = user.getFriendships().get(position);
			holder.position = position;
			holder.btnFollowOnClickListener.setFriendship(friendship);
			holder.btnUnfollowOnClickListener.setFriendship(friendship);
			holder.txtUserName.setText(friendship.getUser().getName());
			holder.onClickUserListener.setUser(friendship.getUser());

			boolean friendIsFollowed = HHUser.userIsFollowed(user, friendship.getUser());
			boolean friendFollowsMe = HHUser.userFollowsMe(user, friendship.getUser());
			boolean friendIsRequested = HHUser.userIsRequested(user, friendship.getUser());
			//boolean userRequestedMe = HHUser.userRequestedMe(user, friendship.getUser());

			if (friendIsFollowed){
				holder.btnFollow.setVisibility(View.GONE);
				holder.btnUnfollow.setVisibility(View.VISIBLE);
			} else {
				holder.btnFollow.setVisibility(View.VISIBLE);
				holder.btnFollow.setEnabled(true);
				holder.btnFollow.clearColorFilter();
				holder.btnUnfollow.setVisibility(View.GONE);
			}

			if (friendIsRequested){
				holder.btnFollow.setColorFilter(ZZZUtility.greyOut);
				holder.btnFollow.setEnabled(false);
			}

			if (friendIsFollowed && friendFollowsMe){
				holder.imgFollowStatus.setImageResource(R.drawable.follow_in_out);
			} else if (friendIsFollowed){
				holder.imgFollowStatus.setImageResource(R.drawable.follow_out);
			} else if (friendFollowsMe){
				holder.imgFollowStatus.setImageResource(R.drawable.follow_in);
			} else {
				holder.imgFollowStatus.setImageResource(R.drawable.follow_none);
			}

			// get User Image
			WebHelper.getFacebookProfilePicture(
				friendship.getUser().getFBUserID(),
				new WebHelper.GetFacebookProfilePictureCallback() {
					@Override
					public void returnFacebookProfilePicture(Bitmap bitmap) {
						holder.imgProfile.setImageBitmap(bitmap);
					}
				});
		}

		@Override
		public int getItemCount() {
			return user.getFriendships().size();
		}

	}

}
