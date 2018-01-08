package com.yosoyo.aaahearhereprototype;

import android.content.Context;
import android.location.Location;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.google.android.gms.maps.model.LatLngBounds;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Database.DatabaseHelper;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHCachedSpotifyTrack;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHComment;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequest;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequestUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHLike;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHMute;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHNotification;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPostFull;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPostFullProcess;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUserFull;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUserFullProcess;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.AuthenticateUserFacebookTask;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.GetUserTask;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.PostUserTask;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHPostTagsArray;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.GetSpotifyAuthorisationCallback;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyAlbum;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyArtist;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyToken;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.Tasks.SpotifyAPIRequestToken;

import java.net.HttpURLConnection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adam on 02/03/16.
 *
 * Responsible for abstracting the provenance of data requested by the app.
 * Data can be requested from two sources: the SQLite Database and/or the Web API. Database requests
 * are handed over to the {@link DatabaseHelper} whereas the web calls are sent to the {@link WebHelper}.
 */
public class AsyncDataManager {
	public static final String TAG = "AsyncDataManager";

	private static Context context; // required for database calls

	public static void setContext(Context newContext){
		context = newContext;
	}

	//**********************************************************************************************
	// FACEBOOK AUTHENTICATION
	//**********************************************************************************************

	public interface AuthenticateUserCallback {
		void returnAuthenticationResult(boolean success);
	}

	/**
	 * Check the {@link AccessToken} against Facebook's API. Success updates the current user.
	 *
	 * @param accessToken	: the {@link AccessToken} to be tested by Facebook
	 * @param callback		: return results via callback
	 */
	public static void authenticateUser(AccessToken accessToken, final AuthenticateUserCallback callback){
		new AuthenticateUserFacebookTask(
			accessToken,
			new AuthenticateUserFacebookTask.Callback() {
				@Override
				public void returnAuthenticationResult(Integer result, HHUserFullProcess returnedUser, String HHAuthToken) {
					if (result == HttpURLConnection.HTTP_OK) {
						// USER ALREADY REGISTERED :: SIGN IN
						HHUser.setCurrentUser(returnedUser);
						HHUser.setAuthorisationToken(HHAuthToken);
						DatabaseHelper.processCurrentUser(
							context,
							returnedUser,
							new DatabaseHelper.ProcessCurrentUserCallback() {
								@Override
								public void returnProcessCurrentUser(HHUserFull hhUserFull) {
									callback.returnAuthenticationResult(true);
								}
							});
					} else if (result == HttpURLConnection.HTTP_ACCEPTED) {
						// USER NOT PREVIOUSLY REGISTERED :: REGISTER
						HHUser user = new HHUser(Profile.getCurrentProfile());
						new PostUserTask(
							user,
							new PostUserTask.Callback() {
								@Override
								public void returnPostUser(Boolean success, HHUser userReturned) {
									callback.returnAuthenticationResult(true);
								}
							}).execute();
					} else {
						// FAILURE
						callback.returnAuthenticationResult(false);
					}
				}
			}).execute();
	}

	//**********************************************************************************************
	// REQUEST SPOTIFY TOKEN
	//**********************************************************************************************

	public interface spotifyAPIRequestTokenCallback {
		void returnSpotifyToken(boolean success);
	}

	/**
	 * Fetch a Spotify Token from the Spotify API
	 *
	 * @param callback		: return results via callback
	 */
	public static void spotifyAPIRequestToken(final spotifyAPIRequestTokenCallback callback){
		new SpotifyAPIRequestToken(
			new SpotifyAPIRequestToken.Callback() {
				@Override
				public void returnSpotifyToken(boolean success, SpotifyToken spotifyToken) {
					if (success) {
						SpotifyToken.setSpotifyToken(spotifyToken);
					}
					callback.returnSpotifyToken(success);
				}
			},
			context).execute();
	}

	//**********************************************************************************************
	// CURRENT USER
	//**********************************************************************************************

	public interface UpdateCurrentUserCallback{
		void returnUpdateCurrentUser(boolean success);
	}

	/**
	 * Update the current user
	 *
	 * @param callback	: return result via callback
	 */
	public static void updateCurrentUser(final UpdateCurrentUserCallback callback){
		new GetUserTask(
			HHUser.getAuthorisationToken(),
			HHUser.getCurrentUserID(),
			new GetUserTask.Callback() {
				@Override
				public void returnGetUser(final boolean success, HHUserFullProcess returnedUser) {
					if (success){
						HHUser.setCurrentUser(returnedUser);
						DatabaseHelper.processCurrentUser(
							context,
							returnedUser,
							new DatabaseHelper.ProcessCurrentUserCallback() {
								@Override
								public void returnProcessCurrentUser(HHUserFull hhUserFull) {
									callback.returnUpdateCurrentUser(true);
								}
							});
					} else {
						callback.returnUpdateCurrentUser(false);
					}
				}
			}
		).execute();
	}

	//**********************************************************************************************
	// POSTS
	//**********************************************************************************************

	// POST POST ----------------------

	public interface PostPostCallback{
		void returnPostPost(boolean success, HHPostFullProcess returnedPost);
	}

	/**
	 * Attempt to post a {@link HHPostFull} to Hear Here
	 *
	 * @param post		: {@link HHPostTagsArray} to post
	 * @param callback	: results returned via callback
	 */
	public static void postPost(final HHPostTagsArray post, final PostPostCallback callback){
		WebHelper.postPost(post, new WebHelper.PostPostCallback() {
			@Override
			public void returnPostPost(boolean success, HHPostFullProcess webPostToProcess) {
				callback.returnPostPost(success, webPostToProcess);
			}
		});
	}

	// GET POST -----------------------

	public interface GetPostCallback {
		void returnGetPost(HHPostFull post);
	}

	public interface GetAllPostsCallback extends GetPostCallback {
		void returnPostList(List<HHPostFull> posts);
		void warnNoEarlierPosts();
		void warnNoLaterPosts();
	}

	/**
	 * Fetch specified {@link HHPostFull} - if current user is allowed to see
	 *
	 * @param postID	: ID of requested post
	 * @param callback	: results returned via callback
	 */

	public static void getPost(long postID, GetPostCallback callback){
		getCachedPost(postID, callback);
		getWebPost(postID, callback);
	}

	private static void getCachedPost(long postID, final GetPostCallback callback){
		DatabaseHelper.getCachedPost(
			context,
			postID,
			new DatabaseHelper.GetCachedPostCallback() {
				@Override
				public void returnGetCachedPost(HHPostFull cachedPost) {
					callback.returnGetPost(cachedPost);
					WebHelper.preLoadPostBitmaps(cachedPost);
				}
			});
	}

