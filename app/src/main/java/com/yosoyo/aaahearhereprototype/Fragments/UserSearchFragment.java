package com.yosoyo.aaahearhereprototype.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adam on 18/03/2016.
 *
 * UserSearchFragment provides a search bar and recycler view to display results.
 */
public class UserSearchFragment extends FeedbackFragment {
	public static final String TAG = "UserSearchFragment";

	private HHUserFull currentUser;

	private RecyclerView lstUsers;
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager layoutManager;

	public UserSearchFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		currentUser = HHUser.getCurrentUser();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem item = menu.findItem(R.id.action_post);
		item.setVisible(false);
		item = menu.findItem(R.id.action_friends);
		item.setVisible(false);
		item = menu.findItem(R.id.action_user_requests);
		item.setVisible(false);

		inflater.inflate(R.menu.menu_search, menu);
		item = menu.findItem(R.id.action_search);
		//noinspection ConstantConditions
		SearchView searchView = new SearchView(getActivity().getActionBar().getThemedContext());
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		item.setActionView(searchView);
		searchView.setIconified(false);
		searchView.requestFocusFromTouch();
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				Log.d(TAG, "search query submitted");
				AsyncDataManager.searchUsers(
					HHUser.getAuthorisationToken(),
					query,
					new AsyncDataManager.SearchUsersCallback() {
						@Override
						public void returnSearchUsers(String query, List<HHUser> foundUsers) {
							UserSearchListAdapter.setFoundUsers(foundUsers);
							adapter.notifyDataSetChanged();
						}
					}
				);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				//Log.d(TAG, "search query changed");
				return false;
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_user_search, container, false);

		lstUsers = (RecyclerView) view.findViewById(R.id.fragment_search_users_lstUsers);

		layoutManager = new LinearLayoutManager(getActivity());
		lstUsers.setLayoutManager(layoutManager);

		final UserSearchListAdapter.AdapterCallback adapterCallback = new UserSearchListAdapter.AdapterCallback() {
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

		adapter = new UserSearchListAdapter(
			getActivity(),
			currentUser,
			new ArrayList<HHUser>(),
			adapterCallback);
		lstUsers.setAdapter(adapter);

		return view;
	}

	static class UserSearchListAdapter extends RecyclerView.Adapter<UserSearchListAdapter.ViewHolder> {
		private final Context context;
		private static HHUserFull currentUser;

		public static void setFoundUsers(List<HHUser> foundUsers) {
			UserSearchListAdapter.foundUsers = foundUsers;
		}

		private static List<HHUser> foundUsers;
		private final AdapterCallback adapterCallback;

		interface AdapterCallback {
			void madeFollowRequest(HHFollowRequestUser followRequest, int position);
			void madeFollowRequestAccepted(HHFollowUser follow, int position);
			void deleteFollow(HHFollowUser deletedFollow, int position);
			void onUserClick(HHUser user);
		}

		private abstract static class OnClickFollowRequestListener implements View.OnClickListener {

			public HHUser foundUser;

			public void setFoundUser(HHUser foundUser) {
				this.foundUser = foundUser;
			}

		}

		static class ViewHolder extends RecyclerView.ViewHolder{

			private class OnClickUserListener implements View.OnClickListener {

				private HHUser foundUser;
				private final AdapterCallback adapterCallback;

				public OnClickUserListener(HHUser foundUser, AdapterCallback adapterCallback){
					this.foundUser = foundUser;
					this.adapterCallback = adapterCallback;
				}

				public void setFoundUser(HHUser foundUser) {
					this.foundUser = foundUser;
				}

				@Override
				public void onClick(View v) {
					adapterCallback.onUserClick(foundUser);
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
			private final Context context;
			//private final AdapterCallback adapterCallback;

			public ViewHolder(Context context, View view, final AdapterCallback adapterCallback){
				super(view);
				this.context = context;
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
							new HHFollowRequest(currentUser.getUser().getID(),
												this.foundUser.getID()),
							new AsyncDataManager.PostFollowRequestCallback() {
								@Override
								public void returnPostFollowRequest(boolean success, HHFollowRequestUser returnedFollowRequest) {
									if (success) {
										adapterCallback
											.madeFollowRequest(returnedFollowRequest, position);
									} else {
										btnFollow.setVisibility(View.VISIBLE);
									}
									btnFollowProgressBar.setVisibility(View.GONE);
								}

								@Override
								public void returnPostFollowRequestAccepted(boolean success, HHFollowUser returnedFollowUser) {
									if (success) {
										adapterCallback
											.madeFollowRequestAccepted(returnedFollowUser,
																	   position);
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
						for (HHFollowUser follow : currentUser.getFollowOuts()){
							if (follow.getUser().equals(this.foundUser)){
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

		public UserSearchListAdapter(Context context, HHUserFull currentUser, List<HHUser> foundUsers, AdapterCallback adapterCallback){
			this.context = context;
			UserSearchListAdapter.currentUser = currentUser;
			UserSearchListAdapter.foundUsers = foundUsers;
			this.adapterCallback = adapterCallback;

			/*Collections.sort(this.currentUser.getFriendships(), new Comparator<HHFriendshipUser>() {
				@Override
				public int compare(HHFriendshipUser lhs, HHFriendshipUser rhs) {
					int lhsScore =
						(userRequestedMe(lhs.getUser()) ? 1 : 0)
							+ (userIsRequested(lhs.getUser()) ? 2 : 0)
							+ (userFollowsMe(lhs.getUser()) ? 4 : 0)
							+ (userIsFollowed(lhs.getUser()) ? 8 : 0);
					int rhsScore =
						(userRequestedMe(rhs.getUser()) ? 1 : 0)
							+ (userIsRequested(rhs.getUser()) ? 2 : 0)
							+ (userFollowsMe(rhs.getUser()) ? 4 : 0)
							+ (userIsFollowed(rhs.getUser()) ? 8 : 0);

					if (lhsScore != rhsScore)
						return rhsScore - lhsScore;

					return lhs.getUser().getLastName().compareTo(rhs.getUser().getLastName());
				}
			});*/
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext())
									  .inflate(R.layout.rv_row_friend, parent, false);
			ViewHolder viewHolder = new ViewHolder(context, view, adapterCallback);

			return viewHolder;
		}

		@Override
		public void onBindViewHolder(final ViewHolder holder, int position) {
			HHUser foundUser = foundUsers.get(position);
			holder.position = position;
			holder.btnFollowOnClickListener.setFoundUser(foundUser);
			holder.btnUnfollowOnClickListener.setFoundUser(foundUser);
			holder.txtUserName.setText(foundUser.getName());
			holder.onClickUserListener.setFoundUser(foundUser);

			boolean friendIsFollowed = HHUser.userIsFollowed(currentUser, foundUser);
			boolean friendFollowsMe = HHUser.userFollowsMe(currentUser, foundUser);
			boolean friendIsRequested = HHUser.userIsRequested(currentUser, foundUser);
			//boolean userRequestedMe = HHUser.userRequestedMe(currentUser, foundUser);

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
				holder.btnFollow.setColorFilter(ZZZUtility.screen(ContextCompat.getColor(context, R.color.adam_theme_darkest)));
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
				foundUser.getFBUserID(),
				new WebHelper.GetFacebookProfilePictureCallback() {
					@Override
					public void returnFacebookProfilePicture(Bitmap bitmap) {
						holder.imgProfile.setImageBitmap(bitmap);
					}
				});
		}

		@Override
		public int getItemCount() {
			return foundUsers.size();
		}

	}

}
