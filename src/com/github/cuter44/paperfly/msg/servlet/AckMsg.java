package com.github.cuter44.paperfly.msg.servlet;

import java.util.Arrays;
import java.util.Date;
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

/** 标记消息为已接收.
 * 如果 com.github.cuter44.paperfly.Constants.CONF_CLEAR_AFTER_READ 为 TRUE, 则同时删除被标记的消息
 * <pre style="font-size:12px">

   <strong>请求</strong>
   POST /msg/ack.api

   <strong>参数</strong>
   uid  :long   , 必需, 对方的uid
   due  :long   , 必需, 时间戳, 清除到此为止的消息

   <strong>响应</strong>
   application/json
   {"error":"no-error"}

   <strong>样例</strong>暂无
 * </pre>
 *
 */
@WebServlet("/msg/ack.api")
public class AckMsg extends HttpServlet
{
    private static final String T = "uid";
    private static final String DUE = "due";

    protected Sitter sitter = Sitter.getInstance();
    protected MsgDao msgDao = MsgDao.getInstance();
    protected MsgMgr msgMgr = MsgMgr.getInstance();

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
            Long    t   = (Long)            notNull(getLongParam(req, T));
            Date    due = new Date((Long)   notNull(getLongParam(req, DUE)));

            this.msgDao.begin();

            this.msgMgr.acknowledge(t, due);

            this.msgDao.commit();

            J.writeNoError(resp);
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
