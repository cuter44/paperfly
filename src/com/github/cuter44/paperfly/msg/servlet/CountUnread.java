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

/** 获得未读消息数.
 * 该接口不支持长连接.
 * 该方法计算未被 ack 的消息数目, aka. 通过 retrieve-unread.api
 * 取得但未通过 ack.api 承认收妥的消息会被计算在内.
 * <pre style="font-size:12px">

   <strong>请求</strong>
   POST /msg/count-unread.api

   <strong>参数</strong>
   uid  :long   , 必需, 自己的uid
   f    :long   , 发件人的uid, 缺省则不区分发件人.

   <strong>响应</strong>
   application/json
   {"count":%d}

   <strong>样例</strong>暂无
 * </pre>
 *
 */
@WebServlet("/msg/count-unread.api")
public class CountUnread extends HttpServlet
{
    private static final String F = "f";
    private static final String T = "uid";

    protected UnreadMsgDao umDao = UnreadMsgDao.getInstance();
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
            Long    t = (Long)  notNull(getLongParam(req, T));
            Long    f =         getLongParam(req, F);

            this.umDao.begin();

            Long count = this.msgMgr.countUnread(t, f);

            this.umDao.commit();

            resp.setContentType("application/json; charset=utf-8");
            resp.setCharacterEncoding("utf-8");
            resp.getWriter().println("{\"count\":"+count+"}");
        }
        catch (Exception ex)
        {
            req.setAttribute(Constants.KEY_EXCEPTION, ex);
            req.getRequestDispatcher(Constants.URI_ERROR_HANDLER).forward(req, resp);
        }
        finally
        {
            this.umDao.close();
        }

        return;
    }
}
