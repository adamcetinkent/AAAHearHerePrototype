package com.yosoyo.aaahearhereprototype.SpotifyClasses;

/**
 * Created by adam on 01/03/16.
 *
 * The Spotify specification for an Album
 */
@SuppressWarnings({"MismatchedReadAndWriteOfArray", "unused"})
public class SpotifyAlbum {

	private String album_type;
	private SpotifyArtistSimple[] artists;
	private String[] available_markets;
	private SpotifyCopyright[] copyrights;
	private SpotifyExternalID externalIDs;
	private SpotifyExternalURL externalURLs;
	private String[] genres;
	private String href;
	private String id;
	private SpotifyImage[] images;
	private String name;
	private int popularity;
	private String release_date;
	private String release_date_precision;
	private SpotifyPaging<SpotifyTrackSimple> tracks;
	private String type;
	private String uri;

	public SpotifyImage getImages(int index) {
		if (index < images.length) {
			return images[index];
		} else {
			return null;
		}
	}

	public String getName(){
		return name;
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

	public String getArtistID(){
		if (artists == null)
			return null;
		return artists[0].getID();
	}

	public String getID(){
		return id;
	}

	public String getImageURL(){
		SpotifyImage image = getImages(0);
		if (image != null){
			return image.getUrl();
		}
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SpotifyAlbum that = (SpotifyAlbum) o;

		return id.equals(that.id);

	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

}
