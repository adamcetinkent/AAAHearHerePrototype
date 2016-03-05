package yosoyo.aaahearhereprototype.TestServerClasses.Tasks.TaskReturns;

import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.TestServerClasses.TestCommentUser;
import yosoyo.aaahearhereprototype.TestServerClasses.TestLikeUser;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPost;
import yosoyo.aaahearhereprototype.TestServerClasses.TestUser;

/**
 * Created by adam on 02/03/16.
 */
public class TestPostUserCommentsNested extends TestPost {

	TestUser user;
	TestCommentUserNested[] comments;
	TestLikeUserNested[] likes;

	public TestUser getUser(){
		return user;
	}

	public List<TestCommentUser> getCommentsList(){
		List<TestCommentUser> testCommentUsers = new ArrayList<>(comments.length);
		for (TestCommentUserNested commentNested : comments){
			TestCommentUser testCommentUser = new TestCommentUser(commentNested);
			testCommentUsers.add(testCommentUser);
		}
		return testCommentUsers;
	}

	public List<TestLikeUser> getLikesList(){
		List<TestLikeUser> testLikeUsers = new ArrayList<>(likes.length);
		for (TestLikeUserNested testLikeUserNested : likes){
			TestLikeUser testLikeUser = new TestLikeUser(testLikeUserNested);
			testLikeUsers.add(testLikeUser);
		}
		return testLikeUsers;
	}

}
