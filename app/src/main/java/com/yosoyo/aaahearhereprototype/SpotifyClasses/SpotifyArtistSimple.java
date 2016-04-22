package com.yosoyo.aaahearhereprototype.SpotifyClasses;

/**
 * Created by Adam Kent on 11/02/2016.
 *
 * The Spotify specification for a simplified Artist.
 */
@SuppressWarnings("unused")
public class SpotifyArtistSimple {

	private SpotifyExternalURL external_urls;
	private String href;
	private String id;
	private String name;
	private String type;
	private String uri;

	public String getName() {
		return name;
	}

	public String getID() {
		return id;
	}
}
