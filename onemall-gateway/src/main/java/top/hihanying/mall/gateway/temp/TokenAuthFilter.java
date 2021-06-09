package top.hihanying.mall.gateway.temp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.hihanying.mall.common.api.CommonResult;
import top.hihanying.mall.common.api.ResultCode;
import top.hihanying.mall.common.service.RedisService;
import top.hihanying.mall.common.utils.JwtUtil;

import java.nio.charset.StandardCharsets;

// @Component
public class TokenAuthFilter implements GlobalFilter, Ordered {
    @Autowired
    private AuthProperties auth;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private AntPathMatcher antPathMatcher;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        // 跳过不需要验证的url
        String path = request.getURI().getPath();
        for (String allowPath : auth.getAllowPaths()) {
            if (antPathMatcher.match(allowPath, path))
                return chain.filter(exchange);
        }
        // 获取 token
        String token = request.getHeaders().getFirst("token");
        // token 为空
        if (token == null) {
            token = request.getQueryParams().getFirst("token");
        }
        if (StringUtils.isEmpty(token)) {
            return authError(response, "请先登陆");
        }
        // 解析 token
        Claims claims = JwtUtil.parseJWT(token);
        if (claims == null) {
            return authError(response, "认证失败，请重新登陆！");
        }
        String username = (String) claims.get("userName"); // userId
        String realToken = (String) redisService.get(username);
        // 如果 redis 中不存在了那就说明过期了或者token失效
        if (realToken == null || !realToken.equals(token)) {
            return authError(response, "认证失败，请重新登陆！");
        }
        // token 有效
        return chain.filter(exchange);
    }

    /**
     * 认证错误输出
     * @param response
     * @param s
     * @return
     */
    private Mono<Void> authError(ServerHttpResponse response, String s) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type","application/json;charset=UTF-8");
        CommonResult<Object> result = CommonResult.failed(ResultCode.UNAUTHORIZED, s);
        String returnStr = "";
        try {
            returnStr = objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        DataBuffer buffer = response.bufferFactory().wrap(returnStr.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Flux.just(buffer));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}