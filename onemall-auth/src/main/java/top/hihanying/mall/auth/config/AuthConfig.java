package top.hihanying.mall.auth.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 认证服务配置
 */
@ComponentScan(basePackages = {
        "top.hihanying.mall.auth",
})
@EnableFeignClients(basePackages = "top.hihanying.mall.auth.service")
@EnableDiscoveryClient
@Configuration
public class AuthConfig {
}
