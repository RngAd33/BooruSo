package com.rngad33.booruso.service;

import java.util.List;

/**
 * 搜索服务接口
 */
public interface SearchService {

    /**
     * 获取缩略图地址表
     *
     * @param searchText 搜索词
     * @return
     */
    List<String> doEasySearch(String searchText, int pageNum);

    /**
     * 获取原图地址
     *
     * @param easyPageUrl 缩略图地址
     * @return 原图地址
     */
    String getOriginalImageUrl(String easyPageUrl);

}