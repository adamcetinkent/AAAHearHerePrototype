package yosoyo.aaahearhereprototype.HHServerClasses;

import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHTagUserNested;

/**
 * Created by adam on 10/03/16.
 */
public class HHTagUser {

	private HHTag tag;
	private HHUser user;

	public HHTagUser(HHTagUserNested nested){
		this.tag = new HHTag(nested);
		this.user = nested.getUser();
	}

	public HHTag getTag() {
		return tag;
	}

	public HHUser getUser() {
		return user;
	}

}
