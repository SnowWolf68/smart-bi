package com.snwolf.bi.controller;

import com.snwolf.bi.annotation.CheckRole;
import com.snwolf.bi.domain.dto.ChartAddDTO;
import com.snwolf.bi.domain.dto.ChartUpdateDTO;
import com.snwolf.bi.domain.dto.IdDTO;
import com.snwolf.bi.domain.entity.Chart;
import com.snwolf.bi.result.Result;
import com.snwolf.bi.service.IChartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chart")
@Api(tags = "图表相关接口")
@RequiredArgsConstructor
public class ChartController {

    private final IChartService chartService;

    @PostMapping("/add")
    @ApiOperation("添加图表")
    public Result<Long> add(@RequestBody ChartAddDTO chartAddDTO){
        Long chartId = chartService.add(chartAddDTO);
        return Result.success(chartId);
    }

    @PostMapping("/delete")
    @ApiOperation("删除图表")
    public Result delete(@RequestBody IdDTO idDTO){
        chartService.delete(idDTO);
        return Result.success();
    }

    @GetMapping("/get")
    @ApiOperation("获取图表")
    public Result<Chart> getById(Long id){
        Chart chart = chartService.getById(id);
        return Result.success(chart);
    }

    @PostMapping("/update")
    @ApiOperation("更新图表")
    @CheckRole(role = "admin")
    public Result update(@RequestBody ChartUpdateDTO chartUpdateDTO){
        chartService.updateWithUserId(chartUpdateDTO);
        return Result.success();
    }
}
