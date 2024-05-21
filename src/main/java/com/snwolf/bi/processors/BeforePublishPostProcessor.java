package com.snwolf.bi.processors;

import com.snwolf.bi.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

@Slf4j
public class BeforePublishPostProcessor implements MessagePostProcessor {
    @Override
    public Message postProcessMessage(Message message) throws AmqpException {
        if(UserHolder.getUser() == null){
            log.info("UserHolder.getUser() == null");
            return message;
        }
        Long userId = UserHolder.getUser().getId();
        message.getMessageProperties().setHeader("userId", userId);
        log.info("设置userId到headers中, userId: {}", userId);
        return message;
    }
}
