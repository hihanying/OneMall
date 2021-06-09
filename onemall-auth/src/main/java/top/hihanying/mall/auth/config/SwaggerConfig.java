package top.hihanying.mall.auth.config;

import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import top.hihanying.mall.common.config.BaseSwaggerConfig;
import top.hihanying.mall.common.config.SwaggerProperties;

/**
 * Swagger API文档相关配置
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig extends BaseSwaggerConfig {

    @Override
    public SwaggerProperties swaggerProperties() {
        return SwaggerProperties.builder()
                .apiBasePackage("top.hihanying.mall.auth.controller")
                .title("OneMall认证中心")
                .description("OneMall认证中心相关接口文档")
                .contactName("Han")
                .version("1.0")
                .enableSecurity(true)
                .build();
    }
}
