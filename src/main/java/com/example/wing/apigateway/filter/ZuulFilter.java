package com.example.wing.apigateway.filter;

/**
 * @author qxs on 2019/1/16.
 */
public abstract class ZuulFilter {

    abstract public void run();

    abstract public String filterType();

    abstract public int filterOrder();
}
