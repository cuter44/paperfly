package com.github.cuter44.util.dao;

/** 表示指定的实体不能找到的 exception
 * @version 1.0.0 builld 20131212
 */
public class EntityNotFoundException
    extends RuntimeException
{
    public EntityNotFoundException()
    {
        super();
    }

    public EntityNotFoundException(String msg)
    {
        super(msg);
    }

    public EntityNotFoundException(Throwable cause)
    {
        super(cause);
    }

    public EntityNotFoundException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
