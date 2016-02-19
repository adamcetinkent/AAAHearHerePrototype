package yosoyo.aaahearhereprototype;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import yosoyo.aaahearhereprototype.TestServerClasses.TestGetUserTask;
import yosoyo.aaahearhereprototype.TestServerClasses.TestUser;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, FacebookCallback<LoginResult>, TestGetUserTask.TestGetUserTaskCallback {
	private static final String TAG = "MainActivity";

	private GoogleApiClient mGoogleApiClient;
	private CallbackManager callbackManager;
	private ProfileTracker profileTracker;
	private TextView googleSignInName;
	private TextView facebookSignInName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate: started");

		FacebookSdk.sdkInitialize(getApplicationContext()); // DO THIS BEFORE SETTING CONTENT VIEW!
		setContentView(R.layout.activity_main);

		// Set up Action Bar
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		/* --- GOOGLE STUFF --- */

		// Configure sign-in to request the user's ID, email address, and basic
		// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
			.requestEmail()
			.build();

		// Build a GoogleApiClient with access to the Google Sign-In API and the
		// options specified by gso.
		mGoogleApiClient = new GoogleApiClient.Builder(this)
			.enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
			.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
			.build();

		findViewById(R.id.google_sign_in_button).setOnClickListener(this);
		googleSignInName = (TextView) findViewById(R.id.googleSignInName);

		/* --- FACEBOOK STUFF --- */

		facebookSignInName = (TextView) findViewById(R.id.facebookSignInName);

		callbackManager = CallbackManager.Factory.create();
		LoginButton loginButton = (LoginButton) findViewById(R.id.facebook_login_button);
		loginButton.registerCallback(callbackManager, this);

		profileTracker = new ProfileTracker() {
			@Override
			protected void onCurrentProfileChanged(
				Profile oldProfile,
				Profile currentProfile) {
				Log.d(TAG, "PROFILE CHANGE:: ");

				if (oldProfile != null) {
					Log.d(TAG, "\t+ oldProfile: " + oldProfile.getName());
				} else {
					Log.d(TAG, "\t+ oldProfile: ---");
				}

				if (currentProfile != null) {
					Log.d(TAG, "\t+ currentProfile: " + currentProfile.getName());
					facebookSignInName.setText("FACEBOOK: " + currentProfile.getName());
				} else {
					Log.d(TAG, "\t+ currentProfile: ---");
					facebookSignInName.setText("FACEBOOK SIGN IN");
				}

			}
		};


		/* RUBY STUFF */

		Button button = (Button) findViewById(R.id.btnVMTestUser);
		button.setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);

		// Set up SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(
			menu.findItem(R.id.search));

		ComponentName componentName = new ComponentName(this, SearchResultsActivity.class);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));

		return true;
	}

	/*	*	*	*	*	*	*	*
	*							*
	* 	GOOGLE SIGN IN STUFF	*
	*							*
	*	*	*	*	*	*	*	*/

	private static final int RC_SIGN_IN = 9001;

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.e(TAG, "Connection failed!");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

			case R.id.google_sign_in_button: {
				Log.d(TAG, "Sign in button pressed!");
				signIn();
				break;
			}

			case R.id.btnVMTestUser: {
				Log.d(TAG, "Get user from VM Server!");

				EditText editText = (EditText) findViewById(R.id.txtVMUserID);
				long id = Long.parseLong(editText.getText().toString());

				TestGetUserTask testGetUserTask = new TestGetUserTask(this, id);
				testGetUserTask.execute();

			}

		}
	}

	private void signIn() {
		Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
		startActivityForResult(signInIntent, RC_SIGN_IN);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
		if (requestCode == RC_SIGN_IN) {
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			handleSignInResult(result);
		}

		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	private void handleSignInResult(GoogleSignInResult result) {
		Log.d(TAG, "handleSignInResult:" + result.isSuccess());
		if (result.isSuccess()) {
			// Signed in successfully, show authenticated UI.
			GoogleSignInAccount acct = result.getSignInAccount();
			googleSignInName.setText("GOOGLE: " + acct.getDisplayName());
			//mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
			//updateUI(true);
		} else {
			// Signed out, show unauthenticated UI.
			//updateUI(false);
			googleSignInName.setText("LOGIN FAILED");
		}
	}

	/*	*	*	*	*	*	*	*
	*							*
	* 	FACEBOOK SIGN IN STUFF	*
	*							*
	*	*	*	*	*	*	*	*/

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

	@Override
	public void processFinish(TestUser testUser) {
		TextView textView = (TextView) findViewById(R.id.txtVMUserTestReturn);
		if (testUser != null){
			textView.setText("User: " + testUser.getFirst_name() + " " + testUser.getLast_name());
		} else {
			textView.setText("Failed to get user");
		}

	}
}
