package top.hihanying.mall.admin.service.impl;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.hihanying.mall.admin.service.UmsMemberService;
import top.hihanying.mall.common.api.CommonResult;
import top.hihanying.mall.mbg.mapper.UmsMemberMapper;
import top.hihanying.mall.mbg.model.UmsMember;
import top.hihanying.mall.mbg.model.UmsMemberExample;

import java.util.ArrayList;
import java.util.List;

@Service
public class UmsMemberServiceImpl implements UmsMemberService {
    @Autowired
    private UmsMemberMapper umsMemberMapper;

    @HystrixCommand(fallbackMethod = "getMemberListFallBack", // 指定 FallBack 方法名
            commandProperties = { // 指定降级条件：超时3s
                @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value="3000")
    })
    @Override
    public CommonResult<List<UmsMember>> getMemberList(Integer errorId) {
        // 1. 运行时异常
        if (errorId == 1) {
            int i = 10 / 0;
        }
        // 2. 超时
        else if (errorId == 2) {
            try {
                Thread.sleep(5000);
                // TimeUnit.MILLISECONDS.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        List<UmsMember> umsMembers = umsMemberMapper.selectByExample(new UmsMemberExample());
        return CommonResult.success(umsMembers);
    }

    public CommonResult<List<UmsMember>> getMemberListFallBack(Integer errorId) {
        UmsMember umsMember = new UmsMember();
        umsMember.setUsername("默认");
        umsMember.setPassword("default");
        ArrayList<UmsMember> list = new ArrayList<>();
        list.add(umsMember);
        return CommonResult.success(list);
    }

}
