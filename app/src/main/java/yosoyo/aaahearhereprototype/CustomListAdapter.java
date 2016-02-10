package yosoyo.aaahearhereprototype;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Adam Kent on 10/02/2016.
 */
public class CustomListAdapter extends ArrayAdapter implements DownloadImageTask.AsyncResponse {

	private final Activity context;
	private final String[] artistNames;
	private final String[] artistImages;
	private final String[] artistDescs;
	private Bitmap[] artistBitmaps;

	public CustomListAdapter(Activity context, String[] artistNames, String[] artistImages, String[] artistDescs){
		super(context, R.layout.mylist, artistNames);

		this.context = context;
		this.artistNames = artistNames;
		this.artistImages = artistImages;
		this.artistDescs = artistDescs;
		this.artistBitmaps = new Bitmap[artistImages.length];
	}

	public View getView(int position, View view, ViewGroup parent){
		LayoutInflater inflater=context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.mylist, null, true);

		TextView txtArtistName = (TextView) rowView.findViewById(R.id.artistname);
		ImageView imgArtwork = (ImageView) rowView.findViewById(R.id.artwork);
		TextView txtArtistDesc = (TextView) rowView.findViewById(R.id.artistdesc);

		txtArtistName.setText(artistNames[position]);
		if (artistImages[position] != null) {
			if (artistBitmaps[position] == null) {
				new DownloadImageTask(imgArtwork, this, position)
					.execute(artistImages[position]);
			} else {
				imgArtwork.setImageBitmap(artistBitmaps[position]);
			}
		}
		txtArtistDesc.setText(artistDescs[position]);
		return rowView;
	}


	@Override
	public void processFinish(Bitmap result, int position) {
		artistBitmaps[position] = result;
	}
}
