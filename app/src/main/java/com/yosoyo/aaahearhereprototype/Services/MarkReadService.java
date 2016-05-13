package com.yosoyo.aaahearhereprototype.Services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import com.yosoyo.aaahearhereprototype.AsyncDataManager;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequestUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHNotification;

/**
 * Created by adam on 12/05/16.
 */
public class MarkReadService extends IntentService {
	private static final String TAG = "MarkReadService";

	public static final String KEY_NOTIFICATION_MARK_READ = TAG + "notification_mark_read";
	public static final String KEY_NOTIFICATION_ACCEPT_FOLLOW_REQUEST = TAG + "notification_accept_follow_request";
	public static final String KEY_NOTIFICATION_NO = TAG + "notification_no";
	public static final String KEY_NOTIFICATION_AUTH_TOKEN = TAG + "notification_auth_token";

	public MarkReadService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		final int notificationID = intent.getIntExtra(KEY_NOTIFICATION_NO, 0);
		final String authToken = intent.getStringExtra(KEY_NOTIFICATION_AUTH_TOKEN);

		if (intent.hasExtra(KEY_NOTIFICATION_MARK_READ)) {
			HHNotification notification = intent.getParcelableExtra(KEY_NOTIFICATION_MARK_READ);

			AsyncDataManager.readNotification(
				authToken,
				notification,
				new AsyncDataManager.ReadNotificationCallback() {
					@Override
					public void returnReadNotification(HHNotification readNotification) {
						if (readNotification != null) {
							NotificationManager notificationManager = (NotificationManager) getSystemService(
								Context.NOTIFICATION_SERVICE);
							notificationManager.cancel(notificationID);
						}
						stopSelf();
					}
				}
			);
		} else if (intent.hasExtra(KEY_NOTIFICATION_ACCEPT_FOLLOW_REQUEST)) {
			HHNotification notification = intent.getParcelableExtra(KEY_NOTIFICATION_ACCEPT_FOLLOW_REQUEST);

			AsyncDataManager.acceptFollowRequest(
				authToken,
				notification,
				new AsyncDataManager.AcceptFollowRequestCallback() {
					@Override
					public void returnAcceptFollowRequest(boolean success, HHFollowRequestUser followRequest) {
						if (success){
							NotificationManager notificationManager = (NotificationManager) getSystemService(
								Context.NOTIFICATION_SERVICE);
							notificationManager.cancel(notificationID);
						}
						stopSelf();
					}
				}
			);
		}
	}

}
