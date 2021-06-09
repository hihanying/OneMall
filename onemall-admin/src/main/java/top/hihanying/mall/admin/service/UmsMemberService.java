package top.hihanying.mall.admin.service;

import top.hihanying.mall.common.api.CommonResult;
import top.hihanying.mall.mbg.model.UmsMember;

import java.util.List;

public interface UmsMemberService {

    CommonResult<List<UmsMember>> getMemberList(Integer id);
}
