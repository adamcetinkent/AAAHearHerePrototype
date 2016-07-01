package com.yosoyo.aaahearhereprototype.Fragments;


import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yosoyo.aaahearhereprototype.AsyncDataManager;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHCachedSpotifyTrack;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequestUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHNotification;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;
import com.yosoyo.aaahearhereprototype.R;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;
import com.yosoyo.aaahearhereprototype.ZZZUtility;

import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsListFragment extends FeedbackFragment {

	private ProgressBar progressBar;
	private RecyclerView lstNotifications;
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager layoutManager;

	public NotificationsListFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_notifications_list, container, false);

		progressBar = (ProgressBar) view.findViewById(R.id.fragment_notifications_list_progressBar);

		lstNotifications = (RecyclerView) view.findViewById(R.id.fragment_notifications_list_lstNotifications);

		layoutManager = new LinearLayoutManager(getActivity());
		lstNotifications.setLayoutManager(layoutManager);

		final NotificationsListAdapterCallback adapterCallback = new NotificationsListAdapterCallback() {
			@Override
			public void acceptRequest(HHFollowRequestUser acceptedFollowRequest, final int position) {
				AsyncDataManager.updateCurrentUser(
					new AsyncDataManager.UpdateCurrentUserCallback() {
						@Override
						public void returnUpdateCurrentUser(boolean success) {
							if (success) {
								getActivity().invalidateOptionsMenu();
								adapter.notifyItemChanged(position);
							}
						}
					}
				);
			}

			@Override
			public void deleteRequest(HHFollowRequestUser deletedFollowRequest, final int position) {
				AsyncDataManager.updateCurrentUser(
					new AsyncDataManager.UpdateCurrentUserCallback() {
						@Override
						public void returnUpdateCurrentUser(boolean success) {
							if (success) {
								getActivity().invalidateOptionsMenu();
								adapter.notifyItemChanged(position);
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

			@Override
			public void onPostClick(HHNotification notification) {
				if (notification != null) {
					requestPostFromNotification(notification);
				}
			}
		};

		AsyncDataManager.getNotifications(false, new AsyncDataManager.GetNotificationsCallback() {
			@Override
			public void returnGetNotifications(List<HHNotification> notifications) {
				adapter = new NotificationsListAdapter(getActivity(), notifications, adapterCallback);
				lstNotifications.setAdapter(adapter);
				progressBar.setVisibility(View.GONE);
			}
		});

		return view;
	}

	private interface NotificationsListAdapterCallback {
		//void madeFollowRequest(HHFollowRequestUser followRequest, int position);
		//void madeFollowRequestAccepted(HHFollowUser follow, int position);
		//void deleteFollow(HHFollowUser deletedFollow, int position);
		void acceptRequest(HHFollowRequestUser acceptedFollowRequest, int position);
		void deleteRequest(HHFollowRequestUser deletedFollowRequest, int position);
		void onUserClick(HHUser user);
		void onPostClick(HHNotification notification);
	}


	private abstract class OnClickUserListener implements View.OnClickListener {
		public HHUser user;
		public void setUser(HHUser user) {
			this.user = user;
		}
	}

	private abstract class OnClickNotificationListener implements View.OnClickListener {
		public HHNotification notification;
		public void setNotification(HHNotification notification) {
			this.notification = notification;
		}
	}

	private abstract class ViewHolderNotification extends RecyclerView.ViewHolder{

		protected int position;
		protected Context context;
		protected TextView txtNotification;
		protected TextView txtDate;
		protected ImageView imgProfile;
		protected ImageView imgNew;
		protected OnClickUserListener onClickUserListener;
		protected NotificationsListAdapterCallback adapterCallback;

		public ViewHolderNotification(final Context context,
									  final View view,
									  final NotificationsListAdapterCallback adapterCallback) {
			super(view);
			this.context = context;
			this.txtNotification = (TextView) view.findViewById(R.id.list_row_notification_txtNotification);
			this.txtDate = (TextView) view.findViewById(R.id.list_row_notification_txtDate);
			this.imgProfile = (ImageView) view.findViewById(R.id.list_row_notification_imgProfile);
			this.imgNew = (ImageView) view.findViewById(R.id.list_row_notification_imgNew);
			this.adapterCallback = adapterCallback;
		}
	}

	private class ViewHolderPost extends ViewHolderNotification {

		private ImageView imgAlbumArt;
		private OnClickNotificationListener onClickNotificationListener;

		public ViewHolderPost(final Context context,
							  final View view,
							  final NotificationsListAdapterCallback adapterCallback) {
			super(context, view, adapterCallback);
			this.imgAlbumArt = (ImageView) view.findViewById(R.id.list_row_notification_post_imgAlbumArt);
			this.onClickUserListener = new OnClickUserListener() {
				@Override
				public void onClick(View v) {
					adapterCallback.onUserClick(this.user);
				}
			};
			imgProfile.setOnClickListener(onClickUserListener);
			this.onClickNotificationListener = new OnClickNotificationListener() {
				@Override
				public void onClick(View v) {
					adapterCallback.onPostClick(notification);
				}
			};
			view.setOnClickListener(onClickNotificationListener);
		}

	}

	private class ViewHolderFollowRequest extends ViewHolderNotification {

		private LinearLayout llButtons;
		private ImageView btnAccept;
		private ImageView btnDelete;
		private ProgressBar btnAcceptProgressBar;
		private ProgressBar btnDeleteProgressBar;
		private OnClickUserListener btnAcceptClickListener;
		private OnClickUserListener btnDeleteClickListener;

		public ViewHolderFollowRequest(final Context context,
									   final View view,
									   final NotificationsListAdapterCallback adapterCallback) {
			super(context, view, adapterCallback);
			this.llButtons = (LinearLayout) view.findViewById(R.id.list_row_notification_follow_request_llButtons);
			this.btnAccept = (ImageView) view.findViewById(R.id.list_row_notification_follow_request_btnAccept);
			this.btnDelete = (ImageView) view.findViewById(R.id.list_row_notification_follow_request_btnDelete);
			this.btnAcceptProgressBar = (ProgressBar) view.findViewById(R.id.list_row_notification_follow_request_btnAccept_progress);
			this.btnDeleteProgressBar = (ProgressBar) view.findViewById(R.id.list_row_notification_follow_request_btnDelete_progress);

			this.onClickUserListener = new OnClickUserListener() {
				@Override
				public void onClick(View v) {
					adapterCallback.onUserClick(this.user);
				}
			};
			view.setOnClickListener(onClickUserListener);

			this.btnAcceptClickListener = new OnClickUserListener() {
				@Override
				public void onClick(View v) {
					btnAccept.setVisibility(View.GONE);
					btnDelete.setColorFilter(ZZZUtility.screen(
						ContextCompat.getColor(ViewHolderFollowRequest.this.context, R.color.adam_theme_darkest)));
					btnDelete.setEnabled(false);
					btnAcceptProgressBar.setVisibility(View.VISIBLE);

					HHFollowRequestUser acceptFollowRequest = null;
					for (HHFollowRequestUser followRequest : HHUser.getCurrentUser().getFollowInRequests()){
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

			btnDeleteClickListener = new OnClickUserListener() {
				@Override
				public void onClick(View v) {
					btnDelete.setVisibility(View.GONE);
					btnAccept.setColorFilter(ZZZUtility.screen(ContextCompat.getColor(ViewHolderFollowRequest.this.context, R.color.adam_theme_darkest)));
					btnAccept.setEnabled(false);
					btnDeleteProgressBar.setVisibility(View.VISIBLE);

					HHFollowRequestUser deleteFollowRequest = null;
					for (HHFollowRequestUser followRequest : HHUser.getCurrentUser().getFollowInRequests()){
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

					AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AdamDialog));
					final HHFollowRequestUser finalDeleteFollowRequest = deleteFollowRequest;
					builder.setTitle("Delete Follow Request")
						   .setMessage(String.format(Locale.ENGLISH,
													 "Are you sure you want to delete the follow request from %1$s?",
													 deleteFollowRequest.getUser().getName()))
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
									   finalDeleteFollowRequest,
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

					int titleDividerID = getResources().getIdentifier("titleDivider", "id", "android");
					View titleDivider = dialog.findViewById(titleDividerID);
					if (titleDivider != null){
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
							titleDivider.setBackgroundColor(getResources().getColor(R.color.adam_theme_base, null));
						} else {
							titleDivider.setBackgroundColor(getResources().getColor(R.color.adam_theme_base));
						}
					}

				}
			};
			btnDelete.setOnClickListener(btnDeleteClickListener);
		}

	}

	private class NotificationsListAdapter extends RecyclerView.Adapter<ViewHolderNotification> {
		private final Context context;
		private final List<HHNotification> notifications;
		private final NotificationsListAdapterCallback callback;

		private NotificationsListAdapter(final Context context,
										 final List<HHNotification> notifications,
										 final NotificationsListAdapterCallback callback) {
			this.context = context;
			this.notifications = notifications;
			this.callback = callback;
		}

		@Override
		public ViewHolderNotification onCreateViewHolder(ViewGroup parent, int viewType) {

			switch (viewType){
				case HHNotification.NOTIFICATION_TYPE_NEW_FOLLOW:
				case HHNotification.NOTIFICATION_TYPE_NEW_FOLLOW_REQUEST:{
					View view = LayoutInflater.from(parent.getContext())
											  .inflate(R.layout.list_row_notification_follow_request, parent, false);
					return new ViewHolderFollowRequest(context, view, callback);
				}
				case HHNotification.NOTIFICATION_TYPE_NEW_COMMENT:
				case HHNotification.NOTIFICATION_TYPE_LIKE_POST:
				case HHNotification.NOTIFICATION_TYPE_NEW_POST:
				default:{
					View view = LayoutInflater.from(parent.getContext())
											  .inflate(R.layout.list_row_notification_post, parent, false);
					return new ViewHolderPost(context, view, callback);
				}
			}
		}

		@Override
		public void onBindViewHolder(final ViewHolderNotification holder, final int position) {
			holder.position = position;
			final HHNotification notification = notifications.get(position);
			holder.txtNotification.setText(notification.getNotificationText());
			holder.txtDate.setText(ZZZUtility.formatDynamicDate(notification.getCreatedAt()));
			holder.onClickUserListener.setUser(notification.getByUser());

			WebHelper.getFacebookProfilePicture(
				notification.getByUser().getFBUserID(),
				new WebHelper.GetFacebookProfilePictureCallback() {
					@Override
					public void returnFacebookProfilePicture(Bitmap bitmap) {
						holder.imgProfile.setImageBitmap(bitmap);
					}
				});

			if (notification.getReadAt() == null && !notification.isNewlyRead()){
				holder.imgNew.setVisibility(View.VISIBLE);
				AsyncDataManager.readNotification(
					HHUser.getAuthorisationToken(),
					notification,
					new AsyncDataManager.ReadNotificationCallback() {
						@Override
						public void returnReadNotification(HHNotification readNotification) {
							if (readNotification != null) {
								readNotification.setNewlyRead(true);
								ZZZUtility.updateList(notifications, readNotification);

								final float[] HSVfrom = new float[3];
								final float[] HSVto = new float[3];

								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
									Color.colorToHSV(getResources().getColor(R.color.adam_theme_base, null), HSVfrom);
									Color.colorToHSV(getResources().getColor(R.color.adam_theme_darkest, null), HSVto);
								} else {
									Color.colorToHSV(getResources().getColor(R.color.adam_theme_base), HSVfrom);
									Color.colorToHSV(getResources().getColor(R.color.adam_theme_darkest), HSVto);
								}

								ValueAnimator animator = ValueAnimator.ofFloat(0,1);
								animator.setDuration(5000);

								final float[] HSVcurrent = new float[3];

								animator.addUpdateListener(
									new ValueAnimator.AnimatorUpdateListener() {
										@Override
										public void onAnimationUpdate(ValueAnimator animation) {
											HSVcurrent[0] = HSVfrom[0] + (HSVto[0]-HSVfrom[0])*animation.getAnimatedFraction();
											HSVcurrent[1] = HSVfrom[1] + (HSVto[1]-HSVfrom[1])*animation.getAnimatedFraction();
											HSVcurrent[2] = HSVfrom[2] + (HSVto[2]-HSVfrom[2])*animation.getAnimatedFraction();

											holder.imgNew.setColorFilter(Color.HSVToColor(HSVcurrent));
										}
									});

								animator.start();

							}
						}
					});
			} else if (notification.isNewlyRead()){
				holder.imgNew.setVisibility(View.VISIBLE);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					holder.imgNew.setColorFilter(getResources().getColor(R.color.adam_theme_darkest, null));
				} else {
					holder.imgNew.setColorFilter(getResources().getColor(R.color.adam_theme_darkest));
				}
			} else {
				holder.imgNew.setVisibility(View.INVISIBLE);
			}

			switch (notification.getNotificationType()){
				case HHNotification.NOTIFICATION_TYPE_NEW_COMMENT:
					holder.txtNotification.setText(
						Html.fromHtml(
							String.format(Locale.ENGLISH,
										  "<font color=\"#e8aa49\"><b>%1$s</b></font> commented on your post",
										  notification.getByUser().getName())
						)
					);
				case HHNotification.NOTIFICATION_TYPE_LIKE_POST: {
					if (notification.getNotificationType() == HHNotification.NOTIFICATION_TYPE_LIKE_POST) {
						holder.txtNotification.setText(
							Html.fromHtml(
								String.format(Locale.ENGLISH,
											  "<font color=\"#e8aa49\"><b>%1$s</b></font> liked your post",
											  notification.getByUser().getName())
							)
						);
					}
				}
				case HHNotification.NOTIFICATION_TYPE_NEW_POST:{

					final ViewHolderPost holderPost = (ViewHolderPost) holder;
					holderPost.onClickNotificationListener.setNotification(notification);

					AsyncDataManager.getSpotifyTrack(
						notification.getPost().getTrack(),
						new AsyncDataManager.GetSpotifyTrackCallback() {
							@Override
							public void returnSpotifyTrack(SpotifyTrack spotifyTrack) {}

							@Override
							public void returnCachedSpotifyTrack(HHCachedSpotifyTrack cachedSpotifyTrack) {
								WebHelper.getSpotifyAlbumArt(
									cachedSpotifyTrack,
									new WebHelper.GetSpotifyAlbumArtCallback() {
										@Override
										public void returnSpotifyAlbumArt(Bitmap bitmap) {
											holderPost.imgAlbumArt.setImageBitmap(bitmap);
										}
									});
								if (notification.getNotificationType() == HHNotification.NOTIFICATION_TYPE_NEW_POST) {
									holder.txtNotification.setText(
										Html.fromHtml(
											String.format(Locale.ENGLISH,
														  "<font color=\"#e8aa49\"><b>%1$s</b></font> posted <font color=\"#e8aa49\"><b>%2$s</b></font> at <font color=\"#e8aa49\"><b>%3$s</b></font>",
														  notification.getByUser().getName(),
														  cachedSpotifyTrack.getName(),
														  notification.getPost().getPlaceName())
										)
									);
								}
							}
						});
					break;
				}
				case HHNotification.NOTIFICATION_TYPE_NEW_FOLLOW :{

					final ViewHolderFollowRequest holderFollow = (ViewHolderFollowRequest) holder;

					holderFollow.llButtons.setVisibility(View.GONE);

					holder.txtNotification.setText(
						Html.fromHtml(
							String.format(Locale.ENGLISH,
										  "<font color=\"#e8aa49\"><b>%1$s</b></font> started following you",
										  notification.getByUser().getName())
						)
					);

					break;
				}
				case HHNotification.NOTIFICATION_TYPE_NEW_FOLLOW_REQUEST:{

					ViewHolderFollowRequest holderFollowRequest = ((ViewHolderFollowRequest) holder);

					holderFollowRequest.llButtons.setVisibility(View.VISIBLE);
					holderFollowRequest.btnAccept.setVisibility(View.VISIBLE);
					holderFollowRequest.btnDelete.setVisibility(View.VISIBLE);
					holderFollowRequest.btnAccept.clearColorFilter();
					holderFollowRequest.btnDelete.clearColorFilter();

					holderFollowRequest.btnAcceptClickListener.setUser(notification.getByUser());
					holderFollowRequest.btnDeleteClickListener.setUser(notification.getByUser());
					boolean requestMissing = true;
					for (HHFollowRequestUser followRequest : HHUser.getCurrentUser().getFollowInRequests()){
						if (followRequest.getUser().equals(notification.getByUser())){
							requestMissing = false;
							break;
						}
					}
					if (requestMissing) {

						boolean requestAccepted = false;

						for (HHFollowUser followUser : HHUser.getCurrentUser().getFollowIns()){
							if (followUser.getUser().equals(notification.getByUser())){
								requestAccepted = true;
								break;
							}
						}

						if (requestAccepted) {
							holderFollowRequest.llButtons.setVisibility(View.GONE);
							holder.txtNotification.setText(
								Html.fromHtml(
									String.format(Locale.ENGLISH,
												  "You accepted a follow request from <font color=\"#e8aa49\"><b>%1$s</b></font>",
												  notification.getByUser().getName())
								)
							);
						} else {
							holderFollowRequest.llButtons.setVisibility(View.GONE);
							holder.txtNotification.setText(
								Html.fromHtml(
									String.format(Locale.ENGLISH,
												  "You rejected a follow request from <font color=\"#e8aa49\"><b>%1$s</b></font>",
												  notification.getByUser().getName())
								)
							);
						}
					} else {
						holder.txtNotification.setText(
							Html.fromHtml(
								String.format(Locale.ENGLISH,
											  "<font color=\"#e8aa49\"><b>%1$s</b></font> sent you a follow request",
											  notification.getByUser().getName())
							)
						);
					}

					break;
				}
			}

		}

		@Override
		public int getItemCount() {
			if (notifications != null)
				return notifications.size();
			return 0;
		}

		@Override
		public int getItemViewType(int position) {
			if (notifications != null)
				return notifications.get(position).getNotificationType();
			return 0;
		}
	}

}
