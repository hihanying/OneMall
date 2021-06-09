package top.hihanying.mall.demo.controller;

import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.hihanying.mall.common.api.CommonResult;
import top.hihanying.mall.demo.api.PmsFeignClient;
import top.hihanying.mall.demo.api.UmsFeignClient;

@Api(tags = "测试用消费者服务")
@RequestMapping("/consumer")
@Controller
@DefaultProperties(defaultFallback = "consumerDefaultFallback")
public class ConsumerController {

    @Autowired
    private PmsFeignClient pmsFeignClient;

    @Autowired
    private UmsFeignClient umsFeignClient;

    @ApiOperation(tags = "getBrandList", value = "获取品牌列表")
    @GetMapping("/getBrandList/{errorCode}")
    @ResponseBody
    public CommonResult getBrandList(@PathVariable Integer errorCode) {
        return pmsFeignClient.getBrandList(errorCode);
    }

    @ApiOperation(tags = "getProductList", value = "获取商品列表")
    @GetMapping("/getProductList")
    @HystrixCommand(fallbackMethod = "getProductListFullDown")
    @ResponseBody
    public CommonResult getProductList() {
        int i = 10 / 0;
        return pmsFeignClient.getProductList();
    }

    public CommonResult getProductListFullDown() {
        return CommonResult.success(null, "getProductList默认返回，调用方法异常或超时");
    }

    @ApiOperation(tags = "getMemberList", value = "获取会员列表")
    @GetMapping("/getMemberList/{errorId}")
    @ResponseBody
    @HystrixCommand
    public CommonResult getMemberList(@PathVariable Integer errorId) {
        int i  = 10 / 0;
        return umsFeignClient.getMemberList(errorId);
    }

    public CommonResult consumerDefaultFallback() {
        return CommonResult.success(null, "consumer默认返回，调用方法异常或超时");
    }




}
