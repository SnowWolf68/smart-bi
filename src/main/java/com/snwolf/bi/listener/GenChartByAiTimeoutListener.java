package com.snwolf.bi.listener;

import com.snwolf.bi.constants.RabbitMqConstants;
import com.snwolf.bi.domain.entity.Chart;
import com.snwolf.bi.domain.entity.Message;
import com.snwolf.bi.service.IChartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class GenChartByAiTimeoutListener {

    private final IChartService chartService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitMqConstants.GEN_CHART_BY_AI_TIMEOUT_QUEUE, durable = "true"),
            exchange = @Exchange(name = RabbitMqConstants.GEN_CHART_BY_AI_TIMEOUT_EXCHANGE, delayed = "true"),
            key = RabbitMqConstants.GEN_CHART_BY_AI_TIMEOUT_KEY
    ))
    public void ttlListener(Message message){
        log.info("收到延迟消息: {}", message);
        Long chartId = message.getChartId();
        Chart oldChart = chartService.getById(chartId);
        if(oldChart.getStatus().equals("wait") || oldChart.getStatus().equals("running")) {
            log.info("图表生成超时, 自动设置图表生成状态为failed");
            Chart chart = Chart.builder()
                    .id(chartId)
                    .status("failed")
                    .execMessage("接口调用超时")
                    .build();
            chartService.updateById(chart);
        }else{
            log.info("图表生成状态正常");
        }
    }
}
