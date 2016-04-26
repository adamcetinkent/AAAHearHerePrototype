package com.yosoyo.aaahearhereprototype.SpotifyClasses;

/**
 * Created by Adam Kent on 09/02/2016.
 *
 * The Spotify specification for an Artist.
 */
@SuppressWarnings({"MismatchedReadAndWriteOfArray", "unused"})
public class SpotifyArtist {

	private SpotifyExternalURL external_urls;
	private SpotifyFollowers followers;
	private String genres[];
	private String href;
	private String id;
	private SpotifyImage images[];
	private String name;
	private int popularity;
	private String type;
	private String uri;

	public SpotifyImage[] getImages() {
		return images;
	}

	public SpotifyImage getImages(int index) {
		if (index < images.length) {
			return images[index];
		} else {
			return null;
		}
	}

	public String getID(){
		return id;
	}

	public String getGenres(){
		if (genres.length == 0)
			return "";
		StringBuilder sb = new StringBuilder();
		for (String genre : genres) {
			sb.append(genre).append("; ");
		}
		return sb.substring(0, sb.length()-2);
	}

	public String getName(){
		return name;
	}

	@Override
	public String toString(){
		return name;
	}

	public String getImageURL(){
		SpotifyImage image = getImages(0);
		if (image != null){
			return image.getUrl();
		}
		return null;
	}

}
