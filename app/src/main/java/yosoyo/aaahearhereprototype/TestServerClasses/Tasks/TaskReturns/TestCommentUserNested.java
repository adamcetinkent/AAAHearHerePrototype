package yosoyo.aaahearhereprototype.TestServerClasses.Tasks.TaskReturns;

import yosoyo.aaahearhereprototype.TestServerClasses.TestComment;
import yosoyo.aaahearhereprototype.TestServerClasses.TestUser;

/**
 * Created by adam on 02/03/16.
 */
public class TestCommentUserNested extends TestComment {

	TestUser user;

	public TestUser getUser(){
		return user;
	}

}
