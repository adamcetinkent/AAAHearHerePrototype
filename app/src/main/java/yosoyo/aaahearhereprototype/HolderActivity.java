package yosoyo.aaahearhereprototype;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import java.util.HashMap;
import java.util.Map;

import yosoyo.aaahearhereprototype.Fragments.FeedFragment;
import yosoyo.aaahearhereprototype.Fragments.FragmentChangeRequestListener;
import yosoyo.aaahearhereprototype.Fragments.FriendsListFragment;
import yosoyo.aaahearhereprototype.Fragments.MapViewFragment;
import yosoyo.aaahearhereprototype.Fragments.PostFragment;
import yosoyo.aaahearhereprototype.Fragments.ProfileFragment;
import yosoyo.aaahearhereprototype.Fragments.RequestFollowFragment;
import yosoyo.aaahearhereprototype.Fragments.UserSearchFragment;
import yosoyo.aaahearhereprototype.HHServerClasses.Database.DatabaseHelper;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUser;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;
import yosoyo.aaahearhereprototype.LocationService.HHBroadcastReceiver;
import yosoyo.aaahearhereprototype.LocationService.LocationListenerService;

public class HolderActivity extends Activity implements FragmentChangeRequestListener {

	public static final int LOCATION_PERMISSIONS = 16442103;
	public static final String KEY_POSITION = "position";
	public static final String VISIBLE_FRAGMENT = "visible_fragment";
	public static final String REQUEST_CODE = "request_code";

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

	private class DrawerItemClickListener implements ListView.OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id){
			Fragment fragment = selectItem(ZZZUtility.getKeyByValue(navigationOptions, position));
			if (fragment != null)
				commitFragmentTransaction(fragment, true);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_holder);

		AsyncDataManager.setContext(this);

		WebHelper.getFacebookProfilePicture(
			Profile.getCurrentProfile().getId(),
			new WebHelper.GetFacebookProfilePictureCallback() {
				@Override
				public void returnFacebookProfilePicture(Bitmap bitmap) {
					HHUser.setProfilePicture(bitmap);
				}
			});

		int[] navOptions = new int[]{
			R.string.navigation_option_home,
			R.string.navigation_option_map,
			R.string.navigation_option_profile,
			R.string.action_search_users,
			R.string.navigation_option_user_profile,
			R.string.action_create_post,
			R.string.action_user_requests,
			R.string.action_friends
		};

		final int NUM_NAV_STRINGS = 4;
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
			Fragment fragment = selectItem(R.string.navigation_option_home);
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
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

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

		if (!isServiceRunning(LocationListenerService.class)) {
			Intent intent = new Intent(this, LocationListenerService.class);
			intent.putExtra(LocationListenerService.USER_ID, HHUser.getCurrentUserID());
			startService(intent);
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
		getMenuInflater().inflate(R.menu.menu_main, menu);

		MenuItem menuPost = menu.findItem(R.id.action_post);
		menuPost.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);

		MenuItem menuRequests = menu.findItem(R.id.action_user_requests);
		menuRequests.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
		HolderActivity.menuRequests = menuRequests;
		if (HHUser.getCurrentUser().getFollowInRequests().size() > 0){
			menuRequests.setIcon(R.drawable.add_user_full);
		}

		MenuItem menuFriensd = menu.findItem(R.id.action_friends);
		menuFriensd.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);

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
				fragment = FeedFragment.newInstance(FeedFragment.USER_FEED, HHUser.getCurrentUserID());
				break;
			}
			case R.string.action_create_post: {
				fragment = new PostFragment();
				break;
			}
			case R.string.action_user_requests: {
				fragment = new RequestFollowFragment();
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
			default: {
				fragment = FeedFragment.newInstance();
			}
		}

		setActionBarTitle(getString(resourceID));
		drawerLayout.closeDrawer(drawerList);

		return fragment;
	}

	private void setActionBarTitle(int position){
		String title;
		title = getString(ZZZUtility.getKeyByValue(navigationOptions, position));
		getActionBar().setTitle(title);
	}

	private void setActionBarTitle(String string){
		getActionBar().setTitle(string);
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
		boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
		menu.findItem(R.id.action_post).setVisible(!drawerOpen);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState){
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration configuration){
		super.onConfigurationChanged(configuration);
		drawerToggle.onConfigurationChanged(configuration);
	}

	@Override
	public void onSaveInstanceState(Bundle bundle){
		super.onSaveInstanceState(bundle);
		bundle.putInt(KEY_POSITION, currentPosition);
	}

	public static Location getLastLocation(Activity activity){
		if (ActivityCompat.checkSelfPermission(
			activity,
			Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
			&& ActivityCompat.checkSelfPermission(
			activity,
			Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, HolderActivity.LOCATION_PERMISSIONS);

			return null;
		}
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
			case FragmentChangeRequestListener.USER_FEED_REQUEST:{
				if (bundle == null){
					fragment = selectItem(R.string.navigation_option_profile);
				} else {
					long userID = bundle.getLong(FeedFragment.USER_ID);
					selectItem(R.string.navigation_option_user_profile);
					fragment = FeedFragment.newInstance(FeedFragment.USER_FEED, userID);
				}
				break;
			}
		}
		if (fragment != null){
			commitFragmentTransaction(fragment, true);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		drawerToggle.setDrawerIndicatorEnabled(true);
	}
}
