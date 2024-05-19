package com.snwolf.bi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.snwolf.bi.domain.dto.*;
import com.snwolf.bi.domain.entity.Chart;
import com.snwolf.bi.exception.*;
import com.snwolf.bi.mapper.ChartMapper;
import com.snwolf.bi.service.IChartService;
import com.snwolf.bi.utils.ExcelUtils;
import com.snwolf.bi.utils.RedisLimiter;
import com.snwolf.bi.utils.UserHolder;
import com.snwolf.bi.utils.ZhipuAiUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart> implements IChartService {

    @Value("${snwolf.prompt}")
    private String prompt;

    private final RedissonClient redissonClient;

    private final ExecutorService GEN_CHART_EXECUTOR = new ThreadPoolExecutor(
            4, 6, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>()
    );

    @Override
    public Long add(ChartAddDTO chartAddDTO) {
        Chart chart = BeanUtil.copyProperties(chartAddDTO, Chart.class);
        UserDTO user = UserHolder.getUser();
        Long userId = user.getId();
        chart.setUserId(userId);
        save(chart);
        return chart.getId();
    }

    @Override
    public void updateWithUserId(ChartUpdateDTO chartUpdateDTO) {
        Long userId = UserHolder.getUser().getId();
        lambdaUpdate()
                .eq(Chart::getId, chartUpdateDTO.getId())
                .set(StrUtil.isNotBlank(chartUpdateDTO.getChartData()), Chart::getChartData, chartUpdateDTO.getChartData())
                .set(StrUtil.isNotBlank(chartUpdateDTO.getChartType()), Chart::getChartType, chartUpdateDTO.getChartType())
                .set(StrUtil.isNotBlank(chartUpdateDTO.getGoal()), Chart::getGoal, chartUpdateDTO.getGoal())
                .set(StrUtil.isNotBlank(chartUpdateDTO.getName()), Chart::getName, chartUpdateDTO.getName())
                .set(Chart::getUserId, userId)
                .update();
    }

    @Override
    public void delete(IdDTO idDTO) {
        // 仅本人或管理员可删除
        Long chartId = idDTO.getId();
        Chart chart = getById(chartId);
        if(chart == null){
            throw new ChartNotExistException("图表不存在");
        }
        Long createUserId = chart.getUserId();
        String currentUserRole = UserHolder.getUser().getUserRole();
        Long currentUserId = UserHolder.getUser().getId();
        if(!(currentUserRole.equals("admin") || currentUserId.equals(createUserId))){
            throw new RoleNotAuthException("当前用户角色无权删除图表");
        }
        removeById(chartId);
    }

    @Override
    public Page<Chart> pageQuery(ChartPageQueryDTO chartPageQueryDTO) {
        Page<Chart> page = new Page<>(chartPageQueryDTO.getCurrent(), chartPageQueryDTO.getPageSize());
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(chartPageQueryDTO.getId() != null && chartPageQueryDTO.getId() != 0, "id", chartPageQueryDTO.getId());
        queryWrapper.like(StrUtil.isNotBlank(chartPageQueryDTO.getName()), "name", chartPageQueryDTO.getName());
        queryWrapper.like(StrUtil.isNotBlank(chartPageQueryDTO.getGoal()), "goal", chartPageQueryDTO.getGoal());
        queryWrapper.eq(StrUtil.isNotBlank(chartPageQueryDTO.getChartType()), "chart_type", chartPageQueryDTO.getChartType());
        queryWrapper.eq(chartPageQueryDTO.getUserId() != null && chartPageQueryDTO.getUserId() != 0, "user_id", chartPageQueryDTO.getUserId());
        Page<Chart> pageResult = page(page, queryWrapper);
        return pageResult;
    }

    @Override
    public String genConclusionByAi(MultipartFile multipartFile, ChartGenDTO chartGenDTO) {
        String name = chartGenDTO.getName();
        String goal = chartGenDTO.getGoal();
        String chartType = chartGenDTO.getChartType();

        String csv = ExcelUtils.excelToCsv(multipartFile);
        log.info("csv: {}", csv);

        StringBuilder message = new StringBuilder();
        message.append("你是一个数据分析师, 接下来我会给你我的分析目标和原始数据, 请告诉我分析结论").append("\n");
        message.append("分析目标: " + goal).append("\n");
        message.append("数据: " + csv).append("\n");
        log.info(message.toString());

        String aiResult = ZhipuAiUtils.sendMessageAndGetResponse(message.toString());
        return aiResult;
    }

    @Override
    public Long genChartByAi(MultipartFile multipartFile, ChartGenDTO chartGenDTO) {
        String name = chartGenDTO.getName();
        String goal = chartGenDTO.getGoal();
        String chartType = chartGenDTO.getChartType();

        try {
            boolean checkResult = checkFile(multipartFile);
            if(!checkResult){
                throw new FileCheckException("文件校验不通过");
            }
        } catch (BaseException e){
            throw e;
        }

        String csv = ExcelUtils.excelToCsv(multipartFile);
        log.info("csv: {}", csv);

        rateLimit();

        StringBuilder message = new StringBuilder();
        message.append(prompt).append("\n");
        message.append("分析目标: " + goal).append("\n");
        if(StrUtil.isNotBlank(chartType)){
            message.append("要求得到的图表类型: " + chartType).append("\n");
        }
        if(StrUtil.isNotBlank(name)){
            message.append("要求得到的图表名称: " + name).append("\n");
        }
        message.append("数据: " + csv).append("\n");
        log.info(message.toString());
        
        Chart chart = Chart.builder()
                .name(name)
                .chartType(chartType)
                .chartData(csv)
                .goal(goal)
                .userId(UserHolder.getUser().getId())
                .build();
        save(chart);

        try {
            GEN_CHART_EXECUTOR.submit(
                    () -> {
                        Chart oldChart = getById(chart.getId());
                        if(!oldChart.getStatus().equals("wait")){
                            throw new ChartStatusException("当前图表状态不可生成");
                        }

                        oldChart = Chart.builder()
                                .id(chart.getId())
                                .status("running")
                                .build();
                        updateById(oldChart);

                        String aiResult = ZhipuAiUtils.sendMessageAndGetResponse(message.toString());
                        // TODO: 对result进行处理, 得到其中的ECharts代码和结论数据, 这里先不进行过滤
                        String chartStr = aiResult;
                        String conclusionStr = aiResult;

                        oldChart = Chart.builder()
                                .id(chart.getId())
                                .status("succeed")
                                .genChart(chartStr)
                                .genResult(conclusionStr)
                                .build();

                        updateById(oldChart);
                    }
            );
        } catch (Exception e) {
            log.info("AI生成图表任务失败: {}", e.getMessage());
            Chart failedChart = Chart.builder()
                    .id(chart.getId())
                    .status("failed")
                    .execMessage(e.getMessage())
                    .build();
            updateById(failedChart);
        }

        return chart.getId();
    }

    private void rateLimit() {
        RedisLimiter redisLimiter = new RedisLimiter(redissonClient);
        // 注意这里key不能使用:来进行分级, 否则限流会出错
        redisLimiter.doLimit("genChartByAi_" + UserHolder.getUser().getId(),
                RateType.OVERALL, 5L, 2L, RateIntervalUnit.SECONDS);
    }

    private boolean checkFile(MultipartFile multipartFile) {
        long size = multipartFile.getSize();
        final long ONE_MB = 1024 * 1024L;
        if(size > ONE_MB){
            throw new FileSizeException("文件过大, 要求上传的文件不超过1MB");
        }
        String suffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        if(!suffix.equals("xlsx") && !suffix.equals("xls")){
            throw new FileTypeException("文件类型不支持, 要求上传的文件类型为xlsx或xls");
        }
        return true;
    }
}
