package com.example.wing.apigateway.filter.pre;

import com.example.wing.apigateway.filter.ZuulFilter;
import com.example.wing.apigateway.http.RequestContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author qxs on 2019/1/16.
 */
public class RequestWrapperFilter extends ZuulFilter {

    @Override
    public void run() {
        System.out.println("pre filter exec");
        String rootUrl = "http://localhost:9090";
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String requestURI = request.getRequestURI();
        String targetURL = rootUrl + requestURI;
        RequestEntity<byte[]> requestEntity = null;
        try {
            requestEntity = createRequestEntity(request, targetURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ctx.setRequestEntity(requestEntity);
    }

    private RequestEntity<byte[]> createRequestEntity(HttpServletRequest request, String targetURL) throws URISyntaxException, IOException {
        String method = request.getMethod();
        HttpMethod httpMethod = HttpMethod.resolve(method);
        MultiValueMap<String, String> headers = createRequestHeaders(request);
        byte[] body = createRequestBody(request);
        return new RequestEntity<>(body,headers,httpMethod,new URI(targetURL));
    }

    private byte[] createRequestBody(HttpServletRequest request) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        return StreamUtils.copyToByteArray(inputStream);
    }

    private MultiValueMap<String, String> createRequestHeaders(HttpServletRequest request) {
        HttpHeaders headers  = new HttpHeaders();
        ArrayList<String> headNames = Collections.list(request.getHeaderNames());
        for (String headName : headNames) {
            ArrayList<String> headerValues = Collections.list(request.getHeaders(headName));
            for (String headerValue : headerValues) {
                headers.add(headName,headerValue);
            }
        }

        return headers;
    }


    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return -1;
    }
}