	public static void getWebPost(long post_id, final GetPostCallback callback){
		WebHelper.getPost(post_id, new WebHelper.GetPostCallback() {
			@Override
			public void returnGetPost(HHPostFullProcess webPostToProcess) {
				ArrayList<HHPostFullProcess> webPostsToProcess = new ArrayList<>();
				webPostsToProcess.add(webPostToProcess);
				DatabaseHelper.processWebPosts(context, webPostsToProcess, callback);
			}
		});
	}

	// GET ALL POSTS

	/**
	 * Fetch the next batch of posts that the current user is allowed to see
	 *
	 * @param beforeTime 	: fetch batch before this date
	 * @param callback		: return results via callback
	 */
	public static void getAllPosts(final Timestamp beforeTime,
								   final Long[] excludeIDs,
								   final GetAllPostsCallback callback){
		//getAllCachedPosts(beforeTime, callback);
		getAllWebPosts(beforeTime, excludeIDs, callback);
	}

	private static void getAllCachedPosts(final Timestamp beforeTime,
										  final GetAllPostsCallback callback){
		if (beforeTime == null)
			return;
		//TODO INCLUDE BEFORETIME
		DatabaseHelper.getAllCachedPosts(context, new DatabaseHelper.GetAllCachedPostsCallback() {
			@Override
			public void returnGetAllCachedPosts(List<HHPostFull> cachedPosts) {
				callback.returnPostList(cachedPosts);
				WebHelper.preLoadPostBitmaps(cachedPosts);
			}
		});
	}

	private static void getAllWebPosts(final Timestamp beforeTime,
									   final Long[] excludeIDs,
									   final GetAllPostsCallback callback){
		WebHelper.getAllPosts(
			beforeTime,
			excludeIDs,
			new WebHelper.GetAllPostsCallback() {
				@Override
				public void returnGetAllPosts(List<HHPostFullProcess> webPostsToProcess) {
					if (webPostsToProcess != null)
						DatabaseHelper.processWebPosts(context, webPostsToProcess, callback);
				}

				@Override
				public void warnNoEarlierPosts(){
					callback.warnNoEarlierPosts();
				}

				@Override
				public void warnNoLaterPosts(){}
			});
	}

	/**
	 * Fetch the next batch of posts that the current user is allowed to see
	 *
	 * @param sinceTime 	: fetch batch since this date
	 * @param callback		: return results via callback
	 */
	public static void getAllPostsSince(final Timestamp sinceTime,
								   		final Long[] excludeIDs,
								   		final GetAllPostsCallback callback){
		//getAllCachedPosts(beforeTime, callback);
		getAllWebPostsSince(sinceTime, excludeIDs, callback);
	}

	private static void getAllCachedPostsSince(final Timestamp beforeTime,
											   final GetAllPostsCallback callback){
		if (beforeTime == null)
			return;
		//TODO INCLUDE BEFORETIME
		DatabaseHelper.getAllCachedPosts(context, new DatabaseHelper.GetAllCachedPostsCallback() {
			@Override
			public void returnGetAllCachedPosts(List<HHPostFull> cachedPosts) {
				callback.returnPostList(cachedPosts);
				WebHelper.preLoadPostBitmaps(cachedPosts);
			}
		});
	}

	private static void getAllWebPostsSince(final Timestamp sinceTime,
									   		final Long[] excludeIDs,
									   		final GetAllPostsCallback callback){
		WebHelper.getAllPostsSince(
			sinceTime,
			excludeIDs,
			new WebHelper.GetAllPostsCallback() {
				@Override
				public void returnGetAllPosts(List<HHPostFullProcess> webPostsToProcess) {
					if (webPostsToProcess != null)
						DatabaseHelper.processWebPosts(context, webPostsToProcess, callback);
				}

				@Override
				public void warnNoEarlierPosts(){
					callback.warnNoEarlierPosts();
				}

				@Override
				public void warnNoLaterPosts(){
					callback.warnNoLaterPosts();
				}
			});
	}

	// GET USER POSTS

	/**
	 * Fetch the next batch of posts by a specific user that the current user is allowed to see
	 *
	 * @param userID		: ID of user whose posts are fetched
	 * @param beforeTime	: fetch batch before this date
	 * @param callback		: return results via callback
	 */
	public static void getUserPosts(final long userID,
									final Timestamp beforeTime,
									final Long[] excludeIDs,
									final GetAllPostsCallback callback){
		//getUserCachedPosts(userID, beforeTime, callback);
		getUserWebPosts(userID, beforeTime, excludeIDs, callback);
	}

	private static void getUserCachedPosts(final long userID,
										   final Timestamp beforeTime,
										   final GetAllPostsCallback callback){
		if (beforeTime == null)
			return;
		//TODO INCLUDE BEFORETIME
		DatabaseHelper.getUserCachedPosts(
			context,
			userID,
			new DatabaseHelper.GetAllCachedPostsCallback() {
				@Override
				public void returnGetAllCachedPosts(List<HHPostFull> cachedPosts) {
					callback.returnPostList(cachedPosts);
					WebHelper.preLoadPostBitmaps(cachedPosts);
				}
			});
	}

	private static void getUserWebPosts(final long userID,
										final Timestamp beforeTime,
										final Long[] excludeIDs,
										final GetAllPostsCallback callback){
		WebHelper.getUserPosts(
			userID,
			beforeTime,
			excludeIDs,
			new WebHelper.GetAllPostsCallback() {
				@Override
				public void returnGetAllPosts(List<HHPostFullProcess> webPostsToProcess) {
					if (webPostsToProcess != null)
						DatabaseHelper.processWebPosts(context, webPostsToProcess, callback);
				}

				@Override
				public void warnNoEarlierPosts(){
					callback.warnNoEarlierPosts();
				}

				@Override
				public void warnNoLaterPosts(){}
			});
	}

	// GET POSTS AT LOCATION

	public interface GetPostsAtLocationCallback{
		void returnPostsAtLocation(Location location, List<HHNotification> returnedPosts);
	}

	/**
	 * Fetch posts visible to current user at given location
	 *
	 * @param location	: {@link Location} to look for posts
	 * @param callback	: results returned via callback
	 */
	public static void getPostsAtLocation(final Location location,
										  final GetPostsAtLocationCallback callback){
		getPostsAtLocation(context,
						   location,
						   HHUser.getCurrentUserID(),
						   HHUser.getAuthorisationToken(),
						   callback);
	}

	/**
	 * Fetch posts visible to user at given location
	 *
	 * @param location	: {@link Location} to look for posts
	 * @param userID	: ID of user requesting posts
	 * @param callback	: results returned via callback
	 */
	public static void getPostsAtLocation(final Location location,
										  final long userID,
										  final String authToken,
										  final GetPostsAtLocationCallback callback) {
		getPostsAtLocation(context, location, userID, authToken, callback);
	}

