package com.rngad33.booruso.controller;

import cn.hutool.core.util.StrUtil;
import com.rngad33.booruso.common.BaseResponse;
import com.rngad33.booruso.model.enums.ErrorCodeEnum;
import com.rngad33.booruso.service.SearchService;
import com.rngad33.booruso.utils.ResultUtils;
import com.rngad33.booruso.utils.ThrowUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 搜索接口
 */
@RestController
@RequestMapping("/search")
public class SearchController {

    @Resource
    private SearchService searchService;

    /**
     * 获取缩略图地址表
     *
     * @param searchText
     * @return
     */
    @GetMapping("/easy")
    public BaseResponse<List<String>> doEasySearch(@RequestParam("searchText") String searchText,
                                                   @RequestParam("pageNum") int pageNum) {
        ThrowUtils.throwIf(StrUtil.isBlank(searchText) || pageNum < 0, ErrorCodeEnum.NO_PARAMS, "参数不能为空！");
        return ResultUtils.success(searchService.doEasySearch(searchText, pageNum));
    }

    /**
     * 获取原图地址
     *
     * @param easyPageUrl 缩略图地址
     * @return 原图地址
     */
    @GetMapping("/final")
    public BaseResponse<String> getOriginalImageUrl(@RequestParam("easyPageUrl") String easyPageUrl) {
        ThrowUtils.throwIf(StrUtil.isBlank(easyPageUrl), ErrorCodeEnum.NO_PARAMS);
        return ResultUtils.success(searchService.getOriginalImageUrl(easyPageUrl));
    }

}