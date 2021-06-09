package top.hihanying.mall.gateway.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

@ComponentScan(basePackages = { "top.hihanying.mall.gateway",
                                "top.hihanying.mall.common.service"}) // 扫描包路径
@EnableDiscoveryClient // 开启服务发现
@Configuration // 声明为配置类不然不会生效
public class GatewayConfig {
    @Bean
    public AntPathMatcher antPathMatcher() {
        return new AntPathMatcher();
    }
}
