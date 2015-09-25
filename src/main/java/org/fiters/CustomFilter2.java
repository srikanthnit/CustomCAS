package org.fiters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cas.CASTicketUtil;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas10TicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;
import org.springframework.util.Assert;

public class CustomFilter2 extends AbstractCasFilter {

    private String              casServerUrl  = null;

    private String              userNameLabel = null;

    private String              passwordLabel = null;

    private String              serviceUrl    = null;
    private String              serverName    = null;
    private static final String cookieName    = "CASTGC";
    private static final String cookiePath    = "/cas-web/";

    protected void initInternal(final FilterConfig filterConfig) throws ServletException {

        super.initInternal(filterConfig);
        casServerUrl = getPropertyFromInitParams(filterConfig, "casServerUrl", null);
        userNameLabel = getPropertyFromInitParams(filterConfig, "userNameLabel", null);
        passwordLabel = getPropertyFromInitParams(filterConfig, "passwordLabel", null);
        serviceUrl = getPropertyFromInitParams(filterConfig, "serviceUrl", null);
        serverName = getPropertyFromInitParams(filterConfig, "serverName", null);
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = ((HttpServletRequest) request);
        HttpServletResponse httpResponse = ((HttpServletResponse) response);
        String TGT = null;
        if (getCookie(httpRequest, cookieName) != null) {

            TGT = getCookie(httpRequest, cookieName);
            authenticateTicket(httpRequest, httpResponse, null, null, TGT);

        } else {

            String user = request.getParameter(userNameLabel);
            String pass = request.getParameter(passwordLabel);
            if ((user != null || pass != null)) {

                authenticateTicket(httpRequest, httpResponse, user, pass, null);

            } else {
                request.getRequestDispatcher("index.html").forward(request, response);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * @param request
     * @param response
     * @param httpRequest
     * @param httpResponse
     * @param user
     * @param pass
     * @throws ServletException
     * @throws IOException
     */
    private void authenticateTicket(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String user, String pass, String tgt) throws ServletException, IOException {
        Assertion assertion;
        String TGT;
        String ticket = null;
        try {
            if (tgt == null) {

                TGT = CASTicketUtil.getTicketGrantingTicket(user, pass, casServerUrl);
            } else {
                TGT = tgt;
            }
            ticket = CASTicketUtil.getServiceGrantingTicket(TGT, serviceUrl, casServerUrl);

        } catch (IOException e) {
            httpRequest.getRequestDispatcher("index.html").forward(httpRequest, httpResponse);
            return;
        }

        Cas10TicketValidator ticketValidator = new Cas10TicketValidator(casServerUrl);

        Map<String, String> customParameters = new HashMap<String, String>();
        customParameters.put("serverName", serverName);
        customParameters.put("casServerUrlPrefix", casServerUrl);

        ticketValidator.setCustomParameters(customParameters);

        try {
            assertion = ticketValidator.validate(ticket, constructServiceUrl(httpRequest, httpResponse));
            if (log.isDebugEnabled()) {
                log.debug("Successfully authenticated user:  " + assertion.getPrincipal().getName());
            }

            httpRequest.getSession().setAttribute("userName", assertion.getPrincipal().getName());
            httpResponse.addCookie(createCookie(httpRequest.getServerName(), TGT));
        } catch (TicketValidationException e) {
            e.printStackTrace();

        }
    }

    private Cookie createCookie(String domain, String value) {
        Cookie cookie = new Cookie(cookieName, value);
        // cookie.setDomain(domain);

        // cookie.setPath(cookiePath);
        // cookie.setSecure(true);
        // limiting the session to 30 minutes
        cookie.setMaxAge(30 * 60);

        return cookie;
    }

    public String getCookie(HttpServletRequest request, String name) {
        Assert.notNull(request, "Request must  not be null");
        Cookie cookies[] = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (name.equals(cookies[i].getName())) {
                    return cookies[i] == null ? null : cookies[i].getValue();
                }
            }
        }
        return null;
    }

}
