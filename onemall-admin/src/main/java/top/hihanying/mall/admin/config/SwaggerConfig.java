package top.hihanying.mall.admin.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import top.hihanying.mall.common.config.BaseSwaggerConfig;

import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import top.hihanying.mall.common.config.SwaggerProperties;

/**
 * Swagger API文档相关配置
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
public class SwaggerConfig extends BaseSwaggerConfig {

    @Override
    public SwaggerProperties swaggerProperties() {
        return SwaggerProperties.builder()
                .apiBasePackage("top.hihanying.mall.admin.controller")
                .title("Demo后台服务")
                .description("Demo后台相关接口文档")
                .contactName("Han")
                .version("1.0")
                .enableSecurity(true)
                .build();
    }
}