package com.agh.EventarzApplication;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@Component
//TODO: Remove. This is unnecessary, but I'm leaving it for now for reference
public class StickySessionCookieAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler
        implements
        AuthenticationSuccessHandler {

    private final String cookiePrefix = "Sticky-Session-Id";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        Cookie cookie = new Cookie(cookiePrefix, "TEST");
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}