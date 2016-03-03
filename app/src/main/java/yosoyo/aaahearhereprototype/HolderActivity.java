package yosoyo.aaahearhereprototype;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import yosoyo.aaahearhereprototype.Fragments.HomeFragment;
import yosoyo.aaahearhereprototype.Fragments.MapViewFragment;
import yosoyo.aaahearhereprototype.Fragments.PostFragment;
import yosoyo.aaahearhereprototype.Fragments.ProfileFragment;
import yosoyo.aaahearhereprototype.TestServerClasses.TestUser;

public class HolderActivity extends Activity implements /*DownloadImageTask.DownloadImageTaskCallback,*/ PostFragmentPostedListener {

	public static final String KEY_POSITION = "position";
	public static final String VISIBLE_FRAGMENT = "visible_fragment";
	public static final String REQUEST_CODE = "request_code";
	public static final String PROFILE_PICTURE = "profile_picture";
	public static final String TEST_USER = "test_user";

	private String[] navigationOptions;
	private ListView drawerList;
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;
	private int currentPosition = 0;

	//public static ZZZDataHolder dataHolder = new ZZZDataHolder();
	//public static AsyncDataManager asyncDataManager = new AsyncDataManager();
	public static TestUser testUser;
	public static Bitmap profilePicture = null;
	public static boolean apiExists = false;
	public static MediaPlayer mediaPlayer = new MediaPlayer();

	public static GoogleApiClient mGoogleApiClient;

	private class DrawerItemClickListener implements ListView.OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id){
			selectItem(position);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_holder);

		AsyncDataManager.setContext(this);

		Intent intent = getIntent();
		if (intent.hasExtra(PROFILE_PICTURE)){
			profilePicture = ZZZUtility.convertByteArrayToBitmap(intent.getByteArrayExtra(PROFILE_PICTURE));
		} else {
			/*new DownloadImageTask(null, this).execute(
				Profile.getCurrentProfile().getProfilePictureUri(200, 200).toString());*/
		}
		testUser = new Gson().fromJson(intent.getStringExtra(TEST_USER), TestUser.class);

		navigationOptions = getResources().getStringArray(R.array.navigation_options);
		drawerList = (ListView) findViewById(R.id.drawer);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		drawerList.setAdapter(
			new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1,
									 navigationOptions));
		drawerList.setOnItemClickListener(new DrawerItemClickListener());

		if (savedInstanceState == null){
			selectItem(0);
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
					if (fragment instanceof HomeFragment) {
						currentPosition = 0;
					}
					if (fragment instanceof MapViewFragment) {
						currentPosition = 1;
					}
					/*if (fragment instanceof PostFragment) {
						currentPosition = 2;
					}*/
					if (fragment instanceof ProfileFragment) {
						currentPosition = 2;
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
				public void onConnectionFailed(ConnectionResult connectionResult) {

				}
			})
			.addApi(LocationServices.API)
			.build();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.menu_main, menu);
		MenuItem menuItem = menu.findItem(R.id.post);
		menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
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

	private void selectItem(int position){
		currentPosition = position;
		Fragment fragment;
		switch (position) {
			case 1: {
				fragment = new MapViewFragment();
				break;
			}
			/*case 2: {
				fragment = new PostFragment();
				break;
			}*/
			case 2: {
				fragment = new ProfileFragment();
				break;
			}
			default: {
				fragment = new HomeFragment();
			}
		}
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.content_frame, fragment, VISIBLE_FRAGMENT);
		ft.addToBackStack(null);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();

		setActionBarTitle(position);

		drawerLayout.closeDrawer(drawerList);
	}

	private void setActionBarTitle(int position){
		String title;
		title = navigationOptions[position];
		getActionBar().setTitle(title);
	}

	private void setActionBarTitle(String string){
		getActionBar().setTitle(string);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem){
		if (drawerToggle.onOptionsItemSelected(menuItem)){
			return true;
		}
		switch (menuItem.getItemId()){
			case (R.id.post):{

				drawerLayout.closeDrawers();

				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.content_frame, new PostFragment(), VISIBLE_FRAGMENT);
				ft.addToBackStack(null);
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.commit();

				setActionBarTitle("Post to Hear Here");
			}
			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
		// use drawerOpen to determine what is visible
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

	/*@Override
	public void returnDownloadedImage(Bitmap result, int position, Marker marker) {
		profilePicture = result;
	}*/

	public static Location getLastLocation(){
		return LocationServices.FusedLocationApi.getLastLocation(HolderActivity.mGoogleApiClient);
	}

	@Override
	public void onPostFragmentPosted(){
		selectItem(1);
	}

	@Override
	public void onBackPressed() {
		selectItem(0);
	}
}
