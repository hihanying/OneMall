package top.hihanying.mall.admin.service;

import top.hihanying.mall.common.dto.UmsAdminLoginParam;
import top.hihanying.mall.common.dto.UmsAdminRegisterParam;
import top.hihanying.mall.common.api.CommonResult;
import top.hihanying.mall.mbg.model.UmsAdmin;

public interface UmsAdminService {

    CommonResult<UmsAdmin> register(UmsAdminRegisterParam registerParam);
    CommonResult<UmsAdmin> login(UmsAdminLoginParam umsAdminLoginParam);
    UmsAdmin getAdminByUsername(String username);
}
