package lu.lllc.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "images")
public class Image {

	@Id
	@Column(name = "id")
	private int id;

	@OneToOne
	@MapsId
	private Tweet tweet;

	@Column
	@Lob
	private byte[] image;

	@Column
	private String mime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Tweet getTweet() {
		return tweet;
	}

	public void setTweet(Tweet tweet) {
		this.tweet = tweet;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getMime() {
		return mime;
	}

	public void setMime(String mime) {
		this.mime = mime;
	}

	public Image() {
		super();
		// TODO Auto-generated constructor stub
	}

}