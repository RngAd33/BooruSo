package com.rngad33.booruso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
class BoorusoApplicationTests {

    /**
     * 批量获取图片详情页地址
     *
     * @throws IOException
     */
    @Test
    void doSearchFromSafebooru() throws IOException {
        Document doc = Jsoup.connect("https://safebooru.org/index.php?page=post&s=list&tags=arknights&pid=0").get();
//        System.out.println(doc);
        // 筛选缩略图元素
        List<String> pictures = new LinkedList<>();
        Elements elements = doc.select("img.preview");
        for (Element element : elements) {
            String m = element.attr("src");
            // 构造原图地址
            String imageId = m.substring(m.lastIndexOf("?") + 1);
            String detailPageUrl = "https://safebooru.org/index.php?page=post&s=view&id=" + imageId;
            String finalPageUrl = this.getOriginalImageUrl(detailPageUrl);
            pictures.add(finalPageUrl);
        }
        System.out.println(pictures);
    }

    /**
     * 从详情页获取原图地址（仅适用于 Safebooru）
     *
     * @param detailPageUrl 缩略图地址
     * @return 原图地址
     * @throws IOException
     */
    private String getOriginalImageUrl(String detailPageUrl) throws IOException {
        Document doc = Jsoup.connect(detailPageUrl).get();

        // 方法1: 通过"Original image"链接获取
        Element originalLink = doc.select("div.link-list a:contains(Original image)").first();
        if (originalLink != null) {
            String href = originalLink.attr("href");
            if (href.startsWith("http")) {
                return href;
            } else if (href.startsWith("/")) {
                return "https://safebooru.org" + href;
            }
        }

        // 备选方法1: 通过解析JavaScript中的image对象获取
        Elements scripts = doc.select("script");
        for (Element script : scripts) {
            String scriptContent = script.data();
            if (scriptContent.contains("image = ")) {
                // 使用正则表达式提取image对象中的信息
                Pattern pattern = Pattern.compile("image = \\{(?:[^{}]|\\{[^{}]*\\})*\\}");
                Matcher matcher = pattern.matcher(scriptContent);

                if (matcher.find()) {
                    String imageObj = matcher.group();
                    // 提取各个字段
                    Pattern fieldPattern = Pattern.compile("'([^']+)'\\s*:\\s*'([^']+)'");
                    Matcher fieldMatcher = fieldPattern.matcher(imageObj);

                    String domain = null, baseDir = null, dir = null, img = null;

                    while (fieldMatcher.find()) {
                        String key = fieldMatcher.group(1);
                        String value = fieldMatcher.group(2);

                        switch (key) {
                            case "domain":
                                domain = value;
                                break;
                            case "base_dir":
                                baseDir = value;
                                break;
                            case "dir":
                                dir = value;
                                break;
                            case "img":
                                img = value;
                                break;
                        }
                    }

                    if (domain != null && baseDir != null && dir != null && img != null) {
                        return domain + "/" + baseDir + "/" + dir + "/" + img;
                    }
                }
            }
        }

        // 备选方法2: 通过<meta>标签获取
        Element metaImage = doc.select("meta[property=og:image]").first();
        if (metaImage != null) {
            return metaImage.attr("content");
        }

        // 备选方法3: 通过<img id="image">获取，但需要转换为原图链接
        Element imageElement = doc.getElementById("image");
        if (imageElement != null) {
            String src = imageElement.attr("src");
            // 将samples路径替换为images路径，去掉sample_前缀
            if (src.contains("/samples/") && src.contains("/sample_")) {
                return src.replace("/samples/", "/images/").replace("/sample_", "/");
            }
            return src;
        }

        return null;
    }

}