package yosoyo.aaahearhereprototype.Fragments;


import android.app.Fragment;
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

import java.util.Collections;
import java.util.Comparator;

import yosoyo.aaahearhereprototype.AsyncDataManager;
import yosoyo.aaahearhereprototype.HHServerClasses.HHFollowRequest;
import yosoyo.aaahearhereprototype.HHServerClasses.HHFollowRequestUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHFollowUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHFriendshipUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUserFull;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;
import yosoyo.aaahearhereprototype.R;
import yosoyo.aaahearhereprototype.ZZZUtility;

/**
 * A simple {@link Fragment} subclass.
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

		FriendsListAdapter.AdapterCallback adapterCallback = new FriendsListAdapter.AdapterCallback() {
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
			public void onUserClick(HHUser user) {
				if (user != null)
					requestUserFeed(user);
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

			public TextView txtUserName;
			public ImageView imgProfile;
			public ImageView imgFollowStatus;
			public ImageView btnFollow;
			public ImageView btnUnfollow;
			public ProgressBar btnFollowProgressBar;
			public OnClickFollowRequestListener btnFollowOnClickListener;
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
									}
									btnFollow.setVisibility(View.VISIBLE);
									btnFollowProgressBar.setVisibility(View.GONE);
								}

								@Override
								public void returnPostFollowRequestAccepted(boolean success,HHFollowUser returnedFollowUser) {

								}
							});
					}
				};
				btnFollow.setOnClickListener(btnFollowOnClickListener);
			}
		}

		public FriendsListAdapter(HHUserFull user, AdapterCallback adapterCallback){
			this.user = user;
			this.adapterCallback = adapterCallback;

			Collections.sort(this.user.getFriendships(), new Comparator<HHFriendshipUser>() {
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
			holder.txtUserName.setText(friendship.getUser().getName());
			holder.onClickUserListener.setUser(friendship.getUser());

			boolean friendIsFollowed = friendIsFollowed(friendship.getUser());
			boolean friendFollowsMe = friendFollowsMe(friendship.getUser());
			boolean friendIsRequested = friendIsRequested(friendship.getUser());
			boolean friendRequestedMe = friendRequestedMe(friendship.getUser());

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

		private boolean friendIsFollowed(HHUser friend){
			for (HHFollowUser follow : user.getFollowOuts()){
				if (follow.getUser().equals(friend)){
					return true;
				}
			}
			return false;
		}

		private boolean friendFollowsMe(HHUser friend){
			for (HHFollowUser follow : user.getFollowIns()){
				if (follow.getUser().equals(friend)){
					return true;
				}
			}
			return false;
		}

		private boolean friendIsRequested(HHUser friend){
			for (HHFollowRequestUser follow : user.getFollowOutRequests()){
				if (follow.getUser().equals(friend)){
					return true;
				}
			}
			return false;
		}

		private boolean friendRequestedMe(HHUser friend){
			for (HHFollowRequestUser follow : user.getFollowInRequests()){
				if (follow.getUser().equals(friend)){
					return true;
				}
			}
			return false;
		}

		@Override
		public int getItemCount() {
			return user.getFriendships().size();
		}

	}

}
