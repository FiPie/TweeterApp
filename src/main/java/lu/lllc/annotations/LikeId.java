//package lu.lllc.annotations;
//
//import java.io.Serializable;
//import java.util.Objects;
//
//import javax.persistence.ManyToOne;
//
//import lu.lllc.entities.Tweet;
//import lu.lllc.entities.User; 
//
//public class LikeId implements Serializable {
//
//	@ManyToOne
//    private User user;
// 
//	@ManyToOne
//    private Tweet tweet;
//	
//    
//    public LikeId(){}
//
//
//	public LikeId(User user, Tweet tweet) {
//		this.user = user;
//		this.tweet = tweet;
//	}
//
//
//	public User getUser() {
//		return user;
//	}
//
//
//	public void setUser(User user) {
//		this.user = user;
//	}
//
//
//	public Tweet getTweet() {
//		return tweet;
//	}
//
//
//	public void setTweet(Tweet tweet) {
//		this.tweet = tweet;
//	}
//
//
//	@Override
//	public int hashCode() {
//		return Objects.hash(tweet, user);
//	}
//
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (!(obj instanceof LikeId))
//			return false;
//		LikeId other = (LikeId) obj;
//		return Objects.equals(tweet, other.tweet) && Objects.equals(user, other.user);
//	}
//
//
//	
//	
//}
