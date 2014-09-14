package com.github.cuter44.util.dump;

import java.lang.StackTraceElement;

/** 简易内存转储宏
 * @version 1.0.0 builld 20131212
 */
public class Dump
{
    public static void dump(byte[] bytes)
    {
        for (int i=0; i<bytes.length; i++)
            System.err.print(String.format("%02x", bytes[i] & 0xff));
        System.err.println();
        return;
    }

    public static void stack()
    {
        StackTraceElement stack[] = Thread.currentThread().getStackTrace();
        for (int i=2; i<stack.length; i++)
            System.err.println(stack[i]);
        return;
    }

}
