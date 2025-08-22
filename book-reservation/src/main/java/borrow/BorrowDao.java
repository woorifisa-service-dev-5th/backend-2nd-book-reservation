package borrow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

public class BorrowDao {
    private final DataSource ds;
    public BorrowDao(DataSource ds) { this.ds = ds; }

    public boolean borrowBook(int userId, int bookId) throws Exception {
        try (Connection con = ds.getConnection()) {
            con.setAutoCommit(false);
            try {
                // 0) 책 정보 조회 (title, isbn, catCode) 
                String title=null, isbn=null, catCode=null;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT title, isbn, catCode FROM book WHERE bookId=?")) {
                    ps.setInt(1, bookId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) { con.rollback(); return false; } // 없는 책
                        title   = rs.getString("title");
                        isbn    = rs.getString("isbn");
                        catCode = rs.getString("catCode"); // DB에 catCode 존재
                    }
                }

                // 1) 중복 대출 방지 (해당 책 Active가 이미 있나)
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT borrowId FROM Borrow WHERE bookId=? AND state='Active' FOR UPDATE")) {
                    ps.setInt(1, bookId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) { con.rollback(); return false; }
                    }
                }

                // 2) INSERT 
                try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO Borrow (bookId,userId,title,isbn,callNum,borrowDate,dueDate,state) " +
                    "VALUES (?,?,?,?,?, NOW(), DATE_ADD(NOW(), INTERVAL 14 DAY), 'Active')")) {
                    ps.setInt(1, bookId);
                    ps.setInt(2, userId);
                    ps.setString(3, title);
                    ps.setString(4, isbn);
                    ps.setString(5, catCode); // catCode → callNum 컬럼에 저장
                    ps.executeUpdate();
                }

                con.commit();
                return true;
            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public boolean returnBook(int borrowId, int userId) throws Exception {
        try (Connection con = ds.getConnection()) {
            con.setAutoCommit(false);
            try {
                Integer id = null;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT borrowId FROM Borrow WHERE borrowId=? AND userId=? AND state='Active' FOR UPDATE")) {
                    ps.setInt(1, borrowId);
                    ps.setInt(2, userId);
                    try (ResultSet rs = ps.executeQuery()) { if (rs.next()) id = rs.getInt(1); }
                }
                if (id == null) { con.rollback(); return false; }
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE Borrow SET state='Returned', returnDate=NOW() WHERE borrowId=?")) {
                    ps.setInt(1, borrowId);
                    ps.executeUpdate();
                }
                con.commit();
                return true;
            } catch (Exception e) { con.rollback(); throw e; }
            finally { con.setAutoCommit(true); }
        }
    }
}

