package com.example.wing.rpc.appcommon;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qxs on 2019/3/5.
 */
@Data
@AllArgsConstructor
public class Product {
    private Long id;//id
    private String sn;//产品编号
    private String name;//产品名称
    private BigDecimal price;//产品价格
}
