package com.example.wing.apigateway.filter.post;

import com.example.wing.apigateway.filter.ZuulFilter;
import com.example.wing.apigateway.http.RequestContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author qxs on 2019/1/16.
 */
public class SendResponseFilter extends ZuulFilter{
    @Override
    public void run() {
        System.out.println("post filter exec");
        try {
            addResponseHeaders();
            writeResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeResponse() throws IOException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletResponse response = ctx.getResponse();
        if (response.getCharacterEncoding() == null) {
            response.setCharacterEncoding("utf-8");
        }
        ResponseEntity responseEntity = ctx.getResponseEntity();
        if (responseEntity.hasBody()) {
            byte[] body = (byte[]) responseEntity.getBody();
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(body);
            outputStream.flush();
        }
    }

    private void addResponseHeaders() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletResponse response = ctx.getResponse();
        ResponseEntity responseEntity = ctx.getResponseEntity();
        HttpHeaders headers = responseEntity.getHeaders();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String headName = entry.getKey();
            List<String> values = entry.getValue();
            for (String headerValue : values) {
                response.addHeader(headName, headerValue);
            }
        }
    }

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 1000;
    }
}
