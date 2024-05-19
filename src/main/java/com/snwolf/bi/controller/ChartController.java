package com.snwolf.bi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.snwolf.bi.annotation.CheckRole;
import com.snwolf.bi.domain.dto.*;
import com.snwolf.bi.domain.entity.Chart;
import com.snwolf.bi.result.Result;
import com.snwolf.bi.service.IChartService;
import com.snwolf.bi.utils.UserHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/list/page")
    @ApiOperation("分页查询图表")
    public Result<Page<Chart>> pageQuery(@RequestBody ChartPageQueryDTO chartPageQueryDTO){
        Page<Chart> result = chartService.pageQuery(chartPageQueryDTO);
        return Result.success(result);
    }

    @PostMapping("/my/list/page")
    @ApiOperation("分页查询当前用户图表")
    public Result<Page<Chart>> myPageQuery(@RequestBody ChartPageQueryDTO chartPageQueryDTO){
        Long userId = UserHolder.getUser().getId();
        chartPageQueryDTO.setUserId(userId);
        return pageQuery(chartPageQueryDTO);
    }

    @PostMapping("/genConclusion")
    public Result<String> genConclusionByAi(@RequestPart("file")MultipartFile multipartFile, ChartGenDTO chartGenDTO){
        String result = chartService.genConclusionByAi(multipartFile, chartGenDTO);
        return Result.success(result);
    }
}
