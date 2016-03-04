package yosoyo.aaahearhereprototype.TestServerClasses;

import android.database.Cursor;

import java.util.List;

import yosoyo.aaahearhereprototype.TestServerClasses.Tasks.TaskReturns.TestPostUserCommentsNested;

/**
 * Created by adam on 02/03/16.
 */
public class TestPostFull implements Comparable {

	TestPost post;
	TestUser user;
	List<TestCommentUser> comments;
	CachedSpotifyTrack track;

	public TestPostFull(TestPostFullProcess that){
		this.post = that.post;
		this.user = that.user;
		this.comments = that.comments;
		this.track = that.track;
	}

	public TestPostFull(TestPostUserCommentsNested nested){
		this.post = new TestPost(nested);
		this.user = nested.getUser();
		this.comments = nested.getCommentsList();

		// GET TRACK SOMEHOW FROM DB OR FROM WEB
	}

	public TestPostFull(Cursor cursor){
		this.post = new TestPost(cursor);
		this.user = new TestUser(cursor);
		this.track = new CachedSpotifyTrack(cursor);
	}

	public TestPost getPost() {
		return post;
	}

	public TestUser getUser() {
		return user;
	}

	public List<TestCommentUser> getComments() {
		return comments;
	}

	public CachedSpotifyTrack getTrack() {
		return track;
	}

	public void setTrack(CachedSpotifyTrack cachedSpotifyTrack){
		this.track = cachedSpotifyTrack;
	}

	public void setComments(List<TestCommentUser> comments) {
		this.comments = comments;
	}

	/*@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		//if (o == null || getClass() != o.getClass()) return false;

		TestPostFull that = (TestPostFull) o;

		return (post != null ? !post.equals(that.post) : that.post != null);
	}*/

	@Override
	public int compareTo(Object that) {
		if (!(that instanceof TestPostFull))
			return -1;

		return ((TestPostFull) that).getPost().getCreatedAt().compareTo(
			this.getPost().getCreatedAt());
	}

	@Override
	public boolean equals(Object o){
		TestPostFull that = (TestPostFull) o;
		return (this.post.id == that.post.id);
	}

	@Override
	public int hashCode() {
		return (int) post.id;
	}

}
