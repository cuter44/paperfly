package com.github.cuter44.paperfly.msg.core;

import java.util.List;
import java.util.Iterator;
import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Order;
import com.github.cuter44.util.dao.*;

import com.github.cuter44.paperfly.msg.dao.*;

public class UnreadMsgDao extends DaoBase<UnreadMsg>
{
  // CONSTRUCT
    public UnreadMsgDao()
    {
        return;
    }

  // SINGLETON
    private static class Singleton
    {
        public static final UnreadMsgDao instance = new UnreadMsgDao();
    }

    public static UnreadMsgDao getInstance()
    {
        return(Singleton.instance);
    }

  // T
    @Override
    public Class classOfT()
    {
        return(UnreadMsg.class);
    }

  // BASE
    public UnreadMsg create(Msg m)
    {
        UnreadMsg um = new UnreadMsg(m);

        this.save(um);

        return(um);
    }

    public void remove(Long id)
    {
        UnreadMsg um = this.get(id);

        if (um == null)
            throw(new EntityNotFoundException("No such UnreadMsg:"+id));

        this.delete(um);

        return;
    }

  // EX
    public List<UnreadMsg> retrieve(Long toUserId, Date since)
    {
        DetachedCriteria dc = DetachedCriteria.forClass(UnreadMsg.class)
            .createCriteria("msg")
                .add(Restrictions.eq("t", toUserId))
                .add(Restrictions.gt("m", since));

        return(
            (List<UnreadMsg>)this.search(dc)
        );
    }

    public Long countUnread(Long toUserId, Long fromUserId)
    {
        DetachedCriteria dc = DetachedCriteria.forClass(UnreadMsg.class)
            .createAlias("msg", "msg")
            .add(Restrictions.eq("msg.t", toUserId));

        if (fromUserId != null)
            dc.add(Restrictions.eq("msg.f", fromUserId));

        return(
            this.count(dc)
        );
    }

}
