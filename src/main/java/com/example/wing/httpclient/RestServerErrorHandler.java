package com.example.wing.httpclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

/**
 * @author qxs on 2018/4/23.
 */
@Slf4j
public class RestServerErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        //返回false表示不管response的status是多少都返回没有错
        //这里可以自己定义那些status code你认为是可以抛Error
        return false;
    }

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
        //这里面可以实现你自己遇到了Error进行合理的处理
        log.error("response error code = "+clientHttpResponse.getStatusCode()+" msg = " +clientHttpResponse.getStatusText());
    }
}
