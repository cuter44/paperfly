package com.github.cuter44.util.dao;

/* base */
import java.io.Serializable;
import java.util.List;
/* hibernate */
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
/* log */
import org.apache.log4j.Logger;

/**
 * DAO 层的 Hibernate 实现
 * <br />
 * 该工具类封装了线程安全的数据库连接及读写操作. 同一个连接进行的读写都在
 * 同一个数据库连接内进行(以保持数据的一致性), 除非上层代码故意不那么做.
 * <br />
 * 这里提供一个读写例程
 * <br />
 <code>
    // 要进行读写操作需要先创建一个事务
    // 注意重复创建或关闭事务会导致不可预料的结果
    HiberDao.begin();

    // 然后就可以进行增删查改等业务
    User u = (User)HiberDao.get(User.class, 1);
    // 业务代码
    u.setName("foobar");
    // 写回
    HiberDao.saveOrUpdate(u);

    // 提交(commit)或回滚(rollback)事务
    HiberDao.commit();
    // 然后可以进行另外一个事务
    HiberDao.begin();
    u = HiberDao.get(User.class, 2);
    // ...或者关闭连接
    HiberDao.close();
 </code>
 * 就是这么简单...
 * <br />
 * 切记要提交并且关闭会话, 否则出现内存溢出, 读写失败什么的概不负责.
 * @version 1.2.3 build 20140110
 */
public class HiberDao
{
    private static Logger logger = Logger.getLogger(HiberDao.class);

  // SESSION MANAGEMENT
    private SessionFactory sf = null;

    private static ThreadLocal<Session> threadSession = new ThreadLocal<Session>()
    {
        protected Session initialValue()
        {
            return(Singleton.instance.newSession());
        }
    };

    // 据传这是出自 Google 工程师之手的 Singleton 写法...
    private static class Singleton
    {
        private static HiberDao instance = new HiberDao();
    }

    private HiberDao()
    {
        try
        {
            // 为了适配 hibernate 4 的写法...
            Configuration cfg = new Configuration()
                .configure();
            ServiceRegistry sr = new ServiceRegistryBuilder()
                .applySettings(
                    cfg.getProperties()
                ).buildServiceRegistry();
            this.sf = cfg.buildSessionFactory(sr);

            // hibernate 3 及以下
            //this.sf = new Configuration()
                //.configure()
                //.buildSessionFactory();

            return;
        }
        catch (Exception ex)
        {
            logger.error("HiberDao init failed", ex);
        }

    }

    /**
     * 生成一个新的Session
     * <br />
     * 允许外部代码调用该方法自行生成新的会话, 通过该方法生成的会话不会被
     * 绑定到线程也不会被 HiberDao 管理, 调用者需要自行负责操作线程和会话.
     */
    public static Session newSession()
    {
        return(Singleton.instance.sf.openSession());
    }

    /** 返回当前线程使用的Session
     * @return 当前线程的 Session 或者 null (如果还没有启始线程)
     */
    public static Session getCurrentThreadSession()
    {
        return(threadSession.get());
    }

  // TRANSACTION
    /**
     * 对应方法的非线程绑定封装, 下同.
     * @exception HibernateException 当 s 已经关闭时.
     */
    public static Session begin(Session s)
    {
        if (!s.getTransaction().isActive())
            s.beginTransaction();

        return(s);
    }

    /**
     * 封装数据库事务的 begin 操作
     * <br />
     * 使用线程唯一的数据库连接, 如果当前线程未创建连接或连接已关闭则自动创建一个新的.
     * <br />
     * 如果当前已存在session但未处于事务中, 将开启一个新事务, 如果已经处于事务中, 则重用已经存在的事务.
     * e.g. 允许重复调用begin(), 但实际的事务界限从第一次begin()开始计算,
     */
    public static Session begin()
    {
        if (!threadSession.get().isOpen())
            threadSession.set(newSession());

        return(begin(threadSession.get()));
    }

    public static Session flush(Session s)
    {
        s.flush();

        return(s);
    }

    /** 封装 flush 操作
     */
    public static Session flush()
    {
        return(
            flush(threadSession.get())
        );
    }

