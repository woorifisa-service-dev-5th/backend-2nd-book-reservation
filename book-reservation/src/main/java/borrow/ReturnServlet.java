package borrow;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

@WebServlet("/return")
public class ReturnServlet extends HttpServlet {
    private BorrowDao dao;

    @Override
    public void init() {
        dao = new BorrowDao(resolveDataSource());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        final String userIdStr   = req.getParameter("userId");
        final String borrowIdStr = req.getParameter("borrowId");
        if (userIdStr == null || borrowIdStr == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "필수 파라미터 누락");
            return;
        }

        final int userId, borrowId;
        try {
            userId   = Integer.parseInt(userIdStr);
            borrowId = Integer.parseInt(borrowIdStr);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "숫자 형식 오류");
            return;
        }

        try {
            boolean ok = dao.returnBook(borrowId, userId); // 본인 대출건 반납
            if (ok) {
                resp.sendRedirect(req.getContextPath() + "/MypageServlet?ret=1");
            } else {
                resp.sendRedirect(req.getContextPath() + "/MypageServlet?ret=0&reason=notfound");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버 오류");
        }
    }

    private DataSource resolveDataSource() {
        Object o = getServletContext().getAttribute("ds");
        if (o instanceof DataSource) return (DataSource) o;

        return new SimpleDataSource(
            "jdbc:mysql://192.168.0.42:3306/book_reservation"
          + "?useSSL=false&allowPublicKeyRetrieval=true"
          + "&useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Seoul",
            "test2", "1234" // BorrowServlet과 동일한 계정/DS 사용 권장
        );
    }
}
