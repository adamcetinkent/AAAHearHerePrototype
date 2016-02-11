package yosoyo.aaahearhereprototype.SpotifyClasses;

/**
 * Created by Adam Kent on 10/02/2016.
 *
 * This is returned by a SpotifyAPIRequest. The type of search request sent to Spotify determines
 * which fields will be filled.
 */
public class SpotifyAPIResponse {

	SpotifyPaging<SpotifyArtist> artists;
	SpotifyPaging<SpotifyTrack> tracks;

	public SpotifyPaging<SpotifyArtist> getArtists() {
		return artists;
	}

	public SpotifyPaging<SpotifyTrack> getTracks() {
		return tracks;
	}

}
