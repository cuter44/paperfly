package com.github.cuter44.paperfly.msg.servlet;

import java.io.*;
import java.util.List;
import java.util.Date;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import com.github.cuter44.util.dao.*;
import static com.github.cuter44.util.servlet.HttpUtil.notNull;
import static com.github.cuter44.util.servlet.HttpUtil.getLongParam;
import static com.github.cuter44.util.servlet.HttpUtil.getIntParam;

import com.github.cuter44.paperfly.Constants;
import com.github.cuter44.paperfly.msg.dao.*;
import com.github.cuter44.paperfly.msg.core.*;

/** 检出未读消息.
 * <pre style="font-size:12px">

   <strong>请求</strong>
   POST /msg/retrieve-unread.api

   <strong>参数</strong>
   uid      :long               , 必需, 表示收件人id
   since    :unix-time-millis   , 必需, 表示截取从该时间后收到的消息, 直到被ack之前消息可以被重复获取
   wait     :time-in-second     , 表示在没有消息可用时要挂起的时间, 受服务器设置限制存在上限. 为0则不等待, 无消息时直接返回, 缺省为0.

   <strong>响应</strong>
   application/json array class=msg.dao.Msg

   <strong>样例</strong>

 * </pre>
 * @see J#write
 *
 */
@WebServlet(
    value="/msg/retrieve-unread.api",
    asyncSupported=true
)
public class RetrieveUnreadMsg extends HttpServlet
{
  // HELPER
    private class ServletMsgSink extends Sitter.MsgSink
    {
      // CONSTRUCT
        private AsyncContext ctx;

        public ServletMsgSink(AsyncContext aCtx)
        {
            this.ctx = aCtx;

            return;
        }

        @Override
        public void msgAvailable()
            throws Exception
        {
            ServletRequest req = ctx.getRequest();
            ServletResponse resp = ctx.getResponse();

            try
            {
                Long t      = (Long)req.getAttribute(RetrieveUnreadMsg.T);
                Date since  = (Date)req.getAttribute(RetrieveUnreadMsg.SINCE);

                RetrieveUnreadMsg.this.umDao.begin();

                List<UnreadMsg> l = RetrieveUnreadMsg.this.msgMgr.retrieveUnread(t, since);

                J.writeUnreadMsg(l, resp);

                ctx.complete();

                RetrieveUnreadMsg.this.umDao.commit();
            }
            catch (Exception ex)
            {
                req.setAttribute(Constants.KEY_EXCEPTION, ex);
                req.getRequestDispatcher(Constants.URI_ERROR_HANDLER).forward(req, resp);
            }
            finally
            {
                RetrieveUnreadMsg.this.umDao.close();
            }
       }
    }

    private class TimeoutHandler
        implements AsyncListener
    {
        private ServletMsgSink ssm;

        public TimeoutHandler(ServletMsgSink aSsm)
        {
            this.ssm = aSsm;

            return;
        }

        @Override
        public void onComplete(AsyncEvent ev) { return; }
        @Override
        public void onError(AsyncEvent ev) { return; }
        @Override
        public void onStartAsync(AsyncEvent ev) { return; }

        @Override
        public void onTimeout(AsyncEvent ev)
        {
            try
            {
                RetrieveUnreadMsg.this.sitter.unreg(this.ssm);

                ServletResponse resp = ev.getSuppliedResponse();
                J.writeEmptyArray(resp);

                ev.getAsyncContext().complete();

                return;
            }
            catch (IOException ex)
            {
                throw(new RuntimeException(ex));
            }
        }

    };

  // SERVLET
    private static final String T = "uid";
    private static final String WAIT = "wait";
    private static final String SINCE = "since";

    private Sitter sitter = Sitter.getInstance();
    private UnreadMsgDao umDao = UnreadMsgDao.getInstance();
    private MsgMgr msgMgr = MsgMgr.getInstance();

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
            Long    t       = (Long)    notNull(getLongParam(req, T));
            Date    since   =           new Date(
                                            (Long)notNull(getLongParam(req, SINCE))
                                        );
            Integer wait    =           getIntParam(req, WAIT);

            wait = wait==null ? 0
                 : wait<=Constants.CONF_RETRIEVE_MAX_WAIT ? wait
                 : Constants.CONF_RETRIEVE_MAX_WAIT;

            this.umDao.begin();

            List<UnreadMsg> l = this.msgMgr.retrieveUnread(t, since);

            this.umDao.commit();

            if (l.size() != 0)
            {
                J.writeUnreadMsg(l, resp);
                return;
            }

            // else
            if (wait==0)
            {
                J.writeEmptyArray(resp);
                return;
            }

            // else
            req.setAttribute(T, t);
            req.setAttribute(SINCE, since);

            AsyncContext ctx = req.startAsync();
            ctx.setTimeout(wait*1000L);

            ServletMsgSink ssm = new ServletMsgSink(ctx);
            this.sitter.reg(t, ssm);

            ctx.addListener(new TimeoutHandler(ssm));
            ctx.start(
                new Runnable()
                {
                    @Override
                    public void run() { return; }
                }
            );


            return;
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
