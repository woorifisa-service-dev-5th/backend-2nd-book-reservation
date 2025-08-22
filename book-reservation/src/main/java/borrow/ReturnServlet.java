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

        // 필수 파라미터 확인
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
            boolean ok = dao.returnBook(borrowId, userId);
            if (ok) {
                // 성공 → PRG: 완료 메시지
                resp.sendRedirect(req.getContextPath() + "/borrow.jsp?ret=1");
            } else {
                // 대출건 없음 → 사유와 함께 리다이렉트
                resp.sendRedirect(req.getContextPath() + "/borrow.jsp?ret=0&reason=notfound");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버 오류");
        }
    }

    /** 컨텍스트 DS가 있으면 사용, 없으면 간단 DS 생성 */
    private DataSource resolveDataSource() {
        Object o = getServletContext().getAttribute("ds");
        if (o instanceof DataSource) return (DataSource) o;

        return new SimpleDataSource(
            "jdbc:mysql://192.168.0.42:3306/book_reservation"
          + "?useSSL=false&allowPublicKeyRetrieval=true"
          + "&useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Seoul",
            "test1", "1234"
        );
    }
}
