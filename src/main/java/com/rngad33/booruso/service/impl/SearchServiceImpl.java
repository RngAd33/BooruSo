package com.rngad33.booruso.service.impl;

import cn.hutool.core.util.StrUtil;
import com.rngad33.booruso.model.enums.ErrorCodeEnum;
import com.rngad33.booruso.service.SearchService;
import com.rngad33.booruso.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 搜索服务实现
 */
@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    /**
     * 根 URL
     */
    private static final String ROOT_URL = "https://safebooru.org/index.php?page=post&s=list&tags=%s&pid=%d";

    /**
     * 获取缩略图地址表
     *
     * @param searchText
     * @return
     */
    @Override
    public List<String> doEasySearch(String searchText, int pageNum) {
        ThrowUtils.throwIf(StrUtil.isBlank(searchText) || pageNum < 0, ErrorCodeEnum.NO_PARAMS, "参数不能为空！");
        try {
            Document doc = Jsoup.connect(String.format(ROOT_URL, searchText, pageNum)).get();
            // 筛选缩略图元素
            List<String> pictures = new LinkedList<>();
            Elements elements = doc.select("img.preview");
            for (Element element : elements) {
                String easyPageUrl = element.attr("src");
                pictures.add(easyPageUrl);
            }
            return pictures;
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * 获取原图地址
     *
     * @param easyPageUrl 缩略图地址
     * @return 原图地址
     */
    @Override
    public String getOriginalImageUrl(String easyPageUrl) {
        ThrowUtils.throwIf(StrUtil.isBlank(easyPageUrl), ErrorCodeEnum.NO_PARAMS);
        try {
            // 构造详情页地址
            String imageId = easyPageUrl.substring(easyPageUrl.lastIndexOf("?") + 1);
            String detailPageUrl = "https://safebooru.org/index.php?page=post&s=view&id=" + imageId;
            return this.doGet(detailPageUrl);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * 从详情页获取原图地址（仅适用于 Safebooru）
     *
     * @param detailPageUrl 详情页地址
     * @return 原图地址
     * @throws IOException
     */
    private String doGet(String detailPageUrl) throws IOException {
        ThrowUtils.throwIf(StrUtil.isBlank(detailPageUrl), ErrorCodeEnum.NO_PARAMS);
        Document doc = Jsoup.connect(detailPageUrl).get();
        // 通过"Original image"链接获取
        Element originalLink = doc.select("div.link-list a:contains(Original image)").first();
        if (originalLink != null) {
            String href = originalLink.attr("href");
            if (href.startsWith("http")) {
                return href;
            } else if (href.startsWith("/")) {
                return "https://safebooru.org" + href;
            }
        }
        return null;
    }

}