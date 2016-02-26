package yosoyo.aaahearhereprototype;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import yosoyo.aaahearhereprototype.Fragments.HomeFragment;
import yosoyo.aaahearhereprototype.Fragments.MapsFragment;
import yosoyo.aaahearhereprototype.Fragments.PostFragment;
import yosoyo.aaahearhereprototype.Fragments.ProfileFragment;

public class HolderActivity extends Activity {

	public static final String KEY_POSITION = "position";
	public static final String VISIBLE_FRAGMENT = "visible_fragment";
	private String[] navigationOptions;
	private ListView drawerList;
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;
	private int currentPosition = 0;

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

		navigationOptions = getResources().getStringArray(R.array.navigation_options);
		drawerList = (ListView) findViewById(R.id.drawer);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		drawerList.setAdapter(
			new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1,
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
					if (fragment instanceof MapsFragment) {
						currentPosition = 1;
					}
					if (fragment instanceof PostFragment) {
						currentPosition = 2;
					}
					if (fragment instanceof ProfileFragment) {
						currentPosition = 3;
					}
					setActionBarTitle(currentPosition);
					drawerList.setItemChecked(currentPosition, true);
				}
			});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private void selectItem(int position){
		currentPosition = position;
		Fragment fragment;
		switch (position) {
			case 1: {
				fragment = new MapsFragment();
				break;
			}
			case 2: {
				fragment = new PostFragment();
				break;
			}
			case 3: {
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

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem){
		if (drawerToggle.onOptionsItemSelected(menuItem)){
			return true;
		}
		switch (menuItem.getItemId()){
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

}
