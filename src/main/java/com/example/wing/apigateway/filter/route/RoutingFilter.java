package com.example.wing.apigateway.filter.route;

import com.example.wing.apigateway.filter.ZuulFilter;
import com.example.wing.apigateway.http.RequestContext;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * @author qxs on 2019/1/17.
 */
public class RoutingFilter extends ZuulFilter {
    @Override
    public void run() {
        System.out.println("route filter exec");
        RequestContext ctx = RequestContext.getCurrentContext();
        RequestEntity requestEntity = ctx.getRequestEntity();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity responseEntity = restTemplate.exchange(requestEntity, byte[].class);
        ctx.setResponseEntity(responseEntity);
    }

    @Override
    public String filterType() {
        return "route";
    }

    @Override
    public int filterOrder() {
        return 0;
    }
}
