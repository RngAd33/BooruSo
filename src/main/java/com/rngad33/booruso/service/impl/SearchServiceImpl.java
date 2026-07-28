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
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 搜索服务实现
 */
@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    @Resource
    private SearchByBooru searchByBooru;

    @Resource
    private SearchByDuiTang searchByDuiTang;

    @Override
    public List<String> doEasySearch(String searchText, int pageNum, int sourceCode) {
        ThrowUtils.throwIf(StrUtil.isBlank(searchText) || pageNum < 0 || sourceCode < 0, ErrorCodeEnum.NO_PARAMS);
        Search search = this.getSearch(sourceCode);
        ThrowUtils.throwIf(search == null, ErrorCodeEnum.PARAMS_ERROR, "不支持的源！");
        return search.doEasySearch(searchText, pageNum, sourceCode);
    }

    @Override
    public String getOriginalImageUrl(String easyPageUrl, int sourceCode) {
        ThrowUtils.throwIf(StrUtil.isBlank(easyPageUrl) || sourceCode < 0, ErrorCodeEnum.NO_PARAMS);
        Search search = this.getSearch(sourceCode);
        ThrowUtils.throwIf(search == null, ErrorCodeEnum.PARAMS_ERROR, "不支持的源！");
        return search.getOriginalImageUrl(easyPageUrl);
    }

    /**
     * 获取搜索源
     *
     * @param sourceCode
     * @return
     */
    private Search getSearch(int sourceCode) {
        switch (sourceCode) {
            case 1:
                return searchByBooru;
            case 2:
                return searchByDuiTang;
            default:
                return null;
        }
    }

}