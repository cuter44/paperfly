package com.github.cuter44.util.dao;

/** 表示实体重复的 exception
 * @version 1.0.0 builld 20131212
 */
public class EntityDuplicatedException
    extends RuntimeException
{
    public EntityDuplicatedException()
    {
        super();
    }

    public EntityDuplicatedException(String msg)
    {
        super(msg);
    }

    public EntityDuplicatedException(Throwable cause)
    {
        super(cause);
    }

    public EntityDuplicatedException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
