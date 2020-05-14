package lu.lllc.dto;

public class LikersRequestDto {

	private String tweetid;
	private int size;
	public LikersRequestDto(String tweetid, int size) {
		this.tweetid = tweetid;
		this.size = size;
	}
	public String getTweetid() {
		return tweetid;
	}
	public void setTweetid(String tweetid) {
		this.tweetid = tweetid;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	

	
 
	
}
