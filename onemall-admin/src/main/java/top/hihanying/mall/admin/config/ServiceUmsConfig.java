package top.hihanying.mall.admin.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"top.hihanying.mall.admin",
                                "top.hihanying.mall.mbg.model",
                                "top.hihanying.mall.common.service"
})
@MapperScan(basePackages = {"top.hihanying.mall.mbg.mapper"})
@EnableDiscoveryClient
@EnableCircuitBreaker
public class ServiceUmsConfig {
}
