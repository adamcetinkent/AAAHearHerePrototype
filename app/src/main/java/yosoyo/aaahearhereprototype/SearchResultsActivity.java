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

import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyAPIResponse;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyArtist;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyImage;

public class SearchResultsActivity extends AppCompatActivity implements SpotifyAPIRequest.SpotifyAPIRequestCallback {
	private static final String tag = "SearchResultsActivity";

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
		Log.d(tag, "Query: " + query);
		SpotifyAPIRequest spotifyAPIRequest = new SpotifyAPIRequest(this, "artist");
		spotifyAPIRequest.execute(query);
	}

	public void processFinish(SpotifyAPIResponse result){
		SpotifyArtist[] artistResults = result.getArtists().getItems();
		Log.d(tag, "JSON search results:\n" + artistResults);
		if (artistResults == null){
			return;
		}

		String artistNameList[] = new String[artistResults.length];
		String artistImageList[] = new String[artistResults.length];
		String artistDescList[] = new String[artistResults.length];
		for (int i = 0; i < artistResults.length; i++){
			artistNameList[i] = artistResults[i].toString();
			SpotifyImage image = artistResults[i].getImages(0);
			if (image != null)
				artistImageList[i] = image.getUrl();
			artistDescList[i] = artistResults[i].getID();
		}

		SearchResultsCustomListAdapter adapter = new SearchResultsCustomListAdapter(this, artistNameList, artistImageList, artistDescList);
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

	}

}
