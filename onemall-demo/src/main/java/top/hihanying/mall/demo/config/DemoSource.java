package top.hihanying.mall.demo.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface DemoSource {
    String DEMO_INPUT = "demo-input";
    String DEMO_OUTPUT = "outputProductAdd";

    @Input(DEMO_INPUT)
    SubscribableChannel inputOut();

    @Output(DEMO_OUTPUT)
    MessageChannel output();
}
