package filter;


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

//Annotation
@WebFilter(
			value= {"/*"},
			initParams=@WebInitParam(name="encoding", value="utf-8")
		    )
public class EncodingFilter implements Filter{

	@Override
	public void destroy() {	}
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		//한글처리...
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		//사전작업(한글처리)의 내용이 서버상의 다음번 컴포넌트에게 계속 적용되기 위한 작업...
		chain.doFilter(request, response);
	}
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}
}