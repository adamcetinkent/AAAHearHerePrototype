package com.yosoyo.aaahearhereprototype.Services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import com.yosoyo.aaahearhereprototype.AsyncDataManager;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHMute;

/**
 * Created by adam on 20/06/16.
 */
public class MutePostService extends IntentService {
	private static final String TAG = "MutePostService";

	public static final String KEY_POST_ID = TAG + "post_id";
	public static final String KEY_NOTIFICATION_NO = TAG + "notification_no";
	public static final String KEY_AUTH_TOKEN = TAG + "notification_auth_token";

	public MutePostService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		final long postID = intent.getLongExtra(KEY_POST_ID, 0);
		final int notification_no = intent.getIntExtra(KEY_NOTIFICATION_NO, 0);
		final String authToken = intent.getStringExtra(KEY_AUTH_TOKEN);

		AsyncDataManager.postMutePost(
			authToken,
			postID,
			new AsyncDataManager.PostMutePostCallback() {
				@Override
				public void returnPostMutePost(boolean success, HHMute returnedMute) {
					if (success){
						NotificationManager notificationManager = (NotificationManager) getSystemService(
							Context.NOTIFICATION_SERVICE);
						notificationManager.cancel(notification_no);
					}
					stopSelf();
				}
			}
		);
	}

}
