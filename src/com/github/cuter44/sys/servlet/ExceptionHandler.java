package com.github.cuter44.paperfly.sys.servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import com.github.cuter44.util.dao.*;
import com.github.cuter44.util.servlet.*;

/**
  GET/POST/HEAD/TRACE /sys/exception.api
 */
@WebServlet("/sys/exception.api")
public class ExceptionHandler extends HttpServlet
{
    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp)
        throws IOException
    {
        Exception ex = (Exception)req.getAttribute("exception");
        this.getServletContext().log("", ex);

        String error = "!error";
        String msg = (ex!=null ? ex.getMessage() : "('·﹏·`)?");

        if (ex instanceof EntityNotFoundException)
            error = "!notfound";
        if (ex instanceof EntityReferencedException)
            error = "!refered";
        if (ex instanceof EntityDuplicatedException)
            error = "!duplicated";
        if (ex instanceof MissingParameterException)
            error = "!parameter";

        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        resp.setContentType("application/json; charset=utf-8");
        PrintWriter out = resp.getWriter();

        out.println("{\"error\":\""+error+"\",\"msg\":\""+msg+"\"}");

        return;
    }
}
