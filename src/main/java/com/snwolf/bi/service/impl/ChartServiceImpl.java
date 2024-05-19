package com.snwolf.bi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.snwolf.bi.domain.dto.*;
import com.snwolf.bi.domain.entity.Chart;
import com.snwolf.bi.exception.ChartNotExistException;
import com.snwolf.bi.exception.RoleNotAuthException;
import com.snwolf.bi.mapper.ChartMapper;
import com.snwolf.bi.service.IChartService;
import com.snwolf.bi.utils.ExcelUtils;
import com.snwolf.bi.utils.UserHolder;
import com.snwolf.bi.utils.ZhipuAiUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart> implements IChartService {

    @Value("${snwolf.prompt}")
    private String prompt;

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
    public String genChartByAi(MultipartFile multipartFile, ChartGenDTO chartGenDTO) {
        String name = chartGenDTO.getName();
        String goal = chartGenDTO.getGoal();
        String chartType = chartGenDTO.getChartType();

        String csv = ExcelUtils.excelToCsv(multipartFile);
        log.info("csv: {}", csv);

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

        String aiResult = ZhipuAiUtils.sendMessageAndGetResponse(message.toString());
        // TODO: 对result进行处理, 得到其中的ECharts代码和结论数据, 这里先不进行过滤
        String chartStr = aiResult;
        String conclusionStr = aiResult;
        Chart chart = Chart.builder()
                .name(name)
                .chartType(chartType)
                .chartData(csv)
                .goal(goal)
                .userId(UserHolder.getUser().getId())
                .genResult(conclusionStr)
                .genChart(chartStr)
                .build();
        save(chart);
        return aiResult;
    }
}
