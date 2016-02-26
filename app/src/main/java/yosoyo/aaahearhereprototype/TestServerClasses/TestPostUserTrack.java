package yosoyo.aaahearhereprototype.TestServerClasses;

/**
 * Created by adam on 26/02/16.
 */
public class TestPostUserTrack {

	TestPostUser testPostUser;
	CachedSpotifyTrack cachedSpotifyTrack;

	public TestPostUserTrack(TestPostUser testPostUser, CachedSpotifyTrack cachedSpotifyTrack){
		this.testPostUser = testPostUser;
		this.cachedSpotifyTrack = cachedSpotifyTrack;
	}

	public TestPostUser getTestPostUser() {
		return testPostUser;
	}

	public CachedSpotifyTrack getCachedSpotifyTrack() {
		return cachedSpotifyTrack;
	}
}
