package com.example.wing.apigateway.http;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author qxs on 2019/1/17.
 */
@WebServlet(name = "zuul", urlPatterns = "/*")
public class ZuulServlet extends HttpServlet {
    private ZuulRunner zuulRunner = new ZuulRunner();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        if(requestURI.contains("ico")){
            return;
        }
        zuulRunner.init(req, resp);
        //执行前置过滤
        zuulRunner.preRoute();
        //执行过滤
        zuulRunner.route();
        //执行后置过滤
        zuulRunner.postRoute();
    }
}
