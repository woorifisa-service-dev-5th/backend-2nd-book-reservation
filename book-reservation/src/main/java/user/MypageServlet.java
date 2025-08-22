package user;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.*;

@WebServlet("/MypageServlet")
public class MypageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        int userId = SessionUtils.getUserId(request);
        String userName = SessionUtils.getUserName(request);
        String userEmail = SessionUtils.getUserEmail(request);

        if (userId == -1) {
            response.sendRedirect("LoginServlet"); // 로그인 안 되어 있으면
            return;
        }

        out.println("<!DOCTYPE html>");
        out.println("<html lang='ko'><head><meta charset='UTF-8'><title>마이페이지</title></head><body>");
        out.println("<h1>안녕하세요, " + userName + "님!</h1>");

        // 사용자 개인정보
        out.println("<p><b>이메일:</b> " + userEmail + "</p>");
        out.println("<hr>");

        // -------------------------------
        // 대출 내역 조회
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://192.168.0.42:3306/book_reservation?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                "test1", "1234");

            String sql = "SELECT bookId, title, isbn, callNum, borrowDate, dueDate, returnDate, state " +
                         "FROM borrow " +
                         "WHERE userId = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            out.println("<h2>대출 내역</h2>");
            out.println("<table border='1'>");
            out.println("<tr><th>제목</th><th>ISBN</th><th>청구기호</th><th>대출일</th><th>반납예정일</th><th>반납일</th><th>상태</th></tr>");

            while (rs.next()) {
                out.println("<tr>");
             //   out.println("<td>" + rs.getInt("bookId") + "</td>");
                out.println("<td>" + rs.getString("title") + "</td>");
                out.println("<td>" + rs.getString("isbn") + "</td>");
                out.println("<td>" + rs.getString("callNum") + "</td>");
                out.println("<td>" + rs.getDate("borrowDate") + "</td>");
                out.println("<td>" + rs.getDate("dueDate") + "</td>");
                java.sql.Date returnDate = rs.getDate("returnDate");
                out.println("<td>" + (returnDate != null ? returnDate : "") + "</td>");
                String state = rs.getString("state");
                if ("Active".equalsIgnoreCase(state)) {
                    state = "대출중";
                } else if ("Returned".equalsIgnoreCase(state)) {
                    state = "반납완료";
                } 
                out.println("<td>" + state + "</td>");
                out.println("</tr>");
            }
            out.println("</table>");

            out.println("<hr>");
            // 최하단 로그아웃 버튼
            out.println("<div style='display: flex; justify-content: space-between;'>");
            out.println("<button type='button' onclick='history.back()'>이전 페이지로</button>");
            out.println("<a href='LogoutServlet'>로그아웃</a>");
            out.println("</div>");

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h2>에러 발생: " + e.getMessage() + "</h2>");
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        out.println("</body></html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
