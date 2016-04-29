package com.yosoyo.aaahearhereprototype.GoogleClasses;

import com.google.android.gms.maps.model.Marker;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPostFull;

/**
 * Created by adam on 28/04/16.
 */
public class PostMarker implements Comparable {

	final Marker marker;
	final HHPostFull post;

	public PostMarker(Marker marker, HHPostFull post) {
		this.marker = marker;
		this.post = post;
	}

	@Override
	public int compareTo(Object that) {
		if (!(that instanceof PostMarker))
			return -1;

		return this.post.compareTo(((PostMarker) that).post);
	}

	public Marker getMarker() {
		return marker;
	}
}
