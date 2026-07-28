package com.rngad33.booruso.manager.jsoup;

import cn.hutool.core.util.StrUtil;
import com.rngad33.booruso.constant.PathConstant;
import com.rngad33.booruso.model.enums.ErrorCodeEnum;
import com.rngad33.booruso.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * 搜索服务实现
 */
@Component
@Slf4j
public class SearchByBooru implements Search {

    @Override
    public List<String> doEasySearch(String searchText, int pageNum, int sourceCode) {
        ThrowUtils.throwIf(StrUtil.isBlank(searchText) || pageNum < 0, ErrorCodeEnum.NO_PARAMS);
        int pid = pageNum * 42;
        try {
            Document doc = Jsoup.connect(String.format(PathConstant.BUURU_ROOT_URL, searchText, pid)).get();
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

    @Override
    public String getOriginalImageUrl(String easyPageUrl) {
        ThrowUtils.throwIf(StrUtil.isBlank(easyPageUrl), ErrorCodeEnum.NO_PARAMS);
        try {
            // 构造详情页地址
            String imageId = easyPageUrl.substring(easyPageUrl.lastIndexOf("?") + 1);
            String detailPageUrl = "https://safebooru.org/index.php?page=post&s=view&id=" + imageId;
            return doGet(detailPageUrl);
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
    private static String doGet(String detailPageUrl) throws IOException {
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