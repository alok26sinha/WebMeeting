package servlet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import security.FormAuthenticationInterceptor;

import common.Config;

@Service("cookieManager")
public class CookieManager {

	public String getUserTokenFromCookie(HttpServletRequest request) {

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				if (FormAuthenticationInterceptor.USER_TOKEN.equals(cookie
						.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	public void setUserTokenInCookie(String userToken, boolean permanent,
			HttpServletRequest request, HttpServletResponse response) {

		int maxAge;
		if (permanent)
			maxAge = FormAuthenticationInterceptor.FIVE_YEARS;
		else
			// This special value signals browser to delete cookie when browser
			// closes
			maxAge = -1;

		createCookie(FormAuthenticationInterceptor.USER_TOKEN, userToken,
				maxAge, getContextPath(request), response);
	}

	public void deleteUserTokenCookie(HttpServletRequest request,
			HttpServletResponse response) {
		// This signals browser to delete cookie immediately
		int maxAge = 0;

		createCookie(FormAuthenticationInterceptor.USER_TOKEN, "", maxAge,
				getContextPath(request), response);
		// Also delete the cookie on /
		createCookie(FormAuthenticationInterceptor.USER_TOKEN, "", maxAge, "/",
				response);
	}

	private void createCookie(String name, String value, int maxAge,
			String path, HttpServletResponse response) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath(path);
		cookie.setMaxAge(maxAge);
		cookie.setHttpOnly(true);
		if (Config.getInstance().isProductionEnvironment())
			cookie.setSecure(true);
		response.addCookie(cookie);
	}

	private String getContextPath(HttpServletRequest request) {
		return request.getContextPath();
	}

}
