package top.hihanying.mall.demo.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.hihanying.mall.common.api.CommonResult;
import top.hihanying.mall.mbg.model.UmsMember;

import java.util.List;

@FeignClient(value = "service-ums")
@Component
public interface UmsFeignClient {
    @GetMapping(value = "/member/getMemberList/{errorId}")
    CommonResult<List<UmsMember>> getMemberList(@PathVariable Integer errorId);
}
