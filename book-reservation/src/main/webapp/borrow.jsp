<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setCharacterEncoding("UTF-8"); %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>도서 대출/반납 테스트</title>
</head>
<body>
  <h1>도서 대출/반납</h1>

  <div id="msg">
    <% 
      String ok = request.getParameter("ok");
      String reason = request.getParameter("reason");
      String ret = request.getParameter("ret");

      if ("1".equals(ok)) {
        out.print("대출이 완료되었습니다.");
      } else if ("0".equals(ok)) {
        if ("dup".equals(reason)) {
          out.print("이미 대출 중인 도서입니다.");
        } else {
          out.print("대출에 실패했습니다.");
        }
      } else if ("1".equals(ret)) {
        out.print("반납이 완료되었습니다.");
      }
    %>
  </div>  

  <h2>도서 대출</h2>
  <form method="post" action="<%= request.getContextPath() %>/borrow" accept-charset="UTF-8">
    <label>회원 ID: <input type="text" name="userId"></label>
    <label>도서 ID: <input type="text" name="bookId"></label>
    <button type="submit">대출하기</button>
  </form>

  <hr>

  <h2>도서 반납</h2>
  <form method="post" action="<%= request.getContextPath() %>/return" accept-charset="UTF-8">
    <label>회원 ID: <input type="text" name="userId"></label>
    <label>대출 ID: <input type="text" name="borrowId"></label>
    <button type="submit">반납하기</button>
  </form>
  
</body>
</html>
