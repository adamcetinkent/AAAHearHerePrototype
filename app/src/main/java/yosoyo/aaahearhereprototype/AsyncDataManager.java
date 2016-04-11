package yosoyo.aaahearhereprototype;

import android.content.Context;
import android.location.Location;

import com.facebook.AccessToken;
import com.facebook.Profile;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.HHServerClasses.Database.DatabaseHelper;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHCachedSpotifyTrack;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHComment;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequest;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequestUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHLike;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPostFull;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPostFullProcess;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUserFull;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUserFullProcess;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.AuthenticateUserFacebookTask;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.GetUserTask;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.PostUserTask;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHPostTagsArray;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;

/**
 * Created by adam on 02/03/16.
 */
public class AsyncDataManager {

	public static final String TAG = "AsyncDataManager";
	private static Context context;

	public static void setContext(Context newContext){
		context = newContext;
	}

	public interface AuthenticateUserCallback {
		void returnAuthenticationResult(boolean success);
	}

	public static void authenticateUser(AccessToken accessToken, final AuthenticateUserCallback callback){
		new AuthenticateUserFacebookTask(
			accessToken,
			new AuthenticateUserFacebookTask.Callback() {
				@Override
				public void returnAuthenticationResult(Integer result, HHUserFullProcess returnedUser) {
					if (result == HttpURLConnection.HTTP_OK) {
						HHUser.setCurrentUser(returnedUser);
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
						callback.returnAuthenticationResult(false);
					}
				}
			}).execute();
	}

	public interface UpdateCurrentUserCallback{
		void returnUpdateCurrentUser(boolean success);
	}

