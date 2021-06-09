package top.hihanying.mall.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.hihanying.mall.common.dto.UmsAdminLoginParam;
import top.hihanying.mall.common.dto.UmsAdminRegisterParam;
import top.hihanying.mall.admin.service.UmsAdminService;
import top.hihanying.mall.common.api.CommonResult;
import top.hihanying.mall.common.expection.Asserts;
import top.hihanying.mall.mbg.mapper.UmsAdminMapper;
import top.hihanying.mall.mbg.model.UmsAdmin;
import top.hihanying.mall.mbg.model.UmsAdminExample;

import java.util.Date;
import java.util.List;

@Service
public class UmsAdminServiceImpl implements UmsAdminService {
    @Autowired
    private UmsAdminMapper umsAdminMapper;
    @Override
    public CommonResult<UmsAdmin> register(UmsAdminRegisterParam registerParam) {
        // 创建用户
        UmsAdmin umsAdmin = new UmsAdmin();
        BeanUtils.copyProperties(registerParam, umsAdmin);
        umsAdmin.setCreateTime(new Date());
        umsAdmin.setStatus(1);
        // 查询是否有相同用户名的用户
        UmsAdminExample example = new UmsAdminExample();
        example.createCriteria().andUsernameEqualTo(umsAdmin.getUsername());
        List<UmsAdmin> umsAdminList = umsAdminMapper.selectByExample(example);
        if (umsAdminList.size() > 0) {
            return CommonResult.failed("用户名重复！");
        }
        // 将密码进行加密操作
        // String encodePassword = BCrypt.hashpw(umsAdmin.getPassword());
        umsAdmin.setPassword(umsAdmin.getPassword());
        umsAdminMapper.insert(umsAdmin);
        return CommonResult.success(umsAdmin);
    }

    @Override
    public CommonResult<UmsAdmin> login(UmsAdminLoginParam umsAdminLoginParam) {
        // 判空
        String username = umsAdminLoginParam.getUsername();
        String password = umsAdminLoginParam.getPassword();
        if(StrUtil.isEmpty(username)||StrUtil.isEmpty(password)){
            Asserts.fail("用户名或密码不能为空！");
            return CommonResult.failed("用户名或密码不能为空！");
        }
        // 判断用户名是否存在
        UmsAdmin umsAdmin = getAdminByUsername(username);
        if (umsAdmin == null) {
            Asserts.fail("用户名不存在！");
            return CommonResult.failed("用户名不存在！");
        }
        // 判断密码是否正确
        if (!StrUtil.equals(umsAdmin.getPassword(), password)) {
            Asserts.fail("密码错误! ");
            return CommonResult.failed("密码错误! ");
        }
        return CommonResult.success(umsAdmin);
    }

    @Override
    public UmsAdmin getAdminByUsername(String username) {
        UmsAdminExample example = new UmsAdminExample();
        example.createCriteria().andUsernameEqualTo(username);
        List<UmsAdmin> adminList = umsAdminMapper.selectByExample(example);
        if (adminList != null && adminList.size() > 0) {
            return adminList.get(0);
        }
        return null;
    }
}
