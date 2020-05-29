package lu.lllc.entities;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "tweets")
@EntityListeners(AuditingEntityListener.class)
public class Tweet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private int id;

	@Size(min = 5, max = 50, message = "Tweet title length should be anything between 5 and 50 characters")
	@Column
	@NotBlank(message = "The title cannot be empty or consist only of whitespace characters")
	private String title;

	@Size(max = 160, message = "Tweet text length should be anything between 5 and 160 characters")
	@Column(name = "tweet_text")
	@NotBlank(message = "Tweet text cannot be empty or consist only of whitespace characters")
	private String tweetText;

	@Column
	@CreatedDate
	private Timestamp created;

	@JsonIgnore
	@JsonManagedReference
	@JoinColumn(name = "userId")
	@ManyToOne
	private User user;

	@JsonBackReference
	@OneToMany(mappedBy = "tweet", fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = Like.class)
	private List<Like> likes;

	@JsonIgnore
	@OneToOne(mappedBy = "tweet", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Image image;

	private int likesNo;
	private int dislikesNo;

	/* constructor */
	public Tweet() {
		super();
	}

	/* getters and setters */
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTweetText() {
		return tweetText;
	}

	public void setTweetText(String tweetText) {
		this.tweetText = tweetText;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Like> getLikes() {
		return likes;
	}

	public void setLikes(List<Like> likes) {
		this.likes = likes;
	}

	public int getLikesNo() {
		return likesNo;
	}

	public void setLikesNo(int likesNo) {
		this.likesNo = likesNo;
	}

	public int getDislikesNo() {
		return dislikesNo;
	}

	public void setDislikesNo(int dislikesNo) {
		this.dislikesNo = dislikesNo;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}
}
