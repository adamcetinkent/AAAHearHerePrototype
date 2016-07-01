package com.yosoyo.aaahearhereprototype.Fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yosoyo.aaahearhereprototype.AsyncDataManager;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequestUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUserFull;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;
import com.yosoyo.aaahearhereprototype.R;
import com.yosoyo.aaahearhereprototype.ZZZUtility;

import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

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
			getActivity(),
			currentUser,
			adapterCallback);

		lstRequests.setAdapter(adapter);

		return view;
	}

	static class FollowRequestAdapter extends RecyclerView.Adapter<FollowRequestAdapter.ViewHolder> {
		private final Context context;
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
			private final Context context;
			//private final AdapterCallback adapterCallback;

			public ViewHolder(final Context context, View view, final AdapterCallback adapterCallback){
				super(view);
				this.context = context;
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
						btnDelete.setColorFilter(ZZZUtility.screen(ContextCompat.getColor(ViewHolder.this.context, R.color.adam_theme_darkest)));
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
						btnAccept.setColorFilter(ZZZUtility.screen(ContextCompat.getColor(ViewHolder.this.context, R.color.adam_theme_darkest)));
						btnAccept.setEnabled(false);
						btnDeleteProgressBar.setVisibility(View.VISIBLE);

						AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AdamDialog));
						builder.setTitle("Delete Follow Request")
							   .setMessage(String.format(Locale.ENGLISH,
														 "Are you sure you want to delete the follow request from %1$s?",
														 followRequest.getUser().getName()))
							   .setOnCancelListener(new DialogInterface.OnCancelListener() {
								   @Override
								   public void onCancel(DialogInterface dialog) {
									   btnDelete.setVisibility(View.VISIBLE);
									   btnDeleteProgressBar.setVisibility(View.GONE);
									   btnAccept.clearColorFilter();
									   btnAccept.setEnabled(true);
								   }
							   })
							   .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
								   @Override
								   public void onClick(final DialogInterface dialog, int which) {

									   AsyncDataManager.deleteFollowRequest(
										   followRequest,
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
												   dialog.dismiss();
											   }
										   });
								   }
							   })
							   .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								   @Override
								   public void onClick(DialogInterface dialog, int which) {
									   dialog.cancel();
								   }
							   });

						AlertDialog dialog = builder.create();
						dialog.show();

						int titleDividerID = context.getResources().getIdentifier("titleDivider", "id", "android");
						View titleDivider = dialog.findViewById(titleDividerID);
						if (titleDivider != null){
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
								titleDivider.setBackgroundColor(context.getResources().getColor(R.color.adam_theme_base, null));
							} else {
								titleDivider.setBackgroundColor(context.getResources().getColor(R.color.adam_theme_base));
							}
						}

					}
				};
				btnDelete.setOnClickListener(btnDeleteOnClickListener);
			}
		}

		public FollowRequestAdapter(Context context, HHUserFull user, AdapterCallback adapterCallback){
			this.context = context;
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
			return new ViewHolder(context, view, adapterCallback);
		}

		@Override
		public void onBindViewHolder(final ViewHolder holder, int position) {
			HHFollowRequestUser followRequest = this.user.getFollowInRequests().get(position);
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
