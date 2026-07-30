package com.rngad33.booruso.controller;

import cn.hutool.core.util.StrUtil;
import com.rngad33.booruso.common.BaseResponse;
import com.rngad33.booruso.model.enums.ErrorCodeEnum;
import com.rngad33.booruso.service.SearchService;
import com.rngad33.booruso.utils.ResultUtils;
import com.rngad33.booruso.utils.ThrowUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 搜索接口
 * 内部通过 OkHttp 连接池 + 异步线程池处理网络请求
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {

    /** 请求最大等待时间（秒） */
    private static final int REQUEST_TIMEOUT = 20;

    @Resource
    private SearchService searchService;

    /**
     * 获取缩略图地址表
     * 使用异步 + 超时控制，避免长时间阻塞
     */
    @GetMapping("/easy")
    public BaseResponse<List<String>> doEasySearch(@RequestParam("searchText") String searchText,
                                                   @RequestParam("pageNum") int pageNum,
                                                   @RequestParam("sourceCode") int sourceCode) {
        ThrowUtils.throwIf(StrUtil.isBlank(searchText) || pageNum < 0 || sourceCode < 0,
                ErrorCodeEnum.NO_PARAMS, "参数不能为空！");
        try {
            // 异步提交任务，带超时等待
            Future<List<String>> future = searchService.doEasySearchAsync(searchText, pageNum, sourceCode);
            List<String> result = future.get(REQUEST_TIMEOUT, TimeUnit.SECONDS);
            return ResultUtils.success(result);
        } catch (TimeoutException e) {
            log.warn("搜索请求超时 - sourceCode: {}, searchText: {}", sourceCode, searchText);
            return ResultUtils.error(ErrorCodeEnum.REQUEST_TIMEOUT, "搜索请求超时，请稍后重试");
        } catch (Exception e) {
            log.error("搜索请求异常 - sourceCode: {}, 错误: {}", sourceCode, e.getMessage());
            return ResultUtils.error(ErrorCodeEnum.SYSTEM_ERROR, "搜索服务暂不可用");
        }
    }

    /**
     * 获取原图地址
     */
    @GetMapping("/final")
    public BaseResponse<String> getOriginalImageUrl(@RequestParam("easyPageUrl") String easyPageUrl,
                                                    @RequestParam("sourceCode") int sourceCode) {
        ThrowUtils.throwIf(StrUtil.isBlank(easyPageUrl) || sourceCode < 0, ErrorCodeEnum.NO_PARAMS);
        return ResultUtils.success(searchService.getOriginalImageUrl(easyPageUrl, sourceCode));
    }
}