	/**
	 * Fetch posts visible to user at given location
	 *
	 * @param context	: {@link Context} for database request
	 * @param location	: {@link Location} to look for posts
	 * @param userID	: ID of user requesting posts
	 * @param authToken : authentication token for Hear Here API
	 * @param callback	: results returned via callback
	 */
	public static void getPostsAtLocation(final Context context,
										  final Location location,
										  final long userID,
										  final String authToken,
										  final GetPostsAtLocationCallback callback){
		WebHelper.getPostsAtLocation(
			location,
			userID,
			authToken,
			new WebHelper.GetPostsAtLocationCallback() {
				@Override
				public void returnGetPostsAtLocation(List<HHNotification> webPosts) {
					callback.returnPostsAtLocation(location, webPosts);
				}
			});
	}

	// GET POSTS WITHIN BOUNDS

	/**
	 * Fetch posts visible to current user within given bounds
	 * @param bounds    	: {@link LatLngBounds} within which to look for posts
	 * @param process		: if true, add the posts to the database
	 * @param excludeIDs	: IDs of posts not to be returned
	 * @param callback  	: results returned via callback
	 */
	public static void getPostsWithinBounds(final LatLngBounds bounds,
											final boolean process,
											final Long[] excludeIDs,
											final GetAllPostsCallback callback){
		getPostsWithinBounds(context, bounds, HHUser.getCurrentUserID(), excludeIDs, process, callback);
	}

	/**
	 * Fetch posts visible to user within given bounds
	 *
	 * @param bounds		: {@link LatLngBounds} within which to look for posts
	 * @param userID		: ID of user requesting posts
	 * @param excludeIDs	: IDs of posts not to be returned
	 * @param process		: if true, add the posts to the database
	 * @param callback		: results returned via callback
	 */
	public static void getPostsWithinBounds(final LatLngBounds bounds,
											final long userID,
											final Long[] excludeIDs,
											final boolean process,
											final GetAllPostsCallback callback) {
		getPostsWithinBounds(context, bounds, userID, excludeIDs, process, callback);
	}

	/**
	 * Fetch posts visible to user within given bounds
	 *
	 * @param context		: {@link Context} for database request
	 * @param bounds		: {@link LatLngBounds} within which to look for posts
	 * @param userID		: ID of user requesting posts
	 * @param excludeIDs	: IDs of posts not to be returned
	 * @param process		: if true, add the posts to the database
	 * @param callback		: results returned via callback
	 */
	public static void getPostsWithinBounds(final Context context,
										  	final LatLngBounds bounds,
										  	final long userID,
											final Long[] excludeIDs,
											final boolean process,
										  	final GetAllPostsCallback callback){
		WebHelper.getPostsWithinBounds(
			bounds,
			userID,
			excludeIDs,
			new WebHelper.GetAllPostsCallback() {
				@Override
				public void returnGetAllPosts(List<HHPostFullProcess> webPostsToProcess) {
					if (webPostsToProcess != null) {
						if (process)
							DatabaseHelper.processWebPosts(context, webPostsToProcess, callback);
						else {
							List<HHPostFull> posts = new ArrayList<>();
							for (HHPostFullProcess processPost : webPostsToProcess){
								posts.add(new HHPostFull(processPost));
							}
							callback.returnPostList(posts);
						}
					}
				}

				@Override
				public void warnNoEarlierPosts() {}

				@Override
				public void warnNoLaterPosts() {
					callback.warnNoLaterPosts();
				}
			});
	}

	//**********************************************************************************************
	// USER
	//**********************************************************************************************

	// GET USER -----------------------

	public interface GetUserCallback {
		void returnGetCachedUser(final HHUserFull returnedUser);
		void returnGetWebUser(final HHUserFull returnedUser);
	}

	/**
	 * Fetch a {@link HHUserFull}
	 *
	 * @param userID	: ID of user
	 * @param webOnly	: if true, does not query database
	 * @param callback	: results returned via callback
	 */

	public static void getUser(final String authToken,
							   final long userID,
							   final boolean webOnly,
							   final GetUserCallback callback){
		if (!webOnly)
			getCachedUser(userID, callback);
		getWebUser(authToken, userID, callback);
	}

	private static void getCachedUser(final long userID, final GetUserCallback callback){
		DatabaseHelper.getUser(
			context,
			userID,
			new DatabaseHelper.GetUserCallback() {
				@Override
				public void returnGetUser(HHUserFull user) {
					callback.returnGetCachedUser(user);
					if (user != null && user.getUser() != null)
						WebHelper.preLoadUserBitmaps(user);
				}
			}
		);
	}

	private static void getWebUser(final String authToken,
								   final long userID,
								   final GetUserCallback callback){
		WebHelper.getUser(
			authToken,
			userID,
			new WebHelper.GetUserCallback() {
				@Override
				public void returnGetUser(HHUserFull user) {
					callback.returnGetWebUser(user);
				}
			}
		);
	}

	// GET USER PRIVACY ---------------

	public interface GetUserPrivacyCallback{
		void returnCachedUserPrivacy(boolean userPrivacy);
		void returnWebUserPrivacy(boolean userPrivacy);
	}

	/**
	 * Determines whether user is private to current user
	 *
	 * @param userID	: ID of user being viewed
	 * @param callback	: results returned via callback
	 */
	public static void getUserPrivacy(final long userID, final GetUserPrivacyCallback callback) {
		getUserPrivacy(context, HHUser.getAuthorisationToken(), userID, false, callback);
	}

	/**
	 * Determines whether user is private to current user
	 *
	 * @param userID	: ID of user being viewed
	 * @param webOnly	: if true, does not query database
	 * @param callback	: results returned via callback
	 */
	public static void getUserPrivacy(final long userID, final boolean webOnly, final GetUserPrivacyCallback callback) {
		getUserPrivacy(context, HHUser.getAuthorisationToken(), userID, webOnly, callback);
	}

	/**
	 * Determines whether user is private to current user
	 *
	 * @param context 	: {@link Context} required for database requests
	 * @param userID	: ID of user being viewed
	 * @param webOnly	: if true, does not query database
	 * @param callback	: results returned via callback
	 */
	public static void getUserPrivacy(Context context,
									  final String authToken,
									  final long userID,
									  final boolean webOnly,
									  final GetUserPrivacyCallback callback){
		if (!webOnly) {
			getUserCachedPrivacy(context, userID, callback);
		}
		getUserWebPrivacy(authToken, userID, callback);
	}

	private static void getUserCachedPrivacy(Context context, final long userID, final  GetUserPrivacyCallback callback){
		DatabaseHelper.getUserCachedPrivacy(
			context,
			userID,
			new DatabaseHelper.GetUserCachedPrivacyCallback() {
				@Override
				public void returnUserCachedPrivacy(boolean userPrivacy) {
					callback.returnCachedUserPrivacy(userPrivacy);
				}
			}
		);
	}

