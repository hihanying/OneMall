package top.hihanying.mall.auth.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import top.hihanying.mall.auth.domain.Oauth2TokenDto;
import top.hihanying.mall.common.api.CommonResult;
import top.hihanying.mall.common.constant.AuthConstant;

import java.security.Principal;
import java.util.Map;

/**
 * 自定义Oauth2获取令牌接口
 */
@RestController
@Api(tags = "认证中心登录认证", value = "认证中心登录认证")
@RequestMapping("/oauth")
public class AuthController {

    @Autowired
    private TokenEndpoint tokenEndpoint;

    @ApiOperation("Oauth2获取token")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "grant_type", value = "授权模式", required = true),
            @ApiImplicitParam(name = "client_id", value = "Oauth2客户端ID", required = true),
            @ApiImplicitParam(name = "client_secret", value = "Oauth2客户端秘钥", required = true),
            @ApiImplicitParam(name = "refresh_token", value = "刷新token"),
            @ApiImplicitParam(name = "username", value = "登录用户名"),
            @ApiImplicitParam(name = "password", value = "登录密码")
    })
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public CommonResult<Oauth2TokenDto> postAccessToken(@ApiIgnore Principal principal, @ApiIgnore @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        OAuth2AccessToken oAuth2AccessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();
        Oauth2TokenDto oauth2TokenDto = Oauth2TokenDto.builder()
                .token(oAuth2AccessToken.getValue())
                .refreshToken(oAuth2AccessToken.getRefreshToken().getValue())
                .expiresIn(oAuth2AccessToken.getExpiresIn())
                .tokenHead(AuthConstant.JWT_TOKEN_PREFIX).build();

        return CommonResult.success(oauth2TokenDto);
    }
}



/*
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.hihanying.mall.auth.api.UmsAdminClient;
import top.hihanying.mall.common.api.CommonResult;
import top.hihanying.mall.common.dto.UmsAdminLoginParam;
import top.hihanying.mall.common.dto.UmsAdminRegisterParam;
import top.hihanying.mall.mbg.model.UmsAdmin;

@Api("认证鉴权管理")
@RequestMapping("/auth")
@Controller
public class AuthController {
    @Autowired
    private UmsAdminClient umsAdminClient;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    @ResponseBody
    public CommonResult<UmsAdmin> register(@Validated @RequestBody UmsAdminRegisterParam registerParam) {
        return umsAdminClient.register(registerParam);
    }

    @ApiOperation(value = "用户登录")
    @PostMapping(value = "/login")
    @ResponseBody
    public CommonResult<String> login(@Validated @RequestBody UmsAdminLoginParam umsAdminLoginParam) {
        return umsAdminClient.login(umsAdminLoginParam);
    }
}*/
