package com.rngad33.booruso.manager.jsoup;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.rngad33.booruso.constant.PathConstant;
import com.rngad33.booruso.model.enums.ErrorCodeEnum;
import com.rngad33.booruso.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * 搜索服务实现
 */
@Component
@Slf4j
public class SearchByDuiTang implements Search {

    @Override
    public List<String> doEasySearch(String searchText, int pageNum, int sourceCode) {
        ThrowUtils.throwIf(StrUtil.isBlank(searchText) || pageNum < 0 || sourceCode < 0, ErrorCodeEnum.NO_PARAMS);
        int after_id = pageNum * 24;
        String sub_ = StrUtil.toString(RandomUtil.randomNumbers(12));

        // 发送请求
        String url = String.format(PathConstant.DUITANG_ROOT_URL, searchText, after_id, sub_);
        String jsonResponse = HttpUtil.get(url);

        // 抓取图片链接
        List<String> pictures = new ArrayList<>();
        Set<String> seenUrls = new HashSet<>();
        JSONObject jsonObject = JSONUtil.parseObj(jsonResponse);
        if (jsonObject.containsKey("data") && jsonObject.getJSONObject("data").containsKey("object_list")) {
            JSONArray objectList = jsonObject.getJSONObject("data").getJSONArray("object_list");
            for (int i = 0; i < objectList.size(); i++) {
                JSONObject item = objectList.getJSONObject(i);
                // 从album.covers字段提取图片路径
                if (item.containsKey("album") && item.getJSONObject("album").containsKey("covers")) {
                    JSONArray coversArray = item.getJSONObject("album").getJSONArray("covers");
                    if (coversArray != null) {
                        for (int j = 0; j < coversArray.size(); j++) {
                            String coverUrl = coversArray.getStr(j);
                            // 去重处理
                            this.dew(coverUrl, seenUrls, pictures);
                        }
                    }
                }
                // 从photo.path字段提取图片路径
                if (item.containsKey("photo")) {
                    JSONObject photo = item.getJSONObject("photo");
                    String imageUrl = photo.getStr("path");
                    // 去重处理
                    this.dew(imageUrl, seenUrls, pictures);
                }
            }
        }
        return pictures;
    }

    @Override
    public String getOriginalImageUrl(String easyPageUrl) {
        log.warn("你干嘛？");
        return "你干嘛？";
    }

    /**
     * 去重
     *
     * @param coverUrl
     * @param seenUrls
     * @param pictures
     */
    private void dew(String coverUrl, Set<String> seenUrls, List<String> pictures) {
        if (coverUrl != null && !seenUrls.contains(coverUrl)) {
            pictures.add(coverUrl);
            seenUrls.add(coverUrl);
        }
    }

}