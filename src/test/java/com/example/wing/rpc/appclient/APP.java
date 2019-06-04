package com.example.wing.rpc.appclient;

import com.example.wing.DemoApplication;
import com.example.wing.rpc.appcommon.IProductService;
import com.example.wing.rpc.appcommon.Product;
import com.example.wing.rpc.client.RpcProxy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author qxs on 2019/3/5.
 * rpc appclient 测试，需要单独部署2个工程
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class APP {

    @Resource
    private RpcProxy rpcProxy;

    private IProductService productService;

    @Before
    public void init() {
        System.out.println("app client init");
//        productService = rpcProxy.getInstance(IProductService.class);
    }


    @Test
    public void testSave() throws Exception {
        productService.save(new Product(2L,"002","内衣", BigDecimal.TEN));
    }

    @Test
    public void testDelete() throws Exception {
        productService.deleteById(2L);
    }

    @Test
    public void testUpdate() throws Exception {
        productService.update(new Product(2L,"002","内衣",BigDecimal.ONE));
    }

    @Test
    public void testGet() throws Exception {
        Product product = productService.get(1L);
        System.out.println("获取到的产品信息为:"+product);
    }
}
