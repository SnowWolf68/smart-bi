package com.snwolf.bi.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.snwolf.bi.domain.dto.*;
import com.snwolf.bi.domain.entity.Chart;
import org.springframework.web.multipart.MultipartFile;

public interface IChartService extends IService<Chart> {
    Long add(ChartAddDTO chartAddDTO);

    void updateWithUserId(ChartUpdateDTO chartUpdateDTO);

    void delete(IdDTO idDTO);

    Page<Chart> pageQuery(ChartPageQueryDTO chartPageQueryDTO);

    String genConclusionByAi(MultipartFile multipartFile, ChartGenDTO chartGenDTO);

    String genChartByAi(MultipartFile multipartFile, ChartGenDTO chartGenDTO);
}
