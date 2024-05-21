package com.snwolf.bi.config;

import com.snwolf.bi.constants.RabbitMqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class RabbitMqErrorMessageConfig {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Bean
    public DirectExchange errorMessageExchange() {
        return new DirectExchange(RabbitMqConstants.GEN_CHAT_BY_AI_ERROR_EXCHANGE);
    }

    @Bean
    public Queue errorMessageQueue() {
        return new Queue(RabbitMqConstants.GEN_CHAT_BY_AI_ERROR_QUEUE);
    }

    @Bean
    public Binding errorMessageBinding(Queue errorMessageQueue, DirectExchange errorMessageExchange) {
        return BindingBuilder.bind(errorMessageQueue).to(errorMessageExchange).with(RabbitMqConstants.GEN_CHAT_BY_AI_ERROR_KEY);
    }

    @Bean
    public MessageRecoverer messageRecoverer(){
        return new RepublishMessageRecoverer(rabbitTemplate, RabbitMqConstants.GEN_CHAT_BY_AI_ERROR_EXCHANGE, RabbitMqConstants.GEN_CHAT_BY_AI_ERROR_KEY);
    }
}
