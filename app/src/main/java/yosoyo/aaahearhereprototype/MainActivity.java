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
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
	private static final String tag = "MainActivity";

	private GoogleApiClient mGoogleApiClient;
	private TextView signInName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(tag, "onCreate: started");

		setContentView(R.layout.activity_main);

		// Set up Action Bar
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

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

		findViewById(R.id.sign_in_button).setOnClickListener(this);
		signInName = (TextView) findViewById(R.id.signInName);

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
		Log.e(tag, "Connection failed!");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.sign_in_button:
				Log.d(tag, "Sign in button pressed!");
				signIn();
				break;
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
	}

	private void handleSignInResult(GoogleSignInResult result) {
		Log.d(tag, "handleSignInResult:" + result.isSuccess());
		if (result.isSuccess()) {
			// Signed in successfully, show authenticated UI.
			GoogleSignInAccount acct = result.getSignInAccount();
			signInName.setText(acct.getDisplayName());
			//mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
			//updateUI(true);
		} else {
			// Signed out, show unauthenticated UI.
			//updateUI(false);
			signInName.setText("LOGIN FAILED");
		}
	}

}
