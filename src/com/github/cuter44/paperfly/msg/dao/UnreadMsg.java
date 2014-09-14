package com.github.cuter44.paperfly.msg.dao;

import java.io.Serializable;

public class UnreadMsg
    implements Serializable
{
  // SERIALIZABLE
    public static final long serialVersionUID = 1L;

  // FIELDS
    protected Long id;

    protected Msg msg;

  // ACCESSOR
    public Long getId()
    {
        return(this.id);
    }

    public void setId(Long newId)
    {
        this.id = newId;

        return;
    }

    public Msg getMsg()
    {
        return(this.msg);
    }

    public void setMsg(Msg newMsg)
    {
        this.msg = newMsg;

        return;
    }

  // CONSTRUCT
    public UnreadMsg()
    {
        return;
    }

    public UnreadMsg(Msg m)
    {
        this();

        this.setMsg(m);

        return;
    }

  // HASH
    @Override
    public int hashCode()
    {
        int hash = 17;

        if (this.id != null)
            hash = hash * 31 + this.id.hashCode();

        return(hash);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return(true);

        if (o!=null && !this.getClass().equals(o.getClass()))
            return(false);

        UnreadMsg um = (UnreadMsg)o;

        return(
            (this.id == um.id) ||
            (this.id != null && this.id.equals(um.id))
        );
    }


}