    public static Session commit(Session s)
    {
        s.getTransaction()
            .commit();

        return(s);
    }
    /**
     * 封装数据库事务的 commit 操作
     */
    public static Session commit()
    {
        return(
            commit(threadSession.get())
        );
    }

    public static Session rollback(Session s)
    {
        s.getTransaction()
            .rollback();

        return(s);
    }
    /**
     * 封装数据库事务的 rollback 操作
     */
    public static Session rollback()
    {
        return(
            rollback(threadSession.get())
        );
    }

    /**
     * 支持重复关闭
     */
    public static void close(Session s)
    {
        if (s.isOpen())
            s.close();

        return;
    }
    /**
     * 封装数据库连接的 close 操作
     * 支持重复关闭
     */
    public static void close()
    {
        close(threadSession.get());
        threadSession.remove();

        return;
    }

  // DAO
    public static Object get(Session s, Class c, Serializable id)
    {
        return(
            s.get(c, id)
        );
    }
    /** 按主键查询
     * @return Object 主键对应对象, 没有则返回 null
     */
    public static Object get(Class c, Serializable id)
    {
        return(
            get(threadSession.get(), c, id)
        );
    }

    public static Object get(Session s, DetachedCriteria dc)
    {
        Object o = null;

        try
        {
            o = dc.getExecutableCriteria(s)
                .uniqueResult();
        }
        catch (HibernateException ex)
        {
            ex.printStackTrace();
            Logger.getLogger("librarica.dao")
                .error(ex.toString());
        }

        return(o);

    }
    /** 按照条件唯一选取
     * @return 符合条件的对象, 存在0个或多于一个结果都返回null
     */
    public static Object get(DetachedCriteria dc)
    {
        return(
            get(threadSession.get(), dc)
        );
    }

    public static void saveOrUpdate(Session s, Object o)
    {
        s.saveOrUpdate(o);

        return;
    }
    /** 创建或更新数据对象
     */
    public static void saveOrUpdate(Object o)
    {
        saveOrUpdate(threadSession.get(), o);

        return;
    }

    public static void save(Session s, Object o)
    {
        s.save(o);

        return;
    }
    /** 创建数据对象
     */
    public static void save(Object o)
    {
        save(threadSession.get(), o);

        return;
    }

    public static void update(Session s, Object o)
    {
        s.update(o);

        return;
    }
    /** 更新数据对象
     */
    public static void update(Object o)
    {
        update(threadSession.get(), o);

        return;
    }

    public static void delete(Session s, Object o)
    {
        s.delete(o);

        return;
    }
    /** 删除数据对象
     */
    public static void delete(Object o)
    {
        delete(threadSession.get(), o);

        return;
    }

    public static List search(Session s, DetachedCriteria dc, Integer start, Integer size)
    {
        Criteria c = dc.getExecutableCriteria(s);

        if (start != null)
            c.setFirstResult(start);
        if (size != null)
            c.setMaxResults(size);

        List li = c.list();

        return(li);
    }
    /** 搜索
     * <br />
     * @param dc 条件
     * @param start 结果分页用, 从第#条记录开始
     * @param limit 结果分页用, 限制最多返回#条记录
     */
    public static List search(DetachedCriteria dc, Integer start, Integer size)
    {
        return(
            search(threadSession.get(), dc, start, size)
        );
    }
    public static List search(Session s, DetachedCriteria dc)
    {
        return(
            search(s, dc, null, null)
        );
    }
    /** search(dc, start, limit) 的简化接口
     */
    public static List search(DetachedCriteria dc)
    {
        return(
            search(threadSession.get(), dc, null, null)
        );
    }

    public static Long count(Session s, DetachedCriteria dc)
    {
        Criteria c = dc.getExecutableCriteria(s)
            .setProjection(
                Projections.rowCount()
            );

        return((Long)c.uniqueResult());
    }
    /** 按照搜索条件计数
     */
    public static Long count(DetachedCriteria dc)
    {
        return(
            count(threadSession.get(), dc)
        );
    }

  // HQL
    public static Query createQuery(Session s, String hql)
    {
        return(
            s.createQuery(hql)
        );
    }
    public static Query createQuery(String hql)
    {
        return(
            createQuery(threadSession.get(), hql)
        );
    }
}
