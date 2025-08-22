package search;

import java.sql.Date;

public class Book {
    private long bookId;       // 책ID
    private String title;      // 도서명
    private String author;     // 저자
    private String translator; // 옮긴이
    private Date pubDate;  // 출판일
    private String isbn;       // ISBN
    private int page;          // 쪽수
    private String image;      // 이미지 URL
    private long catCode;      // 분류코드
    private boolean isBorrow; // 책이 대출중인지 여부
    private String catName;
    
    

    public String getCatName() {
		return catName;
	}

	public void setCatName(String catName) {
		this.catName = catName;
	}

	public Book() {
    	this.isBorrow = false;
    }
    
	public long getBookId() {
		return bookId;
	}

	public void setBookId(long bookId) {
		this.bookId = bookId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTranslator() {
		return translator;
	}

	public void setTranslator(String translator) {
		this.translator = translator;
	}

	public Date getPubDate() {
		return pubDate;
	}

	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public long getCatCode() {
		return catCode;
	}

	public void setCatCode(long catCode) {
		this.catCode = catCode;
	}

	public boolean isBorrow() {
		return isBorrow;
	}

	public void setBorrow(boolean isBorrow) {
		this.isBorrow = isBorrow;
	} 
}
