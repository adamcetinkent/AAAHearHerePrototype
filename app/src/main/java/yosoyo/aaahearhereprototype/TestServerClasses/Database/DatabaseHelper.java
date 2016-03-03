package yosoyo.aaahearhereprototype.TestServerClasses.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

import yosoyo.aaahearhereprototype.AsyncDataManager;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;
import yosoyo.aaahearhereprototype.TestServerClasses.CachedSpotifyTrack;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPostFull;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPostFullProcess;
import yosoyo.aaahearhereprototype.TestServerClasses.WebHelper;

/**
 * Created by adam on 22/02/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = "DatabaseHelper";
	private static final String DB_NAME = "AAAHereHerePrototype";
	private static final int DB_VERSION = 6;

	public DatabaseHelper(Context context){
		super(context, DB_NAME, null, DB_VERSION);
	}

	public static void createDatabase(SQLiteDatabase db){
		Log.d(TAG, "Creating database [" + DB_NAME + " v." + DB_VERSION + "]...");

		db.execSQL(ORMTestPost.SQL_CREATE_TABLE);
		db.execSQL(ORMTestUser.SQL_CREATE_TABLE);
		db.execSQL(ORMTestComment.SQL_CREATE_TABLE);
		db.execSQL(ORMCachedSpotifyTrack.SQL_CREATE_TABLE);
	}

	public static void upgradeDatabase(SQLiteDatabase db, int oldVersion, int newVersion){
		Log.d(TAG, "Updating database [" + DB_NAME + " v." + oldVersion + "] to [" + DB_NAME + " v." + newVersion + "]...");

		db.execSQL(ORMTestPost.SQL_DROP_TABLE);
		db.execSQL(ORMTestUser.SQL_DROP_TABLE);
		db.execSQL(ORMTestComment.SQL_DROP_TABLE);
		db.execSQL(ORMCachedSpotifyTrack.SQL_DROP_TABLE);

		createDatabase(db);
	}

	public static void resetDatabase(Context context){
		Log.d(TAG, "Resetting database...");

		ORMTestPost.resetTable(context);
		ORMTestUser.resetTable(context);
		ORMTestComment.resetTable(context);
		ORMCachedSpotifyTrack.resetTable(context);

		Log.d(TAG, "Database reset");
	}


	public interface GetAllCachedPostsCallback {
		void returnAllCachedPosts(List<TestPostFull> cachedPosts);
	}

	public static void getAllCachedPosts(Context context, final GetAllCachedPostsCallback callback){
		// GET CACHED POSTS FROM DATABASE
		ORMTestPostFull.getAllPosts(context,
									new ORMTestPostFull.DBTestPostFullSelectAllTask.DBTestPostFullSelectAllTaskCallback() {
										@Override
										public void returnTestPosts(List<TestPostFull> testPosts) {
											callback.returnAllCachedPosts(testPosts);
										}
									});
	}

	public static void processWebPosts(final Context context, final AsyncDataManager.GetAllPostsCallback callback, final List<TestPostFullProcess> webPostsToProcess){
		// INSERT POSTS INTO DATABASE
		ORMTestPost.insertPosts(context, webPostsToProcess,
								new ORMTestPost.DBTestPostInsertManyTask.DBTestPostInsertManyTaskCallback() {
									@Override
									public void returnInsertedManyPosts(List<TestPostFullProcess> testPosts) {
										returnProcessedPosts(callback, testPosts);
									}
								});

		// INSERT COMMENTS INTO DATABASE
		ORMTestComment.insertCommentsFromPosts(context, webPostsToProcess,
											   new ORMTestComment.DBTestCommentInsertManyFromPostsTask.DBTestCommentInsertManyFromPostsTaskCallback() {
												   @Override
												   public void returnInsertedManyComments(List<TestPostFullProcess> testPosts) {
													   returnProcessedPosts(callback, testPosts);
												   }
											   });

		// INSERT USERS & COMMENT USERS INTO DATABASE
		ORMTestUser.insertUsersFromPosts(context, webPostsToProcess,
										 new ORMTestUser.DBTestUserInsertManyFromPostsTask.DBTestUserInsertManyFromPostsTaskCallback() {
											 @Override
											 public void returnInsertedManyUsers(List<TestPostFullProcess> testPosts) {
												 returnProcessedPosts(callback, testPosts);
											 }
										 });


		// FIND TRACKS IN DATABASE
		ORMCachedSpotifyTrack
			.getTracksFromPosts(context, webPostsToProcess,
								new ORMCachedSpotifyTrack.DBCachedSpotifyTrackSelectManyFromPostsTask.DBCachedSpotifyTrackSelectManyFromPostsTaskCallback() {
									@Override
									public void returnSelectedManyCachedSpotifyTracks(final List<TestPostFullProcess> testPosts) {
										for (final TestPostFullProcess postProcess : testPosts) {
											if (postProcess.isTrackProcessed()) {

												// TRACK ALREADY IN CACHE
												testProcessPost(callback,
																postProcess,
																testPosts);

											} else {

												// GET TRACK FROM WEB
												WebHelper.getSpotifyTrack(
													postProcess.getPost().getTrack(),
													new WebHelper.GetSpotifyTrackCallback() {
														@Override
														public void returnSpotifyTrack(SpotifyTrack spotifyTrack) {

															postProcess.setTrack(
																new CachedSpotifyTrack(
																	spotifyTrack));

															// INSERT TRACK INTO DATABASE
															ORMCachedSpotifyTrack
																.insertTrackFromPosts(
																	context,
																	postProcess,
																	new ORMCachedSpotifyTrack.DBCachedSpotifyTrackInsertFromPostTask.DBCachedSpotifyTrackInsertFromPostTaskCallback() {
																		@Override
																		public void returnInsertedManyCachedSpotifyTracks(TestPostFullProcess testPost) {
																			testProcessPost(
																				callback,
																				postProcess,
																				testPosts);
																		}
																	});
														}
													});

											}
										}
									}
								});

	}

	private static void returnProcessedPosts(final AsyncDataManager.GetAllPostsCallback callback, List<TestPostFullProcess> testPosts){
		for (TestPostFullProcess postProcess : testPosts){
			testProcessPost(callback, postProcess, testPosts);
		}
	}

	private static void testProcessPost(final AsyncDataManager.GetAllPostsCallback callback, TestPostFullProcess postProcess, List<TestPostFullProcess> testPosts){
		if (postProcess.isPostProcessed() && postProcess.isTrackProcessed() && postProcess.isUsersProcessed() && postProcess.isCommentsProcessed()){
			testPosts.remove(testPosts);
			callback.returnWebPost(postProcess);
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createDatabase(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		upgradeDatabase(db, oldVersion, newVersion);
	}

}
