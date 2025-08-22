<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <title>도서 대출/반납 테스트</title>
  <style>
    body { font-family: system-ui, sans-serif; }
    .msg { margin: 12px 0; font-weight: 700; }
    .msg.ok { color: #0a7; }
    .msg.err { color: #c00; }
    form { margin: 12px 0; }
    label { display:block; margin: 6px 0; }
  </style>
</head>
<body>
  <h1>📚 도서 대출/반납</h1>

  <!-- ✅ 쿼리 파라미터 ok=1 이면 성공 메시지 출력 -->
  <c:choose>
    <c:when test="${param.ok == '1'}">
      <div id="msg" class="msg ok">✅ 대출이 완료되었습니다!</div>
      <script>
        // 주소창에서 ?ok=1 깔끔히 제거 (선택)
        history.replaceState(null, '', location.pathname);
      </script>
    </c:when>
    <c:otherwise>
      <div id="msg" class="msg"></div>
    </c:otherwise>
  </c:choose>

  <h2>도서 대출</h2>
  <form method="post" action="${pageContext.request.contextPath}/borrow" accept-charset="UTF-8">
    <label>회원 ID: <input type="text" name="userId" value="1" /></label>
    <label>도서 ID: <input type="text" name="bookId" value="122" /></label>
    <button type="submit">대출하기</button>
  </form>

  <hr />

  <h2>도서 반납</h2>
  <form method="post" action="${pageContext.request.contextPath}/return" accept-charset="UTF-8">
    <label>회원 ID: <input type="text" name="userId" value="1" /></label>
    <label>대출 ID: <input type="text" name="borrowId" value="555" /></label>
    <button type="submit">반납하기</button>
  </form>
</body>
</html>
