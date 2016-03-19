package yosoyo.aaahearhereprototype.HHServerClasses.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import java.util.List;

import yosoyo.aaahearhereprototype.AsyncDataManager;
import yosoyo.aaahearhereprototype.HHServerClasses.HHCachedSpotifyTrack;
import yosoyo.aaahearhereprototype.HHServerClasses.HHComment;
import yosoyo.aaahearhereprototype.HHServerClasses.HHFollowRequestUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHFollowUser;
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
	private static final int DB_VERSION = 14;

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
		db.execSQL(ORMFollow.SQL_CREATE_TABLE);
		db.execSQL(ORMFollowRequest.SQL_CREATE_TABLE);
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
		db.execSQL(ORMFollow.SQL_DROP_TABLE);
		db.execSQL(ORMFollowRequest.SQL_DROP_TABLE);
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
		ORMFollow.resetTable(context);
		ORMFollowRequest.resetTable(context);
		ORMCachedSpotifyTrack.resetTable(context);

		Log.d(TAG, "Database reset");
	}

	public interface ProcessCurrentUserCallback{
		void returnProcessCurrentUser(HHUserFull hhUserFull);
	}

	public static void processCurrentUser(Context context, HHUserFullProcess user, final ProcessCurrentUserCallback callback){
		// INSERT CURRENT USER, FRIENDSHIP USERS, FOLLOW USERS & FOLLOW_REQUEST USERS
		ORMUser.insertUsersFromUser(
			context,
			user,
			new ORMUser.DBUserInsertManyFromUserTask.Callback() {
				@Override
				public void returnInsertedManyFromUser(long userID, HHUserFullProcess returnedUser) {
					testProcessUser(callback, returnedUser);
				}
			});

		// INSERT FRIENDSHIPS
		ORMFriendship.insertFriendshipsFromUser(
			context,
			user,
			new ORMFriendship.DBFriendshipInsertManyFromUserTask.Callback() {
				@Override
				public void returnInsertedManyFriendships(HHUserFullProcess returnedUser) {
					testProcessUser(callback, returnedUser);
				}
			});

		// INSERT FOLLOWS
		ORMFollow.insertFollowsFromUser(
			context,
			user,
			new ORMFollow.DBFollowInsertManyFromUserTask.Callback() {
				@Override
				public void returnInsertedManyFollows(HHUserFullProcess returnedUser) {
					testProcessUser(callback, returnedUser);
				}
			});

		// INSERT FOLLOW_REQUESTS
		ORMFollowRequest.insertFollowRequestsFromUser(
			context,
			user,
			new ORMFollowRequest.DBFollowRequestInsertManyFromUserTask.Callback() {
				@Override
				public void returnInsertedManyFollowRequests(HHUserFullProcess returnedUser) {
					testProcessUser(callback, returnedUser);
				}
			});
	}

	private static void testProcessUser(final ProcessCurrentUserCallback callback, HHUserFullProcess userToProcess){
		if (userToProcess.isProcessed()){
			callback.returnProcessCurrentUser(new HHUserFull(userToProcess));
		}
	}

	public interface GetAllCachedPostsCallback {
		void returnGetAllCachedPosts(List<HHPostFull> cachedPosts);
	}

	public static void getAllCachedPosts(Context context, final GetAllCachedPostsCallback callback){
		// GET CACHED POSTS FROM DATABASE
		ORMPostFull.getAllPosts(
			context,
			new ORMPostFull.DBPostFullSelectAllTask.Callback() {
				@Override
				public void returnPosts(List<HHPostFull> posts) {
					callback.returnGetAllCachedPosts(posts);
				}
			});
	}

	public static void getUserCachedPosts(Context context, long userID, final GetAllCachedPostsCallback callback){
		// GET CACHED POSTS FROM DATABASE
		ORMPostFull.getUserPosts(
			context,
			userID,
			new ORMPostFull.DBPostFullSelectUserTask.Callback() {
				@Override
				public void returnPosts(List<HHPostFull> posts) {
					callback.returnGetAllCachedPosts(posts);
				}
			});
	}

	public static void processWebPosts(final Context context, final AsyncDataManager.GetWebPostCallback callback, final List<HHPostFullProcess> webPostsToProcess){
		// INSERT POSTS INTO DATABASE
		ORMPost.insertPosts(
			context,
			webPostsToProcess,
			new ORMPost.DBPostInsertManyTask.Callback() {
				@Override
				public void returnInsertedManyPosts(List<HHPostFullProcess> postsToProcess) {
					returnProcessedPosts(callback, postsToProcess);
				}
			});

		// INSERT COMMENTS INTO DATABASE
		ORMComment.insertCommentsFromPosts(
			context,
			webPostsToProcess,
			new ORMComment.DBCommentInsertManyFromPostsTask.Callback() {
				@Override
				public void returnInsertedManyComments(List<HHPostFullProcess> postsToProcess) {
					returnProcessedPosts(callback, postsToProcess);
				}
			});

		// INSERT COMMENTS INTO DATABASE
		ORMLike.insertLikesFromPosts(
			context,
			webPostsToProcess,
			new ORMLike.DBLikeInsertManyFromPostsTask.Callback() {
				@Override
				public void returnInsertedManyLikes(List<HHPostFullProcess> postsToProcess) {
					returnProcessedPosts(callback, postsToProcess);
				}
			});

		// INSERT TAGS INTO DATABASE
		ORMTag.insertTagsFromPosts(
			context,
			webPostsToProcess,
			new ORMTag.DBTagInsertManyFromPostsTask.Callback() {
				@Override
				public void returnInsertedManyTags(List<HHPostFullProcess> postsToProcess) {
					returnProcessedPosts(callback, postsToProcess);
				}
			});

		// INSERT USERS, COMMENT USERS, LIKE USERS, TAG USERS INTO DATABASE
		ORMUser.insertUsersFromPosts(
			context,
			webPostsToProcess,
			new ORMUser.DBUserInsertManyFromPostsTask.Callback() {
				@Override
				public void returnInsertedManyUsers(List<HHPostFullProcess> postsToProcess) {
					returnProcessedPosts(callback, postsToProcess);
				}
			});

		ORMCachedSpotifyTrack.getTracksFromPosts(
			context,
			webPostsToProcess,
			new ORMCachedSpotifyTrack.DBCachedSpotifyTrackSelectManyFromPostsTask.Callback() {
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
												new ORMCachedSpotifyTrack.DBCachedSpotifyTrackInsertFromPostTask.Callback() {
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
		if (postToProcess.isProcessed()){

			//postsToProcess.remove(postToProcess);
			callback.returnGetWebPost(new HHPostFull(postToProcess));
		}
	}

	public interface GetPostsAtLocationCallback{
		void returnGetCachedPostsAtLocation(Location location, List<HHPostFull> posts);
	}

	public static void getPostsAtLocation(Context context, final Location location, final GetPostsAtLocationCallback callback){
		ORMPostFull.getPostsAtLocation(
			context,
			location,
			new ORMPostFull.DBPostSelectAtLocationTask.Callback() {
				@Override
				public void returnPosts(List<HHPostFull> posts) {
					callback.returnGetCachedPostsAtLocation(location, posts);
				}
			});
	}

	public interface InsertCommentCallback{
		void returnInsertComment(Long commentID, HHComment comment);
	}

	public static void insertComment(Context context, HHComment comment, final InsertCommentCallback callback){
		ORMComment.insertComment(
			context,
			comment,
			new ORMComment.DBCommentInsertTask.Callback() {
				@Override
				public void returnInsertedComment(Long commentID, HHComment comment) {
					callback.returnInsertComment(commentID, comment);
				}
			});
	}

	public interface InsertLikeCallback{
		void returnInsertLike(Long likeID, HHLike like);
	}

	public static void insertLike(Context context, HHLike like, final InsertLikeCallback callback){
		ORMLike.insertLike(
			context,
			like,
			new ORMLike.DBLikeInsertTask.Callback() {
				@Override
				public void returnInsertedLike(Long likeID, HHLike like) {
					callback.returnInsertLike(likeID, like);
				}
			});
	}

	public interface DeleteLikeCallback{
		void returnDeleteLike(boolean success);
	}

	public static void deleteLike(Context context, HHLike like, final DeleteLikeCallback callback){
		ORMLike.deleteLike(
			context,
			like,
			new ORMLike.DBLikeDeleteTask.Callback() {
				@Override
				public void returnDeleteLike(boolean success) {
					callback.returnDeleteLike(success);
				}
			});
	}

	public interface InsertFollowCallback{
		void returnInsertFollow(Long followID, HHFollowUser follow);
	}

	public static void insertFollow(Context context, HHFollowUser follow, final InsertFollowCallback callback){
		ORMFollow.insertFollow(
			context,
			follow,
			new ORMFollow.DBFollowInsertTask.Callback() {
				@Override
				public void returnInsertFollow(Long followID, HHFollowUser follow) {
					callback.returnInsertFollow(followID, follow);
				}
			});
	}

	public interface DeleteFollowCallback{
		void returnDeleteFollow(boolean success);
	}

	public static void deleteFollow(Context context, HHFollowUser follow, final DeleteFollowCallback callback){
		ORMFollow.deleteFollow(
			context,
			follow,
			new ORMFollow.DBFollowDeleteTask.Callback() {
				@Override
				public void returnDeleteFollow(boolean success) {
					callback.returnDeleteFollow(success);
				}
			});
	}

	public interface InsertFollowRequestCallback{
		void returnInsertFollowRequest(Long followRequestID, HHFollowRequestUser followRequest);
	}

	public static void insertFollowRequest(Context context, HHFollowRequestUser followRequest, final InsertFollowRequestCallback callback){
		ORMFollowRequest.insertFollowRequest(
			context,
			followRequest,
			new ORMFollowRequest.DBFollowRequestInsertTask.Callback() {
				@Override
				public void returnInsertFollowRequest(Long followRequestID, HHFollowRequestUser followRequest) {
					callback.returnInsertFollowRequest(followRequestID, followRequest);
				}
			});
	}

	public interface DeleteFollowRequestCallback{
		void returnDeleteFollowRequest(boolean success);
	}

	public static void deleteFollowRequest(Context context, HHFollowRequestUser followRequest, final DeleteFollowRequestCallback callback){
		ORMFollowRequest.deleteFollowRequest(
			context,
			followRequest.getFollowRequest(),
			new ORMFollowRequest.DBFollowRequestDelete.Callback() {
				@Override
				public void returnDeletedLike(boolean success) {
					callback.returnDeleteFollowRequest(success);
				}
			});
	}

	public interface GetCachedSpotifyTrackCallback{
		void returnGetCachedSpotifyTrack(HHCachedSpotifyTrack track);
	}

	public static void getCachedSpotifyTrack(Context context, String trackID, final GetCachedSpotifyTrackCallback callback){
		ORMCachedSpotifyTrack.getCachedSpotifyTrack(
			context,
			trackID,
			new ORMCachedSpotifyTrack.GetDBCachedSpotifyTrackTask.Callback() {
				@Override
				public void returnCachedSpotifyTrack(HHCachedSpotifyTrack cachedSpotifyTrack) {
					callback.returnGetCachedSpotifyTrack(cachedSpotifyTrack);
				}
			});
	}

	public interface InsertCachedSpotifyTrackCallback{
		void returnGetCachedSpotifyTrack(HHCachedSpotifyTrack track);
	}

	public static void insertSpotifyTrack(Context context, SpotifyTrack spotifyTrack, final InsertCachedSpotifyTrackCallback callback){
		ORMCachedSpotifyTrack.insertSpotifyTrack(
			context,
			spotifyTrack,
			new ORMCachedSpotifyTrack.InsertCachedSpotifyTrackTask.Callback() {
				@Override
				public void returnInsertCachedSpotifyTrack(Long trackID, HHCachedSpotifyTrack cachedSpotifyTrack) {
					callback.returnGetCachedSpotifyTrack(cachedSpotifyTrack);
				}
			}
		);
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
