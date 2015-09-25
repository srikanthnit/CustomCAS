package org.fiters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomLogoutFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        Cookie cookie = new Cookie("CASTGC", "");
        cookie.setMaxAge(0);
        ((HttpServletResponse) response).addCookie(cookie);
        ((HttpServletRequest) request).getSession().invalidate();
        chain.doFilter(request, response);
    }

    public void destroy() {
    }

}
