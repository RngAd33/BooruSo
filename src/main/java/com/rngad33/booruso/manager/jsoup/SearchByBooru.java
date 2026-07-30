package com.rngad33.booruso.manager.jsoup;

import cn.hutool.core.util.StrUtil;
import com.rngad33.booruso.constant.PathConstant;
import com.rngad33.booruso.model.enums.ErrorCodeEnum;
import com.rngad33.booruso.utils.HttpUtils;
import com.rngad33.booruso.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Safebooru 搜索实现
 * 使用 OkHttp 连接池 + Jsoup 解析 + 异步处理
 */
@Component
@Slf4j
public class SearchByBooru implements Search {

    @Resource
    private HttpUtils httpUtils;

    @Override
    public List<String> doEasySearch(String searchText, int pageNum, int sourceCode) {
        ThrowUtils.throwIf(StrUtil.isBlank(searchText) || pageNum < 0, ErrorCodeEnum.NO_PARAMS);
        int pid = pageNum * 42;
        String url = String.format(PathConstant.BOORU_ROOT_URL, searchText, pid);

        // 通过 OkHttp 连接池获取 HTML
        String html = httpUtils.get(url);
        if (html == null) {
            log.warn("获取页面失败 - URL: {}", url);
            return new LinkedList<>();
        }

        // Jsoup 解析 HTML（不再负责网络请求）
        List<String> pictures = new LinkedList<>();
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("img.preview");
        for (Element element : elements) {
            String easyPageUrl = element.attr("src");
            pictures.add(easyPageUrl);
        }
        return pictures;
    }

    @Override
    public String getOriginalImageUrl(String easyPageUrl) {
        ThrowUtils.throwIf(StrUtil.isBlank(easyPageUrl), ErrorCodeEnum.NO_PARAMS);
        try {
            String imageId = easyPageUrl.substring(easyPageUrl.lastIndexOf("?") + 1);
            String detailPageUrl = "https://safebooru.org/index.php?page=post&s=view&id=" + imageId;
            return doGetOriginalImage(detailPageUrl);
        } catch (Exception e) {
            log.error("获取原图地址异常 - URL: {}, 错误: {}", easyPageUrl, e.getMessage());
            return null;
        }
    }

    /**
     * 异步获取原图地址（CompletableFuture 方式）
     */
    @Async("httpTaskExecutor")
    public CompletableFuture<String> getOriginalImageUrlAsync(String easyPageUrl) {
        return CompletableFuture.completedFuture(getOriginalImageUrl(easyPageUrl));
    }

    /**
     * 从详情页获取原图地址（仅适用于 Safebooru）
     * 使用 OkHttp 连接池获取页面内容，Jsoup 纯解析
     */
    private String doGetOriginalImage(String detailPageUrl) {
        ThrowUtils.throwIf(StrUtil.isBlank(detailPageUrl), ErrorCodeEnum.NO_PARAMS);

        // 通过连接池获取 HTML
        String html = httpUtils.get(detailPageUrl);
        if (html == null) {
            return null;
        }

        // 纯解析，不再发起网络请求
        Document doc = Jsoup.parse(html);
        Element originalLink = doc.select("div.link-list a:contains(Original image)").first();
        if (originalLink != null) {
            String href = originalLink.attr("href");
            if (href.startsWith("http")) {
                return href;
            } else if (href.startsWith("/")) {
                return "https://safebooru.org" + href;
            }
        }

        // 备选：尝试从 img#image 标签获取
        Element imageElement = doc.select("img#image").first();
        if (imageElement != null) {
            return imageElement.attr("src");
        }

        return null;
    }
}
