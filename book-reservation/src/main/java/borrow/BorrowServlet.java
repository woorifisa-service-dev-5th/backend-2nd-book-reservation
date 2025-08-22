package borrow;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;


@WebServlet("/borrow")
public class BorrowServlet extends HttpServlet {
    private BorrowDao dao;

    @Override
    public void init() {
        dao = new BorrowDao(resolveDataSource());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        // 필수 파라미터 검증
        final String userIdStr = req.getParameter("userId");
        final String bookIdStr = req.getParameter("bookId");
        if (userIdStr == null || bookIdStr == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "필수 파라미터 누락");
            return;
        }

        final int userId, bookId;
        try {
            userId = Integer.parseInt(userIdStr);
            bookId = Integer.parseInt(bookIdStr);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "숫자 형식 오류");
            return;
        }

        try {
            boolean ok = dao.borrowBook(userId, bookId); // 책 메타는 DAO에서 조회
            if (ok) {
                // 성공 → search.jsp로 리다이렉트
                resp.sendRedirect(req.getContextPath() + "/search.jsp?ok=1");
            } else {
                // 실패 (중복 대출)
                resp.sendRedirect(req.getContextPath() + "/search.jsp?ok=0&reason=dup");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버 오류");
        }
    }

    /** 컨텍스트의 DataSource가 있으면 사용, 없으면 로컬 설정으로 생성 */
    private DataSource resolveDataSource() {
        Object o = getServletContext().getAttribute("ds");
        if (o instanceof DataSource) return (DataSource) o;

        return new SimpleDataSource(
            "jdbc:mysql://192.168.0.42:3306/book_reservation"
          + "?useSSL=false&allowPublicKeyRetrieval=true"
          + "&useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Seoul",
            "test2", "1234"
        );
    }
}