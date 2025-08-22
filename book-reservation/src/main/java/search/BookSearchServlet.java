package search;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/search")
public class BookSearchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("keyword"); // 검색어
        BookDao dao = new BookDao();
        List<Book> bookList = dao.searchBooks(keyword);

        request.setAttribute("bookList", bookList);

        // 결과를 JSP로 포워딩
        request.getRequestDispatcher("/search.jsp").forward(request, response);
    }
}
