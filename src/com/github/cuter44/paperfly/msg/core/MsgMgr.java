package com.github.cuter44.paperfly.msg.core;

import java.util.List;
import java.util.Iterator;
import java.util.Date;

import org.hibernate.criterion.*;
import com.github.cuter44.util.dao.*;

import com.github.cuter44.paperfly.msg.dao.*;
import com.github.cuter44.paperfly.Constants;

public class MsgMgr
{
  // CONSTRUCT
    protected MsgDao msgDao;
    protected UnreadMsgDao umDao;
    protected Sitter sitter;

    public MsgMgr()
    {
        this.msgDao = MsgDao.getInstance();
        this.umDao = UnreadMsgDao.getInstance();
        this.sitter = Sitter.getInstance();

        return;
    }

  // SINGLETON
    private static class Singleton
    {
        public static final MsgMgr instance = new MsgMgr();
    }

    public static MsgMgr getInstance()
    {
        return(Singleton.instance);
    }

  // MSG
    public void save(Long f, Long t, String c)
    {
        Msg         msg = this.msgDao.create(f, t, c);
        UnreadMsg   um  = this.umDao.create(msg);

        return;
    }

    public List<Msg> retrieve(Long toUserId, Date since)
    {
        return(
            this.msgDao.retrieve(toUserId, since)
        );
    }

    public List<UnreadMsg> retrieveUnread(Long toUserId, Date since)
    {
        return(
            this.umDao.retrieve(toUserId, since)
        );
    }

    public void acknowledge(Long toUserId, Date due)
    {
        //List ids = this.umDao.createQuery("SELECT um_0.msg.id FROM UnreadMsg um_0 WHERE um_0.msg.t=:tid AND um_0.msg.m<=:due")
            //.setLong("tid", toUserId)
            //.setDate("due", due)
            //.list();
        DetachedCriteria dc = DetachedCriteria.forClass(UnreadMsg.class)
            .setProjection(Projections.property("id"))
            .createCriteria("msg")
                .add(Restrictions.eq("t", toUserId))
                .add(Restrictions.le("m", due));
        List<Long> ids = this.umDao.search(dc);

        if (ids.size() == 0)
            return;

        // else
        // {
        this.umDao.createQuery("DELETE FROM UnreadMsg um_0 WHERE um_0.id IN :ids")
            .setParameterList("ids", ids)
            .executeUpdate();

        if (Constants.CONF_CLEAR_AFTER_READ)
            this.msgDao.createQuery("DELETE FROM Msg m_0 WHERE m_0.id IN :ids")
                .setParameterList("ids", ids)
                .executeUpdate();

        // }
        return;
    }

  // MSG
    public Long countUnread(Long toUserId, Long fromUserId)
    {
        return(
            this.umDao.countUnread(toUserId, fromUserId)
        );
    }

}
