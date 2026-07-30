package com.rngad33.booruso.service.impl;

import cn.hutool.core.util.StrUtil;
import com.rngad33.booruso.manager.jsoup.Search;
import com.rngad33.booruso.manager.jsoup.SearchByBooru;
import com.rngad33.booruso.manager.jsoup.SearchByDuiTang;
import com.rngad33.booruso.model.enums.ErrorCodeEnum;
import com.rngad33.booruso.service.SearchService;
import com.rngad33.booruso.utils.ThrowUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 搜索服务实现
 * 支持同步和异步两种调用方式
 */
@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    /** 异步请求最大等待时间（秒） */
    private static final int ASYNC_TIMEOUT = 20;

    @Resource
    private SearchByBooru searchByBooru;

    @Resource
    private SearchByDuiTang searchByDuiTang;

    // ==================== 同步调用 ====================

    @Override
    public List<String> doEasySearch(String searchText, int pageNum, int sourceCode) {
        ThrowUtils.throwIf(StrUtil.isBlank(searchText) || pageNum < 0 || sourceCode < 0, ErrorCodeEnum.NO_PARAMS);
        Search search = this.getSearch(sourceCode);
        ThrowUtils.throwIf(search == null, ErrorCodeEnum.PARAMS_ERROR, "不支持的搜索源！");
        return search.doEasySearch(searchText, pageNum, sourceCode);
    }

    @Override
    public String getOriginalImageUrl(String easyPageUrl, int sourceCode) {
        ThrowUtils.throwIf(StrUtil.isBlank(easyPageUrl) || sourceCode < 0, ErrorCodeEnum.NO_PARAMS);
        Search search = this.getSearch(sourceCode);
        ThrowUtils.throwIf(search == null, ErrorCodeEnum.PARAMS_ERROR, "不支持的搜索源！");
        return search.getOriginalImageUrl(easyPageUrl);
    }

    // ==================== 异步调用（@Async 方式） ====================

    @Async("httpTaskExecutor")
    @Override
    public Future<List<String>> doEasySearchAsync(String searchText, int pageNum, int sourceCode) {
        try {
            List<String> result = doEasySearch(searchText, pageNum, sourceCode);
            return new AsyncResult<>(result);
        } catch (Exception e) {
            log.error("异步搜索异常 - sourceCode: {}, 错误: {}", sourceCode, e.getMessage());
            return new AsyncResult<>(null);
        }
    }

    // ==================== CompletableFuture 方式（推荐） ====================

    /**
     * 异步搜索并返回 CompletableFuture
     * 支持链式调用、组合、超时控制
     */
    public CompletableFuture<List<String>> searchAsync(String searchText, int pageNum, int sourceCode) {
        ThrowUtils.throwIf(StrUtil.isBlank(searchText) || pageNum < 0 || sourceCode < 0, ErrorCodeEnum.NO_PARAMS);

        Search search = this.getSearch(sourceCode);
        ThrowUtils.throwIf(search == null, ErrorCodeEnum.PARAMS_ERROR, "不支持的搜索源！");

        return CompletableFuture
                .supplyAsync(() -> search.doEasySearch(searchText, pageNum, sourceCode))
                .orTimeout(ASYNC_TIMEOUT, TimeUnit.SECONDS)  // 总超时控制
                .exceptionally(ex -> {
                    if (ex instanceof TimeoutException) {
                        log.warn("搜索请求超时 - sourceCode: {}, searchText: {}", sourceCode, searchText);
                    } else {
                        log.error("搜索请求异常 - sourceCode: {}, 错误: {}", sourceCode, ex.getMessage());
                    }
                    return null;
                });
    }

    /**
     * 异步获取原图地址
     */
    public CompletableFuture<String> getOriginalImageUrlAsync(String easyPageUrl, int sourceCode) {
        ThrowUtils.throwIf(StrUtil.isBlank(easyPageUrl) || sourceCode < 0, ErrorCodeEnum.NO_PARAMS);

        return CompletableFuture
                .supplyAsync(() -> getOriginalImageUrl(easyPageUrl, sourceCode))
                .orTimeout(ASYNC_TIMEOUT, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    log.error("获取原图异常 - 错误: {}", ex.getMessage());
                    return null;
                });
    }

    // ==================== 私有方法 ====================

    private Search getSearch(int sourceCode) {
        return switch (sourceCode) {
            case 1 -> searchByBooru;
            case 2 -> searchByDuiTang;
            default -> null;
        };
    }
}
