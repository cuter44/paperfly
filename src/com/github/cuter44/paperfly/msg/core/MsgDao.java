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

public class MsgDao extends DaoBase<Msg>
{
  // CONSTRUCT
    public MsgDao()
    {
        return;
    }

  // SINGLETON
    private static class Singleton
    {
        public static final MsgDao instance = new MsgDao();
    }

    public static MsgDao getInstance()
    {
        return(Singleton.instance);
    }

  // T
    @Override
    public Class<Msg> classOfT()
    {
        return(Msg.class);
    }

  // BASE
    public Msg create(Long fromId, Long toId, String content)
    {
        Msg m = new Msg(fromId, toId, content);

        this.save(m);

        return(m);
    }

    public void remove(Long id)
    {
        Msg m = this.get(id);

        if (m == null)
            throw(new EntityNotFoundException("No such Msg:"+id));

        this.delete(m);

        return;
    }

  // EX
    public List<Msg> retrieve(Long toUserId, Date since)
    {
        DetachedCriteria dc = DetachedCriteria.forClass(Msg.class)
            .add(Restrictions.gt("m", since))
            .add(Restrictions.eq("t", toUserId));

        return(
            (List<Msg>)this.search(dc)
        );
    }


}
