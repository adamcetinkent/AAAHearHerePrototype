package com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.os.Parcel;

import java.sql.Timestamp;

/**
 * Created by adam on 10/03/16.
 *
 * The basic format of all Hear Here active record classes includes their ID and their timestamps.
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

	HHBase(Parcel in) {
		this.id = in.readLong();
		this.created_at = Timestamp.valueOf(in.readString());
		this.updated_at = Timestamp.valueOf(in.readString());
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		HHBase hhBase = (HHBase) o;

		return id == hhBase.id;

	}

	@Override
	public int hashCode() {
		return (int) (id ^ (id >>> 32));
	}

	void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(created_at.toString());
		dest.writeString(updated_at.toString());
	}
}
