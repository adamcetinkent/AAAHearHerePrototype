package com.yosoyo.aaahearhereprototype.SpotifyClasses;

import com.yosoyo.aaahearhereprototype.AsyncDataManager;

import static java.lang.System.currentTimeMillis;

/**
 * Created by Adam Kent on 06/01/2018.
 *
 * The Spotify specification for an Token
 */
public class SpotifyToken extends SpotifyTokenRaw {

	private static SpotifyToken spotifyToken;
	private static long refreshAfter;

	public SpotifyToken(SpotifyTokenRaw rawToken){
		this.access_token = rawToken.access_token;
		this.token_type = rawToken.token_type;
		this.expires_in = rawToken.expires_in;
		this.scope = rawToken.scope;
	}

	public static void setSpotifyToken(SpotifyToken token){
		spotifyToken = token;
		refreshAfter = System.currentTimeMillis() + spotifyToken.expires_in * 500;
	}

	//public static SpotifyToken getSpotifyToken(){ return spotifyToken; }

	public static void getAuthorisation(final GetSpotifyAuthorisationCallback getSpotifyAuthorisationCallback) {

		if (isExpired()) {
			AsyncDataManager.spotifyAPIRequestToken(
				new AsyncDataManager.spotifyAPIRequestTokenCallback() {
					@Override
					public void returnSpotifyToken(boolean success) {
						if (success) {
							getSpotifyAuthorisationCallback.returnSpotifyAuthorisation("Bearer " + spotifyToken.access_token);
						} else {
							getSpotifyAuthorisationCallback.returnSpotifyAuthorisation(null);
						}
					}
				});
		} else {
			getSpotifyAuthorisationCallback.returnSpotifyAuthorisation("Bearer " + spotifyToken.access_token);
		}
	}

	private static boolean isExpired(){
		return currentTimeMillis() > refreshAfter;
	}

}
