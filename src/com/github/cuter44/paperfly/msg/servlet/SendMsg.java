package com.github.cuter44.paperfly.msg.servlet;

import java.util.Arrays;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import com.github.cuter44.util.dao.*;
import static com.github.cuter44.util.servlet.HttpUtil.notNull;
import static com.github.cuter44.util.servlet.HttpUtil.getLongParam;
import static com.github.cuter44.util.servlet.HttpUtil.getParam;
import com.alibaba.fastjson.*;

import com.github.cuter44.paperfly.Constants;
import com.github.cuter44.paperfly.msg.dao.*;
import com.github.cuter44.paperfly.msg.core.*;

/** 发送消息
 * <pre style="font-size:12px">

   <strong>请求</strong>
   POST /msg/send.api

   <strong>参数</strong>
   t    :long   , 必需, 对方的uid
   c    :string , 必需, 消息内容

   <strong>响应</strong>
   application/json
   {"error":"no-error"}

   <strong>样例</strong>暂无
 * </pre>
 *
 */
@WebServlet("/msg/send.api")
public class SendMsg extends HttpServlet
{
    private static final String F = "uid";
    private static final String T = "t";
    private static final String C = "c";

    protected MsgDao msgDao = MsgDao.getInstance();
    protected MsgMgr msgMgr = MsgMgr.getInstance();
    protected Sitter sitter = Sitter.getInstance();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        doPost(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {

        req.setCharacterEncoding("utf-8");

        try
        {
            Long    f = (Long)  notNull(getLongParam(req, F));
            Long    t = (Long)  notNull(getLongParam(req, T));
            String  c = (String)notNull(getParam(req, C));

            this.msgDao.begin();

            this.msgMgr.save(f, t, c);

            this.msgDao.commit();

            J.writeNoError(resp);

            this.sitter.flush(t);
        }
        catch (Exception ex)
        {
            req.setAttribute(Constants.KEY_EXCEPTION, ex);
            req.getRequestDispatcher(Constants.URI_ERROR_HANDLER).forward(req, resp);
        }
        finally
        {
            this.msgDao.close();
        }

        return;
    }
}
