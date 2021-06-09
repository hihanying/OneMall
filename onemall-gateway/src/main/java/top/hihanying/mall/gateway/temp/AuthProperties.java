package top.hihanying.mall.gateway.temp;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "auth")
@Getter
@Setter
public class AuthProperties {
    private List<String> allowPaths;

}
