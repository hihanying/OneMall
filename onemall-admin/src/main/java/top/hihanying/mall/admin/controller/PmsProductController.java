package top.hihanying.mall.admin.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.hihanying.mall.common.api.CommonResult;
import top.hihanying.mall.mbg.model.PmsProduct;
import top.hihanying.mall.admin.service.PmsProductService;

import java.util.List;

@Api(tags = "商品管理Controller")
@RequestMapping("/product")
@Controller
public class PmsProductController {
    @Autowired
    private PmsProductService pmsProductService;

    @ApiOperation("根据商品名称或货号模糊查询")
    @GetMapping(value = "/simpleList")
    @ResponseBody
    public CommonResult getList(String keyword) {
        List<PmsProduct> productList = pmsProductService.list(keyword);
        return CommonResult.success(productList);
    }

}
