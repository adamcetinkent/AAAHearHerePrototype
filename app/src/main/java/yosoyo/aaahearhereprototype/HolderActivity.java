package yosoyo.aaahearhereprototype;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.facebook.Profile;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import yosoyo.aaahearhereprototype.Fragments.FeedFragment;
import yosoyo.aaahearhereprototype.Fragments.MapViewFragment;
import yosoyo.aaahearhereprototype.Fragments.PostFragment;
import yosoyo.aaahearhereprototype.Fragments.ProfileFragment;
import yosoyo.aaahearhereprototype.HHServerClasses.Database.DatabaseHelper;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUser;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;

public class HolderActivity extends Activity implements PostFragmentPostedListener {

	public static final String KEY_POSITION = "position";
	public static final String VISIBLE_FRAGMENT = "visible_fragment";
	public static final String REQUEST_CODE = "request_code";

	private String[] navigationOptions;
	private ListView drawerList;
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;
	private int currentPosition = 0;

	public static boolean apiExists = false;
	public static final MediaPlayer mediaPlayer = new MediaPlayer();

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

		WebHelper.getFacebookProfilePicture(
			Profile.getCurrentProfile().getId(),
			new WebHelper.GetFacebookProfilePictureCallback() {
				@Override
				public void returnFacebookProfilePicture(Bitmap bitmap) {
					HHUser.setProfilePicture(bitmap);
				}
			});

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
					if (fragment instanceof FeedFragment) {
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
				public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

				}
			})
			.addApi(LocationServices.API)
			.addApi(Places.PLACE_DETECTION_API)
			.addApi(Places.GEO_DATA_API)
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
				fragment = new FeedFragment();
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
				break;
			}
			case (R.id.action_reset_database):{
				DatabaseHelper.resetDatabase(this);
				break;
			}
			default:
				return super.onOptionsItemSelected(menuItem);
		}
		return super.onOptionsItemSelected(menuItem);
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

	public static Location getLastLocation(){
		return LocationServices.FusedLocationApi.getLastLocation(HolderActivity.mGoogleApiClient);
	}

	@Override
	public void onPostFragmentPosted(){
		selectItem(1);
	}

	/*@Override
	public void onBackPressed() {
		selectItem(0);
	}*/
}
