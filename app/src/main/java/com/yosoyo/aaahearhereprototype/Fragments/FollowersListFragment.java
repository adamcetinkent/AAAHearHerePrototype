package com.yosoyo.aaahearhereprototype.Fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.yosoyo.aaahearhereprototype.ZZZUtility;

import java.util.Collections;
import java.util.Comparator;

/**
 * Created by adam on 29/03/2016.
 *
 * FollowersListFragment displays a list of the followers of a given user
 */
public class FollowersListFragment extends FeedbackFragment {

	private int followerType;
	public static final String FOLLOWER_TYPE = "follower_type";
	public static final int FOLLOWER_TYPE_IN = 0;
	public static final int FOLLOWER_TYPE_OUT = 1;

	private HHUserFull user;
	private HHUserFull currentUser;
	private long userID;

	private ProgressBar progressBar;
	private RecyclerView lstFollowers;
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager layoutManager;

	public static FollowersListFragment newInstance(int followerType, long userID){
		FollowersListFragment followersListFragment = new FollowersListFragment();

		Bundle arguments = new Bundle();
		arguments.putInt(FOLLOWER_TYPE, followerType);
		arguments.putLong(USER_ID, userID);
		followersListFragment.setArguments(arguments);

		return followersListFragment;
	}

	public FollowersListFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		currentUser = HHUser.getCurrentUser();

