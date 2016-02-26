package yosoyo.aaahearhereprototype;

import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.TestServerClasses.CachedSpotifyTrack;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPostUser;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPostUserTrack;

/**
 * Created by adam on 26/02/16.
 */
public class ZZZDataHolder {

	public static List<TestPostUser> testPostUsers;
	public static List<CachedSpotifyTrack> cachedSpotifyTracks;
	public static List<TestPostUserTrack> testPostUserTracks;

	public static void createTestPostUserTracks(){
		if (testPostUsers == null || cachedSpotifyTracks == null)
			return;

		testPostUserTracks = new ArrayList<>();
		for (TestPostUser testPostUser : testPostUsers){
			for (CachedSpotifyTrack cachedSpotifyTrack : cachedSpotifyTracks){
				if (testPostUser.getTestPost().getTrack().equals(cachedSpotifyTrack.getTrackID())){
					testPostUserTracks.add(new TestPostUserTrack(testPostUser, cachedSpotifyTrack));
				}
			}
		}
	}

}
