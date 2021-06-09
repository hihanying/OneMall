package top.hihanying.mall.admin.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.hihanying.mall.common.dto.UmsAdminLoginParam;
import top.hihanying.mall.common.dto.UmsAdminRegisterParam;
import top.hihanying.mall.admin.service.UmsAdminService;
import top.hihanying.mall.common.api.CommonResult;
import top.hihanying.mall.common.service.RedisService;
import top.hihanying.mall.common.utils.JwtUtil;
import top.hihanying.mall.mbg.model.UmsAdmin;

@Controller
@Api(tags = "UmsAdminController", value = "后台用户管理")
@RequestMapping("/admin")
public class UmsAdminController {
    @Autowired
    private UmsAdminService umsAdminService;
    @Autowired
    private RedisService redisService;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    @ResponseBody
    public CommonResult<UmsAdmin> register(@Validated @RequestBody UmsAdminRegisterParam registerParam) {
        return umsAdminService.register(registerParam);
    }

    @ApiOperation(value = "用户登录：成功返回 token")
    @PostMapping(value = "/login")
    @ResponseBody
    public CommonResult<String> login(@Validated @RequestBody UmsAdminLoginParam umsAdminLoginParam) {
        CommonResult<UmsAdmin> result = umsAdminService.login(umsAdminLoginParam);
        if (result.getCode() != 200) {
            return CommonResult.failed(result.getMessage());
        }
        UmsAdmin umsAdmin = (UmsAdmin) result.getData();
        // 生成 token 并写入 Redis
        String token = JwtUtil.createJWT(umsAdmin.getId(), umsAdmin.getUsername());
        redisService.set(umsAdmin.getUsername(), token);
        redisService.expire(umsAdmin.getUsername(), 60 * 10);
        return CommonResult.success(token, "登陆成功! ");
    }
}
