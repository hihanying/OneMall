package top.hihanying.mall.gateway.component;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixObservableCommand;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.GatewayToStringStyler;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.cloud.gateway.support.ServiceUnavailableException;
import org.springframework.cloud.gateway.support.TimeoutException;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import rx.Observable;
import rx.RxReactiveStreams;
import rx.Subscription;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 定制的 Hystrix 过滤器：
 * 基于每个请求URL创建一个新的Hystrix命令实例进行调用。
 * 每个URL可以指定特有的线程池配置，如果不指定则使用默认的。
 * 每个URL可以配置单独的Hystrix超时时间。
 */
@Component
public class CustomHystrixFilter extends AbstractGatewayFilterFactory<CustomHystrixFilter.Config> {
    private static final String NAME = "CustomHystrix";
    private final ObjectProvider<DispatcherHandler> dispatcherHandlerProvider;
    private volatile DispatcherHandler dispatcherHandler;

    public CustomHystrixFilter(ObjectProvider<DispatcherHandler> dispatcherHandlerProvider) {
        super(CustomHystrixFilter.Config.class);
        this.dispatcherHandlerProvider = dispatcherHandlerProvider;
    }

    private DispatcherHandler getDispatcherHandler() {
        if (this.dispatcherHandler == null) {
            this.dispatcherHandler = (DispatcherHandler)this.dispatcherHandlerProvider.getIfAvailable();
        }

        return this.dispatcherHandler;
    }

    public List<String> shortcutFieldOrder() {
        return Collections.singletonList("name");
    }


    @Override
    public GatewayFilter apply(String routeId, Consumer<CustomHystrixFilter.Config> consumer) {
        CustomHystrixFilter.Config config = (CustomHystrixFilter.Config)this.newConfig();
        consumer.accept(config);
        if (StringUtils.isEmpty(config.getName()) && !StringUtils.isEmpty(routeId)) {
            config.setName(routeId);
        }

        return this.apply(config);
    }

    protected HystrixObservableCommand.Setter createCommandSetter(CustomHystrixFilter.Config config, ServerWebExchange exchange) {
        return config.setter;
    }

    @Override
    public GatewayFilter apply(CustomHystrixFilter.Config config) {
        if (config.setter == null) {
            Assert.notNull(config.name, "A name must be supplied for the Hystrix Command Key");
            HystrixCommandGroupKey groupKey = HystrixCommandGroupKey.Factory.asKey(this.getClass().getSimpleName());
            HystrixCommandKey commandKey = com.netflix.hystrix.HystrixCommandKey.Factory.asKey(config.name);
            config.setter = HystrixObservableCommand.Setter.withGroupKey(groupKey).andCommandKey(commandKey);
        }

        return new GatewayFilter() {
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                // 通过配置文件构造 HystrixCommand
                CustomHystrixFilter.CustomHystrixCommand command = CustomHystrixFilter.this.new CustomHystrixCommand(
                        CustomHystrixFilter.this.createCommandSetter(config, exchange),
                        config.fallbackUri, config.arg, exchange, chain); // 添加自定义的arg
                // 创建 filter
                return Mono.create((s) -> {
                    Subscription sub = command.toObservable().subscribe(s::success, s::error, s::success);
                    s.onCancel(sub::unsubscribe);
                }).onErrorResume((throwable) -> {
                    if (throwable instanceof HystrixRuntimeException) {
                        HystrixRuntimeException e = (HystrixRuntimeException)throwable;
                        HystrixRuntimeException.FailureType failureType = e.getFailureType();
                        switch(failureType) {
                            case TIMEOUT:
                                return Mono.error(new TimeoutException());
                            case SHORTCIRCUIT:
                                return Mono.error(new ServiceUnavailableException());
                            case COMMAND_EXCEPTION:
                                Throwable cause = e.getCause();
                                if (cause instanceof ResponseStatusException || AnnotatedElementUtils.findMergedAnnotation(cause.getClass(), ResponseStatus.class) != null) {
                                    return Mono.error(cause);
                                }
                        }
                    }

                    return Mono.error(throwable);
                }).then();
            }

            public String toString() {
                return GatewayToStringStyler.filterToStringCreator(CustomHystrixFilter.this).append("name", config.getName()).append("fallback", config.fallbackUri).toString();
            }
        };
    }
    @Override
    public String name() {
        return NAME;
    }
    private class CustomHystrixCommand extends HystrixObservableCommand<Void> {
        private final URI fallbackUri;
        private final ServerWebExchange exchange;
        private final GatewayFilterChain chain;

        CustomHystrixCommand(Setter setter, URI fallbackUri, String arg, ServerWebExchange exchange, GatewayFilterChain chain) {
            super(setter);
            // 根据 arg 构造不同的 uri
            String path = fallbackUri.getPath();
            this.fallbackUri = URI.create(path+"?arg="+arg);
            this.exchange = exchange;
            this.chain = chain;
        }
        @Override
        protected Observable<Void> construct() {
            return RxReactiveStreams.toObservable(this.chain.filter(this.exchange));
        }
        @Override
        protected Observable<Void> resumeWithFallback() {
            if (this.fallbackUri == null) {
                return super.resumeWithFallback();
            } else {
                URI uri = this.exchange.getRequest().getURI();
                boolean encoded = ServerWebExchangeUtils.containsEncodedParts(uri);
                URI requestUrl = UriComponentsBuilder.fromUri(uri).host((String)null).port((String)null).uri(this.fallbackUri).scheme((String)null).build(encoded).toUri();
                this.exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, requestUrl);
                this.addExceptionDetails();
                ServerHttpRequest request = this.exchange.getRequest().mutate().uri(requestUrl).build();
                ServerWebExchange mutated = this.exchange.mutate().request(request).build();
                return RxReactiveStreams.toObservable(CustomHystrixFilter.this.getDispatcherHandler().handle(mutated));
            }
        }
        private void addExceptionDetails() {
            Throwable executionException = this.getExecutionException();
            Optional.ofNullable(executionException).ifPresent((exception) -> {
                this.exchange.getAttributes().put(ServerWebExchangeUtils.HYSTRIX_EXECUTION_EXCEPTION_ATTR, exception);
            });
        }
    }

    public static class Config {
        private String name;
        private HystrixObservableCommand.Setter setter;
        private URI fallbackUri;
        private String arg;

        public String getArg() {
            return arg;
        }

        public CustomHystrixFilter.Config setArg(String arg) {
            this.arg = arg;
            return this;
        }

        public Config() {
        }

        public String getName() {
            return this.name;
        }

        public CustomHystrixFilter.Config setName(String name) {
            this.name = name;
            return this;
        }

        public CustomHystrixFilter.Config setFallbackUri(String fallbackUri) {
            if (fallbackUri != null) {
                this.setFallbackUri(URI.create(fallbackUri));
            }

            return this;
        }

        public URI getFallbackUri() {
            return this.fallbackUri;
        }

        public void setFallbackUri(URI fallbackUri) {
            if (fallbackUri != null && !"forward".equals(fallbackUri.getScheme())) {
                throw new IllegalArgumentException("Hystrix Filter currently only supports 'forward' URIs, found " + fallbackUri);
            } else {
                this.fallbackUri = fallbackUri;
            }
        }

        public CustomHystrixFilter.Config setSetter(HystrixObservableCommand.Setter setter) {
            this.setter = setter;
            return this;
        }
    }
}