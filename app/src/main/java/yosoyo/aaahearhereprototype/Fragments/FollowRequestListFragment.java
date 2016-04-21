package yosoyo.aaahearhereprototype.Fragments;


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
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequestUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUserFull;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;
import yosoyo.aaahearhereprototype.R;
import yosoyo.aaahearhereprototype.ZZZUtility;

/**
 * Created by adam on 18/03/2016.
 *
 * FollowRequestListFragment displays the incoming follow requests of the current user.
 */
public class FollowRequestListFragment extends FeedbackFragment {

	private HHUserFull currentUser;

	private RecyclerView lstRequests;
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager layoutManager;

	public FollowRequestListFragment() {
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
		View view = inflater.inflate(R.layout.fragment_request_follow, container, false);

		lstRequests = (RecyclerView) view.findViewById(R.id.fragment_request_follow_lstRequests);

		layoutManager = new LinearLayoutManager(getActivity());
		lstRequests.setLayoutManager(layoutManager);

		final FollowRequestAdapter.AdapterCallback adapterCallback = new FollowRequestAdapter.AdapterCallback() {
			@Override
			public void requestAccepted(final HHFollowRequestUser followRequest, final int position) {

				currentUser.getFollowInRequests().remove(followRequest);
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
			public void requestDeleted(final HHFollowRequestUser followRequest, final int position) {

				currentUser.getFollowInRequests().remove(followRequest);
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
			public void onUserClick(HHUser user) {
				if (user != null)
					requestUserProfile(user);
			}
		};

		adapter = new FollowRequestAdapter(
			currentUser,
			adapterCallback);

		lstRequests.setAdapter(adapter);

		return view;
	}

	static class FollowRequestAdapter extends RecyclerView.Adapter<FollowRequestAdapter.ViewHolder> {
		private final HHUserFull user;
		private final AdapterCallback adapterCallback;

		interface AdapterCallback {
			void requestAccepted(HHFollowRequestUser followRequest, int position);
			void requestDeleted(HHFollowRequestUser followRequest, int position);
			void onUserClick(HHUser user);
		}

		private abstract static class OnClickFollowRequestListener implements View.OnClickListener {

			public HHFollowRequestUser followRequest;

			public void setFollowRequest(HHFollowRequestUser followRequest) {
				this.followRequest = followRequest;
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
			public final ImageView btnAccept;
			public final ImageView btnDelete;
			public final ProgressBar btnAcceptProgressBar;
			public final ProgressBar btnDeleteProgressBar;
			public final OnClickFollowRequestListener btnAcceptOnClickListener;
			public final OnClickFollowRequestListener btnDeleteOnClickListener;
			public final OnClickUserListener onClickUserListener;
			public int position;
			//private final AdapterCallback adapterCallback;

			public ViewHolder(View view, final AdapterCallback adapterCallback){
				super(view);
				//this.adapterCallback = adapterCallback;
				txtUserName = (TextView) view.findViewById(R.id.rv_row_follow_request_txtUserName);
				imgProfile = (ImageView) view.findViewById(R.id.rv_row_follow_request_imgProfile);
				onClickUserListener = new OnClickUserListener(null, adapterCallback);
				txtUserName.setOnClickListener(onClickUserListener);
				imgProfile.setOnClickListener(onClickUserListener);

				btnAccept = (ImageView) view.findViewById(R.id.rv_row_follow_request_btnAccept);
				btnDelete = (ImageView) view.findViewById(R.id.rv_row_follow_request_btnDelete);
				btnAcceptProgressBar = (ProgressBar) view.findViewById(R.id.rv_row_follow_request_btnAccept_progress);
				btnDeleteProgressBar = (ProgressBar) view.findViewById(R.id.rv_row_follow_request_btnDelete_progress);
				btnAcceptOnClickListener = new OnClickFollowRequestListener(){
					@Override
					public void onClick(View v) {
						btnAccept.setVisibility(View.GONE);
						btnDelete.setColorFilter(ZZZUtility.greyOut);
						btnDelete.setEnabled(false);
						btnAcceptProgressBar.setVisibility(View.VISIBLE);
						AsyncDataManager.acceptFollowRequest(
							this.followRequest,
							new AsyncDataManager.AcceptFollowRequestCallback() {
								@Override
								public void returnAcceptFollowRequest(boolean success, HHFollowRequestUser followRequest) {
									if (success){
										adapterCallback.requestAccepted(followRequest, position);
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
				btnAccept.setOnClickListener(btnAcceptOnClickListener);
				btnDeleteOnClickListener = new OnClickFollowRequestListener() {
					@Override
					public void onClick(View v) {
						btnDelete.setVisibility(View.GONE);
						btnAccept.setColorFilter(ZZZUtility.greyOut);
						btnAccept.setEnabled(false);
						btnDeleteProgressBar.setVisibility(View.VISIBLE);
						AsyncDataManager.deleteFollowRequest(
							this.followRequest,
							new AsyncDataManager.DeleteFollowRequestCallback() {
								@Override
								public void returnDeleteFollowRequest(boolean success, HHFollowRequestUser followRequest) {
									if (success){
										adapterCallback.requestDeleted(followRequest, position);
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

		public FollowRequestAdapter(HHUserFull user, AdapterCallback adapterCallback){
			this.user = user;
			this.adapterCallback = adapterCallback;

			Collections.sort(this.user.getFollowInRequests(), new Comparator<HHFollowRequestUser>() {
				@Override
				public int compare(HHFollowRequestUser lhs, HHFollowRequestUser rhs) {
					return lhs.getFollowRequest().getUpdatedAt().compareTo(rhs.getFollowRequest().getUpdatedAt());
				}
			});
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext())
									  .inflate(R.layout.rv_row_follow_request, parent, false);
			ViewHolder viewHolder = new ViewHolder(view, adapterCallback);

			return viewHolder;
		}

		@Override
		public void onBindViewHolder(final ViewHolder holder, int position) {
			HHFollowRequestUser followRequest = this.user.getFollowInRequests().get(position);
			holder.position = position;
			holder.btnAcceptOnClickListener.setFollowRequest(followRequest);
			holder.btnDeleteOnClickListener.setFollowRequest(followRequest);
			holder.txtUserName.setText(followRequest.getUser().getName());
			holder.onClickUserListener.setUser(followRequest.getUser());

			// get User Image
			WebHelper.getFacebookProfilePicture(
				followRequest.getUser().getFBUserID(),
				new WebHelper.GetFacebookProfilePictureCallback() {
					@Override
					public void returnFacebookProfilePicture(Bitmap bitmap) {
						holder.imgProfile.setImageBitmap(bitmap);
					}
				});
		}

		@Override
		public int getItemCount() {
			return this.user.getFollowInRequests().size();
		}

	}

}
