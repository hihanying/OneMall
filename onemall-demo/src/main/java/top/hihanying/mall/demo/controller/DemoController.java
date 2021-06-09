package top.hihanying.mall.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import top.hihanying.mall.demo.config.DemoSource;

@Api(tags = "测试Kafka消息发送")
@RequestMapping("/demo")
@RestController
public class DemoController {
    private final DemoSource demoSource;

    public DemoController(DemoSource demoSource) {
        this.demoSource = demoSource;
    }
    @PostMapping("pushDemo")
    @ApiOperation("发送设备历史消息")
    public String pushDeviceHistory(@RequestBody String msg) {
        this.demoSource.output().send(
                MessageBuilder.withPayload(msg).build());
        return "消息已成功发送";
    }

    @StreamListener(DemoSource.DEMO_OUTPUT)
    public void receive(String messageBody) {
        System.err.println("接收到了消息，内容为："+messageBody);
    }
}
