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
import javax.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // 로그인 폼 보여주기
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang='ko'>");
        out.println("<head><meta charset='UTF-8'><title>로그인</title></head>");
        out.println("<body>");
        out.println("<h1>로그인</h1>");
        out.println("<form method='post' action='LoginServlet'>");
        out.println("이메일: <input type='text' name='email'><br>");
        out.println("비밀번호: <input type='password' name='password'><br>");
        out.println("<input type='submit' value='로그인'>");
        out.println("</form>");
        out.println("<p><a href='RegisterServlet'>회원가입</a></p>");
        out.println("</body></html>");
    }

    // 로그인 처리
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>로그인 결과</title></head><body>");

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://192.168.0.42:3306/book_reservation?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                "test1", "1234");

            String sql = "SELECT * FROM users WHERE mail=? AND password=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // 로그인 성공 → 세션 저장
                HttpSession session = request.getSession();
                session.setAttribute("userId", rs.getInt("userId"));
                session.setAttribute("userName", rs.getString("name"));
                session.setAttribute("userEmail", rs.getString("mail"));

                out.println("<h2>로그인 성공!</h2>");
                out.println("<p>안녕하세요, " + rs.getString("name") + "님</p>");
                out.println("<a href='MypageServlet'>마이페이지로 이동</a>");
                out.println("<form action='LogoutServlet' method='get'>");
                out.println("<input type='submit' value='로그아웃'>");
                out.println("</form>");
            } else {
                out.println("<h2>로그인 실패</h2>");
                out.println("<a href='LoginServlet'>다시 시도</a>");
            }

            out.println("</body></html>");

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h2>에러 발생: " + e.getMessage() + "</h2>");
            out.println("<a href='LoginServlet'>다시 시도</a>");
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }
}
