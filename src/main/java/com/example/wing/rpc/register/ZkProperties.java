package com.example.wing.rpc.register;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author qxs on 2019/3/5.
 */
@Configuration
@Getter
@Setter
public class ZkProperties {
    /**
     * zk地址
     */
    @Value("${zk.registry}")
    private String registry;
}
