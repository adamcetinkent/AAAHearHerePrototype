package yosoyo.aaahearhereprototype;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends AppCompatActivity {

	public static final String tag = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		//findViewById(android.R.id.content).setOnTouchListener(this);
		//setContentView(R.layout.search);

		Log.d(tag, "onCreate: started");

		setContentView(R.layout.activity_main);
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

//		ListView listView = (ListView) findViewById(R.id.listView);
//		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//				Intent intent = new Intent(this, MapsActivity.class);
//				startActivity(intent);
//			}
//		});

		// Get the intent, verify the action and get the query
		/*Intent intent = getIntent();
		//if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			Log.d(tag, "onCreate: intent stuff");
			//String query = intent.getStringExtra(SearchManager.QUERY);
			//doMySearch(query);

			//HTTPThread httpThread = new HTTPThread(this);
			////httpThread.execute(query);
			//httpThread.execute("dummy");
			//Log.d(tag, "onCreate: http executed");

			AsyncArtist httpThread = new AsyncArtist(this);
			httpThread.execute("dummy");
			Log.d(tag, "onCreate: http executed");

		//}*/

	}

	/*public void processFinish(String result){
		//Here you will receive the result fired from async class
		//of onPostExecute(result) method.
		Log.d(tag, "JSON search results:\n" + result);
		String resultList[] = {result};
		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(
			new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, resultList));
	}*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(
			menu.findItem(R.id.search));

		ComponentName componentName = new ComponentName(this, SearchResultsActivity.class);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));

		return true;
	}

	/*@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}*/

//	@Override
//	protected void onListItemClick(ListView list, View view, int position,
//								   long id) {
//		super.onListItemClick(list, view, position, id);
//		Intent intent = new Intent(this, MapsActivity.class);
//		startActivity(intent);
//	}

}