	private static void getUserWebPrivacy(final String authToken,
										  final long userID,
										  final GetUserPrivacyCallback callback){
		WebHelper.getUserPrivacy(
			authToken,
			userID,
			new WebHelper.GetUserPrivacyCallback() {
				@Override
				public void returnGetUserPrivacy(boolean userPrivacy) {
					callback.returnWebUserPrivacy(userPrivacy);
				}
			}
		);
	}

	// GET USER POST COUNT ------------

	public interface GetUserPostCountCallback{
		void returnCachedUserPostCount(int postCount);
		void returnWebUserPostCount(int postCount);
	}

	/**
	 * Fetch number of posts by user
	 *
	 * @param userID	: ID of user
	 * @param callback	: results returned via callback
	 */
	public static void getUserPostCount(final long userID, final GetUserPostCountCallback callback) {
		getUserPostCount(context, userID, false, callback);
	}

	/**
	 * Fetch number of posts by user
	 *
	 * @param userID	: ID of user
	 * @param webOnly 	: if true, does not query database
	 * @param callback	: results returned via callback
	 */
	public static void getUserPostCount(final long userID, final boolean webOnly, final GetUserPostCountCallback callback) {
		getUserPostCount(context, userID, webOnly, callback);
	}

	/**
	 * Fetch number of posts by user
	 *
	 * @param context 	: {@link Context} required for datbase queries
	 * @param userID	: ID of user
	 * @param webOnly 	: if true, does not query database
	 * @param callback	: results returned via callback
	 */
	public static void getUserPostCount(Context context, final long userID, final boolean webOnly, final GetUserPostCountCallback callback){
		if (!webOnly) {
			getUserCachedPostCount(context, userID, callback);
		}
		getUserWebPostCount(userID, callback);
	}

	private static void getUserCachedPostCount(Context context, final long userID, final  GetUserPostCountCallback callback){
		DatabaseHelper.getUserCachedPostCount(
			context,
			userID,
			new DatabaseHelper.GetUserCachedPostCountCallback() {
				@Override
				public void returnUserCachedPostCount(int postCount) {
					callback.returnCachedUserPostCount(postCount);
				}
			}
		);
	}

	private static void getUserWebPostCount(final long userID, final GetUserPostCountCallback callback){
		WebHelper.getUserPostCount(
			userID, new WebHelper.GetUserPostCountCallback() {
				@Override
				public void returnGetUserPostCount(int postCount) {
					callback.returnWebUserPostCount(postCount);
				}
			}
		);
	}

	// GET USER FOLLOWERS IN COUNT ----

	public interface GetUserFollowersInCountCallback{
		void returnCachedUserFollowersInCount(int followersInCount);
		void returnWebUserFollowersInCount(int followersInCount);
	}

	/**
	 * Fetch number of users that follow user
	 *
	 * @param userID	: ID of user
	 * @param callback	: results returned via callback
	 */
	public static void getUserFollowersInCount(final long userID,
											   final GetUserFollowersInCountCallback callback) {
		getUserFollowersInCount(context, HHUser.getAuthorisationToken(), userID, false, callback);
	}

	/**
	 * Fetch number of users that follow user
	 *
	 * @param userID	: ID of user
	 * @param webOnly	: if true, does not query database
	 * @param callback	: results returned via callback
	 */
	public static void getUserFollowersInCount(final long userID, final boolean webOnly, final GetUserFollowersInCountCallback callback) {
		getUserFollowersInCount(context, HHUser.getAuthorisationToken(), userID, webOnly, callback);
	}

	/**
	 * Fetch number of users that follow user
	 *
	 * @param context	: {@link Context} required for database queries
	 * @param userID	: ID of user
	 * @param webOnly	: if true, does not query databse
	 * @param callback	: results returned via callback
	 */
	public static void getUserFollowersInCount(Context context,
											   final String authToken,
											   final long userID,
											   final boolean webOnly,
											   final GetUserFollowersInCountCallback callback){
		if (!webOnly) {
			getUserCachedFollowersInCount(context, userID, callback);
		}
		getUserWebFollowersInCount(authToken, userID, callback);
	}

	private static void getUserCachedFollowersInCount(Context context, final long userID, final  GetUserFollowersInCountCallback callback){
		DatabaseHelper.getUserCachedFollowersInCount(
			context,
			userID,
			new DatabaseHelper.GetUserCachedFollowersInCountCallback() {
				@Override
				public void returnUserCachedFollowersInCount(int followersInCount) {
					callback.returnCachedUserFollowersInCount(followersInCount);
				}
			}
		);
	}

	private static void getUserWebFollowersInCount(final String authToken,
												   final long userID,
												   final GetUserFollowersInCountCallback callback){
		WebHelper.getUserFollowersInCount(
			authToken,
			userID,
			new WebHelper.GetUserFollowersInCountCallback() {
				@Override
				public void returnGetUserFollowersInCount(int followersInCount) {
					callback.returnWebUserFollowersInCount(followersInCount);
				}
			}
		);
	}

	// GET USER FOLLOWERS OUT COUNT ---

	public interface GetUserFollowersOutCountCallback{
		void returnCachedUserFollowersOutCount(int followersOutCount);
		void returnWebUserFollowersOutCount(int followersOutCount);
	}

	/**
	 * Fetch number of users that user follows
	 *
	 * @param userID	: ID of user
	 * @param callback	: results returned via callback
	 */
	public static void getUserFollowersInCount(final long userID, final GetUserFollowersOutCountCallback callback) {
		getUserFollowersOutCount(context, HHUser.getAuthorisationToken(), userID, false, callback);
	}

	/**
	 * Fetch number of users that user follows
	 *
	 * @param userID	: ID of user
	 * @param webOnly 	: if true, does not query database
	 * @param callback	: results returned via callback
	 */
	public static void getUserFollowersOutCount(final long userID, final boolean webOnly, final GetUserFollowersOutCountCallback callback) {
		getUserFollowersOutCount(context, HHUser.getAuthorisationToken(), userID, webOnly, callback);
	}

	/**
	 * Fetch number of users that user follows
	 *
	 * @param context 	: {@link Context} required for database queries
	 * @param userID	: ID of user
	 * @param webOnly 	: if true, does not query database
	 * @param callback	: results returned via callback
	 */
	public static void getUserFollowersOutCount(Context context,
												final String authToken,
												final long userID,
												final boolean webOnly,
												final GetUserFollowersOutCountCallback callback){
		if (!webOnly) {
			getUserCachedFollowersOutCount(context, userID, callback);
		}
		getUserWebFollowersOutCount(authToken, userID, callback);
	}

	private static void getUserCachedFollowersOutCount(Context context, final long userID, final  GetUserFollowersOutCountCallback callback){
		DatabaseHelper.getUserCachedFollowersOutCount(
			context,
			userID,
			new DatabaseHelper.GetUserCachedFollowersOutCountCallback() {
				@Override
				public void returnUserCachedFollowersOutCount(int followersOutCount) {
					callback.returnCachedUserFollowersOutCount(followersOutCount);
				}
			}
		);
	}

