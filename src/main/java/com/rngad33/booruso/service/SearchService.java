package com.rngad33.booruso.service;

import java.util.List;

/**
 * 搜索服务接口
 */
public interface SearchService {

    /**
     * 获取缩略图地址表
     *
     * @param searchText
     * @param pageNum
     * @param sourceCode
     * @return
     */
    List<String> doEasySearch(String searchText, int pageNum, int sourceCode);

    /**
     * 获取原图地址
     *
     * @param easyPageUrl
     * @param sourceCode
     * @return
     */
    String getOriginalImageUrl(String easyPageUrl, int sourceCode);

}