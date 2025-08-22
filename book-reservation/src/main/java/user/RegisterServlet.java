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

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // 회원가입 폼 보여주기
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang='ko'>");
        out.println("<head><meta charset='UTF-8'><title>회원가입</title></head>");
        out.println("<body>");
        out.println("<h1>회원가입</h1>");
        out.println("<form method='post' action='RegisterServlet'>");
        out.println("이메일: <input type='text' name='email'><br>");
        out.println("비밀번호: <input type='password' name='password'><small> (6자리 이상)</small><br>");
        out.println("이름: <input type='text' name='name'><br>");
        out.println("주소: <input type='text' name='address'><br>");
        out.println("<input type='submit' value='회원가입'>");
        out.println("</form>");
        out.println("</body>");
        out.println("</html>");
    }

    // 회원가입 처리
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String address = request.getParameter("address");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>회원가입 결과</title></head><body>");

        // 비밀번호 길이 체크
        if (password == null || password.length() < 6) {
            out.println("<h2>비밀번호는 6자리 이상이어야 합니다.</h2>");
            out.println("<a href='RegisterServlet'>다시 시도</a>");
            out.println("</body></html>");
            return;
        }
        
        // 이메일 형식 체크
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$")) {
            out.println("<h2>올바른 이메일 형식이 아닙니다.</h2>");
            out.println("<a href='RegisterServlet'>다시 시도</a></body></html>");
            return;
        }

        // 이름/주소 빈칸 체크
        if (name == null || name.trim().isEmpty()) {
            out.println("<h2>이름을 입력하세요.</h2>");
            out.println("<a href='RegisterServlet'>다시 시도</a></body></html>");
            return;
        }
        if (address == null || address.trim().isEmpty()) {
            out.println("<h2>주소를 입력하세요.</h2>");
            out.println("<a href='RegisterServlet'>다시 시도</a></body></html>");
            return;
        }

        // 이름주소 길이 체크
        if (name.length() > 100) {
            out.println("<h2>이름은 100자 이하로 입력하세요.</h2>");
            out.println("<a href='RegisterServlet'>다시 시도</a></body></html>");
            return;
        }
        if (address.length() > 255) {
            out.println("<h2>주소는 255자 이하로 입력하세요.</h2>");
            out.println("<a href='RegisterServlet'>다시 시도</a></body></html>");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement checkStmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://192.168.0.42:3306/book_reservation?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                "test1", "1234");

            // 1. 이메일 중복 체크
            String checkSql = "SELECT COUNT(*) FROM users WHERE mail=?";
            checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, email);
            rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count > 0) {
                out.println("<h2>이미 가입된 이메일입니다.</h2>");
                out.println("<a href='RegisterServlet'>다시 시도</a>");
            } else {
                // 2. INSERT
                String sql = "INSERT INTO users (mail, password, name, address) VALUES (?, ?, ?, ?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, email);
                pstmt.setString(2, password);
                pstmt.setString(3, name);
                pstmt.setString(4, address);

                int result = pstmt.executeUpdate();
                if (result > 0) {
                    out.println("<h2>회원가입 성공!</h2>");
                    out.println("<a href='LoginServlet'>로그인 페이지로 이동</a>");
                } else {
                    out.println("<h2>회원가입 실패</h2>");
                    out.println("<a href='RegisterServlet'>다시 시도</a>");
                }
            }

            out.println("</body></html>");

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h2>에러 발생: " + e.getMessage() + "</h2>");
            out.println("<a href='RegisterServlet'>다시 시도</a>");
            out.println("</body></html>");
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (checkStmt != null) checkStmt.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }
}
