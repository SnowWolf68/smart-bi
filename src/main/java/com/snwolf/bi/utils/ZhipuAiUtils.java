package com.snwolf.bi.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ZhipuAiUtils {

    private static String ApiKey = "fc158f96edaf475ad51f2b2a76a92be5.FP5R61fihZz0cbt2";

    private static ClientV4 clientV4 = new ClientV4.Builder(ApiKey).build();

    public static String sendMessageAndGetResponse(String message){
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
        messages.add(chatMessage);
        String requestIdTemplate = "%s";
        String requestId = String.format(requestIdTemplate, System.currentTimeMillis());

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .requestId(requestId)
                .build();
        ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String result = mapper.writeValueAsString(invokeModelApiResp);
            log.info("model output:" + result);
            return result;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
