package com.yosoyo.aaahearhereprototype.SpotifyClasses;

/**
 * Created by Adam Kent on 11/02/2016.
 *
 * The Spotify specification for a Track.
 */
@SuppressWarnings("unused")
public class SpotifyTrack {

	private SpotifyAlbumSimple album;
	private SpotifyArtistSimple[] artists;
	private String[] available_markets;
	private int disc_number;
	private int duration_ms;
	private boolean explicit;
	private SpotifyExternalID external_ids;
	private SpotifyExternalURL external_urls;
	private String href;
	private String id;
	private boolean is_playable;
	private SpotifyTrackLink linked_from;
	private String name;
	private int popularity;
	private String preview_url;
	private int track_number;
	private String type;
	private String uri;

	public SpotifyImage[] getImages() {
		if (album != null)
			return album.getImages();
		else
			return null;
	}

	public SpotifyImage getImages(int index) {
		SpotifyImage[] images = getImages();
		if (images == null)
			return null;
		if (index < images.length) {
			return images[index];
		} else {
			return null;
		}
	}

	public String getID(){
		return id;
	}

	public String getName(){
		return name;
	}

	public SpotifyArtistSimple[] getArtists() {
		return artists;
	}

	public String getArtistName(){
		if (artists == null)
			return null;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < artists.length; i++){
			sb.append(artists[i].getName());
			if (i < artists.length - 1){
				sb.append("; ");
			}
		}
		return sb.toString();
	}

	public String getAlbumName(){
		if (album == null)
			return null;
		return album.getName();
	}

	public String getArtistNameAlbumName(){
		if (artists == null || album == null)
			return null;
		return  getArtistName() + " - " + getAlbumName();
	}

	public String getPreviewUrl() {
		return preview_url;
	}

	@Override
	public String toString(){
		return name;
	}

}
