package top.hihanying.mall.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

/**
 * 后台用户登录参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UmsAdminLoginParam {
    @NotEmpty
    @ApiModelProperty(value = "用户名", required = true)
    private String username;
    @NotEmpty
    @ApiModelProperty(value = "密码", required = true)
    private String password;
}
