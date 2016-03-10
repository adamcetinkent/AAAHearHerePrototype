package yosoyo.aaahearhereprototype;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.HHServerClasses.Database.DatabaseHelper;
import yosoyo.aaahearhereprototype.HHServerClasses.HHComment;
import yosoyo.aaahearhereprototype.HHServerClasses.HHLike;
import yosoyo.aaahearhereprototype.HHServerClasses.HHPostFull;
import yosoyo.aaahearhereprototype.HHServerClasses.HHPostFullProcess;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;

/**
 * Created by adam on 02/03/16.
 */
public class AsyncDataManager {

	public static final String TAG = "AsyncDataManager";
	private static Context context;

	public static void setContext(Context newContext){
		context = newContext;
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

	public static void getAllCachedPosts(final GetAllPostsCallback callback){
		DatabaseHelper.getAllCachedPosts(context, new DatabaseHelper.GetAllCachedPostsCallback() {
			@Override
			public void returnAllCachedPosts(List<HHPostFull> cachedPosts) {
				callback.returnAllCachedPosts(cachedPosts);
			}
		});
	}

	public static void getAllWebPosts(final GetAllPostsCallback callback){
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

}
