package com.yosoyo.aaahearhereprototype.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.yosoyo.aaahearhereprototype.Activities.HolderActivity;
import com.yosoyo.aaahearhereprototype.AsyncDataManager;
import com.yosoyo.aaahearhereprototype.R;

/**
 * Created by adam on 22/04/16.
 *
 * Handles the Facebook login to the app and authorisation with Hear Here server
 */
public class LoginFragment extends FeedbackFragment {

	private static final String TAG = "LoginFragment";

	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_login, container, false);

		LoginButton loginButton = (LoginButton) view.findViewById(R.id.facebook_login_button);
		loginButton.setReadPermissions("user_friends");
		loginButton.registerCallback(HolderActivity.callbackManager, new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {
				startHearHereAuthentication();

				Log.d(TAG, "signed in on Facebook!/n" + loginResult.toString());
			}

			@Override
			public void onCancel() {
				Log.e(TAG, "cancelled Facebook log in!");
			}

			@Override
			public void onError(FacebookException error) {
				Log.e(TAG, "error in Facebook log in!");
			}
		});

		@SuppressWarnings("UnusedAssignment")
		AccessTokenTracker accessTokenTracker = new AccessTokenTracker(){
			@Override
			protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken){
				if (newAccessToken == null){
					Log.d(TAG, "logged out");

				} else {
					Log.d(TAG, "logged in");
				}

			}
		};

		if (isLoggedIn()){
			Log.d(TAG, "logged in");
			startHearHereAuthentication();
		}

		return view;
	}

	private boolean isLoggedIn(){
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		return (accessToken != null && !accessToken.isExpired());
	}

	private void facebookSignInFailed(){
		//facebookSignInName.setText(R.string.placeholder_login_failed);
		//Toast.makeText(this, "Facebook Sign In Failed!", Toast.LENGTH_LONG).show();
		//continueButton.setEnabled(false);
		progressDialog.dismiss();
	}

	private void facebookSignInSucceeded() {

		AsyncDataManager.spotifyAPIRequestToken(
			new AsyncDataManager.spotifyAPIRequestTokenCallback() {
				@Override
				public void returnSpotifyToken(boolean success) {
					if (success) {
						progressDialog.dismiss();
						onLoginSuccess();
					} else {
						progressDialog.dismiss();
					}
				}
			});


	}

	private void startHearHereAuthentication(){
		AccessToken accessToken = AccessToken.getCurrentAccessToken();

		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setTitle("Authenticating with Hear Here");
		progressDialog.setMessage("Please wait...");
		progressDialog.setIndeterminate(false);
		progressDialog.setCancelable(false);
		progressDialog.show();

		AsyncDataManager.authenticateUser(
			accessToken,
			new AsyncDataManager.AuthenticateUserCallback() {
				@Override
				public void returnAuthenticationResult(boolean success) {
					if (success) {
						facebookSignInSucceeded();
					} else {
						facebookSignInFailed();
					}
				}
			});
	}
}
