package com.rngad33.booruso.manager.jsoup;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.rngad33.booruso.constant.PathConstant;
import com.rngad33.booruso.model.enums.ErrorCodeEnum;
import com.rngad33.booruso.utils.HttpUtils;
import com.rngad33.booruso.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 堆糖搜索实现
 * 使用 OkHttp 连接池 + 异步处理
 */
@Component
@Slf4j
public class SearchByDuiTang implements Search {

    @Resource
    private HttpUtils httpUtils;

    @Override
    public List<String> doEasySearch(String searchText, int pageNum, int sourceCode) {
        ThrowUtils.throwIf(StrUtil.isBlank(searchText) || pageNum < 0 || sourceCode < 0, ErrorCodeEnum.NO_PARAMS);
        int after_id = pageNum * 24;
        String sub_ = StrUtil.toString(RandomUtil.randomNumbers(12));

        // 通过 OkHttp 连接池发送请求
        String url = String.format(PathConstant.DUITANG_ROOT_URL, searchText, after_id, sub_);
        String jsonResponse = httpUtils.get(url);

        if (jsonResponse == null) {
            log.warn("获取堆糖数据失败 - searchText: {}, pageNum: {}", searchText, pageNum);
            return new ArrayList<>();
        }

        // 解析 JSON 响应
        List<String> pictures = new ArrayList<>();
        Set<String> seenUrls = new HashSet<>();
        try {
            JSONObject jsonObject = JSONUtil.parseObj(jsonResponse);
            if (jsonObject.containsKey("data") && jsonObject.getJSONObject("data").containsKey("object_list")) {
                JSONArray objectList = jsonObject.getJSONObject("data").getJSONArray("object_list");
                for (int i = 0; i < objectList.size(); i++) {
                    JSONObject item = objectList.getJSONObject(i);
                    // 从 album.covers 字段提取图片路径
                    if (item.containsKey("album") && item.getJSONObject("album").containsKey("covers")) {
                        JSONArray coversArray = item.getJSONObject("album").getJSONArray("covers");
                        if (coversArray != null) {
                            for (int j = 0; j < coversArray.size(); j++) {
                                deduplicate(coversArray.getStr(j), seenUrls, pictures);
                            }
                        }
                    }
                    // 从 photo.path 字段提取图片路径
                    if (item.containsKey("photo")) {
                        String imageUrl = item.getJSONObject("photo").getStr("path");
                        deduplicate(imageUrl, seenUrls, pictures);
                    }
                }
            }
        } catch (Exception e) {
            log.error("解析堆糖响应异常 - 错误: {}", e.getMessage());
        }
        return pictures;
    }

    /**
     * 异步搜索（CompletableFuture 方式）
     */
    @Async("httpTaskExecutor")
    public CompletableFuture<List<String>> doEasySearchAsync(String searchText, int pageNum, int sourceCode) {
        return CompletableFuture.completedFuture(doEasySearch(searchText, pageNum, sourceCode));
    }

    @Override
    public String getOriginalImageUrl(String easyPageUrl) {
        log.warn("堆糖不支持获取原图地址");
        return null;
    }

    /**
     * 去重
     */
    private void deduplicate(String coverUrl, Set<String> seenUrls, List<String> pictures) {
        if (coverUrl != null && !coverUrl.isEmpty() && !seenUrls.contains(coverUrl)) {
            pictures.add(coverUrl);
            seenUrls.add(coverUrl);
        }
    }
}
