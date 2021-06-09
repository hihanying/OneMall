package top.hihanying.mall.auth.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import top.hihanying.mall.common.api.CommonResult;
import top.hihanying.mall.common.dto.UmsAdminLoginParam;
import top.hihanying.mall.common.dto.UmsAdminRegisterParam;
import top.hihanying.mall.mbg.model.UmsAdmin;

//@FeignClient("service-ums")
//@Repository
public interface UmsAdminClient {
    @PostMapping(value = "admin/register")
    CommonResult<UmsAdmin> register(UmsAdminRegisterParam umsAdminRegisterParam);
    @PostMapping(value = "admin/login")
    CommonResult<String> login(UmsAdminLoginParam umsAdminLoginParam);
}
