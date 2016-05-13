package com.yosoyo.aaahearhereprototype;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import com.yosoyo.aaahearhereprototype.Activities.HolderActivity;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHNotification;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;
import com.yosoyo.aaahearhereprototype.Services.MarkReadService;

import java.util.List;

/**
 * Created by adam on 12/05/16.
 */
public class HHNotificationsManager {

	private final Context context;
	private final NotificationManager notificationManager;
	private final String authToken;
	private final String titleString;

	public HHNotificationsManager(Context context,
								  NotificationManager notificationManager,
								  String authToken,
								  String titleString) {
		this.context = context;
		this.notificationManager = notificationManager;
		this.authToken = authToken;
		this.titleString = titleString;
	}

	public AsyncDataManager.GetNotificationsCallback getNotificationsCallback = new AsyncDataManager.GetNotificationsCallback() {

		@Override
		public void returnGetNotifications(List<HHNotification> notifications) {
			if (notifications != null && !notifications.isEmpty()){
				for (final HHNotification notification : notifications){
					WebHelper.getFacebookProfilePicture(
						notification.getByFacebookUserID(),
						new WebHelper.GetFacebookProfilePictureCallback() {
							@Override
							public void returnFacebookProfilePicture(Bitmap bitmap) {
								int notificationID = (int) (notification.getNotificationType() * 10000000 + notification.getID());
								switch (notification.getNotificationType()) {

									case HHNotification.NOTIFICATION_TYPE_NEW_POST:
									case HHNotification.NOTIFICATION_TYPE_LIKE_POST:
									case HHNotification.NOTIFICATION_TYPE_NEW_COMMENT: {

										Intent intentSeePost = new Intent(
											context,
											HolderActivity.class
										);
										intentSeePost.setAction(Intent.ACTION_VIEW);
										intentSeePost.putExtra(
											HolderActivity.KEY_NOTIFICATION_VIEW_POST,
											notification);
										PendingIntent pendingIntentSeePost = PendingIntent
											.getActivity(
												context,
												HolderActivity.REQUEST_CODE_SHOW_POST_FROM_ID,
												intentSeePost,
												PendingIntent.FLAG_UPDATE_CURRENT
											);

										Notification.Action.Builder actionBuilder = null;

										Notification.Builder notificationBuilder = new Notification.Builder(context)
											.setSmallIcon(R.drawable.app_white_icon)
											.setLargeIcon(bitmap)
											.setContentTitle(titleString)
											.setContentText(notification.getNotificationText())
											.setAutoCancel(true)
											.setDefaults(Notification.DEFAULT_VIBRATE)
											.setContentIntent(pendingIntentSeePost);

										if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
											notificationBuilder
												.setCategory(Notification.CATEGORY_SOCIAL)
												.setColor(
													ContextCompat.getColor(
														context,
														R.color.adam_theme_base));
										}

										Notification postNotification = null;
										Notification.Action actionMarkRead = null;
										if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

											Intent intentMarkRead = new Intent(
												context,
												MarkReadService.class
											);
											intentMarkRead.putExtra(
												MarkReadService.KEY_NOTIFICATION_MARK_READ,
												notification);
											intentMarkRead.putExtra(
												MarkReadService.KEY_NOTIFICATION_NO,
												notificationID);
											intentMarkRead.putExtra(
												MarkReadService.KEY_NOTIFICATION_AUTH_TOKEN,
												authToken);
											PendingIntent pendingIntentMarkRead = PendingIntent
												.getService(
													context,
													notificationID,
													intentMarkRead,
													PendingIntent.FLAG_UPDATE_CURRENT
												);

											actionBuilder = new Notification.Action.Builder(
												Icon.createWithResource(context, R.drawable.cross),
												"Mark as Read",
												pendingIntentMarkRead
											);
											actionMarkRead = actionBuilder.build();
											notificationBuilder.addAction(actionMarkRead);
										}
										if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
											postNotification = notificationBuilder.build();
										} else {
											postNotification = notificationBuilder.getNotification();
										}

										notificationManager.notify((int) notificationID, postNotification);
										break;
									}

									case HHNotification.NOTIFICATION_TYPE_NEW_FOLLOW_REQUEST:
									case HHNotification.NOTIFICATION_TYPE_NEW_FOLLOW:{

										Intent intentSeePost = new Intent(
											context,
											HolderActivity.class
										);
										intentSeePost.setAction(Intent.ACTION_VIEW);
										intentSeePost.putExtra(
											HolderActivity.KEY_NOTIFICATION_VIEW_USER,
											notification);
										PendingIntent pendingIntentSeePost = PendingIntent
											.getActivity(
												context,
												HolderActivity.REQUEST_CODE_SHOW_USER_FROM_ID,
												intentSeePost,
												PendingIntent.FLAG_UPDATE_CURRENT
											);

										Notification.Action.Builder actionBuilder = null;

										Notification.Builder notificationBuilder = new Notification.Builder(context)
											.setSmallIcon(R.drawable.app_white_icon)
											.setLargeIcon(bitmap)
											.setContentTitle(titleString)
											.setContentText(notification.getNotificationText())
											.setAutoCancel(true)
											.setDefaults(Notification.DEFAULT_VIBRATE)
											.setContentIntent(pendingIntentSeePost);

										if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
											notificationBuilder
												.setCategory(Notification.CATEGORY_SOCIAL)
												.setColor(
													ContextCompat.getColor(
														context,
														R.color.adam_theme_base));
										}

										Notification postNotification = null;
										if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

											Intent intentMarkRead = new Intent(
												context,
												MarkReadService.class
											);
											intentMarkRead.putExtra(
												MarkReadService.KEY_NOTIFICATION_MARK_READ,
												notification);
											intentMarkRead.putExtra(
												MarkReadService.KEY_NOTIFICATION_NO,
												notificationID);
											intentMarkRead.putExtra(
												MarkReadService.KEY_NOTIFICATION_AUTH_TOKEN,
												authToken);
											PendingIntent pendingIntentMarkRead = PendingIntent
												.getService(
													context,
													notificationID,
													intentMarkRead,
													PendingIntent.FLAG_UPDATE_CURRENT
												);

											actionBuilder = new Notification.Action.Builder(
												Icon.createWithResource(context, R.drawable.cross),
												"Mark as Read",
												pendingIntentMarkRead
											);
											Notification.Action actionMarkRead = actionBuilder.build();
											notificationBuilder.addAction(actionMarkRead);

											if (notification.getNotificationType() == HHNotification.NOTIFICATION_TYPE_NEW_FOLLOW_REQUEST) {
												Intent intentAcceptFollow = new Intent(
													context,
													MarkReadService.class
												);
												intentAcceptFollow.putExtra(
													MarkReadService.KEY_NOTIFICATION_ACCEPT_FOLLOW_REQUEST,
													notification);
												intentAcceptFollow.putExtra(
													MarkReadService.KEY_NOTIFICATION_NO,
													notificationID);
												intentAcceptFollow.putExtra(
													MarkReadService.KEY_NOTIFICATION_AUTH_TOKEN,
													authToken);
												PendingIntent pendingIntentAcceptFollow = PendingIntent
													.getService(
														context,
														notificationID * 2,
														intentAcceptFollow,
														PendingIntent.FLAG_UPDATE_CURRENT
													);

												actionBuilder = new Notification.Action.Builder(
													Icon.createWithResource(context,
																			R.drawable.tick),
													"Accept",
													pendingIntentAcceptFollow
												);
												Notification.Action actionAcceptFollow = actionBuilder
													.build();
												notificationBuilder.addAction(actionAcceptFollow);
											}

										}
										if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
											postNotification = notificationBuilder.build();
										} else {
											postNotification = notificationBuilder.getNotification();
										}

										notificationManager.notify((int) notificationID, postNotification);

										break;
									}
								}
							}
						}
					);
				}
			}
		}
	};

}
