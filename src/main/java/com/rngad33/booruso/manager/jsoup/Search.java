package com.rngad33.booruso.manager.jsoup;

import java.util.List;

public interface Search {

    List<String> doEasySearch(String searchText, int pageNum, int sourceCode);

    String getOriginalImageUrl(String easyPageUrl);

}