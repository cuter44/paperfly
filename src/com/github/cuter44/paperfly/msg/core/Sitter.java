package com.github.cuter44.paperfly.msg.core;

import java.util.*;

import com.github.cuter44.paperfly.msg.dao.*;

public class Sitter
{
  // INTERFACE
    public static abstract class MsgSink
    {
        public abstract void msgAvailable()
            throws Exception;

        private MsgSink chainPrev;
        private MsgSink chainNext;
    }

  // CONSTRUCT
    private HashMap<Long, MsgSink> route;
    private WeakHashMap<MsgSink, Long> reroute;

    public Sitter()
    {
        this.route = new HashMap<Long, MsgSink>();
        this.reroute = new WeakHashMap<MsgSink, Long>();

        return;
    }

  // SINGLETON
    private static class Singleton
    {
        public static final Sitter instance = new Sitter();
    }

    public static Sitter getInstance()
    {
        return(Singleton.instance);
    }

  //
    /**
     * @return true if dispatched immediately, false if not.
     */
    public boolean reg(Long id, MsgSink ms)
    {
        // else
        do
        {
            MsgSink p = this.route.get(id);
            if (p!=null)
            {
                ms.chainNext = p;
                p.chainPrev = ms;
                this.route.put(id, ms);
            }
            else
            {
                this.route.put(id, ms);
            }
        }
        while (this.route.get(id)!=ms);

        this.reroute.put(ms, id);

        return(false);
    }

    public void unreg(MsgSink ms)
    {
        Long id = this.reroute.get(ms);

        // gone
        if (id == null)
            return;

        // else first one
        if (this.route.get(id) == ms)
        {
            this.route.put(id, ms.chainNext);
            return;
        }

        // else chained
        if (ms.chainNext!=null)
            ms.chainNext.chainPrev = ms.chainPrev;
        if (ms.chainPrev!=null)
            ms.chainPrev.chainNext = ms.chainNext;

        return;
    }

    public void flush(Long id)
    {
        MsgSink ms = this.route.remove(id);

        while (ms!=null)
        {
            try
            {
                ms.msgAvailable();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            ms = ms.chainNext;
        }

        return;
    }
}
