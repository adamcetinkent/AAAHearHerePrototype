package yosoyo.aaahearhereprototype;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;

/**
 * Created by Adam Kent on 10/02/2016.
 *
 * Custom Adapter for ListView to show artwork (imageView), title and description (textViews).
 * imgArtwork is downloaded from URLs stored in images.
 */
class SearchResultsCustomListAdapter extends ArrayAdapter<String>{
	private static final String TAG = "SearchResultsCustomListAdapter";

	private final Activity context;
	private final String[] ids;				// underlying IDs
	private final String[] titles;			// Displayed in artistName TextView
	private final String[] images;			// URLs to download for imageView
	private final String[] descriptions;	// Displayed in artistDesc TextView

	public SearchResultsCustomListAdapter(Activity context, String[] ids, String[] titles, String[] images, String[] descriptions){
		super(context, R.layout.list_row_track, titles);

		this.context = context;
		this.ids = ids;
		this.titles = titles;
		this.images = images;
		this.descriptions = descriptions;
	}

	private static class ViewHolder{
		int position;
		TextView txtArtistName;
		TextView txtArtistDesc;
		ImageView imgArtwork;
	}

	public View getView(int position, View convertView, ViewGroup parent){
		final ViewHolder viewHolder;

		if (convertView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			convertView = inflater.inflate(R.layout.list_row_track, parent, false);

			viewHolder = new ViewHolder();

			viewHolder.position = position;

			viewHolder.txtArtistName = (TextView) convertView.findViewById(R.id.list_row_track_artist_name);
			viewHolder.txtArtistDesc = (TextView) convertView.findViewById(R.id.list_row_track_artist_desc);
			viewHolder.imgArtwork = (ImageView) convertView.findViewById(R.id.list_row_track_artwork);

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.position = position;
		}

		viewHolder.txtArtistName.setText(titles[viewHolder.position]); // Set artistName TextView
		if (descriptions[viewHolder.position] != null)
			viewHolder.txtArtistDesc.setText(descriptions[viewHolder.position]); // Set artistDesc TextView

		// Set artwork imgView bitmap
		WebHelper.getSpotifyAlbumArt(
			ids[position],
			images[position],
			new WebHelper.GetSpotifyAlbumArtCallback() {
				@Override
				public void returnSpotifyAlbumArt(Bitmap bitmap) {
					viewHolder.imgArtwork.setImageBitmap(bitmap);
				}
			});

		return convertView;
	}

}
