package com.yosoyo.aaahearhereprototype.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.yosoyo.aaahearhereprototype.AsyncDataManager;
import com.yosoyo.aaahearhereprototype.Fragments.FeedFragment;
import com.yosoyo.aaahearhereprototype.Fragments.FeedbackFragment;
import com.yosoyo.aaahearhereprototype.Fragments.FollowRequestListFragment;
import com.yosoyo.aaahearhereprototype.Fragments.FollowersListFragment;
import com.yosoyo.aaahearhereprototype.Fragments.FragmentChangeRequestListener;
import com.yosoyo.aaahearhereprototype.Fragments.FriendsListFragment;
import com.yosoyo.aaahearhereprototype.Fragments.LoginFragment;
import com.yosoyo.aaahearhereprototype.Fragments.MapViewFragment;
import com.yosoyo.aaahearhereprototype.Fragments.PostFragment;
import com.yosoyo.aaahearhereprototype.Fragments.ProfileFragment;
import com.yosoyo.aaahearhereprototype.Fragments.ProfileMapFragment;
import com.yosoyo.aaahearhereprototype.Fragments.UserSearchFragment;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Database.DatabaseHelper;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPostFull;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;
import com.yosoyo.aaahearhereprototype.LocationService.HHBroadcastReceiver;
import com.yosoyo.aaahearhereprototype.LocationService.LocationListenerService;
import com.yosoyo.aaahearhereprototype.R;
import com.yosoyo.aaahearhereprototype.ZZZUtility;

import java.util.HashMap;
import java.util.Map;

public class HolderActivity extends Activity implements FragmentChangeRequestListener {
	private static final String TAG = "HolderActivity";

	public static final int LOCATION_PERMISSIONS = 16442103;
	public static final String KEY_POSITION = "position";
	public static final String VISIBLE_FRAGMENT = "visible_fragment";
	public static final String REQUEST_CODE = "request_code";
	public static final int REQUEST_CODE_SHOW_POST = 44260420;
	public static final String KEY_NOTIFICATION_POST = TAG + "notification_post";

	public static CallbackManager callbackManager;

	private Map<Integer, Integer> navigationOptions;
	private ListView drawerList;
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;
	private int currentPosition = 0;

	public static boolean apiExists = false;
	public static final MediaPlayer mediaPlayer = new MediaPlayer();

	public static GoogleApiClient mGoogleApiClient;

	private HHBroadcastReceiver broadcastReceiver;

	private static MenuItem menuRequests;

	private FeedbackFragment pendingFragment;

