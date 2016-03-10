package yosoyo.aaahearhereprototype.HHServerClasses;

import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHTagUserNested;

/**
 * Created by adam on 10/03/16.
 */
public class HHTag extends HHLike {

	public HHTag(HHTagUserNested nested){
		super(
			nested.getID(),
			nested.getPostID(),
			nested.getUserID(),
			nested.getCreatedAt(),
			nested.getUpdatedAt()
			 );
	}

}
