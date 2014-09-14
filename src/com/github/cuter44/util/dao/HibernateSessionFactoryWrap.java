package com.github.cuter44.util.dao;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class HibernateSessionFactoryWrap
{
  // CONSTRUCT
    public static final String DEFAULTS = "/hibernate.cfg.xml";

    private SessionFactory sf;

    public HibernateSessionFactoryWrap(String resConf)
    {
        Configuration cfg = new Configuration()
            .configure(resConf);
        ServiceRegistry sr = new ServiceRegistryBuilder()
            .applySettings(
                cfg.getProperties()
            ).buildServiceRegistry();
        this.sf = cfg.buildSessionFactory(sr);

        return;
    }

    @Override
    protected void finalize()
    {
        if (this.sf!=null)
            this.sf.close();

        return;
    }

  // SINGLETON
    private static class Singleton
    {
        public static final HibernateSessionFactoryWrap instance =
            new HibernateSessionFactoryWrap(
                HibernateSessionFactoryWrap.DEFAULTS
            );
    }

    public static HibernateSessionFactoryWrap getInstance()
    {
        return(Singleton.instance);
    }

  // SESSION
    public Session openSession()
    {
        return(
            this.sf.openSession()
        );
    }

    private ThreadLocal<Session> threadLocal = new ThreadLocal<Session>()
        {
            @Override
            protected Session initialValue() { return(HibernateSessionFactoryWrap.this.sf.openSession()); }
        };

    /** returns ThreadLocal session
     */
    public ThreadLocal<Session> getThreadLocal()
    {
        return(
            this.threadLocal
        );
    }

}
