package top.hihanying.mall.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.hihanying.mall.admin.service.UmsMemberService;
import top.hihanying.mall.common.api.CommonResult;
import top.hihanying.mall.mbg.model.UmsMember;

import java.util.List;

@RequestMapping("/member")
@Controller
public class UmsMemberController {
    @Autowired
    private UmsMemberService umsMemberService;

    @GetMapping("/getMemberList/{errorId}")
    @ResponseBody
    public CommonResult<List<UmsMember>> getMemberList(@PathVariable Integer errorId) {
        return umsMemberService.getMemberList(errorId);
    }
}
