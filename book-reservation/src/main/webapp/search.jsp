<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="search.Book" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>도서 검색</title>
    <style>
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            border: 1px solid #999;
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #eee;
        }
        img {
            max-width: 50px;
        }
    </style>
</head>
<body>
    <h2>도서 검색</h2>

    <form action="/search" method="get">
        <select name="criteria" id="criteria">
            <option value="title" <%= "title".equals(request.getParameter("criteria")) ? "selected" : "" %>>제목</option>
            <option value="author" <%= "author".equals(request.getParameter("criteria")) ? "selected" : "" %>>저자</option>
            <option value="isbn" <%= "isbn".equals(request.getParameter("criteria")) ? "selected" : "" %>>ISBN</option>
        </select>

        <input type="text" name="keyword" value="<%= request.getParameter("keyword") != null ? request.getParameter("keyword") : "" %>" placeholder="검색어 입력">
        <input type="submit" value="검색">
    </form>

    <%
        List<Book> books = (List<Book>) request.getAttribute("books");
        if (books != null && !books.isEmpty()) {
    %>
        <h3>검색 결과 (<%= books.size() %>건)</h3>
        <table>
            <thead>
                <tr>
                    <th>책ID</th>
                    <th>제목</th>
                    <th>저자</th>
                    <th>옮긴이</th>
                    <th>출판일</th>
                    <th>ISBN</th>
                    <th>페이지</th>
                    <th>분류</th>
                    <th>대출 여부</th>
                    <th>이미지</th>
                </tr>
            </thead>
            <tbody>
            <%
                for (Book book : books) {
            %>
                <tr>
                    <td><%= book.getBookId() %></td>
                    <td><%= book.getTitle() %></td>
                    <td><%= book.getAuthor() %></td>
                    <td><%= book.getTranslator() %></td>
                    <td><%= book.getPubDate() %></td>
                    <td><%= book.getIsbn() %></td>
                    <td><%= book.getPage() %></td>
                    <td><%= book.isBorrow() ? "대출중" : "대출 가능" %></td>
                    <td>
                        <% if(book.getImage() != null && !book.getImage().isEmpty()) { %>
                            <img src="<%= book.getImage() %>" alt="책 이미지">
                        <% } %>
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
