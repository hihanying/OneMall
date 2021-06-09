package top.hihanying.mall.gateway.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 网关白名单配置
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ConfigurationProperties(prefix="secure.ignore") // 绑定配置
@Configuration
public class IgnoreUrlsConfig {
    private List<String> urls;
}
