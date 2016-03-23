package yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;

import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHTagUserNested;

/**
 * Created by adam on 10/03/16.
 */
public class HHTagUser {

	private final HHTag tag;
	private final HHUser user;

	public HHTagUser(HHTagUserNested nested){
		this.tag = new HHTag(nested);
		this.user = nested.getUser();
	}

	public HHTagUser(Cursor cursor){
		this.tag = new HHTag(cursor);
		this.user = new HHUser(cursor);
	}

	public HHTag getTag() {
		return tag;
	}

	public HHUser getUser() {
		return user;
	}

}
