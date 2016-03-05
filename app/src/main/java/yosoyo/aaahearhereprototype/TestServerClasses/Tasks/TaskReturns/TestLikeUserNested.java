package yosoyo.aaahearhereprototype.TestServerClasses.Tasks.TaskReturns;

import yosoyo.aaahearhereprototype.TestServerClasses.TestLike;
import yosoyo.aaahearhereprototype.TestServerClasses.TestUser;

/**
 * Created by adam on 02/03/16.
 */
public class TestLikeUserNested extends TestLike {

	TestUser user;

	public TestUser getUser(){
		return user;
	}

}
