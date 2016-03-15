package yosoyo.aaahearhereprototype.HHServerClasses.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

import yosoyo.aaahearhereprototype.AsyncDataManager;
import yosoyo.aaahearhereprototype.HHServerClasses.HHCachedSpotifyTrack;
import yosoyo.aaahearhereprototype.HHServerClasses.HHComment;
import yosoyo.aaahearhereprototype.HHServerClasses.HHLike;
import yosoyo.aaahearhereprototype.HHServerClasses.HHPostFull;
import yosoyo.aaahearhereprototype.HHServerClasses.HHPostFullProcess;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUserFull;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUserFullProcess;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;

/**
 * Created by adam on 22/02/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = "DatabaseHelper";
	private static final String DB_NAME = "AAAHereHerePrototype";
	private static final int DB_VERSION = 12;

	public DatabaseHelper(Context context){
		super(context, DB_NAME, null, DB_VERSION);
	}

	private static void createDatabase(SQLiteDatabase db){
		Log.d(TAG, "Creating database [" + DB_NAME + " v." + DB_VERSION + "]...");

		db.execSQL(ORMPost.SQL_CREATE_TABLE);
		db.execSQL(ORMUser.SQL_CREATE_TABLE);
		db.execSQL(ORMComment.SQL_CREATE_TABLE);
		db.execSQL(ORMLike.SQL_CREATE_TABLE);
		db.execSQL(ORMTag.SQL_CREATE_TABLE);
		db.execSQL(ORMFriendship.SQL_CREATE_TABLE);
		db.execSQL(ORMCachedSpotifyTrack.SQL_CREATE_TABLE);
	}

	private static void upgradeDatabase(SQLiteDatabase db, int oldVersion, int newVersion){
		Log.d(TAG, "Updating database [" + DB_NAME + " v." + oldVersion + "] to [" + DB_NAME + " v." + newVersion + "]...");

		db.execSQL(ORMPost.SQL_DROP_TABLE);
		db.execSQL(ORMUser.SQL_DROP_TABLE);
		db.execSQL(ORMComment.SQL_DROP_TABLE);
		db.execSQL(ORMLike.SQL_DROP_TABLE);
		db.execSQL(ORMTag.SQL_DROP_TABLE);
		db.execSQL(ORMFriendship.SQL_DROP_TABLE);
		db.execSQL(ORMCachedSpotifyTrack.SQL_DROP_TABLE);

		createDatabase(db);
	}

	public static void resetDatabase(Context context){
		Log.d(TAG, "Resetting database...");

		ORMPost.resetTable(context);
		ORMUser.resetTable(context);
		ORMComment.resetTable(context);
		ORMLike.resetTable(context);
		ORMTag.resetTable(context);
		ORMFriendship.resetTable(context);
		ORMCachedSpotifyTrack.resetTable(context);

		Log.d(TAG, "Database reset");
	}

	public interface ProcessCurrentUserCallback{
		void returnProcessedCurrentUser(HHUserFull hhUserFull);
	}

	public static void processCurrentUser(Context context, HHUserFullProcess user, final ProcessCurrentUserCallback callback){
		// INSERT CURRENT USER
		ORMUser.insertCurrentUser(
			context,
			user,
			new ORMUser.DBUserInsertCurrentTask.DBUserInsertCurrentTaskCallback() {
				@Override
				public void returnInsertedUser(long userID, HHUserFullProcess returnedUser) {
					testProcessUser(callback, returnedUser);
				}
			});

		// INSERT FRIENDSHIPS
		ORMFriendship.insertFriendshipsFromUser(
			context,
			user,
			new ORMFriendship.DBFriendshipInsertManyFromUserTask.DBFriendshipInsertManyFromUserTaskCallback() {
				@Override
				public void returnInsertedManyFriendships(HHUserFullProcess returnedUser) {
					testProcessUser(callback, returnedUser);
				}
			});
	}

	private static void testProcessUser(final ProcessCurrentUserCallback callback, HHUserFullProcess userToProcess){
		if (userToProcess.isUserProcessed() && userToProcess.isFriendshipsProcessed()){
			callback.returnProcessedCurrentUser(new HHUserFull(userToProcess));
		}
	}

	public interface GetAllCachedPostsCallback {
		void returnAllCachedPosts(List<HHPostFull> cachedPosts);
	}

	public static void getAllCachedPosts(Context context, final GetAllCachedPostsCallback callback){
		// GET CACHED POSTS FROM DATABASE
		ORMPostFull.getAllPosts(context,
								new ORMPostFull.DBPostFullSelectAllTask.DBPostFullSelectAllTaskCallback() {
									@Override
									public void returnPosts(List<HHPostFull> posts) {
										callback.returnAllCachedPosts(posts);
									}
								});
	}

	public static void processWebPosts(final Context context, final AsyncDataManager.GetWebPostCallback callback, final List<HHPostFullProcess> webPostsToProcess){
		// INSERT POSTS INTO DATABASE
		ORMPost.insertPosts(
			context,
			webPostsToProcess,
			new ORMPost.DBPostInsertManyTask.DBPostInsertManyTaskCallback() {
				@Override
				public void returnInsertedManyPosts(List<HHPostFullProcess> postsToProcess) {
					returnProcessedPosts(callback, postsToProcess);
				}
			});

		// INSERT COMMENTS INTO DATABASE
		ORMComment.insertCommentsFromPosts(
			context,
			webPostsToProcess,
			new ORMComment.DBCommentInsertManyFromPostsTask.DBCommentInsertManyFromPostsTaskCallback() {
				@Override
				public void returnInsertedManyComments(List<HHPostFullProcess> postsToProcess) {
					returnProcessedPosts(callback, postsToProcess);
				}
			});

		// INSERT COMMENTS INTO DATABASE
		ORMLike.insertLikesFromPosts(
			context,
			webPostsToProcess,
			new ORMLike.DBLikeInsertManyFromPostsTask.DBLikeInsertManyFromPostsTaskCallback() {
				@Override
				public void returnInsertedManyLikes(List<HHPostFullProcess> postsToProcess) {
					returnProcessedPosts(callback, postsToProcess);
				}
			});

		// INSERT TAGS INTO DATABASE
		ORMTag.insertTagsFromPosts(
			context,
			webPostsToProcess,
			new ORMTag.DBTagInsertManyFromPostsTask.DBTagInsertManyFromPostsTaskCallback() {
				@Override
				public void returnInsertedManyTags(List<HHPostFullProcess> postsToProcess) {
					returnProcessedPosts(callback, postsToProcess);
				}
			});

		// INSERT USERS, COMMENT USERS, LIKE USERS, TAG USERS INTO DATABASE
		ORMUser.insertUsersFromPosts(
			context,
			webPostsToProcess,
			new ORMUser.DBUserInsertManyFromPostsTask.DBUserInsertManyFromPostsTaskCallback() {
				@Override
				public void returnInsertedManyUsers(List<HHPostFullProcess> postsToProcess) {
					returnProcessedPosts(callback, postsToProcess);
				}
			});

		ORMCachedSpotifyTrack.getTracksFromPosts(
			context,
			webPostsToProcess,
			new ORMCachedSpotifyTrack.DBCachedSpotifyTrackSelectManyFromPostsTask.DBCachedSpotifyTrackSelectManyFromPostsTaskCallback() {
				@Override
				public void returnSelectedManyCachedSpotifyTracks(final List<HHPostFullProcess> posts) {
					for (final HHPostFullProcess postToProcess : posts) {
						if (postToProcess.isTrackProcessed()) {

							// TRACK ALREADY IN CACHE
							testProcessPost(callback,
											postToProcess,
											posts);

						} else {

							// GET TRACK FROM WEB
							WebHelper.getSpotifyTrack(
								postToProcess.getPost().getTrack(),
								new WebHelper.GetSpotifyTrackCallback() {
									@Override
									public void returnSpotifyTrack(SpotifyTrack spotifyTrack) {

										postToProcess.setTrack(
											new HHCachedSpotifyTrack(
												spotifyTrack));

										// INSERT TRACK INTO DATABASE
										ORMCachedSpotifyTrack
											.insertTrackFromPosts(
												context,
												postToProcess,
												new ORMCachedSpotifyTrack.DBCachedSpotifyTrackInsertFromPostTask.DBCachedSpotifyTrackInsertFromPostTaskCallback() {
													@Override
													public void returnInsertedManyCachedSpotifyTracks(HHPostFullProcess postToProcess) {
														testProcessPost(
															callback,
															postToProcess,
															posts);
													}
												});
									}
								});

						}
					}
				}
			});

	}

	private static void returnProcessedPosts(final AsyncDataManager.GetWebPostCallback callback, List<HHPostFullProcess> postsToProcess){
		for (HHPostFullProcess postToProcess : postsToProcess){
			testProcessPost(callback, postToProcess, postsToProcess);
		}
	}

	private static void testProcessPost(final AsyncDataManager.GetWebPostCallback callback, HHPostFullProcess postToProcess, List<HHPostFullProcess> postsToProcess){
		if (postToProcess.isPostProcessed()
			&& postToProcess.isTrackProcessed()
			&& postToProcess.isUsersProcessed()
			&& postToProcess.isCommentsProcessed()
			&& postToProcess.isTagsProcessed()){

			//postsToProcess.remove(postToProcess);
			callback.returnWebPost(new HHPostFull(postToProcess));
		}
	}


	public interface InsertCommentCallback{
		void returnInsertedComment(Long commentID, HHComment comment);
	}

	public static void insertComment(Context context, HHComment comment, final InsertCommentCallback callback){
		ORMComment.insertComment(
			context,
			comment,
			new ORMComment.DBCommentInsertTask.DBCommentInsertTaskCallback() {
				@Override
				public void returnInsertedComment(Long commentID, HHComment comment) {
					callback.returnInsertedComment(commentID, comment);
				}
			});
	}

	public interface InsertLikeCallback{
		void returnInsertedLike(Long likeID, HHLike like);
	}

	public static void insertLike(Context context, HHLike like, final InsertLikeCallback callback){
		ORMLike.insertLike(
			context,
			like,
			new ORMLike.DBLikeInsertTask.DBLikeInsertTaskCallback() {
				@Override
				public void returnInsertedLike(Long likeID, HHLike like) {
					callback.returnInsertedLike(likeID, like);
				}
			});
	}

	public interface DeleteLikeCallback{
		void returnDeletedLike(boolean success);
	}

	public static void deleteLike(Context context, HHLike like, final DeleteLikeCallback callback){
		ORMLike.deleteLike(
			context,
			like,
			new ORMLike.DBLikeDeleteTask.DBLikeDeleteTaskCallback() {
				@Override
				public void returnDeletedLike(boolean success) {
					callback.returnDeletedLike(success);
				}
			});
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
