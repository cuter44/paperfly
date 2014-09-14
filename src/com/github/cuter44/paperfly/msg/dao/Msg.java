package com.github.cuter44.paperfly.msg.dao;

import java.io.Serializable;
import java.util.Date;

public class Msg
    implements Serializable
{
  // SERIALIZABLE
    public static long serialVersionUID = 1L;

  // FIELDS
    protected Long id;

    /** from
     */
    protected Long f;
    /** to
     */
    protected Long t;

    /** timestamp
     */
    protected Date m;
    /** content
     */
    protected String c;

  // GETTER/SETTER
    public Long getId()
    {
        return(this.id);
    }
    public void setId(Long aId)
    {
        this.id = aId;

        return;
    }

    public Long getF()
    {
        return(this.f);
    }
    public void setF(Long aF)
    {
        this.f = aF;
        return;
    }

    public Long getT()
    {
        return(this.t);
    }
    public void setT(Long aT)
    {
        this.t = aT;
        return;
    }

    public Date getM()
    {
        return(this.m);
    }
    public void setM(Date aM)
    {
        this.m = aM;
        return;
    }

    public String getC()
    {
        return(this.c);
    }
    public void setC(String aC)
    {
        this.c = aC;
        return;
    }

  // CONSTRUCT
    public Msg()
    {
        this.m = new Date(System.currentTimeMillis());

        return;
    }

    public Msg(Long from, Long to, String content)
    {
        this();

        this.setF(from);
        this.setT(to);
        this.setC(content);

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

        Msg msg = (Msg)o;

        return(
            (this.id == msg.id) ||
            (this.id != null && this.id.equals(msg.id))
        );
    }

}
