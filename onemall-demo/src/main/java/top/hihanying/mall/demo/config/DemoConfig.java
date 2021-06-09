package top.hihanying.mall.demo.config;

import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(basePackages = {
        "top.hihanying.mall.demo",
})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"top.hihanying.mall.demo.api"})
@EnableCircuitBreaker // 启用熔断降级
@Configuration
public class DemoConfig {
}
