package top.hihanying.mall.gateway.handler;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import top.hihanying.mall.common.api.CommonResult;


@RestController
public class defaultFullbackHandler {
    private static final String prefix = "触发网关转发 ";
    private static final String suffix = " 服务熔断！";

    @RequestMapping(value = "/defaultFullback")
    @ResponseStatus
    public Mono<CommonResult> defaultFullback(ServerWebExchange exchange, Throwable throwable) {
        ServerHttpRequest request = exchange.getRequest();
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        String arg = queryParams.get("arg").get(0);
        return Mono.just(CommonResult.failed(prefix+arg+suffix));
    }}
