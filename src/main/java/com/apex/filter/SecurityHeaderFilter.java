package com.apex.filter;

import com.apex.util.CsrfUtil;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Global Security and Encoding Filter.
 * - Enforces UTF-8 character encoding
 * - Sets standard OWASP security headers
 * - Exposes active CSRF token to JSP requests
 */
@WebFilter(urlPatterns = "/*")
public class SecurityHeaderFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");

        // Security Headers
        res.setHeader("X-Content-Type-Options", "nosniff");
        res.setHeader("X-Frame-Options", "SAMEORIGIN");
        res.setHeader("X-XSS-Protection", "1; mode=block");

        // Set CSRF token in request scope for JSP forms
        String csrfToken = CsrfUtil.getToken(req.getSession(true));
        req.setAttribute("csrf_token", csrfToken);

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
