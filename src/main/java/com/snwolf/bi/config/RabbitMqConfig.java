package com.snwolf.bi.config;

import com.snwolf.bi.constants.RabbitMqConstants;
import com.snwolf.bi.processors.AfterReceivePostProcessor;
import com.snwolf.bi.processors.BeforePublishPostProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Configuration
@Slf4j
public class RabbitMqConfig {

    /**
     * 这里不能使用构造方法注入, 否则会出现循环依赖问题, 导致Bean不能成功创建
     */
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RabbitListenerContainerFactory rabbitListenerContainerFactory;

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
        log.info("rabbitListenerContainerFactory: {}", rabbitListenerContainerFactory);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        // 添加自定义的 AfterReceivePostProcessor
        factory.setAfterReceivePostProcessors(afterReceivePostProcessor);
        factory.setMessageConverter(jackson2JsonMessageConverter);

        /*RetryTemplate retryTemplate = new RetryTemplate();
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(2);

        retryTemplate.setRetryPolicy(simpleRetryPolicy);

        factory.setRetryTemplate(retryTemplate);*/

        configurer.configure(factory, connectionFactory);

        return factory;
    }
}
