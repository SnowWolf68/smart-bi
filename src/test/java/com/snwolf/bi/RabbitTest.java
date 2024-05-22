//package com.snwolf.bi;
//
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.amqp.rabbit.annotation.Exchange;
//import org.springframework.amqp.rabbit.annotation.Queue;
//import org.springframework.amqp.rabbit.annotation.QueueBinding;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import javax.annotation.Resource;
//
//@SpringBootTest
//@Slf4j
//public class RabbitTest {
//
//    @Resource
//    private RabbitTemplate rabbitTemplate;
//
//    @Test
//    void test() throws InterruptedException {
//        rabbitTemplate.convertAndSend("bi.test.exchange", "test.key", "hello world");
//        Thread.sleep(10000);
//    }
//
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(name = "bi.test.queue"),
//            exchange = @Exchange(name = "bi.test.exchange", type = "direct"),
//            key = "test.key"
//    ))
//    void listener(String msg){
//        log.info("接收到消息队列: {} 中的消息: {}", "bi.test.queue", msg);
//    }
//}
