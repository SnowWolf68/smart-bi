package com.snwolf.bi.config;

import com.snwolf.bi.processors.AfterReceivePostProcessor;
import com.snwolf.bi.processors.BeforePublishPostProcessor;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Configuration
public class RabbitMqConfig {

    /**
     * 这里不能使用构造方法注入, 否则会出现循环依赖问题, 导致Bean不能成功创建
     */
    @Resource
    private RabbitTemplate rabbitTemplate;

    private AfterReceivePostProcessor afterReceivePostProcessor = new AfterReceivePostProcessor();

    private BeforePublishPostProcessor beforePublishPostProcessor = new BeforePublishPostProcessor();

    private MessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @PostConstruct
    void setProcessors(){
        rabbitTemplate.setBeforePublishPostProcessors(beforePublishPostProcessor);
//        rabbitTemplate.setAfterReceivePostProcessors(afterReceivePostProcessor);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        // 添加自定义的 AfterReceivePostProcessor
        factory.setAfterReceivePostProcessors(afterReceivePostProcessor);
        factory.setMessageConverter(jackson2JsonMessageConverter);

        return factory;
    }
}
