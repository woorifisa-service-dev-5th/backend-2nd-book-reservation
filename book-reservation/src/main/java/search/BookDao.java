package search;

import util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDao {

    // 제목으로 도서 검색
	public List<Book> searchBooks(String keyword, String criteria) {
	    List<Book> books = new ArrayList<>();
	    
	    // criteria 검증
	    if (!("title".equals(criteria) || "author".equals(criteria) || "isbn".equals(criteria))) {
	        criteria = "title";
	    }
	    
	    String sql = "SELECT * FROM Book WHERE " + criteria + " LIKE ?";
	    
	    try (Connection conn = DBUtil.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	         
	        pstmt.setString(1, "%" + keyword + "%");
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            Book book = new Book();
	            book.setBookId(rs.getLong("bookId"));
	            book.setTitle(rs.getString("title"));
	            book.setAuthor(rs.getString("author"));
	            book.setTranslator(rs.getString("translator"));
	            book.setPubDate(rs.getDate("pubDate"));
	            book.setIsbn(rs.getString("isbn"));
	            book.setPage(rs.getInt("page"));
	            book.setImage(rs.getString("image"));
	            book.setCatCode(rs.getLong("catCode"));
	            book.setBorrow(rs.getBoolean("isBorrow"));

	            books.add(book);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return books;
	}

}
