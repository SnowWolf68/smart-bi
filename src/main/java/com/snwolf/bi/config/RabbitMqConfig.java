package com.snwolf.bi.config;

import com.snwolf.bi.processors.AfterReceivePostProcessor;
import com.snwolf.bi.processors.BeforePublishPostProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        // 添加自定义的 AfterReceivePostProcessor
        factory.setAfterReceivePostProcessors(afterReceivePostProcessor);
        factory.setMessageConverter(jackson2JsonMessageConverter);

        // 将配置文件中的配置项配置到我们刚刚自己创建的factory中
        // 如果不进行配置, 那么在配置文件中配置的东西就不会生效, 比如配置的重试策略等等
        configurer.configure(factory, connectionFactory);

        return factory;
    }
}
