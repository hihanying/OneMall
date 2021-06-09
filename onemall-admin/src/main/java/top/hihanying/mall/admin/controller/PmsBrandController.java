package top.hihanying.mall.admin.controller;

import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import top.hihanying.mall.common.api.CommonPage;
import top.hihanying.mall.common.api.CommonResult;
import top.hihanying.mall.mbg.model.PmsBrand;
import top.hihanying.mall.admin.service.PmsBrandService;

import java.util.List;

/**
 * 品牌管理 Controller
 */
@Controller
@RequestMapping("/brand")
@DefaultProperties(defaultFallback = "defaultPmsProductFallback")
public class PmsBrandController {

    @Autowired
    private PmsBrandService pmsBrandService;
    private static final Logger LOGGER = LoggerFactory.getLogger(PmsBrandController.class);

/*
    @RequestMapping(value = "listAll", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PmsBrand>> getBrandList() {
        return CommonResult.success(pmsBrandService.listAllBrand());
    }
*/
    @HystrixCommand(fallbackMethod = "getBrandListPmsProductFallback", commandProperties = {
            @HystrixProperty(name = "fallback.enabled", value = "true"),
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "4000"),
            @HystrixProperty(name = "execution.timeout.enabled", value = "true"),
            @HystrixProperty(name = "circuitBreaker.enabled",value = "true"),// 是否开启断路器
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "10"),// 请求次数
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "10000"), // 休眠窗口期
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "60"),// 失败率达到多少后跳闸
    })
    @RequestMapping(value = "/getBrandList/{errorCode}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getBrandList(@PathVariable Integer errorCode) {
        if (errorCode == 1) {
            int i = 10 / 0;
        }
        else if (errorCode == 2) {
            try {
                Thread.sleep(3500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return CommonResult.success(pmsBrandService.listAllBrand());
    }
    // getBrandList 的 Fallback 方法
    public CommonResult getBrandListPmsProductFallback(@PathVariable Integer errorCode) {
        return CommonResult.success(null, "Pms服务内的 getBrandListPmsProductFallback");
    }

    // 默认的 Fallback 方法
    public CommonResult defaultPmsProductFallback() {
        return CommonResult.success(null, "Pms服务内的defaultProductFallback");
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult createBrand(@RequestBody PmsBrand pmsBrand) {
        CommonResult commonResult;
        int count = pmsBrandService.createBrand(pmsBrand);
        if (count == 1) {
            commonResult = CommonResult.success(pmsBrand);
            LOGGER.debug("createBrand success:{}", pmsBrand);
        } else {
            commonResult = CommonResult.failed("操作失败");
            LOGGER.debug("createBrand failed:{}", pmsBrand);
        }
        return commonResult;
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateBrand(@PathVariable("id") Long id, @RequestBody PmsBrand pmsBrandDto, BindingResult result) {
        CommonResult commonResult;
        int count = pmsBrandService.updateBrand(id, pmsBrandDto);
        if (count == 1) {
            commonResult = CommonResult.success(pmsBrandDto);
            LOGGER.debug("updateBrand success:{}", pmsBrandDto);
        } else {
            commonResult = CommonResult.failed("操作失败");
            LOGGER.debug("updateBrand failed:{}", pmsBrandDto);
        }
        return commonResult;
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult deleteBrand(@PathVariable("id") Long id) {
        int count = pmsBrandService.deleteBrand(id);
        if (count == 1) {
            LOGGER.debug("deleteBrand success :id={}", id);
            return CommonResult.success(null);
        } else {
            LOGGER.debug("deleteBrand failed :id={}", id);
            return CommonResult.failed("操作失败");
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsBrand>> listBrand(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                        @RequestParam(value = "pageSize", defaultValue = "3") Integer pageSize) {
        List<PmsBrand> brandList = pmsBrandService.listBrand(pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(brandList));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PmsBrand> brand(@PathVariable("id") Long id) {
        return CommonResult.success(pmsBrandService.getBrand(id));
    }
}