	private static void getUserWebFollowersOutCount(final String authToken,
													final long userID,
													final GetUserFollowersOutCountCallback callback){
		WebHelper.getUserFollowersOutCount(
			authToken,
			userID,
			new WebHelper.GetUserFollowersOutCountCallback() {
				@Override
				public void returnGetUserFollowersOutCount(int followersOutCount) {
					callback.returnWebUserFollowersOutCount(followersOutCount);
				}
			}
		);
	}

	// SEARCH USERS -------------------

	public interface SearchUsersCallback {
		void returnSearchUsers(final String query, final List<HHUser> foundUsers);
	}

	/**
	 * Search for users
	 *
	 * @param query		: query string to perform search
	 * @param callback	: results returned via callback
	 */
	public static void searchUsers(final String authToken, final String query, final SearchUsersCallback callback){
		WebHelper.searchUsers(authToken, query, new WebHelper.SearchUsersCallback() {
			@Override
			public void returnSearchUsers(List<HHUser> foundUsers) {
				callback.returnSearchUsers(query, foundUsers);
			}
		});
	}

	//**********************************************************************************************
	// COMMENTS
	//**********************************************************************************************

	// POST COMMENT -------------------

	public interface PostCommentCallback{
		void returnPostComment(HHComment returnedComment);
	}

	/**
	 * Attempt to post a {@link HHComment} to Hear Here
	 *
	 * @param comment	: {@link HHComment} to be created
	 * @param callback	: results returned via callback
	 */
	public static void postComment(final String authToken, final HHComment comment, final PostCommentCallback callback){
		WebHelper.postComment(
			authToken,
			comment,
			new WebHelper.PostCommentCallback() {
				@Override
				public void returnPostComment(final HHComment returnedComment) {
					if (returnedComment != null) {
						DatabaseHelper.insertComment(
							context,
							returnedComment,
							new DatabaseHelper.InsertCommentCallback() {
								@Override
								public void returnInsertComment(Long commentID, HHComment comment) {
									callback.returnPostComment(returnedComment);
								}
							});
					} else {
						callback.returnPostComment(null);
					}
				}
			});
	}

	//**********************************************************************************************
	// LIKES
	//**********************************************************************************************

	// POST LIKE ----------------------

	public interface PostLikeCallback{
		void returnPostLike(HHLike returnedLike);
	}

	/**
	 * Attempt to post a {@link HHLike} to Hear Here
	 *
	 * @param like		: {@link HHLike} to be created
	 * @param callback	: results returned via callback
	 */
	public static void postLike(final String authToken, final HHLike like, final PostLikeCallback callback){
		WebHelper.postLike(
			authToken,
			like,
			new WebHelper.PostLikeCallback() {
				@Override
				public void returnPostLike(final HHLike returnedLike) {
					DatabaseHelper.insertLike(
						context,
						returnedLike,
						new DatabaseHelper.InsertLikeCallback() {
							@Override
							public void returnInsertLike(Long likeID, HHLike like) {
								callback.returnPostLike(returnedLike);
							}
						});
				}
			});
	}

	// DELETE LIKE --------------------

	public interface DeleteLikeCallback{
		void returnDeleteLike(boolean success);
	}

	/**
	 * Attempt to delete a {@link HHLike} from Hear Here
	 *
	 * @param like		: {@link HHLike} to be deleted
	 * @param callback	: results returned via callback
	 */
	public static void deleteLike(final String authToken, final HHLike like, final DeleteLikeCallback callback){
		WebHelper.deleteLike(
			authToken,
			like,
			new WebHelper.DeleteLikeCallback() {
				@Override
				public void returnDeleteLike(boolean success) {
					if (success) {
						DatabaseHelper.deleteLike(
							context,
							like,
							new DatabaseHelper.DeleteLikeCallback() {
								@Override
								public void returnDeleteLike(boolean success) {
									callback.returnDeleteLike(success);
								}
							});
					} else {
						callback.returnDeleteLike(false);
					}
				}
			});
	}

	//**********************************************************************************************
	// FOLLOWS
	//**********************************************************************************************

	// DELETE FOLLOW ------------------

	public interface DeleteFollowCallback {
		void returnDeleteFollow(boolean success, HHFollowUser deletedFollow);
	}

	/**
	 * Attempt to delete a {@link HHFollowUser} from Hear Here
	 *
	 * @param follow	: {@link HHFollowUser} to be deleted
	 * @param callback	: results returned via callback
	 */
	public static void deleteFollow(final HHFollowUser follow, final DeleteFollowCallback callback){
		WebHelper.deleteFollow(follow, new WebHelper.DeleteFollowCallback() {
			@Override
			public void returnDeleteFollow(boolean success) {
				if (success) {
					DatabaseHelper.deleteFollow(
						context,
						follow,
						new DatabaseHelper.DeleteFollowCallback() {
							@Override
							public void returnDeleteFollow(boolean success) {
								callback.returnDeleteFollow(success, follow);
							}
						});
				} else {
					callback.returnDeleteFollow(false, follow);
				}
			}
		});
	}

	//**********************************************************************************************
	// FOLLOW REQUESTS
	//**********************************************************************************************

	// POST FOLLOW REQUEST ------------

	public interface PostFollowRequestCallback{
		void returnPostFollowRequest(boolean success, HHFollowRequestUser returnedFollowRequest);
		void returnPostFollowRequestAccepted(boolean success, HHFollowUser returnedFollowUser);
	}

	/**
	 * Attempt to post a {@link HHFollowRequest} to Hear Here
	 *
	 * @param followRequest	: {@link HHFollowRequest} to be created
	 * @param callback		: results returned by callback
	 */
	public static void postFollowRequest(final HHFollowRequest followRequest, final PostFollowRequestCallback callback){
		WebHelper.postFollowRequest(followRequest, new WebHelper.PostFollowRequestCallback() {
			@Override
			public void returnPostFollowRequest(boolean success, HHFollowRequestUser returnedFollowRequest) {
				// follow request has been created, and awaits approval
				if (success) {
					DatabaseHelper.insertFollowRequest(
						context,
						returnedFollowRequest,
						new DatabaseHelper.InsertFollowRequestCallback() {
							@Override
							public void returnInsertFollowRequest(Long followRequestID, HHFollowRequestUser returnedFollowRequest) {
								callback.returnPostFollowRequest(followRequestID != -1,
																 returnedFollowRequest);
							}
						});
				}
			}

			@Override
			public void returnPostFollowRequestAccepted(boolean success, final HHFollowUser returnedFollow) {
				// follow request has been automatically approved, so follow returned instead
				if (success) {
					DatabaseHelper.insertFollow(
						context,
						returnedFollow,
						new DatabaseHelper.InsertFollowCallback() {
							@Override
							public void returnInsertFollow(Long followID, HHFollowUser follow) {
								callback.returnPostFollowRequestAccepted(followID != -1, returnedFollow);
							}
						}
					);
				}
			}
		});
	}

