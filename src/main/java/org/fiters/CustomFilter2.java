package org.fiters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.cas.CASTicketUtil;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas10TicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;

public class CustomFilter2 extends AbstractCasFilter {

    private String casServerUrl  = null;

    private String userNameLabel = null;

    private String passwordLabel = null;

    private String serviceUrl    = null;
    private String serverName    = null;

    protected void initInternal(final FilterConfig filterConfig) throws ServletException {

        super.initInternal(filterConfig);
        casServerUrl = getPropertyFromInitParams(filterConfig, "casServerUrl", null);
        userNameLabel = getPropertyFromInitParams(filterConfig, "userNameLabel", null);
        passwordLabel = getPropertyFromInitParams(filterConfig, "passwordLabel", null);
        serviceUrl = getPropertyFromInitParams(filterConfig, "serviceUrl", null);
        serverName = getPropertyFromInitParams(filterConfig, "serverName", null);
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpSession session = ((HttpServletRequest) request).getSession();
        Assertion assertion = session != null ? (Assertion) session.getAttribute(CONST_CAS_ASSERTION) : null;
        if (assertion != null) {

            String user = assertion.getPrincipal().getName();
            session.setAttribute("userName", user);
            request.getRequestDispatcher("/login").forward(request, response);
            return;

        } else {

            String user = request.getParameter(userNameLabel);
            String pass = request.getParameter(passwordLabel);
            if ((user != null || pass != null)) {

                HttpServletRequest httpRequest = ((HttpServletRequest) request);
                String ticket = null;
                try {

                    ticket = CASTicketUtil.getServiceTicket(serviceUrl, user, pass, casServerUrl);
                } catch (IOException e) {
                    request.getRequestDispatcher("index.html").forward(request, response);
                    return;
                }

                Cas10TicketValidator ticketValidator = new Cas10TicketValidator(casServerUrl);

                Map<String, String> customParameters = new HashMap<String, String>();
                customParameters.put("serverName", serverName);
                customParameters.put("casServerUrlPrefix", casServerUrl);
                customParameters.put("useSession", "true");
                ticketValidator.setCustomParameters(customParameters);

                try {
                    assertion = ticketValidator.validate(ticket, constructServiceUrl(((HttpServletRequest) request), ((HttpServletResponse) response)));
                    if (log.isDebugEnabled()) {
                        log.debug("Successfully authenticated user:  " + assertion.getPrincipal().getName());
                    }

                    httpRequest.setAttribute(CONST_CAS_ASSERTION, assertion);
                    httpRequest.getSession().setAttribute(CONST_CAS_ASSERTION, assertion);
                } catch (TicketValidationException e) {
                    e.printStackTrace();

                }

            }
        }
        chain.doFilter(request, response);
    }

}
