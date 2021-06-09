package top.hihanying.mall.auth.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.hihanying.mall.common.dto.UserDto;

@FeignClient("onemall-admin")
public interface UmsAdminFeignClient {

    @GetMapping("/admin/loadByUsername")
    UserDto loadUserByUsername(@RequestParam String username);
}
