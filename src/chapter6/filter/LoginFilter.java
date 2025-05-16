package chapter6.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter({ "/edit", "/setting" }) // ログインが必要なURLパターンを指定
public class LoginFilter implements Filter {

			//initを実装しないとコンパイルエラーになるからしている
	public void init(FilterConfig fConfig) throws ServletException {

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		// セッションからログインユーザーを取得
		Object loginUser = req.getSession().getAttribute("loginUser");

		if (loginUser != null) {
			chain.doFilter(request, response);
		}else {
			List<String> messages = new ArrayList<>();
			messages.add("ログインしてください");
			req.getSession().setAttribute("errorMessages", messages);
			resp.sendRedirect("login");
		}
	}
	@Override
	public void destroy() {
	}
}
