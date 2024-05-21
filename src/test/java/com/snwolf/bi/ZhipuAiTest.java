//package com.snwolf.bi;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.zhipu.oapi.ClientV4;
//import com.zhipu.oapi.Constants;
//import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
//import com.zhipu.oapi.service.v4.model.ChatMessage;
//import com.zhipu.oapi.service.v4.model.ChatMessageRole;
//import com.zhipu.oapi.service.v4.model.ModelApiResponse;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import javax.annotation.PostConstruct;
//import java.util.ArrayList;
//import java.util.List;
//
//@SpringBootTest
//public class ZhipuAiTest {
//
//    private String ApiKey = "fc158f96edaf475ad51f2b2a76a92be5.FP5R61fihZz0cbt2";
//
//    private ClientV4 clientV4 = new ClientV4.Builder(ApiKey).build();;
//
//    @Test
//    void testInvoke(){
//        List<ChatMessage> messages = new ArrayList<>();
//        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), "给我推荐几首黄诗扶的歌曲");
//        messages.add(chatMessage);
//        String requestIdTemplate = "%s";
//        String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
//
//        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
//                .model(Constants.ModelChatGLM4)
//                .stream(Boolean.FALSE)
//                .invokeMethod(Constants.invokeMethod)
//                .messages(messages)
//                .requestId(requestId)
//                .build();
//        ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            System.out.println("model output:" + mapper.writeValueAsString(invokeModelApiResp));
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//    }
//}