		Bundle arguments = getArguments();
		if (arguments != null){
			handleArguments(arguments);
		}
	}

	private void handleArguments(Bundle arguments){
		followerType = arguments.getInt(FOLLOWER_TYPE);
		userID = arguments.getLong(USER_ID);

		if (userID == HHUser.getCurrentUserID()){
			user = HHUser.getCurrentUser();
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_followers_list, container, false);

		progressBar = (ProgressBar) view.findViewById(R.id.fragment_followers_list_progressBar);

		lstFollowers = (RecyclerView) view.findViewById(R.id.fragment_followers_list_lstFollowers);

		layoutManager = new LinearLayoutManager(getActivity());
		lstFollowers.setLayoutManager(layoutManager);

		final FollowersListAdapterCallback adapterCallback = new FollowersListAdapterCallback() {
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
				adapter.notifyItemRemoved(position);
				adapter.notifyItemRangeChanged(position, adapter.getItemCount());

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
			public void acceptRequest(final HHFollowRequestUser acceptedFollowRequest, final int position) {
				currentUser.getFollowInRequests().remove(acceptedFollowRequest);
				adapter.notifyItemChanged(position);

				AsyncDataManager.updateCurrentUser(
					new AsyncDataManager.UpdateCurrentUserCallback() {
						@Override
						public void returnUpdateCurrentUser(boolean success) {
							if (success) {
								getActivity().invalidateOptionsMenu();

								for (HHFollowUser follow : HHUser.getCurrentUser().getFollowIns()){
									if (follow.getUser().equals(acceptedFollowRequest.getUser())){
										currentUser.getFollowIns().add(follow);
										adapter.notifyItemChanged(position);
										break;
									}
								}
							}
						}
					}
				);
			}

			@Override
			public void deleteRequest(HHFollowRequestUser deletedFollowRequest, final int position) {
				currentUser.getFollowInRequests().remove(deletedFollowRequest);
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

		if (user != null) {
			adapter = new FollowersListAdapter(
				getActivity(),
				user,
				currentUser,
				followerType,
				adapterCallback);
			lstFollowers.setAdapter(adapter);
			progressBar.setVisibility(View.GONE);
		} else {
			AsyncDataManager.getUser(HHUser.getAuthorisationToken(), userID, true, new AsyncDataManager.GetUserCallback() {
				@Override
				public void returnGetCachedUser(HHUserFull returnedUser) {}

				@Override
				public void returnGetWebUser(HHUserFull returnedUser) {
					progressBar.setVisibility(View.GONE);
					if (returnedUser != null) {
						user = returnedUser;
						adapter = new FollowersListAdapter(
							getActivity(),
							user,
							currentUser,
							followerType,
							adapterCallback);
						lstFollowers.setAdapter(adapter);
					}
				}
			});
		}

		return view;
	}


	private interface FollowersListAdapterCallback {
		void madeFollowRequest(HHFollowRequestUser followRequest, int position);
		void madeFollowRequestAccepted(HHFollowUser follow, int position);
		void deleteFollow(HHFollowUser deletedFollow, int position);
		void acceptRequest(HHFollowRequestUser acceptedFollowRequest, int position);
		void deleteRequest(HHFollowRequestUser deletedFollowRequest, int position);
		void onUserClick(HHUser user);
	}

	private abstract class OnClickFollowUserListener implements View.OnClickListener {

		public HHUser user;

		public void setUser(HHUser user) {
			this.user = user;
		}

	}

	private class ViewHolder extends RecyclerView.ViewHolder{

		private class OnClickUserListener implements View.OnClickListener {

			private HHUser user;
			private final FollowersListAdapterCallback adapterCallback;

			public OnClickUserListener(HHUser user, FollowersListAdapterCallback adapterCallback){
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
		public final OnClickFollowUserListener btnFollowOnClickListener;
		public final OnClickFollowUserListener btnUnfollowOnClickListener;

		public final LinearLayout llRequestResponse;
		public final ImageView btnAccept;
		public final ImageView btnDelete;
		public final ProgressBar btnAcceptProgressBar;
		public final ProgressBar btnDeleteProgressBar;
		public final OnClickFollowUserListener btnAcceptClickListener;
		public final OnClickFollowUserListener btnDeleteOnClickListener;

		public final OnClickUserListener onClickUserListener;
		public int position;
		private final Context context;

		public ViewHolder(final Context context, View view, final FollowersListAdapterCallback adapterCallback){
			super(view);
			this.context = context;
			txtUserName = (TextView) view.findViewById(R.id.rv_row_follower_txtUserName);
			imgProfile = (ImageView) view.findViewById(R.id.rv_row_follower_imgProfile);
			onClickUserListener = new OnClickUserListener(null, adapterCallback);
			txtUserName.setOnClickListener(onClickUserListener);
			imgProfile.setOnClickListener(onClickUserListener);

			imgFollowStatus = (ImageView) view.findViewById(R.id.rv_row_follower_imgFollowStatus);
			btnFollow = (ImageView) view.findViewById(R.id.rv_row_follower_btnFollow);
			btnUnfollow = (ImageView) view.findViewById(R.id.rv_row_follower_btnUnfollow);
			btnFollowProgressBar = (ProgressBar) view.findViewById(R.id.rv_row_follower_btnFollow_progress);
			btnUnfollowProgressBar = (ProgressBar) view.findViewById(R.id.rv_row_follower_btnUnfollow_progress);

			llRequestResponse = (LinearLayout) view.findViewById(R.id.rv_row_follower_llRequestResponse);
			btnAccept = (ImageView) view.findViewById(R.id.rv_row_follower_btnAccept);
			btnDelete = (ImageView) view.findViewById(R.id.rv_row_follower_btnDelete);
			btnAcceptProgressBar = (ProgressBar) view.findViewById(R.id.rv_row_follower_btnAccept_progress);
			btnDeleteProgressBar = (ProgressBar) view.findViewById(R.id.rv_row_follower_btnDelete_progress);

			btnFollowOnClickListener = new OnClickFollowUserListener(){
				@Override
				public void onClick(View v) {
					btnFollow.setVisibility(View.GONE);
					btnFollowProgressBar.setVisibility(View.VISIBLE);
					AsyncDataManager.postFollowRequest(
						new HHFollowRequest(currentUser.getUser().getID(), this.user.getID()),
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

			btnUnfollowOnClickListener = new OnClickFollowUserListener(){
				@Override
				public void onClick(View v) {
					btnUnfollow.setVisibility(View.GONE);
					btnUnfollowProgressBar.setVisibility(View.VISIBLE);
					HHFollowUser deleteFollow = null;
					for (HHFollowUser follow : currentUser.getFollowOuts()){
						if (follow.getUser().equals(this.user)){
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

			btnAcceptClickListener = new OnClickFollowUserListener() {
				@Override
				public void onClick(View v) {
					btnAccept.setVisibility(View.GONE);
					btnDelete.setColorFilter(ZZZUtility.screen(ContextCompat.getColor(ViewHolder.this.context, R.color.adam_theme_darkest)));
					btnDelete.setEnabled(false);
					btnAcceptProgressBar.setVisibility(View.VISIBLE);

					HHFollowRequestUser acceptFollowRequest = null;
					for (HHFollowRequestUser followRequest : currentUser.getFollowInRequests()){
						if (followRequest.getUser().equals(this.user)){
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
								if (success){
									adapterCallback.acceptRequest(followRequest, position);
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
			btnAccept.setOnClickListener(btnAcceptClickListener);

			btnDeleteOnClickListener = new OnClickFollowUserListener() {
				@Override
				public void onClick(View v) {
					btnDelete.setVisibility(View.GONE);
					btnAccept.setColorFilter(ZZZUtility.screen(ContextCompat.getColor(ViewHolder.this.context, R.color.adam_theme_darkest)));
					btnAccept.setEnabled(false);
					btnDeleteProgressBar.setVisibility(View.VISIBLE);

					HHFollowRequestUser deleteFollowRequest = null;
					for (HHFollowRequestUser followRequest : currentUser.getFollowInRequests()){
						if (followRequest.getUser().equals(this.user)){
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
								if (success){
									adapterCallback.deleteRequest(followRequest, position);
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
			btnDelete.setOnClickListener(btnDeleteOnClickListener);
		}

	}

	private class FollowersListAdapter extends RecyclerView.Adapter<ViewHolder> {
		private final Context context;
		private final HHUserFull user;
		private final HHUserFull currentUser;
		private final int FOLLOWER_TYPE;
		private final FollowersListAdapterCallback adapterCallback;

		public FollowersListAdapter(final Context context,
									final HHUserFull user,
									final HHUserFull currentUser,
									final int followerType,
									FollowersListAdapterCallback adapterCallback){
			this.context = context;
			this.user = user;
			this.currentUser = currentUser;
			this.FOLLOWER_TYPE = followerType;
			this.adapterCallback = adapterCallback;

			Collections.sort(
				followerType == FOLLOWER_TYPE_IN ?
					this.user.getFollowIns() :
					this.user.getFollowOuts(),
				new Comparator<HHFollowUser>() {
					@Override
					public int compare(HHFollowUser lhs, HHFollowUser rhs) {
						if (lhs.getUser() == null)
							return 1;
						if (rhs.getUser() == null)
							return -1;

						int lhsScore =
							(HHUser.userRequestedMe(currentUser, lhs.getUser()) ? 1 : 0)
								+ (HHUser.userIsRequested(currentUser, lhs.getUser()) ? 2 : 0)
								+ (HHUser.userFollowsMe(currentUser, lhs.getUser()) ? 4 : 0)
								+ (HHUser.userIsFollowed(currentUser, lhs.getUser()) ? 8 : 0);
						int rhsScore =
							(HHUser.userRequestedMe(currentUser, rhs.getUser()) ? 1 : 0)
								+ (HHUser.userIsRequested(currentUser, rhs.getUser()) ? 2 : 0)
								+ (HHUser.userFollowsMe(currentUser, rhs.getUser()) ? 4 : 0)
								+ (HHUser.userIsFollowed(currentUser, rhs.getUser()) ? 8 : 0);

						if (lhsScore != rhsScore)
							return rhsScore - lhsScore;

						return lhs.getUser().getLastName().compareTo(rhs.getUser().getLastName());
					}
				});
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext())
									  .inflate(R.layout.rv_row_follower, parent, false);
			ViewHolder viewHolder = new ViewHolder(context, view, adapterCallback);

			return viewHolder;
		}

		@Override
		public void onBindViewHolder(final ViewHolder holder, int position) {
			HHFollowUser follow;
			if (FOLLOWER_TYPE == FOLLOWER_TYPE_IN) {
				follow = user.getFollowIns().get(position);
			} else if (FOLLOWER_TYPE == FOLLOWER_TYPE_OUT) {
				follow = user.getFollowOuts().get(position);
			} else {
				return;
			}

			holder.position = position;
			holder.txtUserName.setText(follow.getUser().getName());
			holder.onClickUserListener.setUser(follow.getUser());

			if (follow.getUser().equals(currentUser.getUser())){

				holder.imgFollowStatus.setVisibility(View.INVISIBLE);
				holder.btnFollow.setVisibility(View.INVISIBLE);
				holder.btnUnfollow.setVisibility(View.INVISIBLE);
				holder.llRequestResponse.setVisibility(View.GONE);

			} else {
				holder.btnFollowOnClickListener.setUser(follow.getUser());
				holder.btnUnfollowOnClickListener.setUser(follow.getUser());
				holder.btnAcceptClickListener.setUser(follow.getUser());
				holder.btnDeleteOnClickListener.setUser(follow.getUser());

				boolean friendIsFollowed = HHUser.userIsFollowed(currentUser, follow.getUser());
				boolean friendFollowsMe = HHUser.userFollowsMe(currentUser, follow.getUser());
				boolean friendIsRequested = HHUser.userIsRequested(currentUser, follow.getUser());
				boolean friendRequestedMe = HHUser.userRequestedMe(currentUser, follow.getUser());

				if (friendIsFollowed) {
					holder.btnFollow.setVisibility(View.GONE);
					holder.btnUnfollow.setVisibility(View.VISIBLE);
				} else {
					holder.btnFollow.setVisibility(View.VISIBLE);
					holder.btnFollow.setEnabled(true);
					holder.btnFollow.clearColorFilter();
					holder.btnUnfollow.setVisibility(View.GONE);
				}

				if (friendIsRequested) {
					holder.btnFollow.setColorFilter(ZZZUtility.screen(ContextCompat.getColor(context, R.color.adam_theme_darkest)));
					holder.btnFollow.setEnabled(false);
				}

				if (!friendRequestedMe) {
					holder.llRequestResponse.setVisibility(View.GONE);
				} else {
					holder.llRequestResponse.setVisibility(View.VISIBLE);
				}

				if (friendIsFollowed && friendFollowsMe) {
					holder.imgFollowStatus.setImageResource(R.drawable.follow_in_out);
				} else if (friendIsFollowed) {
					holder.imgFollowStatus.setImageResource(R.drawable.follow_out);
				} else if (friendFollowsMe) {
					holder.imgFollowStatus.setImageResource(R.drawable.follow_in);
				} else {
					holder.imgFollowStatus.setImageResource(R.drawable.follow_none);
				}
				holder.imgFollowStatus.setVisibility(View.VISIBLE);
			}

			// get User Image
			WebHelper.getFacebookProfilePicture(
				follow.getUser().getFBUserID(),
				new WebHelper.GetFacebookProfilePictureCallback() {
					@Override
					public void returnFacebookProfilePicture(Bitmap bitmap) {
						holder.imgProfile.setImageBitmap(bitmap);
					}
				});
		}

		@Override
		public int getItemCount() {
			if (FOLLOWER_TYPE == FOLLOWER_TYPE_IN) {
				return user.getFollowIns().size();
			} else if (FOLLOWER_TYPE == FOLLOWER_TYPE_OUT) {
				return user.getFollowOuts().size();
			} else return 0;
		}

	}

}
