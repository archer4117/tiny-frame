package com.example.wing.apigateway.http;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qxs on 2019/1/17.
 */
public class RequestContext extends ConcurrentHashMap<String, Object> {
    private static final Class<? extends RequestContext> CONTEXT_CLASS = RequestContext.class;
    private static final ThreadLocal<? extends RequestContext> THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        try {
            return CONTEXT_CLASS.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    });

    public static RequestContext getCurrentContext() {
        return THREAD_LOCAL.get();
    }

    public void setRequest(HttpServletRequest request) {
        put("request", request);
    }

    public void setResponse(HttpServletResponse response) {
        put("response", response);
    }
    public HttpServletResponse getResponse() {
        return (HttpServletResponse) get("response");
    }

    public HttpServletRequest getRequest() {
        return (HttpServletRequest) get("request");
    }

    public void setRequestEntity(RequestEntity<byte[]> requestEntity) {
        put("requestEntity", requestEntity);
    }

    public RequestEntity getRequestEntity() {
        return (RequestEntity) get("requestEntity");
    }

    public void setResponseEntity(ResponseEntity responseEntity) {
        put("responseEntity", responseEntity);
    }

    public ResponseEntity getResponseEntity() {
        return (ResponseEntity) get("responseEntity");
    }
}
