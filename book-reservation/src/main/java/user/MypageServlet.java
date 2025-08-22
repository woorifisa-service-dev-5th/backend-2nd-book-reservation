package user;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import util.*;

@WebServlet("/MypageServlet")
public class MypageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        int userId = SessionUtils.getUserId(request);
        String userName = SessionUtils.getUserName(request);
        String userEmail = SessionUtils.getUserEmail(request);
        String ctx = request.getContextPath();

        if (userId == -1) {
            response.sendRedirect("LoginServlet");
            return;
        }

        // ✅ 여기서 '한 번만' 선언
        String ret    = request.getParameter("ret");     // "1" or "0"
        String reason = request.getParameter("reason");  // "notfound", "forbidden" ...

        out.println("<!DOCTYPE html>");
        out.println("<html lang='ko'><head><meta charset='UTF-8'><title>마이페이지</title>");
        out.println("<style>table{border-collapse:collapse}th,td{border:1px solid #999;padding:6px}</style>");
        out.println("</head><body>");
        out.println("<h1>안녕하세요, " + userName + "님!</h1>");
        out.println("<p><b>이메일:</b> " + userEmail + "</p>");
        out.println("<hr>");

        // ✅ 반납 결과 메시지 (중복 선언 없이 사용만)
        if ("1".equals(ret)) {
            out.println("<p style='color:green;'>반납이 완료되었습니다.</p>");
        } else if ("0".equals(ret)) {
            String msg = "반납에 실패했습니다.";
            if ("notfound".equals(reason)) msg = "대출 내역을 찾을 수 없습니다.";
            else if ("forbidden".equals(reason)) msg = "본인 대출건만 반납할 수 있습니다.";
            out.println("<p style='color:red;'>" + msg + "</p>");
        }

        // -------------------------------
        // 대출 내역 조회
        try (
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://192.168.0.42:3306/book_reservation?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                "test1", "1234");
            PreparedStatement pstmt = conn.prepareStatement(
                "SELECT borrowId, bookId, title, isbn, callNum, borrowDate, dueDate, returnDate, state " +
                "FROM borrow WHERE userId = ? ORDER BY borrowDate DESC")
        ) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                out.println("<h2>대출 내역</h2>");
                out.println("<table>");
                out.println("<tr><th>제목</th><th>ISBN</th><th>청구기호</th><th>대출일</th><th>반납예정일</th><th>반납일</th><th>상태</th><th>반납</th></tr>");

                while (rs.next()) {
                    int borrowId = rs.getInt("borrowId");
                    String title = rs.getString("title");
                    String isbnVal = rs.getString("isbn");
                    String callNum = rs.getString("callNum");
                    Date borrowDate = rs.getDate("borrowDate");
                    Date dueDate = rs.getDate("dueDate");
                    Date returnDate = rs.getDate("returnDate");
                    String state = rs.getString("state");

                    String stateKo;
                    if ("Active".equalsIgnoreCase(state)) stateKo = "대출중";
                    else if ("Returned".equalsIgnoreCase(state)) stateKo = "반납완료";
                    else if ("Overdue".equalsIgnoreCase(state)) stateKo = "연체";
                    else stateKo = state;

                    out.println("<tr>");
                    out.println("<td>" + title + "</td>");
                    out.println("<td>" + isbnVal + "</td>");
                    out.println("<td>" + callNum + "</td>");
                    out.println("<td>" + (borrowDate != null ? borrowDate : "") + "</td>");
                    out.println("<td>" + (dueDate != null ? dueDate : "") + "</td>");
                    out.println("<td>" + (returnDate != null ? returnDate : "") + "</td>");
                    out.println("<td>" + stateKo + "</td>");

                    out.println("<td>");
                    if ("Active".equalsIgnoreCase(state)) {
                        out.println("<form method='post' action='" + ctx + "/return' style='margin:0;'>");
                        out.println("<input type='hidden' name='userId' value='" + userId + "'>");
                        out.println("<input type='hidden' name='borrowId' value='" + borrowId + "'>");
                        out.println("<button type='submit'>반납하기</button>");
                        out.println("</form>");
                    } else {
                        out.println("<button type='button' disabled>반납불가</button>");
                    }
                    out.println("</td>");

                    out.println("</tr>");
                }
                out.println("</table>");
            }

            out.println("<hr>");
            out.println("<div style='display:flex;justify-content:space-between;'>");
            out.println("<button type='button' onclick='history.back()'>이전 페이지로</button>");
            out.println("<a href='LogoutServlet'>로그아웃</a>");
            out.println("</div>");

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h2>에러 발생: " + e.getMessage() + "</h2>");
        }

        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        doGet(request, response);
    }
}
