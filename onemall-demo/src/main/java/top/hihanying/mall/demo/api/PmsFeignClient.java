package top.hihanying.mall.demo.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.hihanying.mall.common.api.CommonResult;
//import top.hihanying.mall.consumer.api.impl.PmsFeignClientFallback;


@FeignClient(value = "service-pms")// , fallback = PmsFeignClientFallback.class)
public interface PmsFeignClient {
    @GetMapping(value = "/brand/listAll/{errorCode}")
    CommonResult getBrandList(@PathVariable Integer errorCode);
    @GetMapping(value = "/product/simpleList")
    CommonResult getProductList();
}



