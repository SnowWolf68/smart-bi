package com.snwolf.bi.listener;

import com.snwolf.bi.domain.entity.Chart;
import com.snwolf.bi.domain.entity.Message;
import com.snwolf.bi.exception.ChartInfoNotExistException;
import com.snwolf.bi.exception.ChartStatusException;
import com.snwolf.bi.service.IChartService;
import com.snwolf.bi.service.IUserService;
import com.snwolf.bi.utils.UserHolder;
import com.snwolf.bi.utils.ZhipuAiUtils;
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
public class GenChartByAiListener {

    private final IChartService chartService;

    private final IUserService userService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "bi.genChartByAiQueue", durable = "true"),
            exchange = @Exchange(name = "bi.genChartByAiExchange", type = "direct"),
            key = "bi.genChartByAi"
    ))
    public void listen(Message message){
        try {
            log.info("接收到生成任务, 开始执行");

            Long chartId = message.getChartId();

            Long userId = UserHolder.getUser().getId();
            log.info("当前用户id：" + userId);

            String messageStr = message.getMessage();
            log.info("接收到消息：" + chartId + " " + messageStr);

            Chart chart = chartService.getById(chartId);
            if(chart == null){
                throw new ChartInfoNotExistException("当前要生成的图表信息不存在");
            }
            if(chart.getStatus().equals("running") || chart.getStatus().equals("succeed")){
                throw new ChartStatusException("当前图表状态不可生成");
            }

            chart = Chart.builder()
                    .id(chartId)
                    .status("running")
                    .build();
            chartService.updateById(chart);

            String aiResult = ZhipuAiUtils.sendMessageAndGetResponse(messageStr.toString());
            // TODO: 对result进行处理, 得到其中的ECharts代码和结论数据, 这里先不进行过滤
            String chartStr = aiResult;
            String conclusionStr = aiResult;

            chart = Chart.builder()
                    .id(chartId)
                    .status("succeed")
                    .execMessage("执行成功")
                    .genChart(chartStr)
                    .genResult(conclusionStr)
                    .build();

            chartService.updateById(chart);

            userService.deduckCnt(userId);
        } catch (ChartStatusException e) {
            log.info("AI生成图表任务失败: {}", e.getMessage());
            Chart failedChart = Chart.builder()
                    .id(message.getChartId())
                    .status("failed")
                    .execMessage(e.getMessage())
                    .build();
            chartService.updateById(failedChart);
        }
    }
}
