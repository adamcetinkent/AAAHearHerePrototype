package yosoyo.aaahearhereprototype.Fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
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

import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.AsyncDataManager;
import yosoyo.aaahearhereprototype.HHServerClasses.HHFollowRequest;
import yosoyo.aaahearhereprototype.HHServerClasses.HHFollowRequestUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHFollowUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUserFull;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;
import yosoyo.aaahearhereprototype.R;
import yosoyo.aaahearhereprototype.ZZZUtility;

/**
 * A simple {@link Fragment} subclass.
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
		inflater.inflate(R.menu.menu_search, menu);
		MenuItem item = menu.findItem(R.id.action_search);
		SearchView searchView = new SearchView(getActivity().getActionBar().getThemedContext());
		MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		MenuItemCompat.setActionView(item, searchView);
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				Log.d(TAG, "search query submitted");
				AsyncDataManager.searchUsers(
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
					requestUserFeed(user);
			}
		};

		adapter = new UserSearchListAdapter(
			currentUser,
			new ArrayList<HHUser>(),
			adapterCallback);
		lstUsers.setAdapter(adapter);

		return view;
	}

	static class UserSearchListAdapter extends RecyclerView.Adapter<UserSearchListAdapter.ViewHolder> {
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

			public TextView txtUserName;
			public ImageView imgProfile;
			public ImageView imgFollowStatus;
			public ImageView btnFollow;
			public ImageView btnUnfollow;
			public ProgressBar btnFollowProgressBar;
			public ProgressBar btnUnfollowProgressBar;
			public OnClickFollowRequestListener btnFollowOnClickListener;
			public OnClickFollowRequestListener btnUnfollowOnClickListener;
			public OnClickUserListener onClickUserListener;
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

		public UserSearchListAdapter(HHUserFull currentUser, List<HHUser> foundUsers, AdapterCallback adapterCallback){
			this.currentUser = currentUser;
			this.foundUsers = foundUsers;
			this.adapterCallback = adapterCallback;

			/*Collections.sort(this.currentUser.getFriendships(), new Comparator<HHFriendshipUser>() {
				@Override
				public int compare(HHFriendshipUser lhs, HHFriendshipUser rhs) {
					int lhsScore =
						(friendRequestedMe(lhs.getUser()) ? 1 : 0)
							+ (friendIsRequested(lhs.getUser()) ? 2 : 0)
							+ (friendFollowsMe(lhs.getUser()) ? 4 : 0)
							+ (friendIsFollowed(lhs.getUser()) ? 8 : 0);
					int rhsScore =
						(friendRequestedMe(rhs.getUser()) ? 1 : 0)
							+ (friendIsRequested(rhs.getUser()) ? 2 : 0)
							+ (friendFollowsMe(rhs.getUser()) ? 4 : 0)
							+ (friendIsFollowed(rhs.getUser()) ? 8 : 0);

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
			ViewHolder viewHolder = new ViewHolder(view, adapterCallback);

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

			boolean friendIsFollowed = friendIsFollowed(foundUser);
			boolean friendFollowsMe = friendFollowsMe(foundUser);
			boolean friendIsRequested = friendIsRequested(foundUser);
			boolean friendRequestedMe = friendRequestedMe(foundUser);

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
				foundUser.getFBUserID(),
				new WebHelper.GetFacebookProfilePictureCallback() {
					@Override
					public void returnFacebookProfilePicture(Bitmap bitmap) {
						holder.imgProfile.setImageBitmap(bitmap);
					}
				});
		}

		private boolean friendIsFollowed(HHUser friend){
			for (HHFollowUser follow : currentUser.getFollowOuts()){
				if (follow.getUser().equals(friend)){
					return true;
				}
			}
			return false;
		}

		private boolean friendFollowsMe(HHUser friend){
			for (HHFollowUser follow : currentUser.getFollowIns()){
				if (follow.getUser().equals(friend)){
					return true;
				}
			}
			return false;
		}

		private boolean friendIsRequested(HHUser friend){
			for (HHFollowRequestUser follow : currentUser.getFollowOutRequests()){
				if (follow.getUser().equals(friend)){
					return true;
				}
			}
			return false;
		}

		private boolean friendRequestedMe(HHUser friend){
			for (HHFollowRequestUser follow : currentUser.getFollowInRequests()){
				if (follow.getUser().equals(friend)){
					return true;
				}
			}
			return false;
		}

		@Override
		public int getItemCount() {
			return foundUsers.size();
		}

	}

}
