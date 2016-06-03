package com.yosoyo.aaahearhereprototype.HHServerClasses.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import com.yosoyo.aaahearhereprototype.AsyncDataManager;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHCachedSpotifyTrack;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHComment;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollow;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequest;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequestUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFriendshipUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHLike;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPost;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPostFull;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPostFullProcess;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHTag;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUserFull;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUserFullProcess;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;

import java.util.List;

/**
 * Created by adam on 22/02/16.
 *
 * Handles SqLite Database operations.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = "DatabaseHelper";
	private static final String DB_NAME = "AAAHereHerePrototype";
	private static final int DB_VERSION = 18;

	public DatabaseHelper(Context context){
		super(context, DB_NAME, null, DB_VERSION);
	}

	//**********************************************************************************************
	// DATABASE MAINTENANCE
	//**********************************************************************************************

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
		Log.d(TAG, "Updating database ["
			+ DB_NAME + " v." + oldVersion + "] to [" + DB_NAME + " v." + newVersion
			+ "]...");

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

	@Override
	public void onCreate(SQLiteDatabase db) {
		createDatabase(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		upgradeDatabase(db, oldVersion, newVersion);
	}

	//**********************************************************************************************
	// USER
	//**********************************************************************************************

	// PROCESS USER -------------------

	public interface ProcessCurrentUserCallback{
		void returnProcessCurrentUser(HHUserFull hhUserFull);
	}

	/**
	 * Processes a {@link HHUserFullProcess}, inserting each of the relevant {@link HHUser}
	 * objects into the database:
	 * - current user ({@link HHUser})
	 * - friendship users {@link HHFriendshipUser}
	 * - follow users (both inwards and outwards) {@link HHFollowUser}
	 * - follow request users (both inwards and outwards) {@link HHFollowRequestUser}
	 *
	 * @param context	: {@link Context} required for database insertions
	 * @param user		: {@link HHUserFullProcess} to process
	 * @param callback	: results returned via callback
	 */
	public static void processCurrentUser(final Context context,
										  final HHUserFullProcess user,
										  final ProcessCurrentUserCallback callback){
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

	private static void testProcessUser(final ProcessCurrentUserCallback callback,
										final HHUserFullProcess userToProcess){
		if (userToProcess.isProcessed()){
			callback.returnProcessCurrentUser(new HHUserFull(userToProcess));
		}
	}

	// GET USER -----------------------

	public interface GetUserCallback{
		void returnGetUser(HHUserFull user);
	}

	/**
	 * Queries a user from the database
	 *
	 * @param context	: {@link Context} required for database queries
	 * @param userID	: ID or queried user
	 * @param callback	: results returned via callback
	 */
	public static void getUser(final Context context,
							   final long userID,
							   final GetUserCallback callback){
		ORMUserFull.getUser(
			context,
			userID,
			new ORMUserFull.DBGetUserTask.Callback() {
				@Override
				public void returnGetUser(HHUserFull user) {
					callback.returnGetUser(user);
				}
			}
		);
	}

	// GET USER POST COUNT ------------

	public interface GetUserCachedPostCountCallback{
		void returnUserCachedPostCount(int postCount);
	}

	/**
	 * Queries the number of posts by a user in the database
	 *
	 * @param context	: {@link Context} required for database queries
	 * @param userID	: ID of requested user
	 * @param callback	: results returned via callback
	 */
	public static void getUserCachedPostCount(final Context context,
											  final long userID,
											  final GetUserCachedPostCountCallback callback){
		ORMPost.getUserPostCount(
			context,
			userID,
			new ORMPost.DBUserPostCountTask.Callback() {
				@Override
				public void returnPostCount(int postCount) {
					callback.returnUserCachedPostCount(postCount);
				}
			}
		);
	}

	// GET USER FOLLOWERS COUNT IN ----

	public interface GetUserCachedFollowersInCountCallback {
		void returnUserCachedFollowersInCount(int followersInCount);
	}

	/**
	 * Queries the number of users that follow a user in the database
	 *
	 * @param context	: {@link Context} required for database queries
	 * @param userID	: ID of requested user
	 * @param callback	: results returned via callback
	 */
	public static void getUserCachedFollowersInCount(final Context context,
													 final long userID,
													 final GetUserCachedFollowersInCountCallback callback){
		ORMFollow.getUserFollowersInCount(
			context,
			userID,
			new ORMFollow.DBUserFollowersInCountTask.Callback() {
				@Override
				public void returnFollowersInCount(int followersInCount) {
					callback.returnUserCachedFollowersInCount(followersInCount);
				}
			}
		);
	}

	// GET USER FOLLOWERS OUT COUNT ---

	public interface GetUserCachedFollowersOutCountCallback {
		void returnUserCachedFollowersOutCount(int followersOutCount);
	}

	/**
	 * Queries the number of users followed by a user in the database
	 *
	 * @param context	: {@link Context} required for database queries
	 * @param userID	: ID of requested user
	 * @param callback	: results returned via callback
	 */
	public static void getUserCachedFollowersOutCount(final Context context,
													  final long userID,
													  final GetUserCachedFollowersOutCountCallback callback){
		ORMFollow.getUserFollowersOutCount(
			context,
			userID,
			new ORMFollow.DBUserFollowersOutCountTask.Callback() {
				@Override
				public void returnFollowersOutCount(int followersOutCount) {
					callback.returnUserCachedFollowersOutCount(followersOutCount);
				}
			}
		);
	}

	// GET USER PRIVACY ---------------

	public interface GetUserCachedPrivacyCallback {
		void returnUserCachedPrivacy(boolean userPrivacy);
	}

	/**
	 * Queries the privacy of a user as regards the current user
	 *
	 * @param context	: {@link Context} required for database queries
	 * @param userID	: ID of requested user
	 * @param callback	: results returned via callback
	 */
	public static void getUserCachedPrivacy(final Context context,
											final long userID,
											final GetUserCachedPrivacyCallback callback){
		ORMUser.getUserPrivacy(
			context,
			userID,
			new ORMUser.DBUserPrivacyTask.Callback() {
				@Override
				public void returnUserPrivacy(boolean userPrivacy) {
					callback.returnUserCachedPrivacy(userPrivacy);
				}
			}
		);
	}

	//**********************************************************************************************
	// POSTS
	//**********************************************************************************************

	// GET POST -----------------------

	/**
	 * Queries a post from the database
	 *
	 * @param context	: Context required for database queries
	 * @param postID	: ID of queried post
	 * @param callback	: results returned via callback
	 */
	public static void getCachedPost(final Context context,
									 long postID,
									 final GetCachedPostCallback callback){
		// GET CACHED POST FROM DATABASE
		ORMPostFull.getPost(
			context,
			postID,
			new ORMPostFull.DBPostFullSelectTask.Callback() {
				@Override
				public void returnPost(HHPostFull post) {
					callback.returnGetCachedPost(post);
				}
			});
	}

	// GET USER POSTS -----------------

	/**
	 * Queries all posts by the specified user
	 *
	 * @param context	: {@link Context} required for database queries
	 * @param userID	: ID of user whose posts are requested
	 * @param callback	: results returned via callback
	 */
	public static void getUserCachedPosts(final Context context,
										  long userID,
										  final GetAllCachedPostsCallback callback){
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

	// GET ALL POSTS ------------------

	public interface GetAllCachedPostsCallback {
		void returnGetAllCachedPosts(List<HHPostFull> cachedPosts);
	}

	public interface GetCachedPostCallback {
		void returnGetCachedPost(HHPostFull cachedPost);
	}

	/**
	 * Queries all posts
	 *
	 * @param context	: {@link Context} required for database queries
	 * @param callback	: results returned via callback
	 */
	public static void getAllCachedPosts(final Context context,
										 final GetAllCachedPostsCallback callback){
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

	/**
	 * Processes a {@link HHPostFullProcess}, inserting each of the relevant objects into the database:
	 * - posts ({@link HHPost})
	 * - comments {@link HHComment}
	 * - likes {@link HHLike}
	 * - tags {@link HHTag}
	 * - users {@link HHUser} from posts, comments, likes & tags
	 *
	 * If the {@link HHCachedSpotifyTrack} exists in the database, it is returned, otherwise a call
	 * is made to the Spotify API to download the track and insert it into the database.
	 * @param context			: {@link Context} required to insert into the database
	 * @param webPostsToProcess	: {@link List<HHPostFullProcess>} to process
	 * @param callback			: results returned via callback
	 */
	public static void processWebPosts(final Context context,
									   final List<HHPostFullProcess> webPostsToProcess,
									   final AsyncDataManager.GetPostCallback callback){
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

		// INSERT LIKES INTO DATABASE
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
											postToProcess);

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
															postToProcess);
													}
												});
									}
								});

						}
					}
				}
			});

	}

	private static void returnProcessedPosts(final AsyncDataManager.GetPostCallback callback,
											 final List<HHPostFullProcess> postsToProcess){
		for (HHPostFullProcess postToProcess : postsToProcess){
			testProcessPost(callback, postToProcess);
		}
	}

	private static void testProcessPost(final AsyncDataManager.GetPostCallback callback,
										final HHPostFullProcess postToProcess){
		if (postToProcess.isProcessed()){
			callback.returnGetPost(new HHPostFull(postToProcess));
		}
	}

	// GET POSTS AT LOCATION ----------

	public interface GetPostsAtLocationCallback{
		void returnGetCachedPostsAtLocation(Location location, List<HHPostFull> posts);
	}

	/**
	 * Queries posts at the given location
	 *
	 * @param context	: {@link Context} required for database queries
	 * @param location	: {@link Location} of posts
	 * @param callback	: results returned via callback
	 */
	public static void getPostsAtLocation(final Context context,
										  final Location location,
										  final GetPostsAtLocationCallback callback){
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

	//**********************************************************************************************
	// COMMENTS
	//**********************************************************************************************

	// INSERT COMMENT -----------------

	public interface InsertCommentCallback{
		void returnInsertComment(Long commentID, HHComment comment);
	}

	/**
	 * Inserts a {@link HHComment} into the database
	 *
	 * @param context	: {@link Context} required for database insertion
	 * @param comment	: {@link HHComment} to be inserted
	 * @param callback	: results returned via callback
	 */
	public static void insertComment(final Context context,
									 final HHComment comment,
									 final InsertCommentCallback callback){
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

	//**********************************************************************************************
	// LIKES
	//**********************************************************************************************

	// INSERT LIKE --------------------

	public interface InsertLikeCallback{
		void returnInsertLike(Long likeID, HHLike like);
	}

	/**
	 * Inserts a {@link HHLike} into the database
	 *
	 * @param context	: {@link Context} required for database insertions
	 * @param like		: {@link HHLike} to be inserted
	 * @param callback	: results returned via callback
	 */
	public static void insertLike(final Context context,
								  final HHLike like,
								  final InsertLikeCallback callback){
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

	// DELETE LIKE --------------------

	public interface DeleteLikeCallback{
		void returnDeleteLike(boolean success);
	}

	/**
	 * Deletes a {@link HHLike} from the database
	 *
	 * @param context	: {@link Context} required for database deletion
	 * @param like		: {@link HHLike} to be deleted
	 * @param callback	: results returned via callback
	 */
	public static void deleteLike(final Context context,
								  final HHLike like,
								  final DeleteLikeCallback callback){
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

	//**********************************************************************************************
	// FOLLOWS
	//**********************************************************************************************

	// INSERT FOLLOW ------------------

	public interface InsertFollowCallback{
		void returnInsertFollow(Long followID, HHFollowUser follow);
	}

	/**
	 * Inserts a {@link HHFollow} into the database
	 *
	 * @param context	: {@link Context} required for database insertion
	 * @param follow	: {@link HHFollowUser} to be inserted
	 * @param callback	: results returned via callback
	 */
	public static void insertFollow(final Context context,
									final HHFollowUser follow,
									final InsertFollowCallback callback){
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

	// DELETE FOLLOW ------------------

	public interface DeleteFollowCallback{
		void returnDeleteFollow(boolean success);
	}

	/**
	 * Deletes a {@link HHFollow} from the database
	 *
	 * @param context	: {@link Context} required for database deletions
	 * @param follow	: {@link HHFollowUser} to be deleted
	 * @param callback	: results returned via callback
	 */
	public static void deleteFollow(final Context context,
									final HHFollowUser follow,
									final DeleteFollowCallback callback){
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

	//**********************************************************************************************
	// FOLLOW REQUESTS
	//**********************************************************************************************

	// INSERT FOLLOW REQUEST ----------

	public interface InsertFollowRequestCallback{
		void returnInsertFollowRequest(Long followRequestID, HHFollowRequestUser followRequest);
	}

	/**
	 * Inserts a {@link HHFollowRequest} into the database
	 *
	 * @param context		: {@link Context} required for database insertions
	 * @param followRequest	: {@link HHFollowRequestUser} to be inserted
	 * @param callback		: results returned via callback
	 */
	public static void insertFollowRequest(final Context context,
										   final HHFollowRequestUser followRequest,
										   final InsertFollowRequestCallback callback){
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

	// DELETE FOLLOW REQUEST ----------

	public interface DeleteFollowRequestCallback{
		void returnDeleteFollowRequest(boolean success);
	}

	/**
	 * Deletes a {@link HHFollowRequest} from the database
	 *
	 * @param context		: {@link Context} required for database deletions
	 * @param followRequest	: {@link HHFollowRequestUser} to be deleted
	 * @param callback		: results returned via callback
	 */
	public static void deleteFollowRequest(final Context context,
										   final HHFollowRequestUser followRequest,
										   final DeleteFollowRequestCallback callback){
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

	//**********************************************************************************************
	// SPOTIFY TRACKS
	//**********************************************************************************************

	// GET SPOTIFY TRACK --------------

	public interface GetCachedSpotifyTrackCallback{
		void returnGetCachedSpotifyTrack(HHCachedSpotifyTrack track);
	}

	/**
	 * Queries {@link HHCachedSpotifyTrack} from the database
	 *
	 * @param context	: {@link Context} required for database queries
	 * @param trackID	: ID of track requested
	 * @param callback	: results returned via callback
	 */
	public static void getCachedSpotifyTrack(final Context context,
											 final String trackID,
											 final GetCachedSpotifyTrackCallback callback){
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

	// INSERT SPOTIFY TRACK -----------

	public interface InsertCachedSpotifyTrackCallback{
		void returnGetCachedSpotifyTrack(HHCachedSpotifyTrack track);
	}

	/**
	 * Inserts {@link SpotifyTrack} into database as {@link HHCachedSpotifyTrack}
	 *
	 * @param context		: {@link Context} required for database insertions
	 * @param spotifyTrack	: {@link SpotifyTrack} to be inserted
	 * @param callback		: results returned via callback
	 */
	public static void insertSpotifyTrack(final Context context,
										  final SpotifyTrack spotifyTrack,
										  final InsertCachedSpotifyTrackCallback callback){
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

}
