package com.rngad33.booruso;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.rngad33.booruso.constant.PathConstant;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@SpringBootTest
class BoorusoApplicationTests {

    @Test
    void test1() {
        String url = String.format(PathConstant.DUITANG_ROOT_URL, "明日方舟", 24, StrUtil.toString(RandomUtil.randomNumbers(12)));
        String jsonResponse = HttpUtil.get(url);
        //        System.out.println(jsonResponse);
        List<String> pictures = new ArrayList<>();
        Set<String> seenUrls = new HashSet<>();

        if (JSONUtil.isJson(jsonResponse)) {
            JSONObject jsonObject = JSONUtil.parseObj(jsonResponse);

            if (jsonObject.containsKey("data") && jsonObject.getJSONObject("data").containsKey("object_list")) {
                JSONArray objectList = jsonObject.getJSONObject("data").getJSONArray("object_list");

                for (int i = 0; i < objectList.size(); i++) {
                    JSONObject item = objectList.getJSONObject(i);

                    // 从photo字段提取图片路径
                    if (item.containsKey("photo")) {
                        JSONObject photo = item.getJSONObject("photo");
                        String imageUrl = photo.getStr("path");
                        // 去重处理
                        if (imageUrl != null && !seenUrls.contains(imageUrl)) {
                            pictures.add(imageUrl);
                            seenUrls.add(imageUrl);
                        }
                    }
                    // 从blog字段提取
                    else if (item.containsKey("blog")) {
                        JSONObject blog = item.getJSONObject("blog");
                        String imageUrl = blog.getStr("cover_path");
                        if (imageUrl == null) {
                            imageUrl = blog.getStr("photo_path");
                        }

                        if (imageUrl != null && !seenUrls.contains(imageUrl)) {
                            pictures.add(imageUrl);
                            seenUrls.add(imageUrl);
                        }
                    }
                }
            }
        }
        System.out.println(pictures);
    }

}