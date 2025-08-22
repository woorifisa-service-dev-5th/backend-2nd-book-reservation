<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="search.Book" %>


<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>도서 검색</title>
    <style>
        table { width:100%; border-collapse:collapse; margin-top:20px; }
        th, td { border:1px solid #999; padding:8px; text-align:left; }
        th { background:#eee; }
        img { max-width:50px; }
        .muted { color:#888; font-size:0.9em; }
        .actions form { display:inline; margin:0; }
        .actions button[disabled] { opacity:.5; cursor:not-allowed; }
    </style>
</head>

<body>
<%
    String ok = request.getParameter("ok");
    String reason = request.getParameter("reason");
    if ("1".equals(ok)) {
%>
    <p style="color:green;">대출이 완료되었습니다.</p>
<%
    } else if ("0".equals(ok)) {
        if ("dup".equals(reason)) {
%>
    <p style="color:red;">이미 대출 중인 도서입니다.</p>
<%
        } else {
%>
    <p style="color:red;">대출에 실패했습니다.</p>
<%
        }
    }
%>


    <h2>도서 검색</h2>

    <%
        // 세션에서 로그인 사용자 ID 추출 (로그인 시 setAttribute("userId", ...) 했다고 가정)
        Integer loginUserId = (Integer) session.getAttribute("userId");
    %>

    <form action="<%= request.getContextPath()%>/search" method="get">
        <select name="criteria" id="criteria">
            <option value="title"  <%= "title".equals(request.getParameter("criteria"))  ? "selected" : "" %>>제목</option>
            <option value="author" <%= "author".equals(request.getParameter("criteria")) ? "selected" : "" %>>저자</option>
            <option value="isbn"   <%= "isbn".equals(request.getParameter("criteria"))   ? "selected" : "" %>>ISBN</option>
        </select>

        <input type="text" name="keyword" value="<%= request.getParameter("keyword") != null ? request.getParameter("keyword") : "" %>" placeholder="검색어 입력">
        <input type="submit" value="검색">
        <a href='MypageServlet'>마이페이지로 이동</a>
    </form>

    <%
        List<Book> books = (List<Book>) request.getAttribute("bookList");
        if (books != null && !books.isEmpty()) {
    %>
        <h3>검색 결과 (<%= books.size() %>건)</h3>

        <% if (loginUserId == null) { %>
            <p class="muted">※ 대출하려면 먼저 로그인하세요.</p>
        <% } %>

        <table>
            <thead>
                <tr>
                    <th>제목</th>
                    <th>분류</th>
                    <th>저자</th>
                    <th>옮긴이</th>
                    <th>출판일</th>
                    <th>ISBN</th>
                    <th>페이지</th>
                    <th>대출 여부</th>
                    <th>대출</th>
                </tr>
            </thead>
            <tbody>
            <%
                for (Book book : books) {
                    boolean borrowed = book.isBorrow(); // true면 대출중
            %>
                <tr>
                    <td><%= book.getTitle() %></td>
                    <td><%= book.getCatName() %></td>
                    <td><%= book.getAuthor() %></td>
                    <td><%= book.getTranslator() %></td>
                    <td><%= book.getPubDate() %></td>
                    <td><%= book.getIsbn() %></td>
                    <td><%= book.getPage() %></td>
                    <td><%= borrowed ? "대출중" : "대출 가능" %></td>
                   <td class="actions">
				  <form action="<%= request.getContextPath() %>/borrow" method="post">
				    <input type="hidden" name="bookId" value="<%= book.getBookId() %>">
				    <input type="hidden" name="userId" value="<%= loginUserId != null ? loginUserId : 0 %>">
				    <button type="submit"
				            <%= (loginUserId == null || borrowed) ? "disabled" : "" %>>
				      대출하기
				    </button>
				  </form>
				</td>
                </tr>
            <%
                }
            %>
            </tbody>
        </table>
    <%
        } else if (request.getParameter("keyword") != null) {
    %>
        <p>검색 결과가 없습니다.</p>
    <%
        }
    %>
</body>
</html>