	// ACCEPT FOLLOW REQUEST ----------

	public interface AcceptFollowRequestCallback{
		void returnAcceptFollowRequest(boolean success, HHFollowRequestUser followRequest);
	}

	/**
	 * Attempt to accept a {@link HHFollowRequest}
	 *
	 * @param followRequest	: {@link HHFollowRequestUser} to be accepted
	 * @param callback		: results returned via callback
	 */
	public static void acceptFollowRequest(final HHFollowRequestUser followRequest, final AcceptFollowRequestCallback callback){
		WebHelper.acceptFollowRequest(followRequest, new WebHelper.AcceptFollowRequestCallback() {
			@Override
			public void returnAcceptFollowRequest(boolean success, final HHFollowRequestUser acceptedFollowRequest) {
				if (success) {
					DatabaseHelper.deleteFollowRequest(
						context,
						followRequest,
						new DatabaseHelper.DeleteFollowRequestCallback() {
							@Override
							public void returnDeleteFollowRequest(boolean success) {
								callback.returnAcceptFollowRequest(success, followRequest);
							}
						}
					);
				} else {
					callback.returnAcceptFollowRequest(false, followRequest);
				}
			}
		});
	}

	//TODO: DOCUMENTATION
	public static void acceptFollowRequest(final String authToken,
										   final HHNotification notification,
										   final AcceptFollowRequestCallback callback){
		WebHelper.acceptFollowRequest(
			authToken,
			notification,
				new WebHelper.AcceptFollowRequestCallback() {
				@Override
				public void returnAcceptFollowRequest(boolean success, final HHFollowRequestUser acceptedFollowRequest) {
					if (success) {
						DatabaseHelper.deleteFollowRequest(
							context,
							acceptedFollowRequest,
							new DatabaseHelper.DeleteFollowRequestCallback() {
								@Override
								public void returnDeleteFollowRequest(boolean success) {
									callback.returnAcceptFollowRequest(success, acceptedFollowRequest);
								}
							}
						);
					} else {
						callback.returnAcceptFollowRequest(false, acceptedFollowRequest);
					}
				}
			});
	}

	// DELETE FOLLOW REQUEST ----------

	public interface DeleteFollowRequestCallback{
		void returnDeleteFollowRequest(boolean success, HHFollowRequestUser followRequest);
	}

	/**
	 * Attempt to delete a {@link HHFollowRequestUser}
	 *
	 * @param followRequest	: {@link HHFollowRequestUser} to be deleted
	 * @param callback		: results returned via callback
	 */
	public static void deleteFollowRequest(final HHFollowRequestUser followRequest, final DeleteFollowRequestCallback callback){
		WebHelper.deleteFollowRequest(followRequest, new WebHelper.DeleteFollowRequestCallback() {
			@Override
			public void returnDeleteFollowRequest(boolean success) {
				if (success) {
					DatabaseHelper.deleteFollowRequest(
						context,
						followRequest,
						new DatabaseHelper.DeleteFollowRequestCallback() {
							@Override
							public void returnDeleteFollowRequest(boolean success) {
								callback.returnDeleteFollowRequest(success, followRequest);
							}
						}
					);
				} else {
					callback.returnDeleteFollowRequest(false, followRequest);
				}
			}
		});
	}

	//**********************************************************************************************
	// SPOTIFY
	//**********************************************************************************************

	// GET SPOTIFY TRACK --------------

	public interface  GetSpotifyTrackCallback{
		void returnSpotifyTrack(SpotifyTrack spotifyTrack);
		void returnCachedSpotifyTrack(HHCachedSpotifyTrack cachedSpotifyTrack);
	}

	/**
	 * Fetch {@link HHCachedSpotifyTrack}
	 *
	 * @param trackID	: ID of {@link SpotifyTrack}
	 * @param callback	: results returned via callback
	 */
	public static void getSpotifyTrack(final String trackID, final GetSpotifyTrackCallback callback){
		getSpotifyTrack(context, trackID, callback);
	}

	/**
	 * Fetch {@link HHCachedSpotifyTrack}
	 *
	 * @param context	: {@link Context} required for database queries
	 * @param trackID	: ID of {@link SpotifyTrack}
	 * @param callback	: results returned via callback
	 */
	public static void getSpotifyTrack(final Context context, final String trackID, final GetSpotifyTrackCallback callback){

		DatabaseHelper.getCachedSpotifyTrack(
			context,
			trackID,
			new DatabaseHelper.GetCachedSpotifyTrackCallback() {
				@Override
				public void returnGetCachedSpotifyTrack(HHCachedSpotifyTrack track) {
					if (track != null) {
						callback.returnCachedSpotifyTrack(track);
						WebHelper.preLoadTrackBitmaps(track);
						return;
					}

					SpotifyToken.getAuthorisation(
						new GetSpotifyAuthorisationCallback() {
							@Override
							public void returnSpotifyAuthorisation(String authorisation) {
								if (authorisation != null) {
									WebHelper.getSpotifyTrack(trackID, authorisation, new WebHelper.GetSpotifyTrackCallback() {
										@Override
										public void returnSpotifyTrack(SpotifyTrack spotifyTrack) {
											callback.returnSpotifyTrack(spotifyTrack);
											if (spotifyTrack != null) {
												DatabaseHelper.insertSpotifyTrack(
													context,
													spotifyTrack,
													new DatabaseHelper.InsertCachedSpotifyTrackCallback() {
														@Override
														public void returnGetCachedSpotifyTrack(HHCachedSpotifyTrack track) {
															callback.returnCachedSpotifyTrack(track);
														}
													});
											} else {
												callback.returnCachedSpotifyTrack(null);
											}
										}
									});
								} else {
									callback.returnCachedSpotifyTrack(null);
								}
							}
						});

				}
			});
	}

	// GET SPOTIFY ALBUM --------------

	public interface  GetSpotifyAlbumCallback{
		void returnSpotifyAlbum(SpotifyAlbum spotifyAlbum);
	}

	/**
	 * Fetch {@link SpotifyAlbum}
	 *
	 * @param albumID	: ID of {@link SpotifyAlbum}
	 * @param callback	: results returned via callback
	 */
	public static void getSpotifyAlbum(final String albumID, final GetSpotifyAlbumCallback callback){
		getSpotifyAlbum(context, albumID, callback);
	}

