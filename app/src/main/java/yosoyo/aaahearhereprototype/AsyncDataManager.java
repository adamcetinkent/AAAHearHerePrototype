package yosoyo.aaahearhereprototype;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.TestServerClasses.Database.DatabaseHelper;
import yosoyo.aaahearhereprototype.TestServerClasses.Tasks.WebHelper;
import yosoyo.aaahearhereprototype.TestServerClasses.TestComment;
import yosoyo.aaahearhereprototype.TestServerClasses.TestLike;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPostFull;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPostFullProcess;

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
		void returnWebPost(TestPostFull webPost);
	}

	public interface GetAllPostsCallback extends GetWebPostCallback {
		void returnAllCachedPosts(List<TestPostFull> cachedPosts);
	}

	public static void getAllPosts(GetAllPostsCallback callback){
		getAllCachedPosts(callback);
		getAllWebPosts(callback);
	}

	public static void getAllCachedPosts(final GetAllPostsCallback callback){
		DatabaseHelper.getAllCachedPosts(context, new DatabaseHelper.GetAllCachedPostsCallback() {
			@Override
			public void returnAllCachedPosts(List<TestPostFull> cachedPosts) {
				callback.returnAllCachedPosts(cachedPosts);
			}
		});
	}

	public static void getAllWebPosts(final GetAllPostsCallback callback){
		WebHelper.getAllWebPosts(new WebHelper.GetAllWebPostsCallback() {
			@Override
			public void returnAllWebPosts(List<TestPostFullProcess> webPostsToProcess) {
				if (webPostsToProcess != null)
					DatabaseHelper.processWebPosts(context, callback, webPostsToProcess);
			}
		});
	}

	public static void getWebPost(long post_id, final GetWebPostCallback callback){
		WebHelper.getWebPost(post_id, new WebHelper.GetWebPostCallback() {
			@Override
			public void returnWebPost(TestPostFullProcess webPostToProcess) {
				ArrayList<TestPostFullProcess> webPostsToProcess = new ArrayList<>();
				webPostsToProcess.add(webPostToProcess);
				DatabaseHelper.processWebPosts(context, callback, webPostsToProcess);
			}
		});
	}

	public interface PostCommentCallback{
		void returnPostedComment(TestComment returnedComment);
	}

	public static void postComment(final TestComment comment, final PostCommentCallback callback){
		WebHelper.postComment(comment, new WebHelper.PostCommentCallback() {
			@Override
			public void returnPostedComment(final TestComment returnedComment) {
				DatabaseHelper.insertComment(
					context,
					returnedComment,
					new DatabaseHelper.InsertCommentCallback() {
						@Override
						public void returnInsertedComment(Long commentID, TestComment comment) {
							callback.returnPostedComment(returnedComment);
						}
					});
			}
		});
	}

	public interface PostLikeCallback{
		void returnPostedLike(TestLike returnedLike);
	}

	public static void postLike(final TestLike like, final PostLikeCallback callback){
		WebHelper.postLike(like, new WebHelper.PostLikeCallback() {
			@Override
			public void returnPostedLike(final TestLike returnedLike) {
				DatabaseHelper.insertLike(
					context,
					returnedLike,
					new DatabaseHelper.InsertLikeCallback() {
						@Override
						public void returnInsertedLike(Long likeID, TestLike like) {
							callback.returnPostedLike(returnedLike);
						}
					});
			}
		});
	}

	public interface DeleteLikeCallback{
		void returnDeletedLike(boolean success);
	}

	public static void deleteLike(final TestLike like, final DeleteLikeCallback callback){
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
