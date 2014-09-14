package com.github.cuter44.util.dao;

/** 表示对象被外键参照而无法变更的 exception
 * @version 1.0.0 builld 20131212
 */
public class EntityReferencedException
    extends IllegalStateException
{
    public EntityReferencedException()
    {
        super();
    }

    public EntityReferencedException(String msg)
    {
        super(msg);
    }

    public EntityReferencedException(Throwable cause)
    {
        super(cause);
    }

    public EntityReferencedException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