	/**
	 * Fetch {@link SpotifyAlbum}
	 *
	 * @param context	: {@link Context} required for database queries
	 * @param albumID	: ID of {@link SpotifyAlbum}
	 * @param callback	: results returned via callback
	 */
	public static void getSpotifyAlbum(final Context context, final String albumID, final GetSpotifyAlbumCallback callback) {

		SpotifyToken.getAuthorisation(new GetSpotifyAuthorisationCallback() {
			@Override
			public void returnSpotifyAuthorisation(String authorisation) {
				if (authorisation != null){
					WebHelper.getSpotifyAlbum(albumID, authorisation, new WebHelper.GetSpotifyAlbumCallback() {
						@Override
						public void returnSpotifyAlbum(SpotifyAlbum spotifyAlbum) {
							callback.returnSpotifyAlbum(spotifyAlbum);
						}
					});
				} else {
					callback.returnSpotifyAlbum(null);
				}
			}
		});
	}

	// GET SPOTIFY ARTIST -------------

	public interface  GetSpotifyArtistCallback{
		void returnSpotifyArtist(SpotifyArtist spotifyArtist);
	}

	/**
	 * Fetch {@link SpotifyArtist}
	 *
	 * @param artistID	: ID of {@link SpotifyArtist}
	 * @param callback	: results returned via callback
	 */
	public static void getSpotifyArtist(final String artistID, final GetSpotifyArtistCallback callback){
		getSpotifyArtist(context, artistID, callback);
	}

	/**
	 * Fetch {@link SpotifyArtist}
	 *
	 * @param context	: {@link Context} required for database queries
	 * @param artistID	: ID of {@link SpotifyArtist}
	 * @param callback	: results returned via callback
	 */
	public static void getSpotifyArtist(final Context context, final String artistID, final GetSpotifyArtistCallback callback) {

		SpotifyToken.getAuthorisation(new GetSpotifyAuthorisationCallback() {
			@Override
			public void returnSpotifyAuthorisation(String authorisation) {
				if (authorisation != null) {
					WebHelper.getSpotifyArtist(artistID, authorisation, new WebHelper.GetSpotifyArtistCallback() {
						@Override
						public void returnSpotifyArtist(SpotifyArtist spotifyArtist) {
							callback.returnSpotifyArtist(spotifyArtist);
						}
					});
				} else {
					callback.returnSpotifyArtist(null);
				}
			}
		});

	}

	// SEARCH SPOTIFY TRACKS ----------

	public interface SearchSpotifyTracksCallback{
		void returnSearchSpotifyTracks(List<SpotifyTrack> spotifyTracks, int totalTracks);
	}

	/**
	 * Search {@link SpotifyTrack}
	 *
	 * @param query		: search query
	 * @param callback	: results returned via callback
	 */
	public static void searchSpotifyTracks(final String query,
										   final SearchSpotifyTracksCallback callback){
		searchSpotifyTracks(query, 0, callback);
	}

	/**
	 * Search {@link SpotifyTrack}
	 *
	 * @param query		: search query
	 * @param offset	: offset from beginning of search
	 * @param callback	: results returned via callback
	 */
	public static void searchSpotifyTracks(final String query,
										   final int offset,
										   final SearchSpotifyTracksCallback callback){
		SpotifyToken.getAuthorisation(new GetSpotifyAuthorisationCallback() {
			@Override
			public void returnSpotifyAuthorisation(String authorisation) {
				if (authorisation != null){
					WebHelper.searchSpotifyTracks(
						query,
						offset,
						authorisation,
						new WebHelper.SearchSpotifyTracksCallback() {
							@Override
							public void returnSearchSpotifyTracks(List<SpotifyTrack> spotifyTracks, int totalTracks) {
								callback.returnSearchSpotifyTracks(spotifyTracks, totalTracks);
							}
						});
				} else {
					callback.returnSearchSpotifyTracks(null,0);
				}
			}
		});
	}

	// SEARCH SPOTIFY ARTISTS ----------

	public interface SearchSpotifyArtistsCallback{
		void returnSearchSpotifyArtists(List<SpotifyArtist> spotifyArtists, int totalArtists);
	}

	/**
	 * Search {@link SpotifyArtist}
	 *
	 * @param query		: search query
	 * @param callback	: results returned via callback
	 */
	public static void searchSpotifyArtists(final String query,
											final SearchSpotifyArtistsCallback callback){
		searchSpotifyArtists(query, 0, callback);
	}

	/**
	 * Search {@link SpotifyArtist}
	 *
	 * @param query		: search query
	 * @param offset	: offset from beginning of search
	 * @param callback	: results returned via callback
	 */
	public static void searchSpotifyArtists(final String query,
											final int offset,
											final SearchSpotifyArtistsCallback callback){
		SpotifyToken.getAuthorisation(new GetSpotifyAuthorisationCallback() {
			@Override
			public void returnSpotifyAuthorisation(String authorisation) {
				if (authorisation != null){
					WebHelper.searchSpotifyArtists(
						query,
						offset,
						authorisation,
						new WebHelper.SearchSpotifyArtistsCallback() {
							@Override
							public void returnSearchSpotifyArtists(List<SpotifyArtist> spotifyArtists, int totalArtists) {
								callback.returnSearchSpotifyArtists(spotifyArtists, totalArtists);
							}
						});
				} else {
					callback.returnSearchSpotifyArtists(null, 0);
				}
			}
		});
	}

	// SEARCH SPOTIFY ALBUMS ----------

	public interface SearchSpotifyAlbumsCallback{
		void returnSearchSpotifyAlbums(List<SpotifyAlbum> spotifyAlbums, int totalAlbums);
	}

	/**
	 * Search {@link SpotifyAlbum}
	 *
	 * @param query		: search query
	 * @param callback	: results returned via callback
	 */
	public static void searchSpotifyAlbums(final String query,
										   final SearchSpotifyAlbumsCallback callback){
		searchSpotifyAlbums(query, 0, callback);
	}

	/**
	 * Search {@link SpotifyAlbum}
	 *
	 * @param query		: search query
	 * @param offset	: offset from beginning of search
	 * @param callback	: results returned via callback
	 */
	public static void searchSpotifyAlbums(final String query,
										   final int offset,
										   final SearchSpotifyAlbumsCallback callback){
		SpotifyToken.getAuthorisation(new GetSpotifyAuthorisationCallback() {
			@Override
			public void returnSpotifyAuthorisation(String authorisation) {
				if (authorisation != null) {
					WebHelper.searchSpotifyAlbums(
						query,
						offset,
						authorisation,
						new WebHelper.SearchSpotifyAlbumsCallback() {
							@Override
							public void returnSearchSpotifyAlbums(List<SpotifyAlbum> spotifyAlbums, int totalAlbums) {
								callback.returnSearchSpotifyAlbums(spotifyAlbums, totalAlbums);
							}
						});
				} else {
					callback.returnSearchSpotifyAlbums(null, 0);
				}
			}
		});

	}

