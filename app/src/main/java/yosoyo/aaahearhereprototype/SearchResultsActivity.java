package yosoyo.aaahearhereprototype;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyArtist;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyImage;

public class SearchResultsActivity extends AppCompatActivity implements AsyncArtist.AsyncResponse {

	public static final String tag = "SearchResultsActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);
		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {

		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);

			showResults(query);
		}
	}

	private void showResults(String query) {
		// Query your data set and show results
		// ...
		Log.d(tag, "Query: " + query);
		AsyncArtist httpThread = new AsyncArtist(this);
		httpThread.execute(query);
	}

	public void processFinish(SpotifyArtist[] artistResults){
		//Here you will receive the artistResults fired from async class
		//of onPostExecute(artistResults) method.
		Log.d(tag, "JSON search results:\n" + artistResults);
		if (artistResults == null){
			return;
		}

		String artistNameList[] = new String[artistResults.length];
		String artistImageList[] = new String[artistResults.length];
		//String artistGenreList[] = new String[artistResults.length];
		String artistDescList[] = new String[artistResults.length];
		for (int i = 0; i < artistResults.length; i++){
			artistNameList[i] = artistResults[i].toString();
			SpotifyImage image = artistResults[i].getImages(0);
			if (image != null)
				artistImageList[i] = image.getUrl();
			//artistGenreList[i] = artistResults[i].getGenres();
			artistDescList[i] = artistResults[i].getID();
		}

		CustomListAdapter adapter = new CustomListAdapter(this, artistNameList, artistImageList, artistDescList);
		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView txtArtistName = (TextView) view.findViewById(R.id.artistname);
				String artistName = txtArtistName.getText().toString();
				Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
				intent.putExtra("artistName", artistName);
				startActivity(intent);
			}
		});

//		ListView listView = (ListView) findViewById(R.id.listView);
//		listView.setAdapter(
//			new ArrayAdapter<>(this, R.layout.mylist, R.id.Itemname, artistNameList));
	}

}