	public static void updateCurrentUser(final UpdateCurrentUserCallback callback){
		new GetUserTask(
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
									callback.returnUpdateCurrentUser(success);
								}
							});
					} else {
						callback.returnUpdateCurrentUser(success);
					}
				}
			}
		).execute();
	}

	public interface GetPostCallback {
		void returnGetPost(HHPostFull post);
	}

	public interface GetAllPostsCallback extends GetPostCallback {
		void returnPostList(List<HHPostFull> posts);
	}

	public static void getAllPosts(GetAllPostsCallback callback){
		getAllCachedPosts(callback);
		getAllWebPosts(callback);
	}

	private static void getAllCachedPosts(final GetAllPostsCallback callback){
		DatabaseHelper.getAllCachedPosts(context, new DatabaseHelper.GetAllCachedPostsCallback() {
			@Override
			public void returnGetAllCachedPosts(List<HHPostFull> cachedPosts) {
				callback.returnPostList(cachedPosts);
			}
		});
	}

	private static void getAllWebPosts(final GetAllPostsCallback callback){
		WebHelper.getAllPosts(new WebHelper.GetAllPostsCallback() {
			@Override
			public void returnGetAllPosts(List<HHPostFullProcess> webPostsToProcess) {
				if (webPostsToProcess != null)
					DatabaseHelper.processWebPosts(context, callback, webPostsToProcess);
			}
		});
	}

	public static void getUserPosts(long userID, GetAllPostsCallback callback){
		getUserCachedPosts(userID, callback);
		getUserWebPosts(userID, callback);
	}

	private static void getUserCachedPosts(long userID, final GetAllPostsCallback callback){
		DatabaseHelper.getUserCachedPosts(
			context,
			userID,
			new DatabaseHelper.GetAllCachedPostsCallback() {
				@Override
				public void returnGetAllCachedPosts(List<HHPostFull> cachedPosts) {
					callback.returnPostList(cachedPosts);
				}
			});
	}

	private static void getUserWebPosts(long userID, final GetAllPostsCallback callback){
		WebHelper.getUserPosts(
			userID,
			new WebHelper.GetAllPostsCallback() {
				@Override
				public void returnGetAllPosts(List<HHPostFullProcess> webPostsToProcess) {
					if (webPostsToProcess != null)
						DatabaseHelper.processWebPosts(context, callback, webPostsToProcess);
				}
			});
	}

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
				}
			});
	}

	public static void getWebPost(long post_id, final GetPostCallback callback){
		WebHelper.getPost(post_id, new WebHelper.GetPostCallback() {
			@Override
			public void returnGetPost(HHPostFullProcess webPostToProcess) {
				ArrayList<HHPostFullProcess> webPostsToProcess = new ArrayList<>();
				webPostsToProcess.add(webPostToProcess);
				DatabaseHelper.processWebPosts(context, callback, webPostsToProcess);
			}
		});
	}

	public interface GetPostsAtLocationCallback{
		void returnPostsAtLocation(List<HHPostFull> returnedPosts);
	}

	public static void getPostsAtLocation(Location location, final GetPostsAtLocationCallback callback){
		getPostsAtLocation(context, location, HHUser.getCurrentUserID(), callback);
	}

	public static void getPostsAtLocation(Location location, final long userID, final GetPostsAtLocationCallback callback) {
		getPostsAtLocation(context, location, userID, callback);
	}

	public static void getPostsAtLocation(Context context, Location location, final long userID, final GetPostsAtLocationCallback callback){
		/*DatabaseHelper.getPostsAtLocation(
			context,
			location,
			new DatabaseHelper.GetPostsAtLocationCallback() {
				@Override
				public void returnGetCachedPostsAtLocation(Location location, List<HHPostFull> posts) {
					if (posts != null && posts.size() > 0) {
						callback.returnPostsAtLocation(posts);
						return;
					}

					WebHelper.getPostsAtLocation(
						location,
						userID,
						new WebHelper.GetPostsAtLocationCallback() {
							@Override
							public void returnGetPostsAtLocation(List<HHPostFull> webPosts) {
								callback.returnPostsAtLocation(webPosts);
							}
						});

				}
			});*/
		WebHelper.getPostsAtLocation(
			location,
			userID,
			new WebHelper.GetPostsAtLocationCallback() {
				@Override
				public void returnGetPostsAtLocation(List<HHPostFull> webPosts) {
					callback.returnPostsAtLocation(webPosts);
				}
			});
	}

	public interface GetUserPrivacyCallback{
		void returnCachedUserPrivacy(boolean userPrivacy);
		void returnWebUserPrivacy(boolean userPrivacy);
	}

	public static void getUserPrivacy(final long userID, final GetUserPrivacyCallback callback) {
		getUserPrivacy(context, userID, false, callback);
	}

	public static void getUserPrivacy(final long userID, final boolean webOnly, final GetUserPrivacyCallback callback) {
		getUserPrivacy(context, userID, webOnly, callback);
	}

	public static void getUserPrivacy(Context context, final long userID, final boolean webOnly, final GetUserPrivacyCallback callback){
		if (!webOnly) {
			getUserCachedPrivacy(context, userID, callback);
		}
		getUserWebPrivacy(userID, callback);
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

	private static void getUserWebPrivacy(final long userID, final GetUserPrivacyCallback callback){
		WebHelper.getUserPrivacy(
			userID, new WebHelper.GetUserPrivacyCallback() {
				@Override
				public void returnGetUserPrivacy(boolean userPrivacy) {
					callback.returnWebUserPrivacy(userPrivacy);
				}
			}
		);
	}

	public interface GetUserPostCountCallback{
		void returnCachedUserPostCount(int postCount);
		void returnWebUserPostCount(int postCount);
	}

	public static void getUserPostCount(final long userID, final GetUserPostCountCallback callback) {
		getUserPostCount(context, userID, false, callback);
	}

	public static void getUserPostCount(final long userID, final boolean webOnly, final GetUserPostCountCallback callback) {
		getUserPostCount(context, userID, webOnly, callback);
	}

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

	public interface GetUserFollowersInCountCallback{
		void returnCachedUserFollowersInCount(int followersInCount);
		void returnWebUserFollowersInCount(int followersInCount);
	}

	public static void getUserFollowersInCount(final long userID, final GetUserFollowersInCountCallback callback) {
		getUserFollowersInCount(context, userID, false, callback);
	}

	public static void getUserFollowersInCount(final long userID, final boolean webOnly, final GetUserFollowersInCountCallback callback) {
		getUserFollowersInCount(context, userID, webOnly, callback);
	}

	public static void getUserFollowersInCount(Context context, final long userID, final boolean webOnly, final GetUserFollowersInCountCallback callback){
		if (!webOnly) {
			getUserCachedFollowersInCount(context, userID, callback);
		}
		getUserWebFollowersInCount(userID, callback);
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

	private static void getUserWebFollowersInCount(final long userID, final GetUserFollowersInCountCallback callback){
		WebHelper.getUserFollowersInCount(
			userID, new WebHelper.GetUserFollowersInCountCallback() {
				@Override
				public void returnGetUserFollowersInCount(int followersInCount) {
					callback.returnWebUserFollowersInCount(followersInCount);
				}
			}
		);
	}

	public interface GetUserFollowersOutCountCallback{
		void returnCachedUserFollowersOutCount(int followersOutCount);
		void returnWebUserFollowersOutCount(int followersOutCount);
	}

	public static void getUserFollowersInCount(final long userID, final GetUserFollowersOutCountCallback callback) {
		getUserFollowersOutCount(context, userID, false, callback);
	}

	public static void getUserFollowersOutCount(final long userID, final boolean webOnly, final GetUserFollowersOutCountCallback callback) {
		getUserFollowersOutCount(context, userID, webOnly, callback);
	}

	public static void getUserFollowersOutCount(Context context, final long userID, final boolean webOnly, final GetUserFollowersOutCountCallback callback){
		if (!webOnly) {
			getUserCachedFollowersOutCount(context, userID, callback);
		}
		getUserWebFollowersOutCount(userID, callback);
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

	private static void getUserWebFollowersOutCount(final long userID, final GetUserFollowersOutCountCallback callback){
		WebHelper.getUserFollowersOutCount(
			userID, new WebHelper.GetUserFollowersOutCountCallback() {
				@Override
				public void returnGetUserFollowersOutCount(int followersOutCount) {
					callback.returnWebUserFollowersOutCount(followersOutCount);
				}
			}
		);
	}

	public interface PostPostCallback{
		void returnPostPost(boolean success, HHPostFullProcess returnedPost);
	}

	public static void postPost(final HHPostTagsArray post, final PostPostCallback callback){
		WebHelper.postPost(post, new WebHelper.PostPostCallback() {
			@Override
			public void returnPostPost(boolean success, HHPostFullProcess webPostToProcess) {
				callback.returnPostPost(success, webPostToProcess);
			}
		});
	}

	public interface PostCommentCallback{
		void returnPostComment(HHComment returnedComment);
	}

	public static void postComment(final HHComment comment, final PostCommentCallback callback){
		WebHelper.postComment(comment, new WebHelper.PostCommentCallback() {
			@Override
			public void returnPostComment(final HHComment returnedComment) {
				DatabaseHelper.insertComment(
					context,
					returnedComment,
					new DatabaseHelper.InsertCommentCallback() {
						@Override
						public void returnInsertComment(Long commentID, HHComment comment) {
							callback.returnPostComment(returnedComment);
						}
					});
			}
		});
	}

	public interface PostLikeCallback{
		void returnPostLike(HHLike returnedLike);
	}

	public static void postLike(final HHLike like, final PostLikeCallback callback){
		WebHelper.postLike(like, new WebHelper.PostLikeCallback() {
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

	public interface DeleteLikeCallback{
		void returnDeleteLike(boolean success);
	}

	public static void deleteLike(final HHLike like, final DeleteLikeCallback callback){
		WebHelper.deleteLike(like, new WebHelper.DeleteLikeCallback() {
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
					callback.returnDeleteLike(success);
				}
			}
		});
	}

	public interface DeleteFollowCallback {
		void returnDeleteFollow(boolean success, HHFollowUser deletedFollow);
	}

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
					callback.returnDeleteFollow(success, follow);
				}
			}
		});
	}

	public interface PostFollowRequestCallback{
		void returnPostFollowRequest(boolean success, HHFollowRequestUser returnedFollowRequest);
		void returnPostFollowRequestAccepted(boolean success, HHFollowUser returnedFollowUser);
	}

	public static void postFollowRequest(final HHFollowRequest followRequest, final PostFollowRequestCallback callback){
		WebHelper.postFollowRequest(followRequest, new WebHelper.PostFollowRequestCallback() {
			@Override
			public void returnPostFollowRequest(boolean success, HHFollowRequestUser returnedFollowRequest) {
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

	public interface AcceptFollowRequestCallback{
		void returnAcceptFollowRequest(boolean success, HHFollowRequestUser followRequest);
	}

	public static void acceptFollowRequest(final HHFollowRequestUser followRequest, final AcceptFollowRequestCallback callback){
		WebHelper.acceptFollowRequest(followRequest, new WebHelper.AcceptFollowRequestCallback() {
			@Override
			public void returnAcceptFollowRequest(boolean success) {
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
					callback.returnAcceptFollowRequest(success, followRequest);
				}
			}
		});
	}

	public interface DeleteFollowRequestCallback{
		void returnDeleteFollowRequest(boolean success, HHFollowRequestUser followRequest);
	}

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
					callback.returnDeleteFollowRequest(success, followRequest);
				}
			}
		});
	}

	public interface  GetSpotifyTrackCallback{
		void returnSpotifyTrack(HHCachedSpotifyTrack cachedSpotifyTrack);
	}

	public static void getSpotifyTrack(final String trackID, final GetSpotifyTrackCallback callback){
		getSpotifyTrack(context, trackID, callback);
	}

	public static void getSpotifyTrack(final Context context, final String trackID, final GetSpotifyTrackCallback callback){

		DatabaseHelper.getCachedSpotifyTrack(
			context,
			trackID,
			new DatabaseHelper.GetCachedSpotifyTrackCallback() {
				@Override
				public void returnGetCachedSpotifyTrack(HHCachedSpotifyTrack track) {
					if (track != null) {
						callback.returnSpotifyTrack(track);
						return;
					}

					WebHelper.getSpotifyTrack(trackID, new WebHelper.GetSpotifyTrackCallback() {
						@Override
						public void returnSpotifyTrack(SpotifyTrack spotifyTrack) {
							DatabaseHelper.insertSpotifyTrack(
								context,
								spotifyTrack,
								new DatabaseHelper.InsertCachedSpotifyTrackCallback() {
									@Override
									public void returnGetCachedSpotifyTrack(HHCachedSpotifyTrack track) {
										callback.returnSpotifyTrack(track);
									}
								});
						}
					});

				}
			});
	}

	public interface GetUserCallback {
		void returnGetCachedUser(final HHUserFull returnedUser);
		void returnGetWebUser(final HHUserFull returnedUser);
	}

	public static void getUser(final long userID, final boolean webOnly, final GetUserCallback callback){
		if (!webOnly)
			getCachedUser(userID, callback);
		getWebUser(userID, callback);
	}

	private static void getCachedUser(final long userID, final GetUserCallback callback){
		DatabaseHelper.getUser(
			context,
			userID,
			new DatabaseHelper.GetUserCallback() {
				@Override
				public void returnGetUser(HHUserFull user) {
					callback.returnGetCachedUser(user);
				}
			}
		);
	}

	private static void getWebUser(final long userID, final GetUserCallback callback){
		WebHelper.getUser(
			userID,
			new WebHelper.GetUserCallback() {
				@Override
				public void returnGetUser(HHUserFull user) {
					callback.returnGetWebUser(user);
				}
			}
		);
	}

	public interface SearchUsersCallback {
		void returnSearchUsers(final String query, final List<HHUser> foundUsers);
	}

	public static void searchUsers(final String query, final SearchUsersCallback callback){
		WebHelper.searchUsers(query, new WebHelper.SearchUsersCallback() {
			@Override
			public void returnSearchUsers(List<HHUser> foundUsers) {
				callback.returnSearchUsers(query, foundUsers);
			}
		});
	}

}
