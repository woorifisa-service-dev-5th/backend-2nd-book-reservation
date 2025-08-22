package util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionUtils {

	public static int getUserId(HttpServletRequest request) {
	    HttpSession session = request.getSession(false);
	    if (session != null && session.getAttribute("userId") != null) {
	        return (Integer) session.getAttribute("userId");
	    }
	    return -1; // 로그인 안 된 상태
	}
	
	// 로그인한 사용자 이름 가져오기
    public static String getUserName(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userName") != null) {
            return (String) session.getAttribute("userName");
        }
        return null; // 로그인 안 된 상태
    }

    // 로그인한 사용자 이메일 가져오기
    public static String getUserEmail(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userEmail") != null) {
            return (String) session.getAttribute("userEmail");
        }
        return null; // 로그인 안 된 상태
    }
	
}
