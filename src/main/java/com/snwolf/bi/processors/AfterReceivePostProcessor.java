package com.snwolf.bi.processors;

import com.snwolf.bi.domain.dto.UserDTO;
import com.snwolf.bi.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

@Slf4j
public class AfterReceivePostProcessor implements MessagePostProcessor {
    @Override
    public Message postProcessMessage(Message message) throws AmqpException {
        Long userId = message.getMessageProperties().getHeader("userId");
        log.info("从消息中获取userId, userId: {}", userId);
        UserDTO userDTO = UserDTO.builder().id(userId).build();
        UserHolder.saveUser(userDTO);
        return message;
    }
}
