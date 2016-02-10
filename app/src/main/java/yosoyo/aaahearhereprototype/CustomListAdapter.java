package yosoyo.aaahearhereprototype;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Adam Kent on 10/02/2016.
 */
public class CustomListAdapter extends ArrayAdapter {

	private final Activity context;
	private final String[] artistNames;
	private final String[] artistImages;
	private final String[] artistDescs;

	public CustomListAdapter(Activity context, String[] artistNames, String[] artistImages, String[] artistDescs){
		super(context, R.layout.mylist, artistNames);

		this.context = context;
		this.artistNames = artistNames;
		this.artistImages = artistImages;
		this.artistDescs = artistDescs;
	}

	public View getView(int position, View view, ViewGroup parent){
		LayoutInflater inflater=context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.mylist, null, true);

		TextView txtTitle = (TextView) rowView.findViewById(R.id.artistname);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.artwork);
		TextView extraText = (TextView) rowView.findViewById(R.id.artistdesc);

		txtTitle.setText(artistNames[position]);
		if (artistImages[position] != null)
			new DownloadImageTask(imageView).execute(artistImages[position]);
		extraText.setText(artistDescs[position]);
		return rowView;
	}


}
