package yosoyo.aaahearhereprototype;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;

import java.net.HttpURLConnection;

import yosoyo.aaahearhereprototype.TestServerClasses.Tasks.TestCreateUserTask;
import yosoyo.aaahearhereprototype.TestServerClasses.Tasks.TestFacebookAuthenticateUserTask;
import yosoyo.aaahearhereprototype.TestServerClasses.TestUser;

public class MainActivity extends /*AppCompatActivity*/ Activity implements FacebookCallback<LoginResult>,
	/*DownloadImageTask.DownloadImageTaskCallback,*/
	TestFacebookAuthenticateUserTask.TestFacebookAuthenticateUserTaskCallback,
	TestCreateUserTask.TestCreateUserTaskCallback,
	View.OnClickListener
{
	private static final String TAG = "MainActivity";

	private CallbackManager callbackManager;
	private TextView facebookSignInName;
	private Button continueButton;
	private Button shortcutButton;
	private ProgressDialog progressDialog;
	private Bitmap profilePicture;
	private TestUser testUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate: started");

		FacebookSdk.sdkInitialize(getApplicationContext()); // DO THIS BEFORE SETTING CONTENT VIEW!
		setContentView(R.layout.activity_main);

		// Set up Action Bar
		//Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		//setSupportActionBar(myToolbar);

		continueButton = (Button) findViewById(R.id.btnContinue);
		continueButton.setOnClickListener(this);

		shortcutButton = (Button) findViewById(R.id.btnShortcut);
		shortcutButton.setOnClickListener(this);

		/* --- FACEBOOK STUFF --- */

		facebookSignInName = (TextView) findViewById(R.id.facebookSignInName);

		callbackManager = CallbackManager.Factory.create();
		LoginButton loginButton = (LoginButton) findViewById(R.id.facebook_login_button);
		loginButton.registerCallback(callbackManager, this);

		AccessTokenTracker accessTokenTracker = new AccessTokenTracker(){
			@Override
			protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken){
				if (newAccessToken == null){
					Log.d(TAG, "logged out");
					updateUI(false);
				} else {
					Log.d(TAG, "logged in");
				}

			}
		};

		if (isLoggedIn()){
			Log.d(TAG, "logged in");
			startHearHereAuthentication();
		}

	}

	private boolean isLoggedIn(){
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		return (accessToken != null && !accessToken.isExpired());
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);

		return true;
	}

	/*	*	*	*	*	*	*	*
	*							*
	* 	FACEBOOK SIGN IN STUFF	*
	*							*
	*	*	*	*	*	*	*	*/

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void returnAuthenticationResult(Integer result, TestUser testUser) {
		if (result == HttpURLConnection.HTTP_OK) {
			this.testUser = testUser;
			facebookSignInSucceeded();
		} else if (result == HttpURLConnection.HTTP_ACCEPTED) {
			Toast.makeText(this, "Creating new user...", Toast.LENGTH_LONG);
			this.testUser = new TestUser(Profile.getCurrentProfile());
			new TestCreateUserTask(this, testUser).execute();
		} else {
			facebookSignInFailed();
		}
	}

	private void facebookSignInFailed(){
		facebookSignInName.setText("LOGIN FAILED");
		Toast.makeText(this, "Facebook Sign In Failed!", Toast.LENGTH_LONG);
		continueButton.setEnabled(false);
		progressDialog.dismiss();
	}

	private void facebookSignInSucceeded() {
		updateUI(true);
		progressDialog.dismiss();

		proceedToHolderActivity();

	}

	private void proceedToHolderActivity(){
		Intent intent = new Intent(getApplicationContext(), HolderActivity.class);

		if (profilePicture != null) {
			byte[] bytes = ZZZUtility.convertBitmapToByteArray(profilePicture);
			intent.putExtra(HolderActivity.PROFILE_PICTURE, bytes);
		}

		intent.putExtra(HolderActivity.TEST_USER, new Gson().toJson(testUser, TestUser.class));

		startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Logs 'install' and 'app activate' App Events.
		AppEventsLogger.activateApp(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Logs 'app deactivate' App Event.
		AppEventsLogger.deactivateApp(this);
	}


	@Override
	public void onSuccess(LoginResult loginResult) {
		startHearHereAuthentication();

		Log.d(TAG, "signed in on Facebook!/n" + loginResult.toString());
	}

	private void startHearHereAuthentication(){
		AccessToken accessToken = AccessToken.getCurrentAccessToken();

		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("Authenticating with Hear Here");
		progressDialog.setMessage("Please wait...");
		progressDialog.setIndeterminate(false);
		progressDialog.setCancelable(false);
		progressDialog.show();

		new TestFacebookAuthenticateUserTask(this, accessToken).execute();
	}

	@Override
	public void onCancel() {
		Log.e(TAG, "cancelled Facebook log in!");
	}

	@Override
	public void onError(FacebookException error) {
		Log.e(TAG, "error in Facebook log in!");
	}

	/*@Override
	public void returnDownloadedImage(Bitmap result, int position, Marker marker) {
		Log.d(TAG, "User image downloaded");
		profilePicture = result;
	}*/

	@Override
	public void returnResultCreateUser(Boolean success, TestUser testUser) {
		facebookSignInSucceeded();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case (R.id.btnContinue): {
				if (continueButton.isEnabled()){
					proceedToHolderActivity();
					break;
				}
			}
			case (R.id.btnShortcut): {
				testUser = new TestUser(Profile.getCurrentProfile());
				proceedToHolderActivity();
				break;
			}
		}
	}

	private void updateUI(boolean loggedIn){
		if (loggedIn){
			Profile profile = Profile.getCurrentProfile();
			facebookSignInName.setText(profile.getFirstName() + " " + profile.getLastName());
			Toast.makeText(this,
						   "Signed in as " + profile.getFirstName() + " " + profile.getLastName(), Toast.LENGTH_LONG);
			continueButton.setEnabled(true);

			ImageView imageView = (ImageView) findViewById(R.id.imgUserImage);
			/*new DownloadImageTask(imageView, this).execute(profile.getProfilePictureUri(200, 200).toString());*/
		} else {
			facebookSignInName.setText("Logged Out");
			Toast.makeText(MainActivity.this, "Logged out of Facebook", Toast.LENGTH_LONG);
			continueButton.setEnabled(false);

			ImageView imageView = (ImageView) findViewById(R.id.imgUserImage);
			imageView.setImageBitmap(null);
		}
	}
}
