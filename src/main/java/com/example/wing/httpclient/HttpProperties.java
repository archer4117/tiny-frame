package com.example.wing.httpclient;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author qxs on 2018/4/20.
 */
@ConfigurationProperties(prefix = "http")
@Data
public class HttpProperties {

    private Integer maxTotal;

    private Integer defaultMaxPerRoute;

    private Integer connectTimeout;

    private Integer socketTimeout;

    private Integer connectionRequestTimeout;
}
