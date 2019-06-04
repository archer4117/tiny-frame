package com.example.wing.apigateway.http;

import com.example.wing.apigateway.filter.ZuulFilter;
import com.example.wing.apigateway.filter.post.SendResponseFilter;
import com.example.wing.apigateway.filter.pre.RequestWrapperFilter;
import com.example.wing.apigateway.filter.route.RoutingFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qxs on 2019/1/17.
 */
public class ZuulRunner {

    //静态写死过滤器
    private ConcurrentHashMap<String, List<ZuulFilter>> hashFiltersByType = new ConcurrentHashMap<String, List<ZuulFilter>>(){{
        put("pre",new ArrayList<ZuulFilter>(){{
            add(new RequestWrapperFilter());
        }});
        put("route",new ArrayList<ZuulFilter>(){{
            add(new RoutingFilter());
        }});
        put("post",new ArrayList<ZuulFilter>(){{
            add(new SendResponseFilter());
        }});
    }};

    public void init(HttpServletRequest req, HttpServletResponse resp) {
        RequestContext requestContext = RequestContext.getCurrentContext();
        requestContext.setRequest(req);
        requestContext.setResponse(resp);
    }

    public void preRoute() {
        runFilters("pre");
    }

    public void route() {
        runFilters("route");
    }

    public void postRoute() {
        runFilters("post");
    }

    private void runFilters(String sType) {
        List<ZuulFilter> zuulFilters = this.hashFiltersByType.get(sType);
        if (zuulFilters != null) {
            for (ZuulFilter zuulFilter:zuulFilters) {
                zuulFilter.run();
            }
        }
    }
}
