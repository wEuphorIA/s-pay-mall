package cn.bugstack.service.rpc;

import cn.bugstack.domain.vo.ProductVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 @author Euphoria
 @version 1.0
 @description: TODO
 @date 2025/9/22 下午2:30 */
@Service
public class ProductRPC {

    public ProductVO queryProductByProductId(String productId){
        ProductVO productVO = new ProductVO();
        productVO.setProductId(productId);
        productVO.setProductName("测试商品");
        productVO.setProductDesc("这是一个测试商品");
        productVO.setPrice(new BigDecimal("1.68"));
        return productVO;
    }

}
