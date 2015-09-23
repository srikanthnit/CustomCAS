package org.cloudfoundry.samples;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SecondServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter pw = response.getWriter();
        response.setContentType("text/html");
        if (request.getSession().getAttribute("userName") != null) {
            pw.println("Login Success...from second servlet");
        } else {
            response.sendRedirect("index.html");
        }

        pw.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter pw = response.getWriter();
        response.setContentType("text/html");
        if (request.getSession().getAttribute("userName") != null) {
            pw.println("Login Success...from second servlet");
        } else {
            response.sendRedirect("login.html");
        }

        pw.close();
    }
}
