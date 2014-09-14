package com.github.cuter44.paperfly.msg.servlet;

import java.io.*;
import java.util.List;
import javax.servlet.*;

import com.alibaba.fastjson.*;

import com.github.cuter44.paperfly.msg.dao.*;

public class J
{
    private static final String C = "c";
    private static final String F = "f";
    private static final String T = "t";
    private static final String M = "m";

    public static JSONObject jsonize(Msg m)
    {
        JSONObject j = new JSONObject();

        j.put(C, m.getC());
        j.put(F, m.getF());
        j.put(T, m.getT());
        j.put(M, m.getM().getTime());

        return(j);
    }

    public static JSONArray jsonizeMsg(List<Msg> l)
    {
        JSONArray j  =new JSONArray();

        for (Msg m:l)
            j.add(jsonize(m));

        return(j);
    }

    public static void write(Msg m, ServletResponse resp)
        throws IOException
    {
        resp.setContentType("application/json; charset=utf-8");
        resp.setCharacterEncoding("utf-8");
        PrintWriter out = resp.getWriter();

        out.println(
            jsonize(m).toJSONString()
        );

        return;
    }

    public static void writeMsg(List<Msg> l, ServletResponse resp)
        throws IOException
    {
        resp.setContentType("application/json; charset=utf-8");
        resp.setCharacterEncoding("utf-8");
        PrintWriter out = resp.getWriter();

        out.println(
            jsonizeMsg(l).toJSONString()
        );

        return;
    }

    public static JSONObject jsonize(UnreadMsg um)
    {
        return(
            jsonize(um.getMsg())
        );
    }

    public static JSONArray jsonizeUnreadMsg(List<UnreadMsg> l)
    {
        JSONArray j  =new JSONArray();

        for (UnreadMsg um:l)
            j.add(jsonize(um));

        return(j);
    }

    public static void write(UnreadMsg um, ServletResponse resp)
        throws IOException
    {
        resp.setContentType("application/json; charset=utf-8");
        resp.setCharacterEncoding("utf-8");
        PrintWriter out = resp.getWriter();

        out.println(
            jsonize(um).toJSONString()
        );

        return;
    }

    public static void writeUnreadMsg(List<UnreadMsg> l, ServletResponse resp)
        throws IOException
    {
        resp.setContentType("application/json; charset=utf-8");
        resp.setCharacterEncoding("utf-8");
        PrintWriter out = resp.getWriter();

        out.println(
            jsonizeUnreadMsg(l).toJSONString()
        );

        return;
    }

    public static void writeNoError(ServletResponse resp)
        throws IOException
    {
        resp.setContentType("application/json; charset=utf-8");
        resp.setCharacterEncoding("utf-8");
        PrintWriter out = resp.getWriter();

        out.println(
            "{\"error\":\"no-error\"}"
        );

        return;
    }

    public static void writeEmptyArray(ServletResponse resp)
        throws IOException
    {
        resp.setContentType("application/json; charset=utf-8");
        resp.setCharacterEncoding("utf-8");
        PrintWriter out = resp.getWriter();

        out.println(
            "[]"
        );

        return;
    }
}
