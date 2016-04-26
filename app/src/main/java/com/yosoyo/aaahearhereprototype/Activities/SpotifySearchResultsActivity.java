package com.yosoyo.aaahearhereprototype.Activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yosoyo.aaahearhereprototype.AsyncDataManager;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;
import com.yosoyo.aaahearhereprototype.R;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyAlbum;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyArtist;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;
import com.yosoyo.aaahearhereprototype.ZZZUtility;

import java.util.List;

public class SpotifySearchResultsActivity extends Activity{
	private static final String TAG = SpotifySearchResultsActivity.class.getSimpleName();

	public static final String TRACK_JSON = "trackJson";
	public static final String ARTIST_JSON = "artistJson";
	public static final String ALBUM_JSON = "albumJson";

	public static final String QUERY_ARTIST = "queryArtist";
	public static final String QUERY_ALBUM = "queryAlbum";

	public static final int REQUEST_CODE_TRACK = 270027;
	public static final int REQUEST_CODE_ARTIST = 270028;
	public static final int REQUEST_CODE_ALBUM = 270029;
	public static final int REQUEST_CODE_TRACK_ARTIST = 270030;
	public static final int REQUEST_CODE_TRACK_ALBUM = 270031;
	public static final int REQUEST_CODE_TRACK_ARTIST_ALBUM = 270032;
	//public static final int REQUEST_CODE_ARTIST_ALBUM = 270033;
	public static final int REQUEST_CODE_ALBUM_ARTIST = 270034;
	public static final int REQUEST_CODE_TRACK_FROM_ARTIST = 270035;
	public static final int REQUEST_CODE_TRACK_FROM_ALBUM = 270036;

	//private SpotifyTrack[] trackResults;
	//private SpotifyArtist[] artistResults;
	//private SpotifyAlbum[] albumResults;

	private String query;

	private List<SpotifyTrack> trackResults;
	private List<SpotifyArtist> artistResults;
	private List<SpotifyAlbum> albumResults;
	private int requestCode;

	private RecyclerView lstResults;
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager layoutManager;

	private ProgressBar progressBar;

