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
                // 0) 책 정보 조회 (잠금 걸고 싶으면 FOR UPDATE 붙이기)
                String title=null, isbn=null, catCode=null;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT title, isbn, catCode FROM book WHERE bookId=?")) {
                    ps.setInt(1, bookId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) { con.rollback(); return false; }
                        title   = rs.getString("title");
                        isbn    = rs.getString("isbn");
                        catCode = rs.getString("catCode");
                    }
                }

                // 1) 중복 대출 방지 (이미 Active가 있으면 실패)
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT borrowId FROM Borrow WHERE bookId=? AND state='Active' FOR UPDATE")) {
                    ps.setInt(1, bookId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) { con.rollback(); return false; }
                    }
                }

                // 2) 대출 INSERT
                try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO Borrow (bookId,userId,title,isbn,callNum,borrowDate,dueDate,state) " +
                    "VALUES (?,?,?,?,?, NOW(), DATE_ADD(NOW(), INTERVAL 14 DAY), 'Active')")) {
                    ps.setInt(1, bookId);
                    ps.setInt(2, userId);
                    ps.setString(3, title);
                    ps.setString(4, isbn);
                    ps.setString(5, catCode); // catCode를 callNum에 저장
                    ps.executeUpdate();
                }

                // 3) 책 상태 업데이트 (같은 커넥션/트랜잭션)
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE book SET isBorrow=1 WHERE bookId=?")) {
                    ps.setInt(1, bookId);
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
                Integer bookId = null;

                // 1) 현재 유저가 빌린 활성 대출인지 확인하며 bookId 가져오기 (잠금)
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT bookId FROM Borrow " +
                        "WHERE borrowId=? AND userId=? AND state='Active' FOR UPDATE")) {
                    ps.setInt(1, borrowId);
                    ps.setInt(2, userId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) bookId = rs.getInt("bookId");
                    }
                }
                if (bookId == null) { con.rollback(); return false; }

                // 2) 대출 레코드 상태 변경
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE Borrow SET state='Returned', returnDate=NOW() WHERE borrowId=?")) {
                    ps.setInt(1, borrowId);
                    ps.executeUpdate();
                }

                // 3) 책 상태도 반납 처리 (같은 트랜잭션)
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE book SET isBorrow=0 WHERE bookId=?")) {
                    ps.setInt(1, bookId);
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

    
    public boolean updateBorrowStatus(int bookId, boolean borrow) throws Exception {
        String sql = "UPDATE book SET isBorrow = ? WHERE bookId = ?";
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, borrow ? 1 : 0);
            ps.setInt(2, bookId);
            int updated = ps.executeUpdate();
            return updated > 0;
        }
    }
}

