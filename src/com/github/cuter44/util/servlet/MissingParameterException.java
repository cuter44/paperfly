package com.github.cuter44.util.servlet;

import javax.servlet.ServletException;

/** 表示参数缺失的 exception
 * @version 1.0.0 builld 20131212
 */
public class MissingParameterException
    extends ServletException
{
    public MissingParameterException()
    {
        super();
    }

    public MissingParameterException(String paramName)
    {
        super("Missing parameter:" + paramName);
    }
}
