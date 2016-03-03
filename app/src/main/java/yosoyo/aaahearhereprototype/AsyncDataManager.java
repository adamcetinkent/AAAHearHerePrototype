package yosoyo.aaahearhereprototype;

import android.content.Context;

import java.util.List;

import yosoyo.aaahearhereprototype.TestServerClasses.Database.DatabaseHelper;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPostFull;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPostFullProcess;
import yosoyo.aaahearhereprototype.TestServerClasses.WebHelper;

/**
 * Created by adam on 02/03/16.
 */
public class AsyncDataManager {

	public static final String TAG = "AsyncDataManager";
	private static Context context;

	public static void setContext(Context newContext){
		context = newContext;
	}

	public interface GetAllPostsCallback {
		void returnAllCachedPosts(List<TestPostFull> cachedPosts);
		void returnWebPost(TestPostFull webPost);
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
				DatabaseHelper.processWebPosts(context, callback, webPostsToProcess);
			}
		});
	}

}
