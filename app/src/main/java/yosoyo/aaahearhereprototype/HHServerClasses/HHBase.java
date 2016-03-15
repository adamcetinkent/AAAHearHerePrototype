package yosoyo.aaahearhereprototype.HHServerClasses;

import java.sql.Timestamp;

/**
 * Created by adam on 10/03/16.
 */
public abstract class HHBase {

	long id;
	private Timestamp created_at;
	private Timestamp updated_at;

	HHBase(){}

	HHBase(long id, Timestamp created_at, Timestamp updated_at){
		this.id = id;
		this.created_at = created_at;
		this.updated_at = updated_at;
	}

	public long getID(){
		return id;
	}

	public Timestamp getCreatedAt() {
		return created_at;
	}

	public Timestamp getUpdatedAt() {
		return updated_at;
	}

}
