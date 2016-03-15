package yosoyo.aaahearhereprototype;

import android.content.Context;
import android.location.Location;

import com.facebook.AccessToken;
import com.facebook.Profile;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.HHServerClasses.Database.DatabaseHelper;
import yosoyo.aaahearhereprototype.HHServerClasses.HHCachedSpotifyTrack;
import yosoyo.aaahearhereprototype.HHServerClasses.HHComment;
import yosoyo.aaahearhereprototype.HHServerClasses.HHLike;
import yosoyo.aaahearhereprototype.HHServerClasses.HHPostFull;
import yosoyo.aaahearhereprototype.HHServerClasses.HHPostFullProcess;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUserFull;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUserFullProcess;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.AuthenticateUserFacebookTask;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.CreateUserTask;
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
			new AuthenticateUserFacebookTask.AuthenticateUserFacebookTaskCallback() {
				@Override
				public void returnAuthenticationResult(Integer result, HHUserFullProcess returnedUser) {
					if (result == HttpURLConnection.HTTP_OK) {
						HHUser.setCurrentUser(returnedUser);
						DatabaseHelper.processCurrentUser(
							context,
							returnedUser,
							new DatabaseHelper.ProcessCurrentUserCallback() {
								@Override
								public void returnProcessedCurrentUser(HHUserFull hhUserFull) {
									callback.returnAuthenticationResult(true);
								}
							});
					} else if (result == HttpURLConnection.HTTP_ACCEPTED) {
						HHUser user = new HHUser(Profile.getCurrentProfile());
						new CreateUserTask(
							user,
							new CreateUserTask.CreateUserTaskCallback() {
								@Override
								public void returnResultCreateUser(Boolean success, HHUser userReturned) {
									callback.returnAuthenticationResult(true);
								}
							}).execute();
					} else {
						callback.returnAuthenticationResult(false);
					}
				}
			}).execute();
	}

	public interface GetWebPostCallback {
		void returnWebPost(HHPostFull webPost);
	}

	public interface GetAllPostsCallback extends GetWebPostCallback {
		void returnAllCachedPosts(List<HHPostFull> cachedPosts);
	}

	public static void getAllPosts(GetAllPostsCallback callback){
		getAllCachedPosts(callback);
		getAllWebPosts(callback);
	}

	private static void getAllCachedPosts(final GetAllPostsCallback callback){
		DatabaseHelper.getAllCachedPosts(context, new DatabaseHelper.GetAllCachedPostsCallback() {
			@Override
			public void returnAllCachedPosts(List<HHPostFull> cachedPosts) {
				callback.returnAllCachedPosts(cachedPosts);
			}
		});
	}

	private static void getAllWebPosts(final GetAllPostsCallback callback){
		WebHelper.getAllWebPosts(new WebHelper.GetAllWebPostsCallback() {
			@Override
			public void returnAllWebPosts(List<HHPostFullProcess> webPostsToProcess) {
				if (webPostsToProcess != null)
					DatabaseHelper.processWebPosts(context, callback, webPostsToProcess);
			}
		});
	}

	public static void getWebPost(long post_id, final GetWebPostCallback callback){
		WebHelper.getWebPost(post_id, new WebHelper.GetWebPostCallback() {
			@Override
			public void returnWebPost(HHPostFullProcess webPostToProcess) {
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
		getPostsAtLocation(context, location, HHUser.getCurrentUser().getUser().getID(), callback);
	}

	public static void getPostsAtLocation(Location location, final long userID, final GetPostsAtLocationCallback callback) {
		getPostsAtLocation(context, location, userID, callback);
	}

	public static void getPostsAtLocation(Context context, Location location, final long userID, final GetPostsAtLocationCallback callback){
		DatabaseHelper.getPostsAtLocation(
			context,
			location,
			new DatabaseHelper.GetPostsAtLocationCallback() {
				@Override
				public void returnCachedPostsAtLocation(Location location, List<HHPostFull> posts) {
					if (posts != null && posts.size() > 0) {
						callback.returnPostsAtLocation(posts);
						return;
					}

					WebHelper.getWebPostsAtLocation(
						location,
						userID,
						new WebHelper.GetWebPostsAtLocationCallback() {
							@Override
							public void returnWebPostsAtLocation(List<HHPostFull> webPosts) {
								callback.returnPostsAtLocation(webPosts);
							}
						});

				}
			});
	}



	public interface PostPostCallback{
		void returnPostedPost(boolean success, HHPostFullProcess returnedPost);
	}

	public static void postPost(final HHPostTagsArray post, final PostPostCallback callback){
		WebHelper.postPost(post, new WebHelper.PostPostCallback() {
			@Override
			public void returnPostedPost(boolean success, HHPostFullProcess webPostToProcess) {
				callback.returnPostedPost(success, webPostToProcess);
			}
		});
	}

	public interface PostCommentCallback{
		void returnPostedComment(HHComment returnedComment);
	}

	public static void postComment(final HHComment comment, final PostCommentCallback callback){
		WebHelper.postComment(comment, new WebHelper.PostCommentCallback() {
			@Override
			public void returnPostedComment(final HHComment returnedComment) {
				DatabaseHelper.insertComment(
					context,
					returnedComment,
					new DatabaseHelper.InsertCommentCallback() {
						@Override
						public void returnInsertedComment(Long commentID, HHComment comment) {
							callback.returnPostedComment(returnedComment);
						}
					});
			}
		});
	}

	public interface PostLikeCallback{
		void returnPostedLike(HHLike returnedLike);
	}

	public static void postLike(final HHLike like, final PostLikeCallback callback){
		WebHelper.postLike(like, new WebHelper.PostLikeCallback() {
			@Override
			public void returnPostedLike(final HHLike returnedLike) {
				DatabaseHelper.insertLike(
					context,
					returnedLike,
					new DatabaseHelper.InsertLikeCallback() {
						@Override
						public void returnInsertedLike(Long likeID, HHLike like) {
							callback.returnPostedLike(returnedLike);
						}
					});
			}
		});
	}

	public interface DeleteLikeCallback{
		void returnDeletedLike(boolean success);
	}

	public static void deleteLike(final HHLike like, final DeleteLikeCallback callback){
		WebHelper.deleteLike(like, new WebHelper.DeleteLikeCallback() {
			@Override
			public void returnDeletedLike(boolean success) {
				DatabaseHelper.deleteLike(
					context,
					like,
					new DatabaseHelper.DeleteLikeCallback() {
						@Override
						public void returnDeletedLike(boolean success) {
							callback.returnDeletedLike(success);
						}
					});
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
				public void returnCachedSpotifyTrack(HHCachedSpotifyTrack track) {
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
									public void returnCachedSpotifyTrack(HHCachedSpotifyTrack track) {
										callback.returnSpotifyTrack(track);
									}
								});
						}
					});

				}
			});
	}

}