	private class DrawerItemClickListener implements ListView.OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id){
			try {
				//noinspection ConstantConditions
				Fragment fragment = selectItem(ZZZUtility.getKeyByValue(navigationOptions, position));
				if (fragment != null)
					commitFragmentTransaction(fragment, true);
			} catch (NullPointerException e){
				Log.e(TAG, e.getMessage());
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		switch (intent.getAction()){
			case Intent.ACTION_SEND:{

				Log.d(TAG, "NEW INTENT: "+intent.toString());
				String sharedTrackResult = intent.getStringExtra(Intent.EXTRA_TEXT);
				String PREFIX = "https://open.spotify.com/track/";
				String trackID = sharedTrackResult.substring(PREFIX.length());
				Log.d(TAG, "SHARED TRACK: "+trackID);
				Toast.makeText(this, "SHARED TRACK: "+trackID, Toast.LENGTH_LONG).show();

				pendingFragment = PostFragment.newInstance(trackID);

				break;
			}
		}

		FacebookSdk.sdkInitialize(getApplicationContext()); // DO THIS BEFORE SETTING CONTENT VIEW!
		HolderActivity.callbackManager = CallbackManager.Factory.create();
		setContentView(R.layout.activity_holder);

		AsyncDataManager.setContext(this);
		WebHelper.setActivity(this);

		int[] navOptions = new int[]{
			R.string.navigation_option_home,
			R.string.navigation_option_map,
			R.string.navigation_option_profile,
			R.string.action_search_users,
			R.string.navigation_post_test,
			R.string.navigation_option_user_profile,
			R.string.action_create_post,
			R.string.action_user_requests,
			R.string.action_friends
		};

		final int NUM_NAV_STRINGS = 5;
		String[] navStrings = new String[NUM_NAV_STRINGS];
		for (int i = 0; i < NUM_NAV_STRINGS; i++){
			navStrings[i] = getString(navOptions[i]);
		}

		navigationOptions = new HashMap<>();
		for (int i = 0; i < navOptions.length; i++){
			navigationOptions.put(navOptions[i], i);
		}

		drawerList = (ListView) findViewById(R.id.drawer);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		drawerList.setAdapter(
			new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1,
							   navStrings));
		drawerList.setOnItemClickListener(new DrawerItemClickListener());

		if (savedInstanceState == null){
			Fragment fragment = new LoginFragment();
			commitFragmentTransaction(fragment, false);
		} else {
			currentPosition = savedInstanceState.getInt(KEY_POSITION);
			setActionBarTitle(currentPosition);
		}

		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer) {
			@Override
			public void onDrawerClosed(View view){
				super.onDrawerOpened(view);
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View view){
				super.onDrawerOpened(view);
				invalidateOptionsMenu();
			}
		};

		drawerLayout.addDrawerListener(drawerToggle);
		try {
			//noinspection ConstantConditions
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setHomeButtonEnabled(true);
		} catch (NullPointerException e){
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
		drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

		getFragmentManager().addOnBackStackChangedListener(
			new FragmentManager.OnBackStackChangedListener() {
				@Override
				public void onBackStackChanged() {
					FragmentManager fragmentManager = getFragmentManager();
					Fragment fragment = fragmentManager.findFragmentByTag(VISIBLE_FRAGMENT);
					if (fragment instanceof FeedFragment) {
						currentPosition = navigationOptions.get(R.string.navigation_option_home);
					}
					if (fragment instanceof MapViewFragment) {
						currentPosition = navigationOptions.get(R.string.navigation_option_map);
					}
					if (fragment instanceof ProfileFragment) {
						currentPosition = navigationOptions.get(R.string.navigation_option_profile);
					}
					setActionBarTitle(currentPosition);
					drawerList.setItemChecked(currentPosition, true);
				}
			}
		);

		mGoogleApiClient = new GoogleApiClient.Builder(this)
			.addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
				@Override
				public void onConnected(@Nullable Bundle bundle) {
					apiExists = true;
				}

				@Override
				public void onConnectionSuspended(int i) {

				}
			})
			.addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
				@Override
				public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

				}
			})
			.addApi(LocationServices.API)
			.addApi(Places.PLACE_DETECTION_API)
			.addApi(Places.GEO_DATA_API)
			.build();

		if (HHUser.getCurrentUser() != null) {
			if (!isServiceRunning(LocationListenerService.class)) {
				Intent serviceIntent = new Intent(this, LocationListenerService.class);
				serviceIntent.putExtra(LocationListenerService.USER_ID, HHUser.getCurrentUserID());
				startService(serviceIntent);
			}
		}

	}

	private boolean isServiceRunning(Class<?> serviceClass){
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)){
			if (serviceClass.getName().equals(serviceInfo.service.getClassName())){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		if (HHUser.getCurrentUser() != null) {
			getMenuInflater().inflate(R.menu.menu_main, menu);

			MenuItem menuPost = menu.findItem(R.id.action_post);
			menuPost.setShowAsAction(
				MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);

			MenuItem menuRequests = menu.findItem(R.id.action_user_requests);
			menuRequests.setShowAsAction(
				MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
			HolderActivity.menuRequests = menuRequests;
			if (HHUser.getCurrentUser() != null) {
				if (HHUser.getCurrentUser().getFollowInRequests().size() > 0) {
					menuRequests.setIcon(R.drawable.add_user_full);
				}
			}

			MenuItem menuFriensd = menu.findItem(R.id.action_friends);
			menuFriensd.setShowAsAction(
				MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
		}
		return super.onCreateOptionsMenu(menu);
	}

	protected void onStart() {
		mGoogleApiClient.connect();
		super.onStart();
	}

	protected void onStop() {
		mGoogleApiClient.disconnect();
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (broadcastReceiver == null){
			broadcastReceiver = new HHBroadcastReceiver(
				new HHBroadcastReceiver.HHBroadCastReceiverCallback() {
					@Override
					public void returnNewLocation(double lat, double lng) {
						Toast.makeText(HolderActivity.this, "NEW LOCATION: " + lat + " " + lng, Toast.LENGTH_SHORT).show();
					}
				});
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(LocationListenerService.LOCATION_UPDATE);
			registerReceiver(broadcastReceiver, intentFilter);
		}
		AppEventsLogger.activateApp(getApplication());
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(broadcastReceiver);
		broadcastReceiver = null;
	}

	private Fragment selectItem(int resourceID){

		if (navigationOptions.containsKey(resourceID))
			currentPosition = navigationOptions.get(resourceID);

		Fragment fragment;
		switch (resourceID) {
			case R.string.navigation_option_map: {
				fragment = new MapViewFragment();
				break;
			}
			case R.string.navigation_option_profile: {
				fragment = FeedFragment.newInstance(FeedFragment.HOME_PROFILE_FEED, HHUser.getCurrentUserID());
				break;
			}
			case R.string.action_create_post: {
				fragment = new PostFragment();
				break;
			}
			case R.string.action_user_requests: {
				fragment = new FollowRequestListFragment();
				break;
			}
			case R.string.action_friends: {
				fragment = new FriendsListFragment();
				break;
			}
			case R.string.action_search_users: {
				fragment = new UserSearchFragment();
				break;
			}
			case R.string.navigation_post_test: {
				fragment = FeedFragment.newInstance(FeedFragment.SINGLE_POST_FEED, 1, 1);
				break;
			}
			case R.string.navigation_option_home: {
				fragment = FeedFragment.newInstance();
				break;
			}
			default: {
				fragment = new LoginFragment();
			}
		}

		setActionBarTitle(getString(resourceID));
		drawerLayout.closeDrawer(drawerList);

		return fragment;
	}

	private void setActionBarTitle(int position){
		try {
			String title;
			//noinspection ConstantConditions
			title = getString(ZZZUtility.getKeyByValue(navigationOptions, position));
			//noinspection ConstantConditions
			getActionBar().setTitle(title);
		} catch (NullPointerException e){
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
	}

	private void setActionBarTitle(String string){
		try {
			//noinspection ConstantConditions
			getActionBar().setTitle(string);
		} catch (NullPointerException e){
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
	}

	private void commitFragmentTransaction(Fragment fragment, boolean backStack){
		if (drawerToggle != null)
			drawerToggle.setDrawerIndicatorEnabled(false);

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.content_frame, fragment, VISIBLE_FRAGMENT);
		if (backStack)
			ft.addToBackStack(null);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem){
		if (drawerToggle.onOptionsItemSelected(menuItem)){
			return true;
		}
		switch (menuItem.getItemId()){
			case (R.id.action_post):{

				Fragment fragment = selectItem(R.string.action_create_post);
				commitFragmentTransaction(fragment, true);

				return true;
			}
			case (R.id.action_user_requests):{

				Fragment fragment = selectItem(R.string.action_user_requests);
				commitFragmentTransaction(fragment, true);

				return true;
			}
			case (R.id.action_friends):{

				Fragment fragment = selectItem(R.string.action_friends);
				commitFragmentTransaction(fragment, true);

				return true;
			}
			case (R.id.action_reset_database):{
				DatabaseHelper.resetDatabase(this);
				return true;
			}
			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		if (HHUser.getCurrentUser() != null) {
			boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);

			if (currentPosition != navigationOptions.get(R.string.action_search_users)) {
				menu.findItem(R.id.action_post).setVisible(!drawerOpen);
				menu.findItem(R.id.action_friends).setVisible(!drawerOpen);
				menu.findItem(R.id.action_user_requests).setVisible(!drawerOpen);
			}
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState){
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration configuration) {
		super.onConfigurationChanged(configuration);
		drawerToggle.onConfigurationChanged(configuration);
	}

	@Override
	public void onSaveInstanceState(Bundle bundle){
		super.onSaveInstanceState(bundle);
		bundle.putInt(KEY_POSITION, currentPosition);
	}

	public static Location getLastLocation(Activity activity){
		if (activity == null){
			Log.e(TAG, "activity is null?!");
			return null;
		}
		if (ActivityCompat.checkSelfPermission(
			activity,
			Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
			&& ActivityCompat.checkSelfPermission(
			activity,
			Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, HolderActivity.LOCATION_PERMISSIONS);

			return null;
		}
		if (!mGoogleApiClient.isConnected())
			mGoogleApiClient.connect();
		return LocationServices.FusedLocationApi.getLastLocation(HolderActivity.mGoogleApiClient);
	}

	@Override
	public void requestFragmentChange(int fragmentChange, Bundle bundle) {
		Fragment fragment = null;
		switch (fragmentChange){
			case FragmentChangeRequestListener.MAP_VIEW_REQUEST:{
				if (bundle == null){
					fragment = selectItem(R.string.navigation_option_map);
				}
				break;
			}
			case FragmentChangeRequestListener.USER_PROFILE_REQUEST:{
				if (bundle == null){
					fragment = selectItem(R.string.navigation_option_profile);
				} else {
					long userID = bundle.getLong(FeedbackFragment.USER_ID);
					if (userID == HHUser.getCurrentUserID()){
						selectItem(R.string.navigation_option_user_profile);
						fragment = FeedFragment.newInstance(FeedFragment.HOME_PROFILE_FEED, userID);
					} else {
						fragment = FeedFragment.newInstance(FeedFragment.USER_PROFILE_FEED, userID);
					}
				}
				break;
			}
			case FragmentChangeRequestListener.FOLLOWERS_LIST_REQUEST:{
				if (bundle != null){
					long userID = bundle.getLong(FeedbackFragment.USER_ID);
					int followerType = bundle.getInt(FollowersListFragment.FOLLOWER_TYPE);
					fragment = FollowersListFragment.newInstance(followerType, userID);
				}
				break;
			}
		}
		if (fragment != null){
			commitFragmentTransaction(fragment, true);
		}
	}

	@Override
	public void requestProfileModeSwitch(int profileMode, long userID, Bundle bundle) {
		switch (profileMode){
			case ProfileFragment.PROFILE_MODE_FEED:{
				FeedFragment feedFragment;
				feedFragment = FeedFragment.newInstance(bundle);
				feedFragment.setProfileFragmentBundle(bundle);

				commitFragmentTransaction(feedFragment, true);
				break;
			}
			case ProfileFragment.PROFILE_MODE_MAP:{
				ProfileMapFragment profileMapFragment;
				profileMapFragment = ProfileMapFragment.newInstance(bundle);

				commitFragmentTransaction(profileMapFragment, true);
				break;
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		drawerToggle.setDrawerIndicatorEnabled(true);
	}

	@Override
	public void onLoginSuccess(){

		if (!isServiceRunning(LocationListenerService.class)) {
			Intent intent = new Intent(this, LocationListenerService.class);
			intent.putExtra(LocationListenerService.USER_ID, HHUser.getCurrentUserID());
			startService(intent);
		}

		WebHelper.getFacebookProfilePicture(
			Profile.getCurrentProfile().getId(),
			new WebHelper.GetFacebookProfilePictureCallback() {
				@Override
				public void returnFacebookProfilePicture(Bitmap bitmap) {
					HHUser.setProfilePicture(bitmap);
				}
			});

		drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.LEFT);

		if (pendingFragment != null)
			commitFragmentTransaction(pendingFragment, false);
		else
			commitFragmentTransaction(new FeedFragment(), false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		HolderActivity.callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		switch (intent.getAction()){
			case Intent.ACTION_SEND:{

				Log.d(TAG, "NEW INTENT: "+intent.toString());
				String sharedTrackResult = intent.getStringExtra(Intent.EXTRA_TEXT);
				String PREFIX = "https://open.spotify.com/track/";
				String trackID = sharedTrackResult.substring(PREFIX.length());
				Log.d(TAG, "SHARED TRACK: "+trackID);
				Toast.makeText(this, "SHARED TRACK: "+trackID, Toast.LENGTH_LONG).show();

				commitFragmentTransaction(PostFragment.newInstance(trackID), true);

				break;
			}
			case Intent.ACTION_VIEW:{

				Log.d(TAG, "NEW INTENT: "+intent.toString());
				HHPostFull post = intent.getParcelableExtra(KEY_NOTIFICATION_POST);
				if (post != null) {
					commitFragmentTransaction(FeedFragment.newInstance(FeedFragment.SINGLE_POST_FEED,
																	   post.getUser().getID(),
																	   post.getPost().getID()),
											  true);
				}
				break;

			}
		}
	}
}
