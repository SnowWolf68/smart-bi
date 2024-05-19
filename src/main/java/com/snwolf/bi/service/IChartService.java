package com.snwolf.bi.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.snwolf.bi.domain.dto.ChartAddDTO;
import com.snwolf.bi.domain.dto.ChartPageQueryDTO;
import com.snwolf.bi.domain.dto.ChartUpdateDTO;
import com.snwolf.bi.domain.dto.IdDTO;
import com.snwolf.bi.domain.entity.Chart;

public interface IChartService extends IService<Chart> {
    Long add(ChartAddDTO chartAddDTO);

    void updateWithUserId(ChartUpdateDTO chartUpdateDTO);

    void delete(IdDTO idDTO);

    Page<Chart> pageQuery(ChartPageQueryDTO chartPageQueryDTO);
}
