package com.snwolf.bi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.snwolf.bi.domain.dto.ChartAddDTO;
import com.snwolf.bi.domain.dto.ChartUpdateDTO;
import com.snwolf.bi.domain.dto.IdDTO;
import com.snwolf.bi.domain.dto.UserDTO;
import com.snwolf.bi.domain.entity.Chart;
import com.snwolf.bi.exception.ChartNotExistException;
import com.snwolf.bi.exception.RoleNotAuthException;
import com.snwolf.bi.mapper.ChartMapper;
import com.snwolf.bi.service.IChartService;
import com.snwolf.bi.utils.UserHolder;
import org.springframework.stereotype.Service;

@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart> implements IChartService {
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
}