	// GET ARTIST TOP TRACKS ----------

	public interface GetSpotifyArtistTopTracksCallback {
		void returnGetSpotifyArtistTopTracks(List<SpotifyTrack> spotifyTracks);
	}

	/**
	 * Get top {@link SpotifyTrack}s for the specified {@link SpotifyArtist}
	 *
	 * @param artistID	: ID of specified artist
	 * @param callback	: results returned via callback
	 */
	public static void getSpotifyArtistTopTracks(final String artistID,
												 final GetSpotifyArtistTopTracksCallback callback){
		//TODO: something smarter with country codes!
		getSpotifyArtistTopTracks(artistID, "GB", callback);
	}

	/**
	 * Search {@link SpotifyTrack}
	 *
	 * @param artistID	: ID of specified artist
	 * @param country	: country code of Spotify market
	 * @param callback	: results returned via callback
	 */
	public static void getSpotifyArtistTopTracks(final String artistID,
												 final String country,
												 final GetSpotifyArtistTopTracksCallback callback){
		SpotifyToken.getAuthorisation(new GetSpotifyAuthorisationCallback() {
			@Override
			public void returnSpotifyAuthorisation(String authorisation) {
				if (authorisation != null) {
					WebHelper.getSpotifyArtistTopTracks(
						artistID,
						country,
						authorisation,
						new WebHelper.GetSpotifyArtistTopTracksCallback() {
							@Override
							public void returnGetSpotifyArtistTopTracks(List<SpotifyTrack> spotifyTracks) {
								callback.returnGetSpotifyArtistTopTracks(spotifyTracks);
							}
						});
				} else {
					callback.returnGetSpotifyArtistTopTracks(null);
				}
			}
		});

	}

	//TODO DOCUMENTATION ETC

	public interface GetNotificationsCallback {
		void returnGetNotifications(List<HHNotification> notifications);
	}

	public static void getNotifications(final GetNotificationsCallback callback){
		getNotifications(null, null, true, null, callback);
	}

	public static void getNotifications(final boolean newOnly,
										final GetNotificationsCallback callback){
		getNotifications(null, null, newOnly, null, callback);
	}

	public static void getNotifications(final String authToken,
										final GetNotificationsCallback callback){
		getNotifications(authToken, null, true, null, callback);
	}

	public static void getNotifications(final String authToken,
										final Timestamp sinceTime,
										final GetNotificationsCallback callback){
		getNotifications(authToken, sinceTime, true, null, callback);
	}

	public static void getNotifications(final String authToken,
										final Timestamp sinceTime,
										final boolean newOnly,
										final Long[] excludeIDs,
										final GetNotificationsCallback callback){
		WebHelper.getNotifications(
			authToken,
			sinceTime,
			newOnly,
			excludeIDs,
			new WebHelper.GetNotificationsCallback() {
				@Override
				public void returnGetNotifications(List<HHNotification> notifications) {
					callback.returnGetNotifications(notifications);
				}
			});
	}

	public interface ReadNotificationCallback {
		void returnReadNotification(HHNotification readNotification);
	}

	public static void readNotification(final HHNotification notification,
										final ReadNotificationCallback callback){
		readNotification(null, notification, callback);
	}

	public static void readNotification(final String authToken,
										final HHNotification notification,
										final ReadNotificationCallback callback){
		WebHelper.readNotification(
			authToken,
			notification,
			new WebHelper.ReadNotificationCallback(){
				@Override
				public void returnReadNotification(HHNotification readNotification) {
					callback.returnReadNotification(readNotification);
				}
			}
		);
	}

	public interface PostMutePostCallback {
		void returnPostMutePost(boolean success, HHMute returnedMute);
	}

	public static void postMutePost(final String authToken,
									final long postID,
									final PostMutePostCallback callback){
		WebHelper.postMutePost(
			authToken,
			postID,
			new WebHelper.PostMutePostCallback(){
				@Override
				public void returnPostMutePost(boolean success, HHMute returnedMute) {
					callback.returnPostMutePost(success, returnedMute);
				}
			}
		);
	}

	public interface DeleteMutePostCallback {
		void returnDeleteMutePost(boolean success, HHMute deletedMute);
	}

	public static void deleteMutePost(final String authToken,
									  final long postID,
									  final DeleteMutePostCallback callback){
		WebHelper.deleteMutePost(
			authToken,
			postID,
			new WebHelper.DeleteMutePostCallback(){
				@Override
				public void returnDeleteMutePost(boolean success, HHMute deletedMute) {
					callback.returnDeleteMutePost(success, deletedMute);
				}
			}
		);
	}

	public interface UpdateUserProfilePrivacyCallback {
		void returnUpdateUserProfilePrivacy(boolean success, int profilePrivacy);
	}

	public static void updateUserProfilePrivacy(final String authToken,
												final int profilePrivacy,
												final UpdateUserProfilePrivacyCallback callback){
		WebHelper.updateUserProfilePrivacy(
			authToken,
			profilePrivacy,
			new WebHelper.UpdateUserProfilePrivacyCallback(){
				@Override
				public void returnUpdateUserProfilePrivacy(boolean success, int profilePrivacy) {
					if (success)
						HHUser.setCurrentUserProfilePrivacy(profilePrivacy);
					callback.returnUpdateUserProfilePrivacy(success, profilePrivacy);
				}
			}
		);
	}

	public interface UpdateUserSearchPrivacyCallback {
		void returnUpdateUserSearchPrivacy(boolean success, int searchPrivacy);
	}

	public static void updateUserSearchPrivacy(final String authToken,
											   final int searchPrivacy,
											   final UpdateUserSearchPrivacyCallback callback){
		WebHelper.updateUserSearchPrivacy(
			authToken,
			searchPrivacy,
			new WebHelper.UpdateUserSearchPrivacyCallback(){
				@Override
				public void returnUpdateUserSearchPrivacy(boolean success, int searchPrivacy) {
					if (success)
						HHUser.setCurrentUserSearchPrivacy(searchPrivacy);
					callback.returnUpdateUserSearchPrivacy(success, searchPrivacy);
				}
			}
		);
	}

	public interface UpdateUserAutoAcceptCallback {
		void returnUpdateUserAutoAccept(boolean success, int searchPrivacy);
	}

	public static void updateUserAutoAccept(final String authToken,
											final int autoAccept,
											final UpdateUserAutoAcceptCallback callback){
		WebHelper.updateUserAutoAccept(
			authToken,
			autoAccept,
			new WebHelper.UpdateUserAutoAcceptCallback(){
				@Override
				public void returnUpdateUserAutoAccept(boolean success, int autoAccept) {
					if (success)
						HHUser.setCurrentUserAutoAccept(autoAccept);
					callback.returnUpdateUserAutoAccept(success, autoAccept);
				}
			}
		);
	}

}
