package com.rngad33.booruso.service;

import java.util.List;
import java.util.concurrent.Future;

/**
 * 搜索服务接口
 */
public interface SearchService {

    /**
     * 获取缩略图地址表（同步）
     */
    List<String> doEasySearch(String searchText, int pageNum, int sourceCode);

    /**
     * 获取缩略图地址表（异步）
     */
    Future<List<String>> doEasySearchAsync(String searchText, int pageNum, int sourceCode);

    /**
     * 获取原图地址（同步）
     */
    String getOriginalImageUrl(String easyPageUrl, int sourceCode);

}