	private int totalItems;
	private boolean alreadySearching = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spotify_search_results);
		lstResults = (RecyclerView) findViewById(R.id.activity_search_results_lstResults);
		progressBar = (ProgressBar) findViewById(R.id.activity_search_results_progressBar);
		handleIntent(getIntent());
	}



	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {

		requestCode = intent.getIntExtra(HolderActivity.REQUEST_CODE, -1);

		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			query = intent.getStringExtra(SearchManager.QUERY);

			switch (requestCode){
				case REQUEST_CODE_TRACK:{
					query = "track:"+query;
					break;
				}
				case REQUEST_CODE_ALBUM:{
					query = "album:"+query;
					break;
				}
				case REQUEST_CODE_ARTIST:{
					query = "artist:"+query;
					break;
				}
				case REQUEST_CODE_TRACK_ARTIST:{
					String artistName = intent.getStringExtra(QUERY_ARTIST);
					query = "track:"+query+" artist:\""+artistName+"\"";
					break;
				}
				case REQUEST_CODE_TRACK_ALBUM:{
					String albumName = intent.getStringExtra(QUERY_ALBUM);
					query = "track:"+query+" album:\""+albumName+"\"";
					break;
				}
				case REQUEST_CODE_TRACK_ARTIST_ALBUM:{
					String artistName = intent.getStringExtra(QUERY_ARTIST);
					String albumName = intent.getStringExtra(QUERY_ALBUM);
					query = "track:"+query+" artist:\""+artistName+"\""+" album:\""+albumName+"\"";
					break;
				}
				/*case REQUEST_CODE_ARTIST_ALBUM:{
					String albumName = intent.getStringExtra(QUERY_ALBUM);
					query = "artist:"+query+"%20album:%22"+albumName+"%22";
					break;
				}*/
				case REQUEST_CODE_ALBUM_ARTIST:{
					String artistName = intent.getStringExtra(QUERY_ARTIST);
					query = "album:"+query+" artist:\""+artistName+"\"";
					break;
				}
				case REQUEST_CODE_TRACK_FROM_ARTIST:{
					String artistName = intent.getStringExtra(QUERY_ARTIST);
					query = "artist:\""+artistName+"\"";
					break;
				}
				case REQUEST_CODE_TRACK_FROM_ALBUM:{
					String albumName = intent.getStringExtra(QUERY_ALBUM);
					if (intent.hasExtra(QUERY_ARTIST)) {
						String artistName = intent.getStringExtra(QUERY_ARTIST);
						query = "artist:\"" + artistName + "\" album:\"" + albumName + "\"";
					} else {
						query = "album:\"" + albumName + "\"";
					}
					break;
				}
			}

			showResults(query);
		}
	}

	private void showResults(final String query) {
		Log.d(TAG, "Query: " + query);
		switch (requestCode){
			case REQUEST_CODE_TRACK_FROM_ALBUM:
			case REQUEST_CODE_TRACK_FROM_ARTIST:
			case REQUEST_CODE_TRACK_ARTIST_ALBUM:
			case REQUEST_CODE_TRACK_ARTIST:
			case REQUEST_CODE_TRACK_ALBUM:
			case REQUEST_CODE_TRACK:{
				AsyncDataManager.searchSpotifyTracks(
					query,
					new AsyncDataManager.SearchSpotifyTracksCallback() {
						@Override
						public void returnSearchSpotifyTracks(final List<SpotifyTrack> spotifyTracks, int totalTracks) {
							trackResults = spotifyTracks;
							totalItems = totalTracks;

							layoutManager = new LinearLayoutManager(getApplicationContext());
							lstResults.setLayoutManager(layoutManager);

							adapter = new SpotifyTrackSearchResultsAdapter(
								trackResults,
								totalItems,
								new SpotifyTrackSearchResultsAdapter.Callback() {
									@Override
									public void onClick(SpotifyTrack spotifyTrack) {
										Intent resultIntent = new Intent();
										resultIntent.putExtra(TRACK_JSON, new Gson().toJson(spotifyTrack, SpotifyTrack.class));

										setResult(REQUEST_CODE_TRACK, resultIntent);
										finish();
									}

									@Override
									public void nextPage() {
										if (!alreadySearching){
											alreadySearching = true;
											progressBar.setVisibility(View.VISIBLE);
											AsyncDataManager.searchSpotifyTracks(
												query,
												trackResults.size(),
												new AsyncDataManager.SearchSpotifyTracksCallback() {
													@Override
													public void returnSearchSpotifyTracks(List<SpotifyTrack> spotifyTracks, int totalTracks) {
														progressBar.setVisibility(View.GONE);
														int oldSize = trackResults.size();
														trackResults.addAll(spotifyTracks);
														adapter.notifyItemRangeInserted(oldSize, spotifyTracks.size());
														totalItems = totalTracks;
														alreadySearching = false;
													}
												}
											);
										}
									}
								});

							lstResults.setAdapter(adapter);
							progressBar.setVisibility(View.GONE);
						}
					}
				);
				break;
			}
			//case REQUEST_CODE_ARTIST_ALBUM:
			case REQUEST_CODE_ARTIST:{
				AsyncDataManager.searchSpotifyArtists(
					query,
					new AsyncDataManager.SearchSpotifyArtistsCallback() {
						@Override
						public void returnSearchSpotifyArtists(final List<SpotifyArtist> spotifyArtists, int totalArtists) {
							artistResults = spotifyArtists;
							totalItems = totalArtists;

							layoutManager = new LinearLayoutManager(getApplicationContext());
							lstResults.setLayoutManager(layoutManager);

							adapter = new SpotifyArtistSearchResultsAdapter(
								artistResults,
								totalItems,
								new SpotifyArtistSearchResultsAdapter.Callback() {
									@Override
									public void onClick(SpotifyArtist spotifyArtist) {
										Intent resultIntent = new Intent();
										resultIntent.putExtra(ARTIST_JSON, new Gson().toJson(spotifyArtist, SpotifyArtist.class));

										setResult(REQUEST_CODE_ARTIST, resultIntent);
										finish();
									}

									@Override
									public void onLongClick(SpotifyArtist spotifyArtist) {
										Intent intent = new Intent(getApplicationContext(), SpotifySearchResultsActivity.class);
										intent.setAction(Intent.ACTION_SEARCH);
										intent.putExtra(SearchManager.QUERY, query);
										intent.putExtra(HolderActivity.REQUEST_CODE, REQUEST_CODE_TRACK_FROM_ARTIST);
										intent.putExtra(QUERY_ARTIST, spotifyArtist.getName());
										startActivityForResult(intent, SpotifySearchResultsActivity.REQUEST_CODE_TRACK_FROM_ARTIST);
									}

									@Override
									public void nextPage() {
										if (!alreadySearching){
											alreadySearching = true;
											progressBar.setVisibility(View.VISIBLE);
											AsyncDataManager.searchSpotifyArtists(
												query,
												artistResults.size(),
												new AsyncDataManager.SearchSpotifyArtistsCallback() {
													@Override
													public void returnSearchSpotifyArtists(List<SpotifyArtist> spotifyArtists, int totalArtists) {
														progressBar.setVisibility(View.GONE);
														int oldSize = artistResults.size();
														artistResults.addAll(spotifyArtists);
														adapter.notifyItemRangeInserted(oldSize, spotifyArtists.size());
														totalItems = totalArtists;
														alreadySearching = false;
													}
												}
											);
										}
									}
								});

							lstResults.setAdapter(adapter);
							progressBar.setVisibility(View.GONE);
						}
					}
				);
				break;
			}
			case REQUEST_CODE_ALBUM_ARTIST:
			case REQUEST_CODE_ALBUM:{
				AsyncDataManager.searchSpotifyAlbums(
					query,
					new AsyncDataManager.SearchSpotifyAlbumsCallback() {
						@Override
						public void returnSearchSpotifyAlbums(final List<SpotifyAlbum> spotifyAlbums, int totalAlbums) {
							albumResults = spotifyAlbums;
							totalItems = totalAlbums;

							layoutManager = new LinearLayoutManager(getApplicationContext());
							lstResults.setLayoutManager(layoutManager);

							adapter = new SpotifyAlbumSearchResultsAdapter(
								albumResults,
								totalItems,
								new SpotifyAlbumSearchResultsAdapter.Callback() {
									@Override
									public void onClick(SpotifyAlbum spotifyAlbum) {
										Intent resultIntent = new Intent();
										resultIntent.putExtra(ALBUM_JSON, new Gson().toJson(spotifyAlbum, SpotifyAlbum.class));

										setResult(REQUEST_CODE_ALBUM, resultIntent);
										finish();
									}

									@Override
									public void onLongClick(SpotifyAlbum spotifyAlbum) {
										Intent intent = new Intent(getApplicationContext(), SpotifySearchResultsActivity.class);
										intent.setAction(Intent.ACTION_SEARCH);
										intent.putExtra(SearchManager.QUERY, query);
										intent.putExtra(HolderActivity.REQUEST_CODE, REQUEST_CODE_TRACK_FROM_ALBUM);
										intent.putExtra(QUERY_ALBUM, spotifyAlbum.getName());
										if (spotifyAlbum.getArtistName() != null){
											intent.putExtra(QUERY_ARTIST, spotifyAlbum.getArtistName());
										}
										startActivityForResult(intent, SpotifySearchResultsActivity.REQUEST_CODE_TRACK_FROM_ALBUM);
									}

									@Override
									public void nextPage() {
										if (!alreadySearching){
											alreadySearching = true;
											progressBar.setVisibility(View.VISIBLE);
											AsyncDataManager.searchSpotifyAlbums(
												query,
												albumResults.size(),
												new AsyncDataManager.SearchSpotifyAlbumsCallback() {
													@Override
													public void returnSearchSpotifyAlbums(List<SpotifyAlbum> spotifyAlbums, int totalAlbums) {
														progressBar.setVisibility(View.GONE);
														int oldSize = albumResults.size();
														albumResults.addAll(spotifyAlbums);
														adapter.notifyItemRangeInserted(oldSize, spotifyAlbums.size());
														totalItems = totalAlbums;
														alreadySearching = false;
													}
												}
											);
										}
									}
								});

							lstResults.setAdapter(adapter);
							progressBar.setVisibility(View.GONE);
						}
					}
				);
				break;
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		setResult(resultCode, data);
		finish();
	}

	static class SpotifyTrackSearchResultsAdapter extends RecyclerView.Adapter<SpotifyTrackSearchResultsAdapter.ViewHolder> {

		private final List<SpotifyTrack> spotifyTracks;
		private final int totalTracks;
		private final Callback callback;

		interface Callback{
			void onClick(SpotifyTrack spotifyTrack);
			void nextPage();
		}

		private abstract static class OnClickSpotifyTrackListener implements View.OnClickListener{
			SpotifyTrack spotifyTrack;

			public void setSpotifyTrack(SpotifyTrack spotifyTrack) {
				this.spotifyTrack = spotifyTrack;
			}
		}

		static class ViewHolder extends RecyclerView.ViewHolder{
			public final TextView txtTrackName;
			public final TextView txtTrackDesc;
			public final ImageView imgAlbumArt;
			public final OnClickSpotifyTrackListener onClickListener;

			public ViewHolder(View view, final Callback callback) {
				super(view);

				txtTrackName = (TextView) view.findViewById(R.id.list_row_spotify_search_results_txtName);
				txtTrackDesc = (TextView) view.findViewById(R.id.list_row_spotify_search_results_txtDesc);
				imgAlbumArt = (ImageView) view.findViewById(R.id.list_row_spotify_search_results_imgArtwork);

				onClickListener = new OnClickSpotifyTrackListener() {
					@Override
					public void onClick(View v) {
						callback.onClick(this.spotifyTrack);
					}
				};
				view.setOnClickListener(onClickListener);
			}
		}

		public SpotifyTrackSearchResultsAdapter(List<SpotifyTrack> spotifyTracks,
												int totalTracks,
												final Callback callback) {
			this.spotifyTracks = spotifyTracks;
			this.totalTracks = totalTracks;
			this.callback = callback;
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext())
									  .inflate(R.layout.list_row_spotify_search_results, parent, false);
			return new ViewHolder(view, callback);
		}

		@Override
		public void onBindViewHolder(final ViewHolder holder, int position) {
			SpotifyTrack spotifyTrack = spotifyTracks.get(position);
			holder.onClickListener.setSpotifyTrack(spotifyTrack);
			holder.txtTrackName.setText(spotifyTrack.getName());
			holder.txtTrackDesc.setText(spotifyTrack.getArtistNameAlbumName());

			holder.imgAlbumArt.setImageResource(R.drawable.spotify_blank);
			// get User Image
			WebHelper.getSpotifyAlbumArt(
				spotifyTrack.getID(),
				spotifyTrack.getImageURL(),
				new WebHelper.GetSpotifyAlbumArtCallback() {
					@Override
					public void returnSpotifyAlbumArt(Bitmap bitmap) {
						if (bitmap != null)
							holder.imgAlbumArt.setImageBitmap(bitmap);
					}
				});

			if ((position == spotifyTracks.size() - 1) && (spotifyTracks
				.size() < totalTracks)) {
				callback.nextPage();
			}
		}

		@Override
		public int getItemCount() {
			return spotifyTracks.size();
		}

	}

	static class SpotifyArtistSearchResultsAdapter extends RecyclerView.Adapter<SpotifyArtistSearchResultsAdapter.ViewHolder> {

		private final List<SpotifyArtist> spotifyArtists;
		private final int totalArtists;
		private final Callback callback;

		interface Callback{
			void onClick(SpotifyArtist spotifyArtist);
			void onLongClick(SpotifyArtist spotifyArtist);
			void nextPage();
		}

		private abstract static class OnClickSpotifyArtistListener implements View.OnClickListener{
			SpotifyArtist spotifyArtist;

			public void setSpotifyArtist(SpotifyArtist spotifyArtist) {
				this.spotifyArtist = spotifyArtist;
			}
		}

		private abstract static class OnLongClickSpotifyArtistListener implements View.OnLongClickListener{
			SpotifyArtist spotifyArtist;

			public void setSpotifyArtist(SpotifyArtist spotifyArtist) {
				this.spotifyArtist = spotifyArtist;
			}
		}

		static class ViewHolder extends RecyclerView.ViewHolder{
			public final TextView txtArtistName;
			public final TextView txtArtistDesc;
			public final ImageView imgAlbumArt;
			public final OnClickSpotifyArtistListener onClickListener;
			public final OnLongClickSpotifyArtistListener onLongClickListener;

			public ViewHolder(View view, final Callback callback) {
				super(view);

				txtArtistName = (TextView) view.findViewById(R.id.list_row_spotify_search_results_txtName);
				txtArtistDesc = (TextView) view.findViewById(R.id.list_row_spotify_search_results_txtDesc);
				imgAlbumArt = (ImageView) view.findViewById(R.id.list_row_spotify_search_results_imgArtwork);

				onClickListener = new OnClickSpotifyArtistListener() {
					@Override
					public void onClick(View v) {
						callback.onClick(this.spotifyArtist);
					}
				};
				view.setOnClickListener(onClickListener);

				onLongClickListener = new OnLongClickSpotifyArtistListener() {
					@Override
					public boolean onLongClick(View v) {
						callback.onLongClick(this.spotifyArtist);
						return true;
					}
				};
				view.setOnLongClickListener(onLongClickListener);
			}
		}

		public SpotifyArtistSearchResultsAdapter(List<SpotifyArtist> spotifyArtists,
												int totalArtists,
												final Callback callback) {
			this.spotifyArtists = spotifyArtists;
			this.totalArtists = totalArtists;
			this.callback = callback;
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext())
									  .inflate(R.layout.list_row_spotify_search_results, parent, false);
			return new ViewHolder(view, callback);
		}

		@Override
		public void onBindViewHolder(final ViewHolder holder, int position) {
			SpotifyArtist spotifyArtist = spotifyArtists.get(position);
			holder.onClickListener.setSpotifyArtist(spotifyArtist);
			holder.onLongClickListener.setSpotifyArtist(spotifyArtist);
			holder.txtArtistName.setText(spotifyArtist.getName());
			holder.txtArtistDesc.setText(spotifyArtist.getGenres());

			holder.imgAlbumArt.setImageResource(R.drawable.spotify_blank);
			// get User Image
			WebHelper.getSpotifyAlbumArt(
				spotifyArtist.getID(),
				spotifyArtist.getImageURL(),
				new WebHelper.GetSpotifyAlbumArtCallback() {
					@Override
					public void returnSpotifyAlbumArt(Bitmap bitmap) {
						if (bitmap != null)
							holder.imgAlbumArt.setImageBitmap(bitmap);
					}
				});

			if ((position == spotifyArtists.size()-1) && (spotifyArtists.size() < totalArtists)){
				callback.nextPage();
			}
		}

		@Override
		public int getItemCount() {
			return spotifyArtists.size();
		}

	}

	static class SpotifyAlbumSearchResultsAdapter extends RecyclerView.Adapter<SpotifyAlbumSearchResultsAdapter.ViewHolder> {

		private final List<SpotifyAlbum> spotifyAlbums;
		private final int totalAlbums;
		private final Callback callback;

		interface Callback{
			void onClick(SpotifyAlbum spotifyAlbum);
			void onLongClick(SpotifyAlbum spotifyAlbum);
			void nextPage();
		}

		private abstract static class OnClickSpotifyAlbumListener implements View.OnClickListener{
			SpotifyAlbum spotifyAlbum;

			public void setSpotifyAlbum(SpotifyAlbum spotifyAlbum) {
				this.spotifyAlbum = spotifyAlbum;
			}
		}

		private abstract static class OnLongClickSpotifyAlbumListener implements View.OnLongClickListener{
			SpotifyAlbum spotifyAlbum;

			public void setSpotifyAlbum(SpotifyAlbum spotifyAlbum) {
				this.spotifyAlbum = spotifyAlbum;
			}
		}

		static class ViewHolder extends RecyclerView.ViewHolder{
			public final TextView txtAlbumName;
			public final TextView txtAlbumDesc;
			public final ImageView imgAlbumArt;
			public final OnClickSpotifyAlbumListener onClickListener;
			public final OnLongClickSpotifyAlbumListener onLongClickListener;

			public ViewHolder(View view, final Callback callback) {
				super(view);

				txtAlbumName = (TextView) view.findViewById(R.id.list_row_spotify_search_results_txtName);
				txtAlbumDesc = (TextView) view.findViewById(R.id.list_row_spotify_search_results_txtDesc);
				imgAlbumArt = (ImageView) view.findViewById(R.id.list_row_spotify_search_results_imgArtwork);

				onClickListener = new OnClickSpotifyAlbumListener() {
					@Override
					public void onClick(View v) {
						callback.onClick(this.spotifyAlbum);
					}
				};
				view.setOnClickListener(onClickListener);

				onLongClickListener = new OnLongClickSpotifyAlbumListener() {
					@Override
					public boolean onLongClick(View v) {
						callback.onLongClick(this.spotifyAlbum);
						return true;
					}
				};
				view.setOnLongClickListener(onLongClickListener);
			}
		}

		public SpotifyAlbumSearchResultsAdapter(List<SpotifyAlbum> spotifyAlbums,
												 int totalAlbums,
												 final Callback callback) {
			this.spotifyAlbums = spotifyAlbums;
			this.totalAlbums = totalAlbums;
			this.callback = callback;
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext())
									  .inflate(R.layout.list_row_spotify_search_results, parent, false);
			return new ViewHolder(view, callback);
		}

		@Override
		public void onBindViewHolder(final ViewHolder holder, int position) {
			SpotifyAlbum spotifyAlbum = spotifyAlbums.get(position);
			holder.onClickListener.setSpotifyAlbum(spotifyAlbum);
			holder.onLongClickListener.setSpotifyAlbum(spotifyAlbum);
			holder.txtAlbumName.setText(spotifyAlbum.getName());
			holder.txtAlbumDesc.setText(spotifyAlbum.getArtistName());
			if (spotifyAlbum.getArtistName() == null){
				AsyncDataManager.getSpotifyAlbum(
					spotifyAlbum.getID(),
					new AsyncDataManager.GetSpotifyAlbumCallback() {
						@Override
						public void returnSpotifyAlbum(SpotifyAlbum spotifyAlbum) {
							if (spotifyAlbum != null) {
								ZZZUtility.updateList(spotifyAlbums, spotifyAlbum);
								holder.txtAlbumDesc.setText(spotifyAlbum.getArtistName());
								holder.onClickListener.setSpotifyAlbum(spotifyAlbum);
								holder.onLongClickListener.setSpotifyAlbum(spotifyAlbum);
							}
						}
					}
				);
			}

			holder.imgAlbumArt.setImageResource(R.drawable.spotify_blank);
			// get User Image
			WebHelper.getSpotifyAlbumArt(
				spotifyAlbum.getID(),
				spotifyAlbum.getImageURL(),
				new WebHelper.GetSpotifyAlbumArtCallback() {
					@Override
					public void returnSpotifyAlbumArt(Bitmap bitmap) {
						if (bitmap != null)
							holder.imgAlbumArt.setImageBitmap(bitmap);
					}
				});

			if ((position == spotifyAlbums.size()-1) && (spotifyAlbums.size() < totalAlbums)){
				callback.nextPage();
			}
		}

		@Override
		public int getItemCount() {
			return spotifyAlbums.size();
		}

	}

}
