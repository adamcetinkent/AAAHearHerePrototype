package com.yosoyo.aaahearhereprototype.GoogleClasses;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by adam on 08/03/16.
 *
 * A streamlined {@link Place} for use within Hear Here
 */
public class SimpleGooglePlace {

	private final String id;
	private final String name;
	private final LatLng latLng;

	public SimpleGooglePlace(Place place){
		this.id = place.getId();
		this.name = place.getName().toString();
		this.latLng = place.getLatLng();
	}

	public String getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public LatLng getLatLng() {
		return latLng;
	}

	@Override
	public String toString() {
		return name;
	}
}
