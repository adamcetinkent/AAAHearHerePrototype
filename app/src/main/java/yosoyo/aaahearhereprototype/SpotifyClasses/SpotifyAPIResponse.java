package yosoyo.aaahearhereprototype.SpotifyClasses;

/**
 * Created by Adam Kent on 10/02/2016.
 *
 * This is returned by a SpotifyAPIRequest. The type of search request sent to Spotify determines
 * which fields will be filled.
 */
public class SpotifyAPIResponse {

	SpotifyPaging<SpotifyArtist> artists;

	public SpotifyPaging<SpotifyArtist> getArtists() {
		return artists;
	}

}
